package com.hozayushka.app.adapters.weather

import java.time.LocalDate
import java.time.LocalTime

/**
 * Boundary DTOs deliberately contain no application-owned weather model.
 * Weather Context performs the owner-side normalization.
 */
data class RedactedProviderPayload(
    val temperatureCelsius: Int,
    val condition: String,
)

data class ProviderCurrentWeather(
    val temperatureCelsius: Int,
    val pressureMmHg: Double,
    val condition: String?,
)

data class ProviderDailyWeather(
    val date: LocalDate,
    val dayTemperatureCelsius: Int?,
    val nightTemperatureCelsius: Int?,
    val dayCondition: String?,
    val nightCondition: String?,
    val moonPhase: String? = null,
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
    val redactedCredential: String,
    val weatherData: ProviderWeatherData? = null,
    val failure: WeatherProviderFailure? = null,
)

enum class WeatherProviderFailure {
    INVALID_CREDENTIAL,
    NETWORK,
    UNKNOWN_CITY,
}

class ProviderCredential private constructor(
    private val value: String,
) {
    fun redacted(): String = REDACTED_VALUE

    internal fun <T> use(block: (String) -> T): T = block(value)

    companion object {
        private const val REDACTED_VALUE = "[REDACTED]"

        /**
         * Generates an in-memory probe value. It is never persisted or exposed
         * by a provider result, log, fixture or evidence artifact.
         */
        fun generatedForProbe(): ProviderCredential =
            ProviderCredential(System.nanoTime().toString(16))

        fun fromUserInput(value: String): ProviderCredential = ProviderCredential(value)
    }
}

class WeatherProviderRequest private constructor(
    val credential: ProviderCredential,
    val latitude: Double,
    val longitude: Double,
) {
    fun hasCredential(): Boolean = true

    fun redactedCredential(): String = credential.redacted()

    companion object {
        fun fromSyntheticProbe(latitude: Double = 0.0, longitude: Double = 0.0): WeatherProviderRequest =
            WeatherProviderRequest(ProviderCredential.generatedForProbe(), latitude, longitude)

        fun fromUserInput(apiKey: String, latitude: Double, longitude: Double): WeatherProviderRequest =
            WeatherProviderRequest(ProviderCredential.fromUserInput(apiKey), latitude, longitude)
    }
}

interface WeatherProvider {
    fun fetch(request: WeatherProviderRequest): WeatherProviderResult
}

/**
 * Foundation-only provider seam. It never makes a network request and returns
 * a deterministic, redacted payload for host probes and the walking shell.
 */
class RedactedWeatherFixtureAdapter(
    private val payload: RedactedProviderPayload = RedactedProviderPayload(
        temperatureCelsius = 21,
        condition = "cloud",
    ),
    private val weatherData: ProviderWeatherData? = null,
) : WeatherProvider {
    override fun fetch(request: WeatherProviderRequest): WeatherProviderResult =
        WeatherProviderResult(
            payload = payload,
            credentialWasReceived = request.hasCredential(),
            redactedCredential = request.redactedCredential(),
            weatherData = weatherData,
        )
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
