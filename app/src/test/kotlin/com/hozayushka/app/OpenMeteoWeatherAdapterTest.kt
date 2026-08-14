package com.hozayushka.app

import com.hozayushka.app.adapters.weather.OpenMeteoWeatherAdapter
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherTransport
import com.hozayushka.app.adapters.weather.WeatherTransportResponse
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenMeteoWeatherAdapterTest {
    @Test
    fun keylessForecastRequestAndFixtureDecodeStayProviderIdentified() {
        val transport = RecordingTransport(fixture())
        val result = OpenMeteoWeatherAdapter(transport).fetch(
            WeatherProviderRequest.withoutCredential(40.28, 69.62),
        )

        assertEquals(WeatherProviderId.OPEN_METEO, result.provider)
        assertFalse(result.credentialWasReceived)
        assertNull(result.redactedCredential)
        assertNull(result.failure)
        assertTrue(transport.headers.isEmpty())
        assertTrue(transport.url.startsWith("https://api.open-meteo.com/v1/forecast?"))
        assertTrue(transport.url.contains("latitude=40.28"))
        assertTrue(transport.url.contains("longitude=69.62"))
        assertTrue(transport.url.contains("timezone=auto"))
        assertTrue(transport.url.contains("forecast_days=10"))
        assertTrue(transport.url.contains("current=temperature_2m,surface_pressure,weather_code"))
        assertTrue(transport.url.contains("hourly=temperature_2m,weather_code"))
        assertTrue(transport.url.contains("daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset"))
        assertFalse(transport.url.contains("appid", ignoreCase = true))
        assertFalse(transport.url.contains("apikey", ignoreCase = true))

        val data = requireNotNull(result.weatherData)
        assertEquals("Asia/Dushanbe", data.apiTimeZone)
        assertEquals(2, data.current.temperatureCelsius)
        assertEquals(1000.0, data.current.pressureHpa, 0.0001)
        assertEquals("wmo:3", data.current.condition)
        assertEquals(10, data.daily.size)
        assertEquals(8, data.hourly.size)
        assertNull(data.daily.first().moonPhase)
        assertEquals("07:00", data.daily.first().sunrise.toString())
    }

    @Test
    fun providerHttpMalformedAndTimeoutFailuresAreBounded() {
        var credentialTransportCalls = 0
        val credentialRejected = OpenMeteoWeatherAdapter(WeatherTransport { _, _ ->
            credentialTransportCalls += 1
            WeatherTransportResponse(200, fixture())
        }).fetch(WeatherProviderRequest.fromSyntheticProbe())
        assertEquals(WeatherProviderFailure.PROVIDER_MISMATCH, credentialRejected.failure)
        assertEquals(0, credentialTransportCalls)

        assertEquals(WeatherProviderFailure.UNKNOWN_CITY, fetchFailure(400))
        assertEquals(WeatherProviderFailure.UNKNOWN_CITY, fetchFailure(404))
        assertEquals(WeatherProviderFailure.NETWORK, fetchFailure(500))
        assertEquals(
            WeatherProviderFailure.MALFORMED_RESPONSE,
            OpenMeteoWeatherAdapter(WeatherTransport { _, _ -> WeatherTransportResponse(200, "{}") })
                .fetch(WeatherProviderRequest.withoutCredential()).failure,
        )
        assertEquals(
            WeatherProviderFailure.TIMEOUT,
            OpenMeteoWeatherAdapter(WeatherTransport { _, _ -> throw SocketTimeoutException() })
                .fetch(WeatherProviderRequest.withoutCredential()).failure,
        )
    }

    @Test
    fun nullableArrayValuesKeepTheirOriginalTimestampAndDatePositions() {
        val body = fixture()
            .replace("[-2, -1, 2, 3, 2, 1, 0, -1]", "[-2, null, 2, 3, 2, 1, 0, -1]")
            .replace("[2, 3, 4, 5, 1, 2, 4, 3, 5, 6]", "[2, null, 4, 5, 1, 2, 4, 3, 5, 6]")
        val result = OpenMeteoWeatherAdapter(WeatherTransport { _, _ -> WeatherTransportResponse(200, body) })
            .fetch(WeatherProviderRequest.withoutCredential())
        val data = requireNotNull(result.weatherData)

        assertNull(data.hourly[1].temperatureCelsius)
        assertEquals(2, data.hourly[2].temperatureCelsius)
        assertNull(data.daily[1].dayTemperatureCelsius)
        assertEquals(4, data.daily[2].dayTemperatureCelsius)
    }

    private fun fetchFailure(status: Int): WeatherProviderFailure? =
        OpenMeteoWeatherAdapter(WeatherTransport { _, _ -> WeatherTransportResponse(status, "") })
            .fetch(WeatherProviderRequest.withoutCredential())
            .failure

    private fun fixture(): String = requireNotNull(
        javaClass.getResourceAsStream("/fixtures/open-meteo-redacted-weather.json"),
    ).bufferedReader().use { it.readText() }

    private class RecordingTransport(private val body: String) : WeatherTransport {
        var url: String = ""
        var headers: Map<String, String> = emptyMap()

        override fun execute(url: String, headers: Map<String, String>): WeatherTransportResponse {
            this.url = url
            this.headers = headers
            return WeatherTransportResponse(200, body)
        }
    }
}
