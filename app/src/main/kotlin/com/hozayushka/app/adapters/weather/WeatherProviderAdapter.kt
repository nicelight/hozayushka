package com.hozayushka.app.adapters.weather

import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

enum class WeatherProviderId(
    val storageId: String,
    val displayName: String,
    val capabilities: WeatherProviderCapabilities,
) {
    OPEN_METEO(
        storageId = "open_meteo",
        displayName = "Open-Meteo",
        capabilities = WeatherProviderCapabilities(
            supportedDailyRecords = 10,
            mayOmitElapsedHourlySlots = false,
            suppliesMoonPhase = false,
        ),
    ),
    OPEN_WEATHER(
        storageId = "open_weather",
        displayName = "OpenWeather",
        capabilities = WeatherProviderCapabilities(
            supportedDailyRecords = 8,
            mayOmitElapsedHourlySlots = true,
            suppliesMoonPhase = true,
        ),
    ),
    ;

    companion object {
        fun fromStorage(value: String?): WeatherProviderId? =
            entries.firstOrNull { it.storageId == value }
    }
}

data class WeatherProviderCapabilities(
    val supportedDailyRecords: Int,
    val mayOmitElapsedHourlySlots: Boolean,
    val suppliesMoonPhase: Boolean,
)

/** Transport DTOs remain provider input; Weather Context owns product normalization. */
data class RedactedProviderPayload(
    val temperatureCelsius: Int,
    val condition: String,
)

data class ProviderCurrentWeather(
    val temperatureCelsius: Int,
    val pressureHpa: Double,
    val condition: String?,
)

data class ProviderDailyWeather(
    val date: LocalDate,
    val dayTemperatureCelsius: Int?,
    val nightTemperatureCelsius: Int?,
    val dayCondition: String?,
    val nightCondition: String?,
    val moonPhase: String? = null,
    val sunrise: LocalTime? = null,
    val sunset: LocalTime? = null,
)

data class ProviderHourlyWeather(
    val date: LocalDate,
    val time: LocalTime,
    val temperatureCelsius: Int?,
    val condition: String?,
)

/** Raw provider boundary data. Weather Context owns normalization and persistence. */
data class ProviderWeatherData(
    val apiTimeZone: String,
    val current: ProviderCurrentWeather,
    val daily: List<ProviderDailyWeather>,
    val hourly: List<ProviderHourlyWeather> = emptyList(),
)

data class WeatherProviderResult(
    val payload: RedactedProviderPayload,
    val credentialWasReceived: Boolean,
    val redactedCredential: String?,
    val weatherData: ProviderWeatherData? = null,
    val failure: WeatherProviderFailure? = null,
    val provider: WeatherProviderId = WeatherProviderId.OPEN_METEO,
)

enum class WeatherProviderFailure {
    MISSING_CREDENTIAL,
    INVALID_CREDENTIAL,
    NETWORK,
    UNKNOWN_CITY,
    TIMEOUT,
    MALFORMED_RESPONSE,
    PROVIDER_MISMATCH,
}

class ProviderCredential private constructor(
    private val value: String,
) {
    fun redacted(): String = REDACTED_VALUE

    internal fun <T> use(block: (String) -> T): T = block(value)

    companion object {
        private const val REDACTED_VALUE = "[REDACTED]"

        fun generatedForProbe(): ProviderCredential =
            ProviderCredential(System.nanoTime().toString(16))

        fun fromUserInput(value: String): ProviderCredential = ProviderCredential(value)
    }
}

class WeatherProviderRequest private constructor(
    private val credential: ProviderCredential?,
    val latitude: Double,
    val longitude: Double,
) {
    fun hasCredential(): Boolean = credential != null

    fun redactedCredential(): String? = credential?.redacted()

    internal fun <T> useCredential(block: (String) -> T): T? = credential?.use(block)

    companion object {
        fun withoutCredential(latitude: Double = 0.0, longitude: Double = 0.0): WeatherProviderRequest =
            WeatherProviderRequest(null, latitude, longitude)

        fun fromSyntheticProbe(latitude: Double = 0.0, longitude: Double = 0.0): WeatherProviderRequest =
            WeatherProviderRequest(ProviderCredential.generatedForProbe(), latitude, longitude)

        fun fromUserInput(apiKey: String, latitude: Double, longitude: Double): WeatherProviderRequest =
            WeatherProviderRequest(ProviderCredential.fromUserInput(apiKey), latitude, longitude)
    }
}

interface WeatherProvider {
    val providerId: WeatherProviderId
        get() = WeatherProviderId.OPEN_METEO

    fun fetch(request: WeatherProviderRequest): WeatherProviderResult
}

fun interface WeatherFixture {
    fun fetch(request: WeatherProviderRequest): WeatherProviderResult
}

/** Foundation-only fake. It performs no network request and is not a provider adapter. */
class RedactedWeatherFixture(
    private val payload: RedactedProviderPayload = RedactedProviderPayload(
        temperatureCelsius = 21,
        condition = "cloud",
    ),
    private val weatherData: ProviderWeatherData? = null,
    private val provider: WeatherProviderId = WeatherProviderId.OPEN_METEO,
) : WeatherFixture {
    override fun fetch(request: WeatherProviderRequest): WeatherProviderResult =
        WeatherProviderResult(
            payload = payload,
            credentialWasReceived = request.hasCredential(),
            redactedCredential = request.redactedCredential(),
            weatherData = weatherData,
            provider = provider,
        )
}

internal data class WeatherTransportResponse(
    val statusCode: Int,
    val body: String,
)

internal fun interface WeatherTransport {
    fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse
}

internal class HttpUrlConnectionWeatherTransport : WeatherTransport {
    override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MILLIS
            readTimeout = TIMEOUT_MILLIS
            instanceFollowRedirects = false
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

internal sealed interface JsonValue
internal data class JsonObject(val values: Map<String, JsonValue>) : JsonValue
internal data class JsonArray(val values: List<JsonValue>) : JsonValue
internal data class JsonString(val value: String) : JsonValue
internal data class JsonNumber(val value: Double) : JsonValue
internal object JsonNull : JsonValue

internal fun JsonObject.stringValue(name: String): String? =
    (values[name] as? JsonString)?.value

internal fun JsonObject.numberValue(name: String): Double? =
    (values[name] as? JsonNumber)?.value

internal fun JsonObject.intValue(name: String): Int? = when (val value = values[name]) {
    is JsonNumber -> value.value.roundToInt()
    is JsonString -> value.value.toIntOrNull()
    else -> null
}

internal fun JsonObject.objectValue(name: String): JsonObject? = values[name] as? JsonObject
internal fun JsonObject.arrayValue(name: String): JsonArray? = values[name] as? JsonArray

internal class JsonParser(private val content: String) {
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
        while (content.getOrNull(index)?.let {
                it == '-' || it == '+' || it == '.' || it == 'e' || it == 'E' || it in '0'..'9'
            } == true) {
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

object RedactedFixtureParser {
    fun parse(content: String): RedactedProviderPayload {
        val temperature = Regex("\\\"temperatureCelsius\\\"\\s*:\\s*(-?\\d+)")
            .find(content)
            ?.groupValues
            ?.get(1)
            ?.toIntOrNull()
            ?: error("Foundation weather fixture has no temperatureCelsius")
        val condition = Regex("\\\"condition\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"")
            .find(content)
            ?.groupValues
            ?.get(1)
            ?: error("Foundation weather fixture has no condition")
        return RedactedProviderPayload(temperature, condition)
    }
}
