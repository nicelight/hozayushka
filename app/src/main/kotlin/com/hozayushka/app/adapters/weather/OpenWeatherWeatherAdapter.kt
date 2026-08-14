package com.hozayushka.app.adapters.weather

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import kotlin.math.roundToInt

internal class OpenWeatherWeatherAdapter(
    private val transport: WeatherTransport = HttpUrlConnectionWeatherTransport(),
) : WeatherProvider {
    override val providerId: WeatherProviderId = WeatherProviderId.OPEN_WEATHER

    override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
        val response = try {
            request.useCredential { credential ->
                transport.execute(
                    url = requestUrl(request.latitude, request.longitude, credential),
                    headers = emptyMap(),
                )
            } ?: return failure(WeatherProviderFailure.INVALID_CREDENTIAL, credentialWasReceived = false)
        } catch (_: SocketTimeoutException) {
            return failure(WeatherProviderFailure.TIMEOUT)
        } catch (_: IOException) {
            return failure(WeatherProviderFailure.NETWORK)
        } catch (_: RuntimeException) {
            return failure(WeatherProviderFailure.MALFORMED_RESPONSE)
        }

        if (response.statusCode !in 200..299) {
            return failure(statusFailure(response.statusCode))
        }
        val data = OpenWeatherResponseParser.parse(response.body)
            ?: return failure(WeatherProviderFailure.MALFORMED_RESPONSE)
        return WeatherProviderResult(
            payload = RedactedProviderPayload(
                temperatureCelsius = data.current.temperatureCelsius,
                condition = data.current.condition.orEmpty(),
            ),
            credentialWasReceived = true,
            redactedCredential = "[REDACTED]",
            weatherData = data,
            provider = WeatherProviderId.OPEN_WEATHER,
        )
    }

    private fun failure(
        reason: WeatherProviderFailure,
        credentialWasReceived: Boolean = true,
    ): WeatherProviderResult = WeatherProviderResult(
        payload = RedactedProviderPayload(0, ""),
        credentialWasReceived = credentialWasReceived,
        redactedCredential = if (credentialWasReceived) "[REDACTED]" else null,
        failure = reason,
        provider = WeatherProviderId.OPEN_WEATHER,
    )

    private fun requestUrl(latitude: Double, longitude: Double, credential: String): String = String.format(
        Locale.US,
        "%s?lat=%s&lon=%s&units=metric&exclude=minutely,alerts&appid=%s",
        ENDPOINT,
        latitude,
        longitude,
        URLEncoder.encode(credential, StandardCharsets.UTF_8.name()),
    )

    private companion object {
        const val ENDPOINT = "https://api.openweathermap.org/data/3.0/onecall"

        fun statusFailure(statusCode: Int): WeatherProviderFailure = when (statusCode) {
            401, 403 -> WeatherProviderFailure.INVALID_CREDENTIAL
            400, 404 -> WeatherProviderFailure.UNKNOWN_CITY
            else -> WeatherProviderFailure.NETWORK
        }
    }
}

private object OpenWeatherResponseParser {
    fun parse(content: String): ProviderWeatherData? = runCatching {
        val root = JsonParser(content).parse() as? JsonObject ?: return null
        val zoneId = responseZone(root) ?: return null
        val current = root.objectValue("current") ?: return null
        val currentTemperature = current.numberValue("temp")?.roundToInt() ?: return null
        val currentPressure = current.numberValue("pressure") ?: return null
        if (!currentPressure.isFinite()) return null

        ProviderWeatherData(
            apiTimeZone = zoneId.id,
            current = ProviderCurrentWeather(
                temperatureCelsius = currentTemperature,
                pressureHpa = currentPressure,
                condition = current.weatherCondition(),
            ),
            daily = root.arrayValue("daily")?.values.orEmpty().map { value ->
                parseDaily(value as? JsonObject ?: error("malformed daily record"), zoneId)
            },
            hourly = root.arrayValue("hourly")?.values.orEmpty().map { value ->
                parseHourly(value as? JsonObject ?: error("malformed hourly record"), zoneId)
            },
        )
    }.getOrNull()

    private fun responseZone(root: JsonObject): ZoneId? {
        root.stringValue("timezone")?.takeIf(String::isNotBlank)?.let { timezone ->
            runCatching { ZoneId.of(timezone) }.getOrNull()?.let { return it }
        }
        val offset = root.intValue("timezone_offset") ?: return null
        return runCatching { ZoneOffset.ofTotalSeconds(offset) }.getOrNull()
    }

    private fun parseDaily(value: JsonObject, zoneId: ZoneId): ProviderDailyWeather {
        val instant = value.numberValue("dt")?.toLong()?.let(Instant::ofEpochSecond)
            ?: error("missing daily timestamp")
        val temperature = value.objectValue("temp")
        return ProviderDailyWeather(
            date = instant.atZone(zoneId).toLocalDate(),
            dayTemperatureCelsius = temperature?.numberValue("day")?.roundToInt(),
            nightTemperatureCelsius = temperature?.numberValue("night")?.roundToInt(),
            dayCondition = value.weatherCondition(),
            nightCondition = value.weatherCondition(),
            moonPhase = value.numberValue("moon_phase")?.toString(),
            sunrise = value.epochLocalTime("sunrise", zoneId),
            sunset = value.epochLocalTime("sunset", zoneId),
        )
    }

    private fun parseHourly(value: JsonObject, zoneId: ZoneId): ProviderHourlyWeather {
        val instant = value.numberValue("dt")?.toLong()?.let(Instant::ofEpochSecond)
            ?: error("missing hourly timestamp")
        val zoned = instant.atZone(zoneId)
        return ProviderHourlyWeather(
            date = zoned.toLocalDate(),
            time = zoned.toLocalTime().withSecond(0).withNano(0),
            temperatureCelsius = value.numberValue("temp")?.roundToInt(),
            condition = value.weatherCondition(),
        )
    }

    private fun JsonObject.weatherCondition(): String? {
        val weather = arrayValue("weather")?.values?.firstOrNull() as? JsonObject ?: return null
        return weather.intValue("id")?.let { "owm:$it" }
    }

    private fun JsonObject.epochLocalTime(name: String, zoneId: ZoneId): LocalTime? =
        numberValue(name)?.toLong()?.let(Instant::ofEpochSecond)?.atZone(zoneId)?.toLocalTime()
}
