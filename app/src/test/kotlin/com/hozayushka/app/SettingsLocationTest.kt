package com.hozayushka.app

import com.hozayushka.app.adapters.weather.ProviderCurrentWeather
import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.settings.ApiKeyValidationError
import com.hozayushka.app.settings.BundledLocationCatalog
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationCatalogEntry
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherRefreshTrigger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsLocationTest {
    @Test
    fun validKeyReloadsAndInvalidInputPreservesLastValidValueWithoutRedactionLeak() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        val valid = "synthetic-valid-key"

        assertTrue(settings.updateApiKey(valid).accepted)
        assertTrue(settings.hasStoredApiKey())
        assertEquals(valid, settings.withWeatherApiKey { it })

        val missing = settings.updateApiKey("   ")
        assertFalse(missing.accepted)
        assertEquals(ApiKeyValidationError.MISSING, missing.error)
        assertEquals(valid, settings.withWeatherApiKey { it })

        val invalid = settings.updateApiKey("bad key")
        assertFalse(invalid.accepted)
        assertEquals(ApiKeyValidationError.INVALID, invalid.error)
        assertEquals(valid, settings.withWeatherApiKey { it })
        assertFalse(settings.snapshot().toString().contains(valid))
    }

    @Test
    fun defaultAndSelectedLocationReloadWithCoordinatesAndRefreshRequest() {
        val catalog = BundledLocationCatalog.fromTsv(catalogFixture)
        var weather: WeatherCapability? = null
        val settings = SettingsCapability(
            stateStore = InMemorySettingsStateStore(),
            onValidLocationChanged = {
                weather?.refreshIfNeeded(
                    nowMillis = NOW,
                    networkAvailable = true,
                    trigger = WeatherRefreshTrigger.LOCATION_CHANGE,
                    requireStoredCredential = true,
                )
            },
            catalog = catalog,
        )
        settings.ensureDefaultLocation()
        assertEquals("Худжанд", settings.currentLocation()?.cityLabel)
        assertEquals(40.2833, settings.currentLocation()?.latitude ?: 0.0, 0.0001)

        settings.updateApiKey("synthetic-valid-key")
        val provider = CapturingProvider()
        weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), provider)
        val selected = catalog.searchCities("TJ", "dushanbe").single()
        settings.saveCatalogLocation(selected)

        assertEquals(selected.toLocationContext(), settings.currentLocation())
        assertEquals(selected.latitude, provider.request?.latitude ?: 0.0, 0.0001)
        assertEquals(selected.longitude, provider.request?.longitude ?: 0.0, 0.0001)
        assertEquals("[REDACTED]", provider.request?.redactedCredential())
        assertEquals(selected.cityDisplayName, weather?.snapshot()?.cityLabel)
    }

    @Test
    fun offlineCountryFirstSearchIsCaseInsensitiveAndCityScopedToSelectedCountry() {
        val catalog = BundledLocationCatalog.fromTsv(catalogFixture)

        assertEquals("TJ", catalog.searchCountries("tajik").single().code)
        assertTrue(catalog.searchCities(null, "Khujand").isEmpty())
        assertEquals("1514879", catalog.searchCities("TJ", "ХУДЖАНД").single().cityId)
        assertEquals("1514879", catalog.searchCities("TJ", "Khujand").single().cityId)
        assertEquals("Худжанд", catalog.searchCities("TJ", "Khujand").single().cityDisplayName)
        assertEquals("Springfield", catalog.searchCities("US", "spring").single().cityDisplayName)
        assertTrue(catalog.searchCities("RU", "Khujand").isEmpty())
    }

    @Test
    fun missingKeyKeepsLocationAndExposesOwningInlineMessage() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        settings.saveFoundationLocation(
            com.hozayushka.app.settings.LocationContext("Худжанд", 40.2833, 69.6167, "Asia/Dushanbe"),
        )
        val weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), CapturingProvider())

        assertNull(weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.LOCATION_CHANGE, true))
        assertEquals("API-ключ не указан", weather.inlineErrorMessage())
        assertEquals("Худжанд", settings.currentLocation()?.cityLabel)
    }

    @Test
    fun providerFailuresKeepValidSettingsAndExposeOwningInlineMessage() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        settings.updateApiKey("synthetic-valid-key")
        settings.saveFoundationLocation(
            com.hozayushka.app.settings.LocationContext(
                cityLabel = "Худжанд",
                latitude = 40.2833,
                longitude = 69.6167,
                apiTimeZone = "Asia/Dushanbe",
            ),
        )
        val original = settings.currentLocation()
        val weather = WeatherCapability(
            settings,
            InMemoryWeatherCacheStore(),
            FailingProvider(WeatherProviderFailure.NETWORK),
        )

        assertNull(weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.LOCATION_CHANGE, true))
        assertEquals("Нет подключения", weather.inlineErrorMessage())
        assertEquals(original, settings.currentLocation())
        assertTrue(settings.hasStoredApiKey())
        assertNotNull(settings.withWeatherApiKey { "[REDACTED]" })
    }

    @Test
    fun invalidCredentialAndUnknownCityRemainInlineAndDoNotReplaceLocation() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        settings.updateApiKey("synthetic-valid-key")
        settings.saveFoundationLocation(
            com.hozayushka.app.settings.LocationContext("Худжанд", 40.2833, 69.6167, "Asia/Dushanbe"),
        )
        val original = settings.currentLocation()
        listOf(
            WeatherProviderFailure.INVALID_CREDENTIAL to "Неверный API-ключ",
            WeatherProviderFailure.UNKNOWN_CITY to "Город не найден",
        ).forEach { (failure, expectedMessage) ->
            val weather = WeatherCapability(
                settings,
                InMemoryWeatherCacheStore(),
                FailingProvider(failure),
            )
            assertNull(weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.LOCATION_CHANGE, true))
            assertEquals(expectedMessage, weather.inlineErrorMessage())
            assertEquals(original, settings.currentLocation())
        }
    }

    private class CapturingProvider : WeatherProvider {
        var request: WeatherProviderRequest? = null

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            this.request = request
            return WeatherProviderResult(
                payload = RedactedProviderPayload(21, "cloud"),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
            )
        }
    }

    private class FailingProvider(private val failure: WeatherProviderFailure) : WeatherProvider {
        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult = WeatherProviderResult(
            payload = RedactedProviderPayload(0, "cloud"),
            credentialWasReceived = request.hasCredential(),
            redactedCredential = request.redactedCredential(),
            failure = failure,
        )
    }

    private companion object {
        const val NOW = 1_704_196_800_000L
        val catalogFixture = """
            TJ\tТаджикистан\tTajikistan\t1514879\tХуджанд\tKhujand\tKhujand\t40.2833\t69.6167\tAsia/Dushanbe
            TJ\tТаджикистан\tTajikistan\t1221874\tДушанбе\tDushanbe\tDushanbe\t38.5358\t68.7791\tAsia/Dushanbe
            RU\tРоссия\tRussia\t524901\tМосква\tMoscow\tMoscow\t55.7522\t37.6156\tEurope/Moscow
            US\tСША\tUnited States\t999\t\tSpringfield\tSpringfield\t39.78\t-89.65\tAmerica/Chicago
        """.trimIndent().replace("\\t", "\t")
    }
}
