package com.hozayushka.app.adapters.weather

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import kotlin.math.roundToInt

internal data class WeatherTransportResponse(
    val statusCode: Int,
    val body: String,
)

internal fun interface WeatherTransport {
    fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse
}

internal class YandexWeatherAdapter(
    private val transport: WeatherTransport = HttpUrlConnectionWeatherTransport(),
) : WeatherProvider {
    override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
        return try {
            val response = request.withRequest { credential ->
                transport.execute(
                    url = requestUrl(request.latitude, request.longitude),
                    headers = mapOf("X-Yandex-Weather-Key" to credential),
                )
            }
            if (response.statusCode !in 200..299) {
                failure(request, statusFailure(response.statusCode))
            } else {
                val data = YandexResponseParser.parse(response.body)
                    ?: return failure(request, WeatherProviderFailure.NETWORK)
                WeatherProviderResult(
                    payload = RedactedProviderPayload(
                        temperatureCelsius = data.current.temperatureCelsius,
                        condition = data.current.condition.orEmpty(),
                    ),
                    credentialWasReceived = request.hasCredential(),
                    redactedCredential = request.redactedCredential(),
                    weatherData = data,
                )
            }
        } catch (_: IOException) {
            failure(request, WeatherProviderFailure.NETWORK)
        } catch (_: RuntimeException) {
            failure(request, WeatherProviderFailure.NETWORK)
        }
    }

    private fun failure(
        request: WeatherProviderRequest,
        failure: WeatherProviderFailure,
    ): WeatherProviderResult = WeatherProviderResult(
        payload = RedactedProviderPayload(0, ""),
        credentialWasReceived = request.hasCredential(),
        redactedCredential = request.redactedCredential(),
        failure = failure,
    )

    private fun requestUrl(latitude: Double, longitude: Double): String =
        String.format(
            Locale.US,
            "%s?lat=%s&lon=%s&hours=true",
            ENDPOINT,
            latitude,
            longitude,
        )

    private fun WeatherProviderRequest.withRequest(block: (String) -> WeatherTransportResponse): WeatherTransportResponse =
        credential.use(block)

    private companion object {
        const val ENDPOINT = "https://api.weather.yandex.ru/v2/forecast"

        fun statusFailure(statusCode: Int): WeatherProviderFailure = when (statusCode) {
            401, 403 -> WeatherProviderFailure.INVALID_CREDENTIAL
            404 -> WeatherProviderFailure.UNKNOWN_CITY
            else -> WeatherProviderFailure.NETWORK
        }
    }
}

internal class HttpUrlConnectionWeatherTransport : WeatherTransport {
    override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MILLIS
            readTimeout = TIMEOUT_MILLIS
            headers.forEach { (name, value) -> setRequestProperty(name, value) }
        }
        return try {
            val statusCode = connection.responseCode
            val body = if (statusCode in 200..299) {
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                ""
            }
            WeatherTransportResponse(statusCode, body)
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 5_000
    }
}

private object YandexResponseParser {
    fun parse(content: String): ProviderWeatherData? = runCatching {
        val root = JsonParser(content).parse() as? JsonObject ?: return null
        val info = root.objectValue("info") ?: return null
        val timezone = info.objectValue("tzinfo")?.stringValue("name")?.takeIf(String::isNotBlank)
            ?: return null
        val fact = root.objectValue("fact") ?: return null
        val currentTemperature = fact.numberValue("temp")?.roundToInt() ?: return null
        val currentPressure = fact.numberValue("pressure_mm") ?: return null
        if (!currentPressure.isFinite()) return null

        val forecasts = root.arrayValue("forecasts")?.values
            ?: root.objectValue("forecast")?.let(::listOf)
            ?: return null
        if (forecasts.isEmpty()) return null
        val daily = forecasts.map { forecast -> parseDaily(forecast as? JsonObject ?: return null) }
        val hourly = forecasts.flatMap { forecast ->
            parseHourly(forecast as? JsonObject ?: return null)
        }
        ProviderWeatherData(
            apiTimeZone = timezone,
            current = ProviderCurrentWeather(
                temperatureCelsius = currentTemperature,
                pressureMmHg = currentPressure,
                condition = fact.stringValue("condition"),
            ),
            daily = daily,
            hourly = hourly,
        )
    }.getOrNull()

    private fun parseDaily(forecast: JsonObject): ProviderDailyWeather {
        val date = forecast.stringValue("date")?.let { LocalDate.parse(it) }
            ?: throw IllegalArgumentException("missing daily date")
        val parts = forecast.arrayValue("parts")?.values.orEmpty()
        val day = parts.firstOrNull { isDayPart(it) } as? JsonObject
            ?: throw IllegalArgumentException("missing daily day part")
        val night = parts.firstOrNull { isNightPart(it) } as? JsonObject
            ?: throw IllegalArgumentException("missing daily night part")
        val dayTemperature = day.temperature() ?: throw IllegalArgumentException("missing daily day temperature")
        val nightTemperature = night.temperature() ?: throw IllegalArgumentException("missing daily night temperature")
        return ProviderDailyWeather(
            date = date,
            dayTemperatureCelsius = dayTemperature,
            nightTemperatureCelsius = nightTemperature,
            dayCondition = day.stringValue("condition"),
            nightCondition = night.stringValue("condition"),
            moonPhase = forecast.stringValue("moon_code")
                ?: forecast.stringValue("moon_phase")
                ?: forecast.stringValue("moon_text"),
        )
    }

