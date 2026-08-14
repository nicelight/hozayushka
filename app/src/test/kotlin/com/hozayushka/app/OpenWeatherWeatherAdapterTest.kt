package com.hozayushka.app

import com.hozayushka.app.adapters.weather.OpenWeatherWeatherAdapter
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherTransport
import com.hozayushka.app.adapters.weather.WeatherTransportResponse
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenWeatherWeatherAdapterTest {
    @Test
    fun oneCallRequestUsesSyntheticAppidOnlyTransientlyAndReturnsRedactedEnvelope() {
        val transport = RecordingTransport(fixture())
        val syntheticKey = syntheticKey()
        val result = OpenWeatherWeatherAdapter(transport).fetch(
            WeatherProviderRequest.fromUserInput(syntheticKey, 40.28, 69.62),
        )

        assertEquals(WeatherProviderId.OPEN_WEATHER, result.provider)
        assertTrue(result.credentialWasReceived)
        assertEquals("[REDACTED]", result.redactedCredential)
        assertNull(result.failure)
        assertTrue(transport.headers.isEmpty())
        assertTrue(transport.url.startsWith("https://api.openweathermap.org/data/3.0/onecall?"))
        assertTrue(transport.url.contains("lat=40.28"))
        assertTrue(transport.url.contains("lon=69.62"))
        assertTrue(transport.url.contains("units=metric"))
        assertTrue(transport.url.contains("exclude=minutely,alerts"))
        assertTrue(
            transport.url.contains(
                "appid" + "=" + URLEncoder.encode(syntheticKey, StandardCharsets.UTF_8.name()),
            ),
        )
        assertFalse(result.toString().contains(syntheticKey))

        val data = requireNotNull(result.weatherData)
        assertEquals("Asia/Dushanbe", data.apiTimeZone)
        assertEquals(2, data.current.temperatureCelsius)
        assertEquals(1000.0, data.current.pressureHpa, 0.0001)
        assertEquals("owm:803", data.current.condition)
        assertEquals(8, data.daily.size)
        assertEquals(8, data.hourly.size)
        assertEquals("0.5", data.daily.first().moonPhase)
    }

    @Test
    fun missingKeyAuthMalformedAndTimeoutFailuresNeverCreateFallback() {
        val noRequest = CountingTransport(fixture())
        val missing = OpenWeatherWeatherAdapter(noRequest).fetch(WeatherProviderRequest.withoutCredential())
        assertEquals(WeatherProviderFailure.INVALID_CREDENTIAL, missing.failure)
        assertEquals(0, noRequest.calls)
        assertFalse(missing.credentialWasReceived)

        assertEquals(WeatherProviderFailure.INVALID_CREDENTIAL, fetchFailure(401))
        assertEquals(WeatherProviderFailure.INVALID_CREDENTIAL, fetchFailure(403))
        assertEquals(WeatherProviderFailure.UNKNOWN_CITY, fetchFailure(404))
        assertEquals(WeatherProviderFailure.NETWORK, fetchFailure(500))
        assertEquals(
            WeatherProviderFailure.MALFORMED_RESPONSE,
            OpenWeatherWeatherAdapter(WeatherTransport { _, _ -> WeatherTransportResponse(200, "{}") })
                .fetch(request()).failure,
        )
        assertEquals(
            WeatherProviderFailure.TIMEOUT,
            OpenWeatherWeatherAdapter(WeatherTransport { _, _ -> throw SocketTimeoutException() })
                .fetch(request()).failure,
        )
    }

    @Test
    fun absentOptionalConditionAndMoonFieldsRemainNullWithoutDecodeFailure() {
        val body = fixture()
            .replaceFirst("\"weather\": [{\"id\": 803, \"main\": \"Clouds\"}]", "\"weather\": []")
            .replaceFirst("\"moon_phase\": 0.50, ", "")
        val result = OpenWeatherWeatherAdapter(WeatherTransport { _, _ -> WeatherTransportResponse(200, body) })
            .fetch(request())
        val data = requireNotNull(result.weatherData)

        assertNull(data.current.condition)
        assertNull(data.daily.first().moonPhase)
    }

    private fun fetchFailure(status: Int): WeatherProviderFailure? =
        OpenWeatherWeatherAdapter(WeatherTransport { _, _ -> WeatherTransportResponse(status, "") })
            .fetch(request())
            .failure

    private fun request(): WeatherProviderRequest =
        WeatherProviderRequest.fromUserInput(syntheticKey(), 40.28, 69.62)

    private fun fixture(): String = requireNotNull(
        javaClass.getResourceAsStream("/fixtures/openweather-redacted-weather.json"),
    ).bufferedReader().use { it.readText() }

    private fun syntheticKey(): String = buildString {
        append(OpenWeatherWeatherAdapterTest::class.java.name.hashCode().toUInt().toString(16))
        append('-')
        append(System.nanoTime().toString(16))
    }

    private class RecordingTransport(private val body: String) : WeatherTransport {
        var url: String = ""
        var headers: Map<String, String> = emptyMap()

        override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse {
            this.url = url
            this.headers = headers
            return WeatherTransportResponse(200, body)
        }
    }

    private class CountingTransport(private val body: String) : WeatherTransport {
        var calls: Int = 0

        override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse {
            calls += 1
            return WeatherTransportResponse(200, body)
        }
    }
}
