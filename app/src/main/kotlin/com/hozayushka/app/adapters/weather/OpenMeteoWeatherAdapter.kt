package com.hozayushka.app.adapters.weather

import java.io.IOException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.roundToInt

internal class OpenMeteoWeatherAdapter(
    private val transport: WeatherTransport = HttpUrlConnectionWeatherTransport(),
) : WeatherProvider {
    override val providerId: WeatherProviderId = WeatherProviderId.OPEN_METEO

    override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
        if (request.hasCredential()) return failure(WeatherProviderFailure.PROVIDER_MISMATCH)
        return try {
            val response = transport.execute(
                url = requestUrl(request.latitude, request.longitude),
                headers = emptyMap(),
            )
            if (response.statusCode !in 200..299) {
                failure(statusFailure(response.statusCode))
            } else {
                val data = OpenMeteoResponseParser.parse(response.body)
                    ?: return failure(WeatherProviderFailure.MALFORMED_RESPONSE)
                WeatherProviderResult(
                    payload = RedactedProviderPayload(
                        temperatureCelsius = data.current.temperatureCelsius,
                        condition = data.current.condition.orEmpty(),
                    ),
                    credentialWasReceived = false,
                    redactedCredential = null,
                    weatherData = data,
                    provider = WeatherProviderId.OPEN_METEO,
                )
            }
        } catch (_: SocketTimeoutException) {
            failure(WeatherProviderFailure.TIMEOUT)
        } catch (_: IOException) {
            failure(WeatherProviderFailure.NETWORK)
        } catch (_: RuntimeException) {
            failure(WeatherProviderFailure.MALFORMED_RESPONSE)
        }
    }

    private fun failure(reason: WeatherProviderFailure): WeatherProviderResult =
        WeatherProviderResult(
            payload = RedactedProviderPayload(0, ""),
            credentialWasReceived = false,
            redactedCredential = null,
            failure = reason,
            provider = WeatherProviderId.OPEN_METEO,
        )

    private fun requestUrl(latitude: Double, longitude: Double): String = String.format(
        Locale.US,
        "%s?latitude=%s&longitude=%s&timezone=auto&forecast_days=10" +
            "&current=temperature_2m,surface_pressure,weather_code" +
            "&hourly=temperature_2m,weather_code" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset",
        ENDPOINT,
        latitude,
        longitude,
    )

    private companion object {
        const val ENDPOINT = "https://api.open-meteo.com/v1/forecast"

        fun statusFailure(statusCode: Int): WeatherProviderFailure = when (statusCode) {
            400, 404 -> WeatherProviderFailure.UNKNOWN_CITY
            else -> WeatherProviderFailure.NETWORK
        }
    }
}

private object OpenMeteoResponseParser {
    fun parse(content: String): ProviderWeatherData? = runCatching {
        val root = JsonParser(content).parse() as? JsonObject ?: return null
        val timezone = root.stringValue("timezone")?.takeIf(String::isNotBlank) ?: return null
        val current = root.objectValue("current") ?: return null
        val currentTemperature = current.numberValue("temperature_2m")?.roundToInt() ?: return null
        val currentPressure = current.numberValue("surface_pressure") ?: return null
        if (!currentPressure.isFinite()) return null

        ProviderWeatherData(
            apiTimeZone = timezone,
            current = ProviderCurrentWeather(
                temperatureCelsius = currentTemperature,
                pressureHpa = currentPressure,
                condition = current.intValue("weather_code")?.let { "wmo:$it" },
            ),
            daily = parseDaily(root.objectValue("daily") ?: return null),
            hourly = root.objectValue("hourly")?.let(::parseHourly).orEmpty(),
        )
    }.getOrNull()

    private fun parseDaily(daily: JsonObject): List<ProviderDailyWeather> {
        val dates = daily.requiredStringList("time").map(LocalDate::parse)
        val codes = daily.nullableNumberList("weather_code")
        val maximums = daily.nullableNumberList("temperature_2m_max")
        val minimums = daily.nullableNumberList("temperature_2m_min")
        val sunrises = daily.nullableStringList("sunrise")
        val sunsets = daily.nullableStringList("sunset")
        require(dates.isNotEmpty())
        return dates.mapIndexed { index, date ->
            val condition = codes.getOrNull(index)?.roundToInt()?.let { "wmo:$it" }
            ProviderDailyWeather(
                date = date,
                dayTemperatureCelsius = maximums.getOrNull(index)?.roundToInt(),
                nightTemperatureCelsius = minimums.getOrNull(index)?.roundToInt(),
                dayCondition = condition,
                nightCondition = condition,
                sunrise = sunrises.getOrNull(index)?.let { LocalDateTime.parse(it).toLocalTime() },
                sunset = sunsets.getOrNull(index)?.let { LocalDateTime.parse(it).toLocalTime() },
            )
        }
    }

    private fun parseHourly(hourly: JsonObject): List<ProviderHourlyWeather> {
        val times = hourly.requiredStringList("time").map(LocalDateTime::parse)
        val temperatures = hourly.nullableNumberList("temperature_2m")
        val codes = hourly.nullableNumberList("weather_code")
        return times.mapIndexed { index, dateTime ->
            ProviderHourlyWeather(
                date = dateTime.toLocalDate(),
                time = dateTime.toLocalTime(),
                temperatureCelsius = temperatures.getOrNull(index)?.roundToInt(),
                condition = codes.getOrNull(index)?.roundToInt()?.let { "wmo:$it" },
            )
        }
    }

    private fun JsonObject.requiredStringList(name: String): List<String> =
        arrayValue(name)?.values.orEmpty().map { value ->
            (value as? JsonString)?.value ?: error("missing required array value")
        }

    private fun JsonObject.nullableStringList(name: String): List<String?> =
        arrayValue(name)?.values.orEmpty().map { (it as? JsonString)?.value }

    private fun JsonObject.nullableNumberList(name: String): List<Double?> =
        arrayValue(name)?.values.orEmpty().map { (it as? JsonNumber)?.value }
}