    private fun parseHourly(forecast: JsonObject): List<ProviderHourlyWeather> {
        val date = forecast.stringValue("date")?.let(LocalDate::parse)
            ?: throw IllegalArgumentException("missing hourly date")
        return forecast.arrayValue("hours")?.values.orEmpty().map { value ->
            val hour = value as? JsonObject ?: throw IllegalArgumentException("malformed hourly record")
            val time = hour.intValue("hour") ?: throw IllegalArgumentException("missing hourly time")
            val temperature = hour.numberValue("temp")?.roundToInt()
                ?: throw IllegalArgumentException("missing hourly temperature")
            val condition = hour.stringValue("condition")
                ?: throw IllegalArgumentException("missing hourly condition")
            ProviderHourlyWeather(
                date = date.plusDays(if (time >= 24) time / 24L else 0L),
                time = LocalTime.of(time % 24, 0),
                temperatureCelsius = temperature,
                condition = condition,
            )
        }
    }

    private fun JsonObject.temperature(): Int? =
        numberValue("temp_avg")?.roundToInt() ?: numberValue("temp")?.roundToInt()

    private fun isDayPart(value: JsonValue): Boolean {
        val part = value as? JsonObject ?: return false
        return part.stringValue("part_name") == "day" || part.stringValue("daytime") == "d"
    }

    private fun isNightPart(value: JsonValue): Boolean {
        val part = value as? JsonObject ?: return false
        return part.stringValue("part_name") == "night" || part.stringValue("daytime") == "n"
    }
}

private sealed interface JsonValue

private data class JsonObject(val values: Map<String, JsonValue>) : JsonValue

private data class JsonArray(val values: List<JsonValue>) : JsonValue

private data class JsonString(val value: String) : JsonValue

private data class JsonNumber(val value: Double) : JsonValue

private object JsonNull : JsonValue

private fun JsonObject.stringValue(name: String): String? =
    (values[name] as? JsonString)?.value

private fun JsonObject.numberValue(name: String): Double? =
    (values[name] as? JsonNumber)?.value

private fun JsonObject.intValue(name: String): Int? = when (val value = values[name]) {
    is JsonNumber -> value.value.roundToInt()
    is JsonString -> value.value.toIntOrNull()
    else -> null
}

private fun JsonObject.objectValue(name: String): JsonObject? = values[name] as? JsonObject

private fun JsonObject.arrayValue(name: String): JsonArray? = values[name] as? JsonArray

private class JsonParser(private val content: String) {
    private var index = 0

    fun parse(): JsonValue {
        skipWhitespace()
        val value = parseValue()
        skipWhitespace()
        require(index == content.length) { "trailing JSON" }
        return value
    }

    private fun parseValue(): JsonValue {
        skipWhitespace()
        return when (content.getOrNull(index)) {
            '{' -> parseObject()
            '[' -> parseArray()
            '"' -> JsonString(parseString())
            't' -> parseLiteral("true", JsonNumber(1.0))
            'f' -> parseLiteral("false", JsonNumber(0.0))
            'n' -> parseLiteral("null", JsonNull)
            '-', in '0'..'9' -> JsonNumber(parseNumber())
            else -> error("invalid JSON value")
        }
    }

    private fun parseObject(): JsonObject {
        expect('{')
        val values = linkedMapOf<String, JsonValue>()
        skipWhitespace()
        if (takeIf('}')) return JsonObject(values)
        while (true) {
            skipWhitespace()
            val key = parseString()
            skipWhitespace()
            expect(':')
            values[key] = parseValue()
            skipWhitespace()
            if (takeIf('}')) return JsonObject(values)
            expect(',')
        }
    }

    private fun parseArray(): JsonArray {
        expect('[')
        val values = mutableListOf<JsonValue>()
        skipWhitespace()
        if (takeIf(']')) return JsonArray(values)
        while (true) {
            values += parseValue()
            skipWhitespace()
            if (takeIf(']')) return JsonArray(values)
            expect(',')
        }
    }

    private fun parseString(): String {
        expect('"')
        val result = StringBuilder()
        while (true) {
            when (val character = content.getOrNull(index++) ?: error("unterminated JSON string")) {
                '"' -> return result.toString()
                '\\' -> result.append(parseEscape())
                else -> result.append(character)
            }
        }
    }

    private fun parseEscape(): Char = when (val escaped = content.getOrNull(index++) ?: error("invalid JSON escape")) {
        '"', '\\', '/' -> escaped
        'b' -> '\b'
        'f' -> '\u000C'
        'n' -> '\n'
        'r' -> '\r'
        't' -> '\t'
        'u' -> content.substring(index, index + 4).toInt(16).toChar().also { index += 4 }
        else -> error("invalid JSON escape")
    }

    private fun parseNumber(): Double {
        val start = index
        while (content.getOrNull(index)?.let { it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' || it in '0'..'9' } == true) {
            index += 1
        }
        return content.substring(start, index).toDouble()
    }

    private fun <T : JsonValue> parseLiteral(literal: String, value: T): T {
        require(content.startsWith(literal, index)) { "invalid JSON literal" }
        index += literal.length
        return value
    }

    private fun expect(character: Char) {
        require(content.getOrNull(index++) == character) { "expected JSON character" }
    }

    private fun takeIf(character: Char): Boolean {
        if (content.getOrNull(index) != character) return false
        index += 1
        return true
    }

    private fun skipWhitespace() {
        while (content.getOrNull(index)?.isWhitespace() == true) index += 1
    }
}
