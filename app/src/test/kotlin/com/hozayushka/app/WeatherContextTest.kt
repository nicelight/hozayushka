package com.hozayushka.app

import com.hozayushka.app.adapters.weather.ProviderCurrentWeather
import com.hozayushka.app.adapters.weather.ProviderDailyWeather
import com.hozayushka.app.adapters.weather.ProviderHourlyWeather
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.weather.WeatherCacheRecord
import com.hozayushka.app.weather.WeatherCacheStore
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.PressureDirection
import com.hozayushka.app.weather.TemperaturePalette
import com.hozayushka.app.weather.WeatherCardPresentation
import com.hozayushka.app.weather.WeatherCardSlot
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherFreshness
import com.hozayushka.app.weather.WeatherIllustration
import com.hozayushka.app.weather.WeatherRefreshTrigger
import com.hozayushka.app.weather.WeatherSnapshot
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherContextTest {
    private val midday = 1_704_196_800_000L // 2024-01-02T12:00:00Z
    private val location = LocationContext("Test city", 40.0, 69.0, "UTC")

    @Test
    fun projectionKeepsAcceptedOrderSizingAndCardFields() {
        val weather = weatherWith(QueueProvider(sampleData()))
        weather.refresh(request(), midday)

        val projection = weather.projection(midday)

        assertEquals(
            listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
            projection.cards.map { it.slot },
        )
        assertFalse(projection.cards[0].isTodaySize)
        assertTrue(projection.cards[1].isTodaySize)
        assertFalse(projection.cards[2].isTodaySize)
        assertEquals("+2°", projection.cards[1].temperatureText)
        assertEquals("#448153", projection.cards[1].backgroundHex)
        assertEquals(WeatherIllustration.CLOUD, projection.cards[1].illustration)
        assertEquals(2, projection.cards[1].date.dayOfMonth)
    }

    @Test
    fun repeatedProjectionReadsReuseOneDisplayReadySnapshot() {
        val store = CountingWeatherCacheStore()
        val weather = weatherWith(QueueProvider(sampleData()), store)
        weather.refresh(request(), midday)
        store.resetLoadCount()

        val first = weather.projection(midday)
        val second = weather.projection(midday)

        assertSame(first, second)
        assertEquals(0, store.loadRecordCalls)
    }

    @Test
    fun acceptedRefreshInvalidatesOnceAndFailedRefreshPreservesLastProjection() {
        val store = CountingWeatherCacheStore()
        val weather = weatherWith(
            QueueProvider(sampleData(pressure = 100.0), sampleData(pressure = 104.1)),
            store,
        )
        weather.refresh(request(), midday)
        val beforeRefresh = weather.projection(midday)

        val accepted = weather.refresh(request(), midday + 30L * 60L * 1_000L)
        assertNotNull(accepted)
        assertNotSame(beforeRefresh, accepted!!.projection)
        assertSame(accepted.projection, weather.projection(midday + 30L * 60L * 1_000L))

        val failingStore = CountingWeatherCacheStore()
        val failingWeather = weatherWith(SuccessThenFailureProvider(sampleData()), failingStore)
        failingWeather.refresh(request(), midday)
        val successfulProjection = failingWeather.projection(midday)
        failingStore.resetLoadCount()

        assertNull(failingWeather.refresh(request(), midday + 1_000L))
        assertSame(successfulProjection, failingWeather.projection(midday + 1_000L))
        assertEquals(0, failingStore.loadRecordCalls)
    }

    @Test
    fun locationTimePressureAndFreshnessBoundariesRebuildTheSnapshot() {
        val store = CountingWeatherCacheStore()
        val settings = InMemorySettingsStateStore()
        val locationReader = SettingsCapability(settings).also { it.saveFoundationLocation(location) }
        val weather = WeatherCapability(
            locationReader = locationReader,
            cacheStore = store,
            openMeteoProvider = QueueProvider(sampleData(pressure = 100.0), sampleData(pressure = 104.1)),
            openWeatherProvider = QueueProvider(sampleData()),
        )
        weather.refresh(request(), midday - 3L * 60L * 60L * 1_000L)
        weather.refresh(request(), midday)

        val first = weather.projection(midday)
        assertSame(first, weather.projection(midday + 1_000L))

        locationReader.saveFoundationLocation(location.copy(cityLabel = "Changed city"))
        val afterLocation = weather.projection(midday + 1_000L)
        assertNotSame(first, afterLocation)

        val afterPressureBoundary = weather.projection(midday + 3L * 60L * 60L * 1_000L + 1L)
        assertNotSame(afterLocation, afterPressureBoundary)
        assertEquals(0, afterPressureBoundary.cards[1].pressureArrowCount)

        val night = weather.projection(midday + 6L * 60L * 60L * 1_000L)
        assertNotSame(afterPressureBoundary, night)
        assertEquals(WeatherIllustration.MOON, night.cards[1].illustration)

        val stale = weather.projection(midday + 24L * 60L * 60L * 1_000L + 1L)
        assertNotSame(night, stale)
        assertEquals(WeatherFreshness.STALE_EMPTY, stale.freshness)
        assertTrue(stale.cards.all { it.temperatureCelsius == null && it.pressureArrowCount == 0 })
    }

    @Test
    fun timezoneAndNightMoonFallbackUseSelectedCityData() {
        val nightMillis = 1_704_238_200_000L // 2024-01-03T00:30:00Z
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(location.copy(apiTimeZone = "Asia/Dushanbe"))
        }
        val weather = WeatherCapability(
            locationReader = settings,
            cacheStore = InMemoryWeatherCacheStore(),
            openMeteoProvider = QueueProvider(sampleData(apiTimeZone = "Asia/Dushanbe", moonPhase = null)),
            openWeatherProvider = QueueProvider(sampleData()),
        )
        weather.refresh(request(), nightMillis)

        val today = weather.projection(nightMillis).cards[1]

        assertEquals(LocalDate.of(2024, 1, 3), today.date)
        assertEquals(4, today.temperatureCelsius)
        assertEquals(WeatherIllustration.MOON, today.illustration)
        assertEquals("regular", today.moonPhase)
    }

    @Test
    fun paletteIsExplicitAndSignClampAndGlassRulesAreDeterministic() {
        assertEquals(78, TemperaturePalette.all.size)
        assertEquals("#9653A4", TemperaturePalette.colorFor(-100))
        assertEquals("#4A0F00", TemperaturePalette.colorFor(100))
        assertEquals("#4F7EA6", TemperaturePalette.colorFor(0))
        assertEquals(0, WeatherCardPresentation.pseudoGlass(-1f).intensity.toInt())
        assertEquals(1f, WeatherCardPresentation.pseudoGlass(2f).intensity)
        assertTrue(WeatherCardPresentation.pseudoGlass(0.45f).isStatic)
        assertTrue(WeatherCardPresentation.pseudoGlass(0.45f).lightEdgeAlpha > 0)
    }

    @Test
    fun refreshTriggersCacheFreshnessAndStaleContours() {
        val provider = QueueProvider(sampleData())
        val weather = weatherWith(provider)

        assertNull(weather.refreshIfNeeded(midday, networkAvailable = false, WeatherRefreshTrigger.LAUNCH))
        assertTrue(weather.refreshIfNeeded(midday, networkAvailable = true, WeatherRefreshTrigger.LAUNCH) != null)
        assertNull(weather.refreshIfNeeded(midday + 1_000L, networkAvailable = true, WeatherRefreshTrigger.SCHEDULED))
        val scheduledAt = midday + 30L * 60L * 1_000L
        assertTrue(
            weather.refreshIfNeeded(
                scheduledAt,
                networkAvailable = true,
                WeatherRefreshTrigger.SCHEDULED,
            ) != null,
        )
        assertTrue(
            weather.refreshIfNeeded(
                scheduledAt + 1_000L,
                networkAvailable = true,
                WeatherRefreshTrigger.LOCATION_CHANGE,
            ) != null,
        )
        assertEquals(WeatherFreshness.FRESH, weather.projection(midday + 23L * 60L * 60L * 1_000L).freshness)
        val stale = weather.projection(scheduledAt + 1_000L + 24L * 60L * 60L * 1_000L + 1L)
        assertEquals(WeatherFreshness.STALE_EMPTY, stale.freshness)
        assertEquals(4, stale.cards.size)
        assertTrue(stale.cards.all { it.temperatureCelsius == null && it.pressureArrowCount == 0 })
        assertEquals(3, provider.calls)
    }

    @Test
    fun selectedWeatherActivationLeavesClockAndTimerControlTraceUnchanged() {
        val clockTicks = listOf(0L, 1_000L, 5_000L, 60_000L, 60_001L)
        val controlTimer = TimerCapability(InMemoryTimerStateStore())
        controlTimer.start(midday, 60_000L)
        val controlTrace = clockTicks.map { controlTimer.snapshotAt(midday + it) }
        val controlCancelled = controlTimer.handleGesture(midday + 1_000L, TimerGesture.DOUBLE_TAP)

        val controlOverdueTimer = TimerCapability(InMemoryTimerStateStore())
        controlOverdueTimer.start(midday, 1_000L)
        val controlDismissed = controlOverdueTimer.handleGesture(midday + 1_001L, TimerGesture.SINGLE_TAP)

        val settingsStore = InMemorySettingsStateStore()
        lateinit var weather: WeatherCapability
        val settings = SettingsCapability(
            stateStore = settingsStore,
            onValidOpenWeatherApiKeySaved = {
                weather.refreshIfNeeded(midday, true, WeatherRefreshTrigger.PROVIDER_CHANGE)
            },
        )
        settings.saveFoundationLocation(location)
        settings.updateWeatherProvider(com.hozayushka.app.settings.WeatherProviderSelection.OPEN_WEATHER)
        val openMeteo = object : WeatherProvider {
            override val providerId: WeatherProviderId = WeatherProviderId.OPEN_METEO

            override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
                error("non-selected provider invoked")
            }
        }
        var openWeatherCalls = 0
        val openWeather = object : WeatherProvider {
            override val providerId: WeatherProviderId = WeatherProviderId.OPEN_WEATHER

            override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
                openWeatherCalls += 1
                return WeatherProviderResult(
                    payload = RedactedProviderPayload(21, "owm:803"),
                    credentialWasReceived = request.hasCredential(),
                    redactedCredential = request.redactedCredential(),
                    weatherData = sampleData(),
                    provider = providerId,
                )
            }
        }
        val store = InMemoryWeatherCacheStore()
        weather = WeatherCapability(settings, store, openMeteo, openWeather)
        assertNull(weather.refreshIfNeeded(midday, true, WeatherRefreshTrigger.PROVIDER_CHANGE))
        assertEquals("OpenWeather: API-ключ не указан", weather.inlineErrorMessage())

        val treatmentTimer = TimerCapability(InMemoryTimerStateStore())
        treatmentTimer.start(midday, 60_000L)
        assertTrue(settings.updateOpenWeatherApiKey(syntheticKey()).accepted)
        val treatmentTrace = clockTicks.map { treatmentTimer.snapshotAt(midday + it) }
        val treatmentCancelled = treatmentTimer.handleGesture(midday + 1_000L, TimerGesture.DOUBLE_TAP)

        val treatmentOverdueTimer = TimerCapability(InMemoryTimerStateStore())
        treatmentOverdueTimer.start(midday, 1_000L)
        val treatmentDismissed = treatmentOverdueTimer.handleGesture(midday + 1_001L, TimerGesture.SINGLE_TAP)

        assertEquals(controlTrace, treatmentTrace)
        assertEquals(controlCancelled, treatmentCancelled)
        assertEquals(controlDismissed, treatmentDismissed)
        assertEquals(1, openWeatherCalls)
        assertNull(weather.inlineErrorMessage())
        assertEquals(WeatherFreshness.FRESH, weather.projection(midday).freshness)
        assertEquals(WeatherProviderId.OPEN_WEATHER, store.loadRecord()?.provider)
        assertEquals(location.cityLabel, store.loadRecord()?.snapshot?.cityLabel)
        assertTrue(store.loadRecord()?.locationIdentity.orEmpty().isNotBlank())
    }

    @Test
    fun pressureThresholdsAndTwelveHourZeroFallbackAreOwnedByWeatherContext() {
        val provider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData(pressure = 104.1),
        )
        val weather = weatherWith(provider)
        weather.refresh(request(), midday - 3L * 60L * 60L * 1_000L)
        weather.refresh(request(), midday)
        val trend = weather.projection(midday).cards[1]
        assertEquals(2, trend.pressureArrowCount)
        assertEquals(PressureDirection.UP, trend.pressureDirection)

        val fallbackProvider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData(pressure = 101.0),
            sampleData(pressure = 101.0),
        )
        val fallbackWeather = weatherWith(fallbackProvider)
        fallbackWeather.refresh(request(), midday - 12L * 60L * 60L * 1_000L)
        fallbackWeather.refresh(request(), midday - 3L * 60L * 60L * 1_000L)
        fallbackWeather.refresh(request(), midday)
        val fallback = fallbackWeather.projection(midday).cards[1]
        assertEquals(1, fallback.pressureArrowCount)
        assertEquals(PressureDirection.UP, fallback.pressureDirection)
    }

    @Test
    fun unknownConditionAndMissingOptionalFieldsUseNeutralFallbackWithoutText() {
        val data = sampleData(currentCondition = "alien-weather", nightCondition = null, moonPhase = null)
        val weather = weatherWith(QueueProvider(data))
        weather.refresh(request(), midday)

        val today = weather.projection(midday).cards[1]
        assertEquals(WeatherIllustration.NEUTRAL_CLOUD, today.illustration)
        assertEquals(2, today.temperatureCelsius)
        assertTrue(today.temperatureText!!.contains("°"))
        assertFalse(today.temperatureText!!.contains("cloud", ignoreCase = true))
    }

    @Test
    fun incompleteStructuredRefreshDoesNotReplaceSuccessfulCache() {
        val provider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData().copy(
                current = ProviderCurrentWeather(40, Double.NaN, "rain"),
                daily = emptyList(),
            ),
        )
        val weather = weatherWith(provider)
        assertTrue(weather.refresh(request(), midday) != null)
        assertNull(weather.refresh(request(), midday + 30L * 60L * 1_000L))
        assertEquals(2, weather.snapshot()!!.temperatureCelsius)
        assertEquals("cloud", weather.snapshot()!!.condition)
    }

    @Test
    fun supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots() {
        val now = 1_704_196_800_000L // 2024-01-02T12:00:00Z
        val weather = weatherWith(
            QueueProvider(
                sampleData(
                    apiTimeZone = "Asia/Dushanbe",
                    hourly = fullDayHourly(LocalDate.of(2024, 1, 2)),
                ),
            ),
        )

        assertNotNull(weather.refresh(request(), now))

        val projection = weather.hourlyProjection(now)
        assertNotNull(projection)
        assertEquals("Asia/Dushanbe", projection!!.apiTimeZone)
        assertEquals(
            listOf(
                LocalTime.of(6, 0),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(15, 0),
                LocalTime.of(18, 0),
                LocalTime.of(21, 0),
                LocalTime.MIDNIGHT,
                LocalTime.of(3, 0),
            ),
            projection.cards.map { it.slotTime },
        )
        assertEquals(
            listOf(
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 3),
                LocalDate.of(2024, 1, 3),
            ),
            projection.cards.map { it.date },
        )
    }

    @Test
    fun selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable() {
        val now = 1_704_196_800_000L // 2024-01-02T12:00:00Z
        val base = fullDayHourly(LocalDate.of(2024, 1, 2))
        val variants = listOf(
            base.filterNot { it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(9, 0) }
                .plus(ProviderHourlyWeather(LocalDate.of(2024, 1, 2), LocalTime.of(10, 0), 5, "cloud")),
            base.map {
                if (it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(12, 0)) {
                    it.copy(temperatureCelsius = null)
                } else {
                    it
                }
            },
            base.map {
                if (it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(15, 0)) {
                    it.copy(condition = null)
                } else {
                    it
                }
            },
        )

        variants.forEach { hourly ->
            val weather = weatherWith(
                QueueProvider(
                    sampleData(
                        apiTimeZone = "Asia/Dushanbe",
                        hourly = hourly,
                    ),
                ),
            )

            assertNotNull(weather.refresh(request(), now))
            assertNull(weather.hourlyProjection(now))
        }
    }

    @Test
    fun incompleteFullDailyConditionDataDoesNotReplaceSuccessfulCache() {
        val complete = fullDailyData(hourly = fullDayHourly(LocalDate.of(2024, 1, 2)))
        val incomplete = complete.copy(
            daily = complete.daily.mapIndexed { index, day ->
                if (index == 4) day.copy(nightCondition = null) else day
            },
        )
        val weather = weatherWith(QueueProvider(complete, incomplete))

        assertNotNull(weather.refresh(request(), midday))
        val cachedLongTerm = weather.longTermProjection(midday)
        val cachedSnapshot = weather.snapshot()
        assertNotNull(cachedLongTerm)
        assertTrue(requireNotNull(cachedLongTerm).cards.none { it.illustration == WeatherIllustration.NEUTRAL_CLOUD })

        assertNotNull(weather.refresh(request(), midday + 1_000L))
        assertEquals(cachedLongTerm, weather.longTermProjection(midday + 1_000L))
        assertEquals(cachedSnapshot?.copy(updatedAtMillis = midday + 1_000L), weather.snapshot())
    }

    @Test
    fun emptyHourlyPayloadDoesNotReplaceSuccessfulHourlyCache() {
        val complete = sampleData(hourly = fullDayHourly(LocalDate.of(2024, 1, 2)))
        val invalidHourlyPayloads = listOf(
            emptyList<ProviderHourlyWeather>(),
            complete.hourly.map {
                if (it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(12, 0)) {
                    it.copy(condition = null)
                } else {
                    it
                }
            },
        )

        invalidHourlyPayloads.forEach { invalidHourly ->
            val weather = weatherWith(QueueProvider(complete, complete.copy(hourly = invalidHourly)))

            assertNotNull(weather.refresh(request(), midday))
            val cachedHourly = weather.hourlyProjection(midday)
            val cachedSnapshot = weather.snapshot()
            assertNotNull(cachedHourly)

            assertNotNull(weather.refresh(request(), midday + 1_000L))
            assertEquals(cachedHourly, weather.hourlyProjection(midday + 1_000L))
            assertEquals(cachedSnapshot?.copy(updatedAtMillis = midday + 1_000L), weather.snapshot())
        }
    }

    @Test
    fun yesterdayUsesLargestChangeAndHistoryIsInstallationRelativeSevenDays() {
        val provider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData(pressure = 104.1),
            sampleData(pressure = 102.0),
            sampleData(baseDate = LocalDate.of(2024, 1, 10), pressure = 100.0),
        )
        val weather = weatherWith(provider)
        val yesterdayFirst = midday - 20L * 60L * 60L * 1_000L
        val yesterdayLargest = midday - 18L * 60L * 60L * 1_000L
        weather.refresh(request(), yesterdayFirst)
        weather.refresh(request(), yesterdayLargest)
        weather.refresh(request(), midday)

        val yesterday = weather.projection(midday).cards[0]
        assertEquals(2, yesterday.pressureArrowCount)
        assertEquals(PressureDirection.UP, yesterday.pressureDirection)

        val eightDaysLater = midday + 8L * 24L * 60L * 60L * 1_000L
        weather.refresh(request(), eightDaysLater)
        assertEquals(0, weather.projection(eightDaysLater).cards[1].pressureArrowCount)
    }

    private fun weatherWith(
        provider: WeatherProvider = QueueProvider(sampleData()),
        cacheStore: WeatherCacheStore = InMemoryWeatherCacheStore(),
    ): WeatherCapability {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also { it.saveFoundationLocation(location) }
        return WeatherCapability(settings, cacheStore, provider, provider)
    }

    private fun request(): WeatherProviderRequest = WeatherProviderRequest.withoutCredential()

    private fun syntheticKey(): String = buildString {
        append(WeatherContextTest::class.java.name.hashCode().toUInt().toString(16))
        append('-')
        append(midday.toString(16))
    }

    private fun sampleData(
        apiTimeZone: String = "UTC",
        baseDate: LocalDate = LocalDate.of(2024, 1, 2),
        pressure: Double = 100.0,
        currentCondition: String? = "cloud",
        nightCondition: String? = "clear",
        moonPhase: String? = "half",
        hourly: List<ProviderHourlyWeather> = emptyList(),
    ): ProviderWeatherData {
        return ProviderWeatherData(
            apiTimeZone = apiTimeZone,
            current = ProviderCurrentWeather(2, pressure, currentCondition),
            daily = (-1..2).map { offset ->
                ProviderDailyWeather(
                    date = baseDate.plusDays(offset.toLong()),
                    dayTemperatureCelsius = 2 + offset,
                    nightTemperatureCelsius = 3 + offset,
                    dayCondition = "cloud",
                    nightCondition = nightCondition,
                    moonPhase = moonPhase,
                )
            },
            hourly = hourly,
        )
    }

    private fun fullDailyData(hourly: List<ProviderHourlyWeather>): ProviderWeatherData =
        sampleData(hourly = hourly).copy(
            daily = (0L until 10L).map { offset ->
                ProviderDailyWeather(
                    date = LocalDate.of(2024, 1, 2).plusDays(offset),
                    dayTemperatureCelsius = 2 + offset.toInt(),
                    nightTemperatureCelsius = 3 + offset.toInt(),
                    dayCondition = "cloud",
                    nightCondition = "clear",
                    moonPhase = "half",
                )
            },
        )

    private fun fullDayHourly(startDate: LocalDate): List<ProviderHourlyWeather> =
        (0..1).flatMap { dayOffset ->
            (0..23).map { hour ->
                ProviderHourlyWeather(
                    date = startDate.plusDays(dayOffset.toLong()),
                    time = LocalTime.of(hour, 0),
                    temperatureCelsius = hour - 4,
                    condition = "cloud",
                )
            }
        }

    private class QueueProvider(vararg private val values: ProviderWeatherData) : WeatherProvider {
        var calls: Int = 0
            private set

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            val value = values[minOf(calls, values.lastIndex)]
            calls += 1
            return WeatherProviderResult(
                payload = RedactedProviderPayload(value.current.temperatureCelsius, value.current.condition.orEmpty()),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                weatherData = value,
            )
        }
    }

    private class CountingWeatherCacheStore : WeatherCacheStore {
        private val delegate = InMemoryWeatherCacheStore()
        var loadRecordCalls: Int = 0
            private set

        override fun loadRecord(): WeatherCacheRecord? {
            loadRecordCalls += 1
            return delegate.loadRecord()
        }

        override fun saveRecord(record: WeatherCacheRecord) = delegate.saveRecord(record)

        override fun reset() = delegate.reset()

        fun resetLoadCount() {
            loadRecordCalls = 0
        }
    }

    private class SuccessThenFailureProvider(
        private val success: ProviderWeatherData,
    ) : WeatherProvider {
        private var calls = 0

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            if (calls++ == 0) {
                return WeatherProviderResult(
                    payload = RedactedProviderPayload(success.current.temperatureCelsius, success.current.condition.orEmpty()),
                    credentialWasReceived = request.hasCredential(),
                    redactedCredential = request.redactedCredential(),
                    weatherData = success,
                )
            }
            return WeatherProviderResult(
                payload = RedactedProviderPayload(0, ""),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                failure = WeatherProviderFailure.NETWORK,
            )
        }
    }
}
