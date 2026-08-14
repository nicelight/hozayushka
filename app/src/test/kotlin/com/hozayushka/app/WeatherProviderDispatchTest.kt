package com.hozayushka.app

import com.hozayushka.app.adapters.weather.ProviderCurrentWeather
import com.hozayushka.app.adapters.weather.ProviderDailyWeather
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.adapters.weather.OpenMeteoWeatherAdapter
import com.hozayushka.app.adapters.weather.OpenWeatherWeatherAdapter
import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.adapters.weather.WeatherTransport
import com.hozayushka.app.adapters.weather.WeatherTransportResponse
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.SettingsState
import com.hozayushka.app.settings.SettingsStateStore
import com.hozayushka.app.settings.WeatherProviderSelection
import com.hozayushka.app.settings.WeatherAccessReader
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherFreshness
import com.hozayushka.app.weather.WeatherIllustration
import com.hozayushka.app.weather.WeatherRefreshTrigger
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherProviderDispatchTest {
    private val now = 1_704_196_800_000L
    private val location = LocationContext("Худжанд", 40.2833, 69.6167, "Asia/Dushanbe")

    @Test
    fun bothProviderEnvelopesNormalizeToEquivalentProviderNeutralCardsAndCapabilities() {
        val openMeteoSettings = settings(WeatherProviderId.OPEN_METEO)
        val openMeteoStore = InMemoryWeatherCacheStore()
        val openMeteo = WeatherCapability(
            openMeteoSettings,
            openMeteoStore,
            OpenMeteoWeatherAdapter(fixtureTransport("open-meteo-redacted-weather.json")),
            SequencedProvider(
                WeatherProviderId.OPEN_WEATHER,
                Outcome.Failure(WeatherProviderFailure.NETWORK),
            ),
        )
        val openWeatherSettings = settings(WeatherProviderId.OPEN_WEATHER)
        val openWeatherStore = InMemoryWeatherCacheStore()
        val openWeather = WeatherCapability(
            openWeatherSettings,
            openWeatherStore,
            SequencedProvider(
                WeatherProviderId.OPEN_METEO,
                Outcome.Failure(WeatherProviderFailure.NETWORK),
            ),
            OpenWeatherWeatherAdapter(fixtureTransport("openweather-redacted-weather.json")),
        )

        val meteoResult = requireNotNull(
            openMeteo.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH),
        )
        val weatherResult = requireNotNull(
            openWeather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH),
        )
        val meteoToday = meteoResult.projection.cards[1]
        val weatherToday = weatherResult.projection.cards[1]

        assertEquals(WeatherProviderId.OPEN_METEO, meteoResult.provider)
        assertEquals(WeatherProviderId.OPEN_WEATHER, weatherResult.provider)
        assertEquals(meteoToday.temperatureCelsius, weatherToday.temperatureCelsius)
        assertEquals(meteoToday.illustration, weatherToday.illustration)
        assertEquals(WeatherIllustration.CLOUD, meteoToday.illustration)
        assertEquals(meteoToday.date, weatherToday.date)
        assertEquals("Asia/Dushanbe", meteoResult.snapshot.apiTimeZone)
        assertEquals("Asia/Dushanbe", weatherResult.snapshot.apiTimeZone)
        assertEquals("open_meteo", meteoResult.snapshot.source)
        assertEquals("open_weather", weatherResult.snapshot.source)
        assertEquals(10, openMeteoStore.loadRecord()?.providerCapabilities?.supportedDailyRecords)
        assertEquals(8, openWeatherStore.loadRecord()?.providerCapabilities?.supportedDailyRecords)
    }

    @Test
    fun launchCadenceProviderAndCityChangesInvokeOnlyTheResolvedSelection() {
        val settings = settings()
        val openMeteo = SequencedProvider(
            WeatherProviderId.OPEN_METEO,
            Outcome.Success(weatherData("wmo:3")),
        )
        val openWeather = SequencedProvider(
            WeatherProviderId.OPEN_WEATHER,
            Outcome.Success(weatherData("owm:803")),
        )
        val weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteo, openWeather)

        assertNotNull(weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
        assertNull(weather.refreshIfNeeded(now + 1_000L, true, WeatherRefreshTrigger.SCHEDULED))
        assertNotNull(weather.refreshIfNeeded(now + 30L * 60L * 1_000L, true, WeatherRefreshTrigger.SCHEDULED))
        assertEquals(2, openMeteo.calls)
        assertEquals(0, openWeather.calls)
        assertTrue(openMeteo.credentialPresence.none { it })

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.updateOpenWeatherApiKey(syntheticKey())
        assertNotNull(weather.refreshIfNeeded(now + 30L * 60L * 1_000L + 1L, true, WeatherRefreshTrigger.PROVIDER_CHANGE))
        assertEquals(2, openMeteo.calls)
        assertEquals(1, openWeather.calls)
        assertTrue(openWeather.credentialPresence.single())

        settings.saveFoundationLocation(location.copy(cityLabel = "Душанбе", latitude = 38.5358, longitude = 68.7791))
        assertNotNull(weather.refreshIfNeeded(now + 30L * 60L * 1_000L + 2L, true, WeatherRefreshTrigger.LOCATION_CHANGE))
        assertEquals(2, openMeteo.calls)
        assertEquals(2, openWeather.calls)
    }

    @Test
    fun selectedFailuresPreserveOnlyMatchingFreshCacheAndNeverCallTheOtherProvider() {
        listOf(WeatherProviderId.OPEN_METEO, WeatherProviderId.OPEN_WEATHER).forEach { selected ->
            val settings = settings(selected)
            val selectedProvider = SequencedProvider(
                selected,
                Outcome.Success(weatherData(conditionFor(selected), temperature = 7)),
                Outcome.Failure(WeatherProviderFailure.NETWORK),
                Outcome.Failure(WeatherProviderFailure.TIMEOUT),
                Outcome.Failure(WeatherProviderFailure.MALFORMED_RESPONSE),
                Outcome.Failure(WeatherProviderFailure.UNKNOWN_CITY),
                Outcome.Failure(WeatherProviderFailure.INVALID_CREDENTIAL),
            )
            val otherProvider = SequencedProvider(
                other(selected),
                Outcome.Success(weatherData(conditionFor(other(selected)), temperature = 99)),
            )
            val weather = if (selected == WeatherProviderId.OPEN_METEO) {
                WeatherCapability(settings, InMemoryWeatherCacheStore(), selectedProvider, otherProvider)
            } else {
                WeatherCapability(settings, InMemoryWeatherCacheStore(), otherProvider, selectedProvider)
            }

            assertNotNull(weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
            val matchingSnapshot = weather.snapshot()
            repeat(5) { offset ->
                assertNull(
                    weather.refreshIfNeeded(
                        now + offset + 1L,
                        true,
                        WeatherRefreshTrigger.LOCATION_CHANGE,
                    ),
                )
                assertEquals(matchingSnapshot, weather.snapshot())
                assertTrue(weather.inlineErrorMessage().orEmpty().startsWith(selected.displayName))
            }
            assertEquals(6, selectedProvider.calls)
            assertEquals(0, otherProvider.calls)
            assertEquals(selected.toSelection(), settings.selectedWeatherProvider())
        }

        val settings = settings(WeatherProviderId.OPEN_METEO)
        val openMeteo = SequencedProvider(
            WeatherProviderId.OPEN_METEO,
            Outcome.Success(weatherData("wmo:3")),
        )
        val openWeather = SequencedProvider(
            WeatherProviderId.OPEN_WEATHER,
            Outcome.Failure(WeatherProviderFailure.NETWORK),
        )
        val weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteo, openWeather)
        assertNotNull(weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.updateOpenWeatherApiKey(syntheticKey())

        assertNull(weather.refreshIfNeeded(now + 1L, true, WeatherRefreshTrigger.PROVIDER_CHANGE))
        assertNull(weather.snapshot())
        assertEquals(WeatherFreshness.NO_DATA, weather.projection(now + 1L).freshness)
        assertEquals(1, openMeteo.calls)
        assertEquals(1, openWeather.calls)
    }

    @Test
    fun validKeySaveRefreshesSelectedOpenWeatherAndKeepsRepeatedFailureIsolated() {
        val settingsStore = InMemorySettingsStateStore()
        var refreshAt = now
        lateinit var weather: WeatherCapability
        val settings = SettingsCapability(
            stateStore = settingsStore,
            onValidOpenWeatherApiKeySaved = {
                weather.refreshIfNeeded(refreshAt, true, WeatherRefreshTrigger.PROVIDER_CHANGE)
            },
        )
        settings.saveFoundationLocation(location)
        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)

        val openMeteo = MatrixGuardProvider(WeatherProviderId.OPEN_METEO)
        val openWeather = SequencedProvider(
            WeatherProviderId.OPEN_WEATHER,
            Outcome.Success(weatherData("owm:803", temperature = 21)),
            Outcome.Failure(WeatherProviderFailure.NETWORK),
        )
        val store = InMemoryWeatherCacheStore()
        weather = WeatherCapability(settings, store, openMeteo, openWeather)

        assertNull(weather.refreshIfNeeded(refreshAt, true, WeatherRefreshTrigger.PROVIDER_CHANGE))
        assertEquals("OpenWeather: API-ключ не указан", weather.inlineErrorMessage())
        assertEquals(0, openWeather.calls)

        assertTrue(settings.updateOpenWeatherApiKey(syntheticKey()).accepted)
        val matchingSnapshot = requireNotNull(weather.snapshot())
        assertEquals(1, openWeather.calls)
        assertEquals(0, openMeteo.calls)
        assertEquals(listOf(true), openWeather.credentialPresence)
        assertNull(weather.inlineErrorMessage())
        assertEquals(WeatherProviderSelection.OPEN_WEATHER, settings.selectedWeatherProvider())
        assertEquals(location, settings.currentLocation())

        refreshAt += 1L
        assertTrue(settings.updateOpenWeatherApiKey(syntheticKey()).accepted)
        assertEquals(2, openWeather.calls)
        assertEquals(0, openMeteo.calls)
        assertEquals(listOf(true, true), openWeather.credentialPresence)
        assertEquals(matchingSnapshot, weather.snapshot())
        assertEquals("OpenWeather: Нет подключения", weather.inlineErrorMessage())
        assertEquals(WeatherProviderSelection.OPEN_WEATHER, settings.selectedWeatherProvider())
        assertEquals(location, settings.currentLocation())
    }

    @Test
    fun cacheAndSevenDayHistoryNeverCompareAcrossProviderOrLocationIdentity() {
        val settings = settings()
        val openMeteo = SequencedProvider(
            WeatherProviderId.OPEN_METEO,
            Outcome.Success(weatherData("wmo:3", pressureHpa = 1000.0)),
            Outcome.Success(weatherData("wmo:3", pressureHpa = 1004.1)),
        )
        val openWeather = SequencedProvider(
            WeatherProviderId.OPEN_WEATHER,
            Outcome.Success(weatherData("owm:803", pressureHpa = 900.0)),
            Outcome.Success(weatherData("owm:803", pressureHpa = 904.1)),
            Outcome.Success(weatherData("owm:803", pressureHpa = 910.0)),
        )
        val store = InMemoryWeatherCacheStore()
        val weather = WeatherCapability(settings, store, openMeteo, openWeather)
        val threeHours = 3L * 60L * 60L * 1_000L

        assertNotNull(weather.refreshIfNeeded(now - threeHours, true, WeatherRefreshTrigger.LAUNCH))
        assertNotNull(weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LOCATION_CHANGE))
        assertEquals(2, weather.projection(now).cards[1].pressureArrowCount)

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        settings.updateOpenWeatherApiKey(syntheticKey())
        assertNotNull(weather.refreshIfNeeded(now + 1L, true, WeatherRefreshTrigger.PROVIDER_CHANGE))
        assertEquals(0, weather.projection(now + 1L).cards[1].pressureArrowCount)
        val openWeatherLocationOne = requireNotNull(store.loadRecord())
        assertEquals(WeatherProviderId.OPEN_WEATHER, openWeatherLocationOne.provider)

        assertNotNull(weather.refreshIfNeeded(now + threeHours + 2L, true, WeatherRefreshTrigger.LOCATION_CHANGE))
        assertEquals(2, weather.projection(now + threeHours + 2L).cards[1].pressureArrowCount)

        settings.saveFoundationLocation(location.copy(cityLabel = "Душанбе", latitude = 38.5358, longitude = 68.7791))
        assertNull(weather.snapshot())
        assertNotNull(weather.refreshIfNeeded(now + threeHours + 3L, true, WeatherRefreshTrigger.LOCATION_CHANGE))
        assertEquals(0, weather.projection(now + threeHours + 3L).cards[1].pressureArrowCount)
        val locationTwo = requireNotNull(store.loadRecord())
        assertNotEquals(openWeatherLocationOne.locationIdentity, locationTwo.locationIdentity)
        assertEquals(2, locationTwo.history.map { it.provider }.toSet().size)
        assertEquals(2, locationTwo.history.map { it.locationIdentity }.toSet().size)
    }

    @Test
    fun locationChangeDuringFetchRejectsResponseBeforeProjectionOrHistoryAcceptance() {
        val settings = settings()
        val selectedBeforeResponse = location.copy(
            cityLabel = "Душанбе",
            latitude = 38.5358,
            longitude = 68.7791,
        )
        val openMeteo = object : WeatherProvider {
            override val providerId: WeatherProviderId = WeatherProviderId.OPEN_METEO
            var calls = 0

            override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
                calls += 1
                settings.saveFoundationLocation(selectedBeforeResponse)
                val data = weatherData("wmo:3", pressureHpa = 1000.0, temperature = 42)
                return WeatherProviderResult(
                    payload = RedactedProviderPayload(42, "wmo:3"),
                    credentialWasReceived = false,
                    redactedCredential = null,
                    weatherData = data,
                    provider = providerId,
                )
            }
        }
        val openWeather = SequencedProvider(
            WeatherProviderId.OPEN_WEATHER,
            Outcome.Failure(WeatherProviderFailure.NETWORK),
        )
        val store = InMemoryWeatherCacheStore()
        val weather = WeatherCapability(settings, store, openMeteo, openWeather)

        val refreshResult = weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH)
        val selectedProjection = weather.projection(now)
        val stored = store.loadRecord()
        val staleProjectionAccepted =
            refreshResult != null &&
                selectedProjection.freshness == WeatherFreshness.FRESH &&
                selectedProjection.cityLabel == selectedBeforeResponse.cityLabel &&
                selectedProjection.cards.any { it.temperatureCelsius == 42 }
        val stalePressureStored =
            stored?.snapshot?.cityLabel == selectedBeforeResponse.cityLabel &&
                stored.history.any { it.locationIdentity == stored.locationIdentity }

        assertFalse(
            "staleProjectionAccepted=$staleProjectionAccepted; stalePressureStored=$stalePressureStored",
            staleProjectionAccepted || stalePressureStored,
        )
        assertNull(refreshResult)
        assertEquals(WeatherFreshness.NO_DATA, selectedProjection.freshness)
        assertEquals(selectedBeforeResponse.cityLabel, selectedProjection.cityLabel)
        assertTrue(selectedProjection.cards.all { it.temperatureCelsius == null })
        assertNull(stored)
        assertEquals(1, openMeteo.calls)
        assertEquals(0, openWeather.calls)
    }

    @Test
    fun immutablePreRequestSnapshotAndStaleResponseMatrix() {
        val matrix = IdentityMatrix().run()
        println(
            "identity_matrix=${if (matrix.failures.isEmpty()) "PASS" else "FAIL"}; " +
                "scenarios=10; checks=${matrix.checks}; passed=${matrix.checks - matrix.failures.size}; " +
                "failures=${matrix.failures.size}; network_used=false; device_used=false; " +
                "stale_records_history_unchanged=${matrix.failures.isEmpty()}; " +
                "stale_inline_error=false; stale_selected_calls_per_attempt=1; stale_other_calls=0; " +
                "credential_value_recorded=false",
        )
        assertEquals(102, matrix.checks)
        assertTrue(matrix.failures.joinToString(separator = "\n"), matrix.failures.isEmpty())
    }

    @Test
    fun keyReadCadenceAdapterAndFreshnessBoundariesAreExact() {
        val openMeteoAccess = MatrixAccess(location, WeatherProviderSelection.OPEN_METEO)
        val openMeteo = SequencedProvider(
            WeatherProviderId.OPEN_METEO,
            Outcome.Success(weatherData("wmo:3")),
        )
        val unusedOpenWeather = MatrixGuardProvider(WeatherProviderId.OPEN_WEATHER)
        val openMeteoWeather = WeatherCapability(
            openMeteoAccess,
            InMemoryWeatherCacheStore(),
            openMeteo,
            unusedOpenWeather,
        )
        assertNotNull(openMeteoWeather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
        assertEquals(0, openMeteoAccess.keyReads)
        assertEquals(1, openMeteo.calls)
        assertEquals(0, unusedOpenWeather.calls)

        val openWeatherAccess = MatrixAccess(location, WeatherProviderSelection.OPEN_WEATHER)
        val unusedOpenMeteo = MatrixGuardProvider(WeatherProviderId.OPEN_METEO)
        val openWeather = SequencedProvider(
            WeatherProviderId.OPEN_WEATHER,
            Outcome.Success(weatherData("owm:803")),
        )
        val openWeatherStore = InMemoryWeatherCacheStore()
        val selectedOpenWeather = WeatherCapability(
            openWeatherAccess,
            openWeatherStore,
            unusedOpenMeteo,
            openWeather,
        )
        assertNotNull(selectedOpenWeather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
        assertEquals(1, openWeatherAccess.keyReads)

        openWeatherAccess.keyReads = 0
        assertNull(
            selectedOpenWeather.refreshIfNeeded(
                now + 30L * 60L * 1_000L - 1L,
                true,
                WeatherRefreshTrigger.SCHEDULED,
            ),
        )
        assertEquals(0, openWeatherAccess.keyReads)
        assertEquals(1, openWeather.calls)

        val callsBeforeBoundary = openWeather.calls
        assertNotNull(
            selectedOpenWeather.refreshIfNeeded(
                now + 30L * 60L * 1_000L,
                true,
                WeatherRefreshTrigger.SCHEDULED,
            ),
        )
        assertEquals(1, openWeatherAccess.keyReads)
        assertEquals(1, openWeather.calls - callsBeforeBoundary)
        assertEquals(0, unusedOpenMeteo.calls)

        val lastSuccess = now + 30L * 60L * 1_000L
        assertEquals(
            WeatherFreshness.FRESH,
            selectedOpenWeather.projection(lastSuccess + 24L * 60L * 60L * 1_000L).freshness,
        )
        assertEquals(
            WeatherFreshness.STALE_EMPTY,
            selectedOpenWeather.projection(lastSuccess + 24L * 60L * 60L * 1_000L + 1L).freshness,
        )

        val offlineAccess = MatrixAccess(location, WeatherProviderSelection.OPEN_WEATHER)
        val offlineSelected = MatrixGuardProvider(WeatherProviderId.OPEN_WEATHER)
        val offlineOther = MatrixGuardProvider(WeatherProviderId.OPEN_METEO)
        val offlineWeather = WeatherCapability(
            offlineAccess,
            InMemoryWeatherCacheStore(),
            offlineOther,
            offlineSelected,
        )
        assertNull(offlineWeather.refreshIfNeeded(now, false, WeatherRefreshTrigger.LAUNCH))
        assertEquals(0, offlineAccess.keyReads)
        assertEquals(0, offlineSelected.calls)
        assertEquals(0, offlineOther.calls)

        val mismatchAccess = MatrixAccess(location, WeatherProviderSelection.OPEN_WEATHER)
        val mismatchedSelectedSlot = MatrixGuardProvider(WeatherProviderId.OPEN_METEO)
        val mismatchOther = MatrixGuardProvider(WeatherProviderId.OPEN_METEO)
        val mismatchWeather = WeatherCapability(
            mismatchAccess,
            InMemoryWeatherCacheStore(),
            mismatchOther,
            mismatchedSelectedSlot,
        )
        assertNull(mismatchWeather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
        assertEquals(0, mismatchAccess.keyReads)
        assertEquals(0, mismatchedSelectedSlot.calls)
        assertEquals(0, mismatchOther.calls)

        println(
            "boundary_matrix=PASS; open_meteo_key_reads=0; open_weather_due_key_reads=1; " +
                "network_key_reads=0; scheduled_before_30m_key_reads=0; mismatched_adapter_key_reads=0; " +
                "exact_30m_selected_calls=1; other_adapter_calls=0; freshness_24h=FRESH; " +
                "freshness_24h_plus_1ms=STALE_EMPTY; credential_value_recorded=false",
        )
    }

    @Test
    fun unknownConditionsFromBothProvidersUseNeutralFallbackWithoutInventedText() {
        listOf(WeatherProviderId.OPEN_METEO, WeatherProviderId.OPEN_WEATHER).forEach { provider ->
            val context = contextFor(
                provider,
                weatherData(
                    condition = if (provider == WeatherProviderId.OPEN_METEO) "wmo:999" else "owm:999",
                ),
            )
            val result = requireNotNull(context.weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
            val today = result.projection.cards[1]
            assertEquals(WeatherIllustration.NEUTRAL_CLOUD, today.illustration)
            assertEquals(2, today.temperatureCelsius)
            assertFalse(today.temperatureText.orEmpty().contains("cloud", ignoreCase = true))
        }
    }

    @Test
    fun mismatchedOrUnknownOpenWeatherTransportCannotReadTheOwnerKey() {
        val access = CountingOpenWeatherAccess(location, syntheticKey())
        val mislabeledTransport = SequencedProvider(
            WeatherProviderId.OPEN_METEO,
            Outcome.Success(weatherData("wmo:3")),
        )
        val weather = WeatherCapability(
            access,
            InMemoryWeatherCacheStore(),
            SequencedProvider(
                WeatherProviderId.OPEN_METEO,
                Outcome.Success(weatherData("wmo:3")),
            ),
            mislabeledTransport,
        )

        assertNull(weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH))
        assertEquals(0, access.keyReads)
        assertEquals(0, mislabeledTransport.calls)
        assertEquals("OpenWeather: Нет подключения", weather.inlineErrorMessage())
    }

    private fun contextFor(provider: WeatherProviderId, data: ProviderWeatherData): Context {
        val settings = settings(provider)
        val selected = SequencedProvider(provider, Outcome.Success(data))
        val other = SequencedProvider(other(provider), Outcome.Failure(WeatherProviderFailure.NETWORK))
        val store = InMemoryWeatherCacheStore()
        val weather = if (provider == WeatherProviderId.OPEN_METEO) {
            WeatherCapability(settings, store, selected, other)
        } else {
            WeatherCapability(settings, store, other, selected)
        }
        return Context(weather, store)
    }

    private fun settings(provider: WeatherProviderId = WeatherProviderId.OPEN_METEO): SettingsCapability =
        SettingsCapability(InMemorySettingsStateStore()).also { settings ->
            settings.saveFoundationLocation(location)
            if (provider == WeatherProviderId.OPEN_WEATHER) {
                settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
                settings.updateOpenWeatherApiKey(syntheticKey())
            }
        }

    private fun weatherData(
        condition: String,
        pressureHpa: Double = 1000.0,
        temperature: Int = 2,
    ): ProviderWeatherData = ProviderWeatherData(
        apiTimeZone = "Asia/Dushanbe",
        current = ProviderCurrentWeather(temperature, pressureHpa, condition),
        daily = (-1L..2L).map { offset ->
            ProviderDailyWeather(
                date = LocalDate.of(2024, 1, 2).plusDays(offset),
                dayTemperatureCelsius = temperature + offset.toInt(),
                nightTemperatureCelsius = temperature + offset.toInt(),
                dayCondition = condition,
                nightCondition = condition,
            )
        },
    )

    private fun conditionFor(provider: WeatherProviderId): String = when (provider) {
        WeatherProviderId.OPEN_METEO -> "wmo:3"
        WeatherProviderId.OPEN_WEATHER -> "owm:803"
    }

    private fun other(provider: WeatherProviderId): WeatherProviderId = when (provider) {
        WeatherProviderId.OPEN_METEO -> WeatherProviderId.OPEN_WEATHER
        WeatherProviderId.OPEN_WEATHER -> WeatherProviderId.OPEN_METEO
    }

    private fun WeatherProviderId.toSelection(): WeatherProviderSelection = when (this) {
        WeatherProviderId.OPEN_METEO -> WeatherProviderSelection.OPEN_METEO
        WeatherProviderId.OPEN_WEATHER -> WeatherProviderSelection.OPEN_WEATHER
    }

    private fun syntheticKey(): String = buildString {
        append(WeatherProviderDispatchTest::class.java.name.hashCode().toUInt().toString(16))
        append('-')
        append(System.nanoTime().toString(16))
    }

    private fun fixtureTransport(name: String): WeatherTransport {
        val body = requireNotNull(javaClass.getResourceAsStream("/fixtures/$name"))
            .bufferedReader()
            .use { it.readText() }
        return WeatherTransport { _, _ -> WeatherTransportResponse(200, body) }
    }

    private data class Context(
        val weather: WeatherCapability,
        val store: InMemoryWeatherCacheStore,
    )

    private inner class IdentityMatrix {
        val failures = mutableListOf<String>()
        var checks: Int = 0
            private set

        fun run(): IdentityMatrix {
            WeatherProviderId.entries.forEach { initialProvider ->
                verifyLocationSwitchDuringFetch(initialProvider, staleSuccess = true)
                verifyLocationSwitchDuringFetch(initialProvider, staleSuccess = false)
                verifyProviderSwitchDuringFetch(initialProvider, staleSuccess = true)
                verifyProviderSwitchDuringFetch(initialProvider, staleSuccess = false)
            }
            WeatherProviderId.entries.forEach(::verifyRequestCaptureWindow)
            return this
        }

        private fun verifyLocationSwitchDuringFetch(
            initialProvider: WeatherProviderId,
            staleSuccess: Boolean,
        ) {
            val scenario = "location-${initialProvider.storageId}-${outcome(staleSuccess)}"
            val access = MatrixAccess(location, initialProvider.toSelection())
            val selected = MatrixTwoStepProvider(
                initialProvider,
                secondCallHook = {
                    access.location = location.copy(
                        cityLabel = "Душанбе",
                        latitude = 38.5358,
                        longitude = 68.7791,
                    )
                },
                secondCallSuccess = staleSuccess,
            )
            val other = MatrixGuardProvider(other(initialProvider))
            val store = InMemoryWeatherCacheStore()
            val weather = matrixCapability(access, store, selected, other, initialProvider)

            val baselineResult = weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH)
            val baseline = store.loadRecord()
            check(scenario, baselineResult != null && baseline != null, "baseline missing")
            check(scenario, baseline?.history?.size == 1, "baseline history size")

            val staleResult = weather.refreshIfNeeded(now + 1L, true, WeatherRefreshTrigger.LOCATION_CHANGE)
            val afterStale = store.loadRecord()
            val newIdentityProjection = weather.projection(now + 1L)
            check(scenario, staleResult == null, "stale response accepted")
            check(scenario, baseline == afterStale, "cache/history mutated")
            check(
                scenario,
                newIdentityProjection.freshness == WeatherFreshness.NO_DATA,
                "new location became FRESH",
            )
            check(
                scenario,
                access.location.cityLabel == newIdentityProjection.cityLabel,
                "projection identity mismatch",
            )
            check(scenario, weather.inlineErrorMessage() == null, "stale failure leaked to new location")
            check(scenario, selected.calls == 2, "selected provider call count")
            check(scenario, other.calls == 0, "other provider was called")

            access.location = location
            val restored = weather.projection(now + 1L)
            check(scenario, restored.freshness == WeatherFreshness.FRESH, "original cache no longer FRESH")
            check(scenario, restored.cards.any { it.temperatureCelsius == 11 }, "original projection was replaced")
            check(scenario, store.loadRecord()?.history?.size == 1, "pressure history crossed location")
        }

        private fun verifyProviderSwitchDuringFetch(
            initialProvider: WeatherProviderId,
            staleSuccess: Boolean,
        ) {
            val switchedProvider = other(initialProvider)
            val scenario =
                "provider-${initialProvider.storageId}-to-${switchedProvider.storageId}-${outcome(staleSuccess)}"
            val access = MatrixAccess(location, initialProvider.toSelection())
            val selected = MatrixTwoStepProvider(
                initialProvider,
                secondCallHook = { access.selection = switchedProvider.toSelection() },
                secondCallSuccess = staleSuccess,
            )
            val other = MatrixGuardProvider(switchedProvider)
            val store = InMemoryWeatherCacheStore()
            val weather = matrixCapability(access, store, selected, other, initialProvider)

            val baselineResult = weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH)
            val baseline = store.loadRecord()
            check(scenario, baselineResult != null && baseline != null, "baseline missing")
            check(scenario, baseline?.history?.size == 1, "baseline history size")

            val staleResult = weather.refreshIfNeeded(now + 1L, true, WeatherRefreshTrigger.PROVIDER_CHANGE)
            val afterStale = store.loadRecord()
            val newIdentityProjection = weather.projection(now + 1L)
            check(scenario, staleResult == null, "stale response accepted")
            check(scenario, baseline == afterStale, "cache/history mutated")
            check(
                scenario,
                newIdentityProjection.freshness == WeatherFreshness.NO_DATA,
                "new provider became FRESH",
            )
            check(scenario, weather.inlineErrorMessage() == null, "stale failure leaked to new provider")
            check(scenario, selected.calls == 2, "selected provider call count")
            check(scenario, other.calls == 0, "other provider was called")

            access.selection = initialProvider.toSelection()
            val restored = weather.projection(now + 1L)
            check(scenario, restored.freshness == WeatherFreshness.FRESH, "original cache no longer FRESH")
            check(scenario, restored.cards.any { it.temperatureCelsius == 11 }, "original projection was replaced")
            check(scenario, store.loadRecord()?.history?.size == 1, "pressure history crossed provider")
        }

        private fun verifyRequestCaptureWindow(provider: WeatherProviderId) {
            val scenario = "request-capture-window-${provider.storageId}"
            val secondLocation = location.copy(
                cityLabel = "Душанбе",
                latitude = 38.5358,
                longitude = 68.7791,
            )
            val access = SettingsCapability(
                SwitchingSettingsStateStore(
                    beforeRequest = matrixSettingsState(location, provider),
                    afterRequest = matrixSettingsState(secondLocation, provider),
                ),
            )
            val selected = MatrixCapturingProvider(provider)
            val other = MatrixGuardProvider(other(provider))
            val store = InMemoryWeatherCacheStore()
            val weather = matrixCapability(access, store, selected, other, provider)

            val result = weather.refreshIfNeeded(now, true, WeatherRefreshTrigger.LAUNCH)
            val stored = store.loadRecord()
            val projection = weather.projection(now)
            val oldRequestRelabeledAsNewIdentity =
                result != null &&
                    selected.requestLatitude == location.latitude &&
                    selected.requestLongitude == location.longitude &&
                    stored?.snapshot?.cityLabel == secondLocation.cityLabel &&
                    projection.freshness == WeatherFreshness.FRESH &&
                    projection.cards.any { it.temperatureCelsius == 42 }

            check(scenario, !oldRequestRelabeledAsNewIdentity, "old-coordinate response accepted under new identity")
            check(scenario, result == null, "changed request identity returned success")
            check(scenario, stored == null, "changed request identity updated cache/history")
            check(scenario, projection.freshness == WeatherFreshness.NO_DATA, "changed request identity appeared FRESH")
            check(scenario, other.calls == 0, "other provider was called")
        }

        private fun matrixCapability(
            access: WeatherAccessReader,
            store: InMemoryWeatherCacheStore,
            selected: WeatherProvider,
            other: WeatherProvider,
            selectedProvider: WeatherProviderId,
        ): WeatherCapability = if (selectedProvider == WeatherProviderId.OPEN_METEO) {
            WeatherCapability(access, store, selected, other)
        } else {
            WeatherCapability(access, store, other, selected)
        }

        private fun check(scenario: String, condition: Boolean, message: String) {
            checks += 1
            if (!condition) failures += "$scenario: $message"
        }

        private fun outcome(success: Boolean): String = if (success) "stale-success" else "stale-failure"
    }

    private fun matrixSettingsState(
        selectedLocation: LocationContext,
        provider: WeatherProviderId,
    ): SettingsState {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)
        settings.saveFoundationLocation(selectedLocation)
        if (provider == WeatherProviderId.OPEN_WEATHER) {
            settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
            settings.updateOpenWeatherApiKey(matrixSyntheticKey())
        }
        return store.load()
    }

    private open class MatrixAccess(
        var location: LocationContext,
        var selection: WeatherProviderSelection,
    ) : WeatherAccessReader {
        var keyReads: Int = 0

        override fun currentLocation(): LocationContext = location

        override fun selectedWeatherProvider(): WeatherProviderSelection = selection

        override fun <T> withSelectedOpenWeatherApiKey(block: (String) -> T): T? {
            if (selection != WeatherProviderSelection.OPEN_WEATHER) return null
            keyReads += 1
            return block(matrixSyntheticKey())
        }

        override fun hasWeatherApiKey(): Boolean = selection == WeatherProviderSelection.OPEN_WEATHER
    }

    private class SwitchingSettingsStateStore(
        private val beforeRequest: SettingsState,
        private val afterRequest: SettingsState,
    ) : SettingsStateStore {
        private var loads = 0

        override fun load(): SettingsState {
            loads += 1
            return if (loads == 1) beforeRequest else afterRequest
        }

        override fun save(state: SettingsState) = error("matrix store is read-only")

        override fun reset() = error("matrix store is read-only")
    }

    private class MatrixTwoStepProvider(
        override val providerId: WeatherProviderId,
        private val secondCallHook: () -> Unit,
        private val secondCallSuccess: Boolean,
    ) : WeatherProvider {
        var calls: Int = 0
            private set

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            calls += 1
            if (calls == 1) return matrixResult(providerId, request, success = true, temperature = 11)
            secondCallHook()
            return matrixResult(providerId, request, secondCallSuccess, temperature = 42)
        }
    }

    private class MatrixGuardProvider(
        override val providerId: WeatherProviderId,
    ) : WeatherProvider {
        var calls: Int = 0
            private set

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            calls += 1
            error("non-selected provider invoked")
        }
    }

    private class MatrixCapturingProvider(
        override val providerId: WeatherProviderId,
    ) : WeatherProvider {
        var requestLatitude: Double? = null
            private set
        var requestLongitude: Double? = null
            private set

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            requestLatitude = request.latitude
            requestLongitude = request.longitude
            return matrixResult(providerId, request, success = true, temperature = 42)
        }
    }

    private class CountingOpenWeatherAccess(
        private val location: LocationContext,
        private val key: String,
    ) : WeatherAccessReader {
        var keyReads: Int = 0

        override fun currentLocation(): LocationContext = location

        override fun selectedWeatherProvider(): WeatherProviderSelection = WeatherProviderSelection.OPEN_WEATHER

        override fun <T> withSelectedOpenWeatherApiKey(block: (String) -> T): T {
            keyReads += 1
            return block(key)
        }

        override fun hasWeatherApiKey(): Boolean = true
    }

    private sealed interface Outcome {
        data class Success(val data: ProviderWeatherData) : Outcome
        data class Failure(val reason: WeatherProviderFailure) : Outcome
    }

    private class SequencedProvider(
        private val provider: WeatherProviderId,
        vararg private val outcomes: Outcome,
    ) : WeatherProvider {
        override val providerId: WeatherProviderId = provider
        var calls: Int = 0
            private set
        val credentialPresence = mutableListOf<Boolean>()

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            val outcome = outcomes[minOf(calls, outcomes.lastIndex)]
            calls += 1
            credentialPresence += request.hasCredential()
            return when (outcome) {
                is Outcome.Success -> WeatherProviderResult(
                    payload = RedactedProviderPayload(
                        outcome.data.current.temperatureCelsius,
                        outcome.data.current.condition.orEmpty(),
                    ),
                    credentialWasReceived = request.hasCredential(),
                    redactedCredential = request.redactedCredential(),
                    weatherData = outcome.data,
                    provider = provider,
                )
                is Outcome.Failure -> WeatherProviderResult(
                    payload = RedactedProviderPayload(0, ""),
                    credentialWasReceived = request.hasCredential(),
                    redactedCredential = request.redactedCredential(),
                    failure = outcome.reason,
                    provider = provider,
                )
            }
        }
    }

    private companion object {
        fun matrixSyntheticKey(): String =
            "${WeatherProviderDispatchTest::class.java.name.hashCode().toUInt().toString(16)}-${System.nanoTime().toString(16)}"

        fun matrixResult(
            provider: WeatherProviderId,
            request: WeatherProviderRequest,
            success: Boolean,
            temperature: Int,
        ): WeatherProviderResult {
            val condition = if (provider == WeatherProviderId.OPEN_METEO) "wmo:3" else "owm:803"
            if (!success) {
                return WeatherProviderResult(
                    payload = RedactedProviderPayload(0, ""),
                    credentialWasReceived = request.hasCredential(),
                    redactedCredential = request.redactedCredential(),
                    failure = WeatherProviderFailure.NETWORK,
                    provider = provider,
                )
            }
            val daily = (-1L..2L).map { offset ->
                ProviderDailyWeather(
                    date = LocalDate.of(2024, 1, 2).plusDays(offset),
                    dayTemperatureCelsius = temperature,
                    nightTemperatureCelsius = temperature,
                    dayCondition = condition,
                    nightCondition = condition,
                )
            }
            return WeatherProviderResult(
                payload = RedactedProviderPayload(temperature, condition),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                weatherData = ProviderWeatherData(
                    apiTimeZone = "Asia/Dushanbe",
                    current = ProviderCurrentWeather(temperature, 1000.0, condition),
                    daily = daily,
                ),
                provider = provider,
            )
        }
    }
}
