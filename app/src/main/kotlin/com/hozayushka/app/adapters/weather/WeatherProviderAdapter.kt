package com.hozayushka.app.adapters.weather

/**
 * Boundary DTOs deliberately contain no application-owned weather model.
 * Weather Context performs the owner-side normalization.
 */
data class RedactedProviderPayload(
    val temperatureCelsius: Int,
    val condition: String,
)

data class WeatherProviderResult(
    val payload: RedactedProviderPayload,
    val credentialWasReceived: Boolean,
    val redactedCredential: String,
)

class ProviderCredential private constructor(
    private val value: String,
) {
    fun redacted(): String = REDACTED_VALUE

    internal fun use(block: (String) -> Unit) {
        block(value)
    }

    companion object {
        private const val REDACTED_VALUE = "[REDACTED]"

        /**
         * Generates an in-memory probe value. It is never persisted or exposed
         * by a provider result, log, fixture or evidence artifact.
         */
        fun generatedForProbe(): ProviderCredential =
            ProviderCredential(System.nanoTime().toString(16))
    }
}

class WeatherProviderRequest private constructor(
    val credential: ProviderCredential,
) {
    fun hasCredential(): Boolean = true

    fun redactedCredential(): String = credential.redacted()

    companion object {
        fun fromSyntheticProbe(): WeatherProviderRequest =
            WeatherProviderRequest(ProviderCredential.generatedForProbe())
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
) : WeatherProvider {
    override fun fetch(request: WeatherProviderRequest): WeatherProviderResult =
        WeatherProviderResult(
            payload = payload,
            credentialWasReceived = request.hasCredential(),
            redactedCredential = request.redactedCredential(),
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
