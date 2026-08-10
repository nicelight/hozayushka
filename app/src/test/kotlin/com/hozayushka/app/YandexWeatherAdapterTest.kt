package com.hozayushka.app

import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.adapters.weather.RedactedWeatherFixtureAdapter
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherTransport
import com.hozayushka.app.adapters.weather.WeatherTransportResponse
import com.hozayushka.app.adapters.weather.YandexWeatherAdapter
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YandexWeatherAdapterTest {
    private val now = Instant.parse("2024-01-02T07:00:00Z").toEpochMilli()

    @Test
    fun capturesAcceptedRequestShapeAndMapsCurrentDailyHourlyDataWithoutCredentialReceipt() {
        val transport = RecordingTransport(successBody())
        val adapter = YandexWeatherAdapter(transport)
        val credential = "synthetic-" + System.nanoTime().toString(16)

        val result = adapter.fetch(WeatherProviderRequest.fromUserInput(credential, 40.28, 69.62))

        assertNull(result.failure)
        assertEquals("https://api.weather.yandex.ru/v2/forecast?lat=40.28&lon=69.62&hours=true", transport.url)
        assertTrue(transport.headerWasPresent)
        assertFalse(transport.headerValueWasInUrl)
        assertEquals("[REDACTED]", result.redactedCredential)
        assertTrue(result.credentialWasReceived)
        assertEquals("Asia/Dushanbe", result.weatherData?.apiTimeZone)
        assertEquals(18, result.weatherData?.current?.temperatureCelsius)
        assertEquals(745.0, result.weatherData?.current?.pressureMmHg ?: 0.0, 0.0)
        assertEquals(10, result.weatherData?.daily?.size)
        assertEquals(8, result.weatherData?.hourly?.size)
    }

    @Test
    fun weatherContextPreservesCacheOnStatusTimeoutIoAndMalformedFailures() {
        val transport = SequenceTransport(
            listOf(
                Step.Response(200, successBody()),
                Step.Response(503, ""),
                Step.Timeout,
                Step.Io,
                Step.Response(200, "{malformed"),
            ),
        )
        val weather = weatherCapability(YandexWeatherAdapter(transport))
        val request = WeatherProviderRequest.fromSyntheticProbe(40.28, 69.62)

        assertNotNull(weather.refresh(request, now))
        val cached = requireNotNull(weather.snapshot())
        assertNull(weather.refresh(request, now + 1_000L))
        assertEquals(cached, weather.snapshot())
        assertNull(weather.refresh(request, now + 2_000L))
        assertEquals(cached, weather.snapshot())
        assertNull(weather.refresh(request, now + 3_000L))
        assertEquals(cached, weather.snapshot())
        assertNull(weather.refresh(request, now + 4_000L))
        assertEquals(cached, weather.snapshot())
    }

    @Test
    fun statusCategoriesRemainBoundedAndOptionalFieldsUseNeutralFallback() {
        assertEquals(WeatherProviderFailure.INVALID_CREDENTIAL, YandexWeatherAdapter(RecordingTransport("")).fetchWithStatus(401))
        assertEquals(WeatherProviderFailure.UNKNOWN_CITY, YandexWeatherAdapter(RecordingTransport("")).fetchWithStatus(404))

        val optional = weatherCapability(YandexWeatherAdapter(RecordingTransport(optionalFieldsBody())))
        val nightNow = Instant.parse("2024-01-01T19:00:00Z").toEpochMilli()
        val refreshed = optional.refresh(WeatherProviderRequest.fromSyntheticProbe(), nightNow)

        assertNotNull(refreshed)
        assertEquals("neutral-cloud", refreshed?.snapshot?.condition)
        assertEquals("regular", requireNotNull(refreshed).projection.cards[1].moonPhase)
    }

    @Test
    fun fixtureRefreshUsesOnlyInjectedFixtureProvider() {
        val transport = RecordingTransport(throws = true)
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Fixture city", 40.28, 69.62, "Asia/Dushanbe"))
        }
        val weather = WeatherCapability(
            locationReader = settings,
            cacheStore = InMemoryWeatherCacheStore(),
            provider = YandexWeatherAdapter(transport),
            fixtureProvider = RedactedWeatherFixtureAdapter(RedactedProviderPayload(21, "cloud")),
        )

        assertNotNull(weather.refreshFoundationFixture(now))
        assertEquals(0, transport.calls)
    }

    private fun weatherCapability(adapter: YandexWeatherAdapter): WeatherCapability {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.28, 69.62, "Asia/Dushanbe"))
        }
        return WeatherCapability(settings, InMemoryWeatherCacheStore(), adapter)
    }

    private fun successBody(): String = fixture("yandex-redacted-weather.json")

    private fun optionalFieldsBody(): String = """
        {
          "info": { "tzinfo": { "name": "Asia/Dushanbe" } },
          "fact": { "temp": 18, "pressure_mm": 745 },
          "forecasts": [{
            "date": "2024-01-02",
            "parts": [
              { "part_name": "night", "temp_avg": 4 },
              { "part_name": "day", "temp_avg": 18 }
            ]
          }]
        }
    """.trimIndent()

    private fun fixture(name: String): String =
        requireNotNull(javaClass.getResourceAsStream("/fixtures/$name"))
            .bufferedReader()
            .use { it.readText() }

    private fun YandexWeatherAdapter.fetchWithStatus(status: Int): WeatherProviderFailure? {
        val transport = RecordingTransport(response = WeatherTransportResponse(status, ""))
        return YandexWeatherAdapter(transport)
            .fetch(WeatherProviderRequest.fromSyntheticProbe())
            .failure
    }

    private class RecordingTransport(
        private val body: String = "",
        private val response: WeatherTransportResponse? = null,
        private val throws: Boolean = false,
    ) : WeatherTransport {
        var url: String? = null
        var headerWasPresent: Boolean = false
        var headerValueWasInUrl: Boolean = false
        var calls: Int = 0

        override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse {
            calls += 1
            if (throws) error("fixture transport must not be called")
            this.url = url
            val value = headers["X-Yandex-Weather-Key"]
            headerWasPresent = value != null
            headerValueWasInUrl = value != null && url.contains(value)
            return response ?: WeatherTransportResponse(200, body)
        }
    }

    private class SequenceTransport(private val steps: List<Step>) : WeatherTransport {
        private var index = 0

        override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse = when (val step = steps[index++]) {
            is Step.Response -> WeatherTransportResponse(step.status, step.body)
            Step.Timeout -> throw SocketTimeoutException("bounded synthetic timeout")
            Step.Io -> throw IOException("bounded synthetic io")
        }
    }

    private sealed interface Step {
        data class Response(val status: Int, val body: String) : Step
        data object Timeout : Step
        data object Io : Step
    }
}
