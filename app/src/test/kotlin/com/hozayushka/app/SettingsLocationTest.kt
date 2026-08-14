package com.hozayushka.app

import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.settings.ApiKeyValidationError
import com.hozayushka.app.settings.BundledLocationCatalog
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationCatalogEntry
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.SettingsContentSection
import com.hozayushka.app.settings.WeatherProviderSelection
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherRefreshTrigger
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsLocationTest {
    @Test
    fun characterByCharacterOpenWeatherInputOnlyValidatesUntilCommitBoundary() {
        val sourceFile = listOf(
            File("app/src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt"),
            File("src/main/kotlin/com/hozayushka/app/settings/SettingsCapability.kt"),
        ).first(File::isFile)
        val source = sourceFile.readText()
        val watcherStart = source.indexOf("keyInput.addTextChangedListener")
        val watcherEnd = source.indexOf("fun renderProviderContext()", watcherStart)
        assertTrue("OpenWeather key watcher was not found", watcherStart >= 0)
        assertTrue("OpenWeather key watcher boundary was not found", watcherEnd > watcherStart)
        val watcher = source.substring(watcherStart, watcherEnd)

        // The UI watcher must be local for every typed prefix; saving remains a
        // separate commit at the existing IME/focus/leave-Settings boundary.
        assertTrue(watcher.contains("renderKeyValidation"))
        assertTrue(source.contains("fun renderKeyValidation"))
        assertFalse(watcher.contains("updateOpenWeatherApiKey"))
        assertTrue(source.contains("setOnEditorActionListener"))
        assertTrue(source.contains("setOnFocusChangeListener"))
        assertTrue(source.contains("commitOpenWeatherApiKey"))
        assertTrue(source.contains("keyInput.clearFocus()"))

        val store = InMemorySettingsStateStore()
        var keySaveRequests = 0
        val settings = SettingsCapability(
            stateStore = store,
            onValidOpenWeatherApiKeySaved = { keySaveRequests += 1 },
        )
        val completeKey = syntheticSecret()

        try {
            settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
            completeKey.forEach { _ ->
                // Character-by-character UI input only renders local validation;
                // no Settings state or save callback may change before commit.
                assertFalse(settings.hasStoredOpenWeatherApiKey())
                assertEquals(0, keySaveRequests)
            }

            assertTrue(settings.updateOpenWeatherApiKey(completeKey).accepted)
            assertTrue(settings.hasStoredOpenWeatherApiKey())
            assertEquals(1, keySaveRequests)
        } finally {
            settings.resetFoundationState()
        }
    }

    @Test
    fun firstRunDefaultKeepsOpenWeatherKeyLocalWithoutGenericHandoff() {
        val store = InMemorySettingsStateStore()
        var providerRefreshRequests = 0
        val settings = SettingsCapability(
            stateStore = store,
            onValidProviderChanged = { providerRefreshRequests += 1 },
        )
        val syntheticSecret = syntheticSecret()

        try {
            assertEquals(WeatherProviderSelection.OPEN_METEO, settings.selectedWeatherProvider())
            assertFalse(settings.hasWeatherApiKey())
            assertNull(settings.withSelectedOpenWeatherApiKey { true })

            val inapplicable = settings.updateOpenWeatherApiKey(syntheticSecret)

            assertFalse(inapplicable.accepted)
            assertNull(inapplicable.error)
            assertFalse(settings.hasStoredOpenWeatherApiKey())
            assertTrue(settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER))
            assertEquals(1, providerRefreshRequests)
            assertTrue(settings.updateOpenWeatherApiKey(syntheticSecret).accepted)
            assertTrue(settings.hasWeatherApiKey())
            assertSelectedOpenWeatherKeyAccess(settings)
            assertFalse(settings.snapshot().toString().contains(syntheticSecret))

            val reopened = SettingsCapability(store)
            assertEquals(WeatherProviderSelection.OPEN_WEATHER, reopened.selectedWeatherProvider())
            assertTrue(reopened.hasStoredOpenWeatherApiKey())
            assertSelectedOpenWeatherKeyAccess(reopened)

            assertTrue(reopened.updateWeatherProvider(WeatherProviderSelection.OPEN_METEO))
            assertFalse(reopened.hasWeatherApiKey())
            assertNull(reopened.withSelectedOpenWeatherApiKey { true })
            assertTrue(reopened.hasStoredOpenWeatherApiKey())
            assertEquals(
                WeatherProviderSelection.OPEN_METEO,
                SettingsCapability(store).selectedWeatherProvider(),
            )

        } finally {
            settings.resetFoundationState()
        }
    }

    @Test
    fun runtimeSyntheticSecretIsAbsentFromDurableArtifacts() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        val syntheticSecret = syntheticSecret()

        try {
            settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
            assertTrue(settings.updateOpenWeatherApiKey(syntheticSecret).accepted)
            assertSelectedOpenWeatherKeyAccess(settings)

            val roots = listOf(
                File("app/src"),
                File(".tasks/TASK-019-T3-FT-008-W16"),
                File(".protocols/TASK-019-T3-FT-008-W16"),
                File("app/build/reports"),
                File("app/build/outputs/apk/debug/app-debug.apk"),
            ).filter(File::exists)
            val leaks = roots.flatMap { root ->
                if (root.isFile) listOf(root) else root.walkTopDown().filter(File::isFile).toList()
            }.filter { it.containsBytes(syntheticSecret.encodeToByteArray()) }

            assertTrue(
                "Synthetic secret appeared outside owner-local memory: ${leaks.map(File::getPath)}",
                leaks.isEmpty(),
            )
        } finally {
            settings.resetFoundationState()
        }
    }

    @Test
    fun settingsResourcesDeclareBothProviderContextsAndOpenMeteoAttribution() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        val stringsFile = listOf(
            File("app/src/main/res/values/strings.xml"),
            File("src/main/res/values/strings.xml"),
        ).first(File::isFile)
        val resources = stringsFile.readText()
        val missing = listOf(
            "Open-Meteo",
            "OpenWeather",
            "https://open-meteo.com/",
            "CC BY 4.0",
        ).filterNot(resources::contains)

        assertTrue("Missing provider Settings resources: $missing", missing.isEmpty())
        assertEquals(
            listOf(
                SettingsContentSection.WEATHER_PROVIDER,
                SettingsContentSection.LOCATION,
                SettingsContentSection.OPEN_METEO_ATTRIBUTION,
                SettingsContentSection.GEONAMES_ATTRIBUTION,
                SettingsContentSection.ALERT,
                SettingsContentSection.PERSONALIZATION,
                SettingsContentSection.TIMER_PRESETS,
                SettingsContentSection.BACK,
            ),
            settings.settingsContentOrder(),
        )
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        val openWeatherOrder = settings.settingsContentOrder()
        assertEquals(SettingsContentSection.OPEN_WEATHER_KEY, openWeatherOrder[1])
        assertTrue(
            openWeatherOrder.indexOf(SettingsContentSection.OPEN_METEO_ATTRIBUTION) <
                openWeatherOrder.indexOf(SettingsContentSection.BACK),
        )
        assertTrue(
            openWeatherOrder.indexOf(SettingsContentSection.GEONAMES_ATTRIBUTION) <
                openWeatherOrder.indexOf(SettingsContentSection.BACK),
        )
        settings.resetFoundationState()
    }

    @Test
    fun untaggedLegacyFailuresAreNotRelabeledAndLocalKeyErrorsRemainOwned() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val syntheticSecret = syntheticSecret()

        try {
            settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
            settings.updateOpenWeatherApiKey(syntheticSecret)
            settings.saveFoundationLocation(
                com.hozayushka.app.settings.LocationContext(
                    cityLabel = "Худжанд",
                    latitude = 40.2833,
                    longitude = 69.6167,
                    apiTimeZone = "Asia/Dushanbe",
                ),
            )
            val lastValid = settings.snapshot()

            val untaggedTransportErrors = listOf(
                "Нет подключения",
                "Город не найден",
            )
            untaggedTransportErrors.forEach { message ->
                val contextual = settings.contextualizeWeatherError(message)
                assertEquals(message, contextual)
                assertFalse(contextual.orEmpty().contains("OpenWeather"))
                assertFalse(contextual.orEmpty().contains("Open-Meteo"))
                assertEquals(lastValid, settings.snapshot())
            }

            val missing = settings.updateOpenWeatherApiKey("   ")
            assertEquals(ApiKeyValidationError.MISSING, missing.error)
            assertEquals("OpenWeather: API-ключ не указан", "OpenWeather: ${missing.error?.message}")
            val invalid = settings.updateOpenWeatherApiKey("bad key")
            assertEquals(ApiKeyValidationError.INVALID, invalid.error)
            assertEquals("OpenWeather: Неверный API-ключ", "OpenWeather: ${invalid.error?.message}")
            assertNull(settings.contextualizeWeatherError(ApiKeyValidationError.MISSING.message))
            assertNull(settings.contextualizeWeatherError(ApiKeyValidationError.INVALID.message))
            assertEquals(lastValid, settings.snapshot())

            settings.updateWeatherProvider(WeatherProviderSelection.OPEN_METEO)
            val openMeteoState = settings.snapshot()
            assertNull(settings.contextualizeWeatherError(ApiKeyValidationError.MISSING.message))
            assertNull(settings.contextualizeWeatherError(ApiKeyValidationError.INVALID.message))
            assertEquals("Нет подключения", settings.contextualizeWeatherError("Нет подключения"))
            assertEquals(openMeteoState, settings.snapshot())
        } finally {
            settings.resetFoundationState()
        }
    }

    @Test
    fun validKeyReloadsAndInvalidInputPreservesLastValidValueWithoutRedactionLeak() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        val valid = syntheticSecret()

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        assertTrue(settings.updateOpenWeatherApiKey(valid).accepted)
        assertTrue(settings.hasStoredOpenWeatherApiKey())
        assertSelectedOpenWeatherKeyAccess(settings)

        val missing = settings.updateOpenWeatherApiKey("   ")
        assertFalse(missing.accepted)
        assertEquals(ApiKeyValidationError.MISSING, missing.error)
        assertTrue(settings.hasStoredOpenWeatherApiKey())
        assertSelectedOpenWeatherKeyAccess(settings)

        val invalid = settings.updateOpenWeatherApiKey("bad key")
        assertFalse(invalid.accepted)
        assertEquals(ApiKeyValidationError.INVALID, invalid.error)
        assertTrue(settings.hasStoredOpenWeatherApiKey())
        assertSelectedOpenWeatherKeyAccess(settings)
        assertFalse(settings.snapshot().toString().contains(valid))
        val reopened = SettingsCapability(store)
        assertTrue(reopened.hasStoredOpenWeatherApiKey())
        assertSelectedOpenWeatherKeyAccess(reopened)
        settings.resetFoundationState()
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
                )
            },
            catalog = catalog,
        )
        settings.ensureDefaultLocation()
        assertEquals("Худжанд", settings.currentLocation()?.cityLabel)
        assertEquals(40.2833, settings.currentLocation()?.latitude ?: 0.0, 0.0001)

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.updateOpenWeatherApiKey(syntheticSecret())
        val openMeteoProvider = CapturingProvider(WeatherProviderId.OPEN_METEO)
        val openWeatherProvider = CapturingProvider(WeatherProviderId.OPEN_WEATHER)
        weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteoProvider, openWeatherProvider)
        assertNotNull(weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.LAUNCH))
        assertEquals(0, openMeteoProvider.invocationCount)
        assertEquals(1, openWeatherProvider.invocationCount)
        assertTrue(openWeatherProvider.request?.hasCredential() == true)
        assertSelectedOpenWeatherKeyAccess(settings)

        val selected = catalog.searchCities("TJ", "dushanbe").single()
        settings.saveCatalogLocation(selected)

        assertEquals(selected.toLocationContext(), settings.currentLocation())
        assertEquals(0, openMeteoProvider.invocationCount)
        assertEquals(2, openWeatherProvider.invocationCount)
        assertEquals(selected.cityDisplayName, weather.snapshot()?.cityLabel)
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
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.saveFoundationLocation(
            com.hozayushka.app.settings.LocationContext("Худжанд", 40.2833, 69.6167, "Asia/Dushanbe"),
        )
        val openMeteoProvider = CapturingProvider(WeatherProviderId.OPEN_METEO)
        val openWeatherProvider = CapturingProvider(WeatherProviderId.OPEN_WEATHER)
        val weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteoProvider, openWeatherProvider)

        assertNull(weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.LOCATION_CHANGE))
        assertEquals("OpenWeather: API-ключ не указан", weather.inlineErrorMessage())
        assertEquals(weather.inlineErrorMessage(), settings.contextualizeWeatherError(weather.inlineErrorMessage()))
        assertEquals(0, openMeteoProvider.invocationCount)
        assertEquals(0, openWeatherProvider.invocationCount)
        assertEquals("Худжанд", settings.currentLocation()?.cityLabel)
        assertEquals(WeatherProviderSelection.OPEN_WEATHER, settings.selectedWeatherProvider())
    }

    @Test
    fun storedOpenWeatherKeyInvokesOnlySelectedOpenWeatherOnLocationRefresh() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.updateOpenWeatherApiKey(syntheticSecret())
        settings.saveFoundationLocation(
            com.hozayushka.app.settings.LocationContext(
                cityLabel = "Худжанд",
                latitude = 40.2833,
                longitude = 69.6167,
                apiTimeZone = "Asia/Dushanbe",
            ),
        )
        val original = settings.currentLocation()
        val openMeteoProvider = CapturingProvider(WeatherProviderId.OPEN_METEO)
        val openWeatherProvider = CapturingProvider(WeatherProviderId.OPEN_WEATHER)
        val weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteoProvider, openWeatherProvider)

        assertNotNull(weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.LOCATION_CHANGE))
        assertEquals(0, openMeteoProvider.invocationCount)
        assertEquals(1, openWeatherProvider.invocationCount)
        assertTrue(openWeatherProvider.request?.hasCredential() == true)
        assertEquals("[REDACTED]", openWeatherProvider.request?.redactedCredential())
        assertNull(weather.inlineErrorMessage())
        assertEquals(original, settings.currentLocation())
        assertEquals(WeatherProviderSelection.OPEN_WEATHER, settings.selectedWeatherProvider())
        assertTrue(settings.hasStoredOpenWeatherApiKey())
        assertSelectedOpenWeatherKeyAccess(settings)
    }

    @Test
    fun validOpenWeatherKeySaveRequestsSelectedRefreshAfterMissingKeyState() {
        val store = InMemorySettingsStateStore()
        var refreshRequests = 0
        lateinit var weather: WeatherCapability
        val settings = SettingsCapability(
            stateStore = store,
            onValidProviderChanged = {
                refreshRequests += 1
                weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.PROVIDER_CHANGE)
            },
            onValidOpenWeatherApiKeySaved = {
                refreshRequests += 1
                weather.refreshIfNeeded(NOW, true, WeatherRefreshTrigger.PROVIDER_CHANGE)
            },
        )
        val originalLocation = com.hozayushka.app.settings.LocationContext(
            "Худжанд",
            40.2833,
            69.6167,
            "Asia/Dushanbe",
        )
        settings.saveFoundationLocation(originalLocation)
        val openMeteoProvider = CapturingProvider(WeatherProviderId.OPEN_METEO)
        val openWeatherProvider = CapturingProvider(WeatherProviderId.OPEN_WEATHER)
        weather = WeatherCapability(
            settings,
            InMemoryWeatherCacheStore(),
            openMeteoProvider,
            openWeatherProvider,
        )

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        assertEquals(1, refreshRequests)
        assertEquals("OpenWeather: API-ключ не указан", weather.inlineErrorMessage())

        assertTrue(settings.updateOpenWeatherApiKey(syntheticSecret()).accepted)

        assertEquals(2, refreshRequests)
        assertEquals(1, openWeatherProvider.invocationCount)
        assertEquals(0, openMeteoProvider.invocationCount)
        assertNull(weather.inlineErrorMessage())
        assertEquals(originalLocation, settings.currentLocation())
        assertEquals(WeatherProviderSelection.OPEN_WEATHER, settings.selectedWeatherProvider())
    }

    @Test
    fun invalidBlankAndOpenMeteoKeySavesAreInertWhileRepeatedValidSavesNotify() {
        val store = InMemorySettingsStateStore()
        var keySaveRequests = 0
        val settings = SettingsCapability(
            stateStore = store,
            onValidOpenWeatherApiKeySaved = { keySaveRequests += 1 },
        )

        assertFalse(settings.updateOpenWeatherApiKey(syntheticSecret()).accepted)
        assertEquals(0, keySaveRequests)
        assertFalse(settings.hasStoredOpenWeatherApiKey())

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        assertFalse(settings.updateOpenWeatherApiKey("   ").accepted)
        assertEquals(0, keySaveRequests)
        assertFalse(settings.updateOpenWeatherApiKey("bad key").accepted)
        assertEquals(0, keySaveRequests)

        assertTrue(settings.updateOpenWeatherApiKey(syntheticSecret()).accepted)
        assertEquals(1, keySaveRequests)
        assertTrue(settings.updateOpenWeatherApiKey(syntheticSecret()).accepted)
        assertEquals(2, keySaveRequests)
        assertTrue(settings.hasStoredOpenWeatherApiKey())

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_METEO)
        assertFalse(settings.updateOpenWeatherApiKey(syntheticSecret()).accepted)
        assertEquals(2, keySaveRequests)
        assertEquals(WeatherProviderSelection.OPEN_METEO, settings.selectedWeatherProvider())
    }

    @Test
    fun untaggedLegacyInvalidCredentialAndUnknownCityDoNotClaimSelectedProvider() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.updateOpenWeatherApiKey(syntheticSecret())
        settings.saveFoundationLocation(
            com.hozayushka.app.settings.LocationContext("Худжанд", 40.2833, 69.6167, "Asia/Dushanbe"),
        )
        val original = settings.currentLocation()
        assertNull(settings.contextualizeWeatherError("Неверный API-ключ"))
        assertEquals("Город не найден", settings.contextualizeWeatherError("Город не найден"))
        assertFalse(settings.contextualizeWeatherError("Город не найден").orEmpty().contains("OpenWeather"))
        assertEquals(original, settings.currentLocation())
        assertEquals(WeatherProviderSelection.OPEN_WEATHER, settings.selectedWeatherProvider())
    }

    private class CapturingProvider(private val provider: WeatherProviderId) : WeatherProvider {
        override val providerId: WeatherProviderId = provider
        var invocationCount: Int = 0
        var request: WeatherProviderRequest? = null

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            invocationCount += 1
            this.request = request
            return WeatherProviderResult(
                payload = RedactedProviderPayload(21, "cloud"),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                provider = provider,
            )
        }
    }

    private companion object {
        const val NOW = 1_704_196_800_000L
        fun syntheticSecret(): String = buildString {
            append(SettingsLocationTest::class.java.name.hashCode().toUInt().toString(16))
            append('-')
            append(NOW.toString(16))
        }

        fun assertSelectedOpenWeatherKeyAccess(settings: SettingsCapability) {
            var callbackInvoked = false
            val result = settings.withSelectedOpenWeatherApiKey {
                callbackInvoked = true
                "[REDACTED]"
            }
            assertEquals("[REDACTED]", result)
            assertTrue(callbackInvoked)
        }

        fun File.containsBytes(needle: ByteArray): Boolean {
            val bytes = readBytes()
            if (needle.isEmpty() || needle.size > bytes.size) return false
            return (0..bytes.size - needle.size).any { start ->
                needle.indices.all { offset -> bytes[start + offset] == needle[offset] }
            }
        }

        val catalogFixture = """
            TJ\tТаджикистан\tTajikistan\t1514879\tХуджанд\tKhujand\tKhujand\t40.2833\t69.6167\tAsia/Dushanbe
            TJ\tТаджикистан\tTajikistan\t1221874\tДушанбе\tDushanbe\tDushanbe\t38.5358\t68.7791\tAsia/Dushanbe
            RU\tРоссия\tRussia\t524901\tМосква\tMoscow\tMoscow\t55.7522\t37.6156\tEurope/Moscow
            US\tСША\tUnited States\t999\t\tSpringfield\tSpringfield\t39.78\t-89.65\tAmerica/Chicago
        """.trimIndent().replace("\\t", "\t")
    }
}
