package com.hozayushka.app

import android.view.Window
import com.hozayushka.app.adapters.platform.AudioProbeResult
import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.adapters.weather.ProviderCurrentWeather
import com.hozayushka.app.adapters.weather.ProviderDailyWeather
import com.hozayushka.app.adapters.weather.ProviderHourlyWeather
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.forecast.ForecastSessionCapability
import com.hozayushka.app.forecast.ForecastSessionState
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.WeatherProviderSelection
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.WeatherCardPresentation
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherIllustration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ForecastSessionTest {
    private val now = Instant.parse("2024-01-02T12:00:00Z").toEpochMilli()
    private val cityDayStart = Instant.parse("2024-01-02T01:00:00Z").toEpochMilli()
    private val cityNight = Instant.parse("2024-01-02T00:59:59Z").toEpochMilli()

    @Test
    fun completeRedactedFixtureMapsEightSlotsIntoTwoRowsAndUsesCityTimezone() {
        val weather = weatherWith(completeHourlyData())
        assertTrue(weather.refresh(WeatherProviderRequest.withoutCredential(), now) != null)

        val projection = requireNotNull(weather.hourlyProjection(now))

        assertEquals(listOf("06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "00:00", "03:00"), projection.cards.map { it.slotTimeText })
        assertEquals(listOf(0L, 0L, 0L, 0L, 0L, 0L, 1L, 1L), projection.cards.map { it.dayOffset })
        assertEquals(listOf(4, 4), projection.rows.map { it.size })
        assertTrue(projection.cards.all { it.pressureArrowCount == 0 && it.backgroundHex.startsWith("#") })
        assertEquals("Asia/Dushanbe", projection.apiTimeZone)
    }

    @Test
    fun sharedPresentationMapsEveryHourlyIllustrationToVisibleContent() {
        assertTrue(WeatherIllustration.entries.all {
            WeatherCardPresentation.illustrationText(it).isNotBlank()
        })
    }

    @Test
    fun completeReadModelIsConsumedByHourlySessionWithSharedCardInputs() {
        val weather = weatherWith(completeHourlyData())
        weather.refresh(WeatherProviderRequest.withoutCredential(), now)
        val session = ForecastSessionCapability(weather, FakePlatform(now))

        val opened = session.openHourly(now)

        assertEquals(ForecastSessionState.OPEN, opened.state)
        assertEquals(listOf(4, 4), opened.rows.map { it.size })
        assertEquals(
            listOf("06:00", "09:00", "12:00", "15:00", "18:00", "21:00", "00:00", "03:00"),
            opened.rows.flatten().map { it.slotTimeText },
        )
        assertTrue(opened.rows.flatten().all { card ->
            card.pressureArrowCount == 0 &&
                card.backgroundHex.startsWith("#") &&
                WeatherCardPresentation.illustrationText(card.illustration).isNotBlank()
        })
    }

    @Test
    fun incompleteHourlyDataStaysUnavailableAndDoesNotCreateSession() {
        val weather = weatherWith(completeHourlyData().copy(hourly = completeHourlyData().hourly.dropLast(1)))
        assertTrue(weather.refresh(WeatherProviderRequest.withoutCredential(), now) != null)

        val session = ForecastSessionCapability(weather, FakePlatform(now))
        val rejected = session.openHourly(now)

        assertEquals(ForecastSessionState.CLOSED, rejected.state)
        assertEquals(ForecastSessionCapability.HOURLY_UNAVAILABLE_MESSAGE, rejected.message)
        assertTrue(rejected.rows.isEmpty())
    }

    @Test
    fun selectedProvidersRequireAllEightSlotsAndNeverBorrowMissingValues() {
        val today = LocalDate.of(2024, 1, 2)
        val expectedSlots = listOf(6, 9, 12, 15, 18, 21)
            .map { today to LocalTime.of(it, 0) } +
            listOf(0, 3).map { today.plusDays(1) to LocalTime.of(it, 0) }

        WeatherProviderId.entries.forEach { selectedProvider ->
            val complete = selectedProviderFixture(selectedProvider, completeHourlyData())
            assertTrue(complete.weather.refresh(requestFor(selectedProvider), now) != null)
            val projection = requireNotNull(complete.weather.hourlyProjection(now))
            assertEquals(expectedSlots, projection.cards.map { it.date to it.slotTime })
            assertEquals(selectedProvider.storageId, complete.weather.snapshot()?.source)
            assertEquals(1, complete.selectedProvider.calls)
            assertEquals(0, complete.otherProvider.calls)

            val opened = ForecastSessionCapability(complete.weather, FakePlatform(now)).openHourly(now)
            assertEquals(ForecastSessionState.OPEN, opened.state)
            assertEquals(listOf(4, 4), opened.rows.map { it.size })

            expectedSlots.forEach { missingSlot ->
                val incomplete = selectedProviderFixture(
                    selectedProvider,
                    completeHourlyData().copy(
                        hourly = completeHourlyData().hourly.filterNot {
                            it.date == missingSlot.first && it.time == missingSlot.second
                        },
                    ),
                )
                assertTrue(incomplete.weather.refresh(requestFor(selectedProvider), now) != null)
                assertNull(incomplete.weather.hourlyProjection(now))

                val rejected = ForecastSessionCapability(incomplete.weather, FakePlatform(now)).openHourly(now)
                assertEquals(ForecastSessionState.CLOSED, rejected.state)
                assertEquals(ForecastSessionCapability.HOURLY_UNAVAILABLE_MESSAGE, rejected.message)
                assertTrue(rejected.rows.isEmpty())
                assertEquals(selectedProvider.storageId, incomplete.weather.snapshot()?.source)
                assertEquals(1, incomplete.selectedProvider.calls)
                assertEquals(0, incomplete.otherProvider.calls)
            }
        }
    }

    @Test
    fun selectedProviderChangeDoesNotBorrowAnotherProviderHourlyCache() {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
        }
        val openMeteo = CountingProvider(WeatherProviderId.OPEN_METEO, completeHourlyData())
        val openWeather = CountingProvider(WeatherProviderId.OPEN_WEATHER, completeHourlyData())
        val weather = WeatherCapability(
            settings,
            InMemoryWeatherCacheStore(),
            openMeteo,
            openWeather,
        )

        assertTrue(weather.refresh(WeatherProviderRequest.withoutCredential(), now) != null)
        assertTrue(weather.hourlyProjection(now) != null)
        assertTrue(settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER))

        val rejected = ForecastSessionCapability(weather, FakePlatform(now)).openHourly(now)

        assertEquals(ForecastSessionState.CLOSED, rejected.state)
        assertEquals(ForecastSessionCapability.HOURLY_UNAVAILABLE_MESSAGE, rejected.message)
        assertTrue(rejected.rows.isEmpty())
        assertEquals(0, openWeather.calls)
    }

    @Test
    fun sharedSessionTimingAndGesturesFollowAcceptedTransitions() {
        val weather = weatherWith(completeHourlyData())
        weather.refresh(WeatherProviderRequest.withoutCredential(), now)
        val session = ForecastSessionCapability(weather, FakePlatform(now))

        assertEquals(ForecastSessionState.OPEN, session.openHourly(now).state)
        assertEquals(ForecastSessionState.CLOSED, session.snapshotAt(now + 3_000L).state)

        session.openHourly(now)
        assertEquals(ForecastSessionState.HINT, session.singleTap(now + 100L).state)
        assertEquals(ForecastSessionState.HINT, session.snapshotAt(now + 30_000L).state)
        assertEquals(ForecastSessionState.CLOSED, session.doubleTap(now + 31_000L).state)
    }

    @Test
    fun holdKeepsSessionOpenBeyondOriginalDeadlineAndReleaseClosesImmediately() {
        val weather = weatherWith(completeHourlyData())
        weather.refresh(WeatherProviderRequest.withoutCredential(), now)
        val session = ForecastSessionCapability(weather, FakePlatform(now))

        assertEquals(ForecastSessionState.OPEN, session.openHourly(now).state)
        assertEquals(ForecastSessionState.OPEN, session.hold(now + 600L).state)
        assertEquals(ForecastSessionState.OPEN, session.snapshotAt(now + 3_500L).state)
        assertEquals(ForecastSessionState.CLOSED, session.release(now + 3_500L).state)
    }

    @Test
    fun completeTenDayReadModelSurvivesOwnerReloadAndOpensFromLongTermEntry() {
        val store = InMemoryWeatherCacheStore()
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
        }
        val provider = SingleProvider(completeLongTermData())
        val weather = WeatherCapability(settings, store, provider, provider)

        assertTrue(weather.refresh(WeatherProviderRequest.withoutCredential(), cityDayStart) != null)
        val beforeReload = requireNotNull(weather.longTermProjection(cityDayStart))
        val reloadedWeather = WeatherCapability(settings, store, provider, provider)
        val afterReload = requireNotNull(reloadedWeather.longTermProjection(cityDayStart))

        assertEquals(beforeReload, afterReload)
        assertEquals((0L..9L).map { LocalDate.of(2024, 1, 2).plusDays(it) }, afterReload.cards.map { it.date })
        assertEquals(listOf(5, 5), afterReload.rows.map { it.size })
        assertEquals("02", afterReload.cards.first().dateDayText)
        assertTrue(afterReload.cards.all {
            !it.temperatureText.isNullOrBlank() &&
                it.backgroundHex?.startsWith("#") == true &&
                it.illustration != null &&
                it.pressureArrowCount == 0
        })

        val session = ForecastSessionCapability(reloadedWeather, FakePlatform(cityDayStart))
        val opened = session.openLongTerm(cityDayStart)
        assertEquals(ForecastSessionState.OPEN, opened.state)
        assertEquals(listOf(5, 5), opened.longTermRows.map { it.size })
    }

    @Test
    fun longTermProjectionUsesSelectedCityDayNightAndRejectsIncompleteDailyFields() {
        val complete = completeLongTermData()
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
        }
        val store = InMemoryWeatherCacheStore()
        val completeProvider = SingleProvider(complete)
        val weather = WeatherCapability(settings, store, completeProvider, completeProvider)
        weather.refresh(WeatherProviderRequest.withoutCredential(), cityDayStart)

        val day = requireNotNull(weather.longTermProjection(cityDayStart))
        val night = requireNotNull(weather.longTermProjection(cityNight))
        assertEquals(10, day.cards.first().temperatureCelsius)
        assertEquals(20, night.cards.first().temperatureCelsius)
        assertEquals(WeatherIllustration.MOON, night.cards.first().illustration)

        val incomplete = complete.copy(
            daily = complete.daily.mapIndexed { index, value ->
                if (index == 4) value.copy(nightTemperatureCelsius = null) else value
            },
        )
        val incompleteProvider = SingleProvider(incomplete)
        val failedRefreshWeather = WeatherCapability(settings, store, incompleteProvider, incompleteProvider)
        assertTrue(failedRefreshWeather.refresh(WeatherProviderRequest.withoutCredential(), cityDayStart) != null)
        assertEquals(day, requireNotNull(failedRefreshWeather.longTermProjection(cityDayStart)))

        val brokenProvider = SingleProvider(incomplete)
        val brokenWeather = WeatherCapability(settings, InMemoryWeatherCacheStore(), brokenProvider, brokenProvider)
        assertTrue(brokenWeather.refresh(WeatherProviderRequest.withoutCredential(), cityDayStart) != null)
        val rejected = ForecastSessionCapability(brokenWeather, FakePlatform(cityDayStart)).openLongTerm(cityDayStart)
        assertEquals(ForecastSessionState.CLOSED, rejected.state)
        assertEquals(ForecastSessionCapability.LONG_TERM_UNAVAILABLE_MESSAGE, rejected.message)
        assertTrue(rejected.longTermRows.isEmpty())
    }

    @Test
    fun longTermSessionUsesSharedTimingAndGestureContract() {
        val weather = weatherWith(completeLongTermData())
        weather.refresh(WeatherProviderRequest.withoutCredential(), cityDayStart)
        val session = ForecastSessionCapability(weather, FakePlatform(cityDayStart))

        assertEquals(ForecastSessionState.OPEN, session.openLongTerm(cityDayStart).state)
        assertEquals(ForecastSessionState.CLOSED, session.snapshotAt(cityDayStart + 3_000L).state)
        session.openLongTerm(cityDayStart)
        assertEquals(ForecastSessionState.HINT, session.singleTap(cityDayStart + 100L).state)
        assertEquals(ForecastSessionState.HINT, session.snapshotAt(cityDayStart + 30_000L).state)
        assertEquals(ForecastSessionState.CLOSED, session.doubleTap(cityDayStart + 31_000L).state)
        session.openLongTerm(cityDayStart)
        assertEquals(ForecastSessionState.OPEN, session.hold(cityDayStart + 600L).state)
        assertEquals(ForecastSessionState.OPEN, session.snapshotAt(cityDayStart + 3_500L).state)
        assertEquals(ForecastSessionState.CLOSED, session.release(cityDayStart + 3_500L).state)
    }

    @Test
    fun selectedProvidersKeepTenDayHorizonAndOpenWeatherUsesHonestEightPlusTwoProjection() {
        WeatherProviderId.entries.forEach { selectedProvider ->
            val supportedRecords = selectedProvider.capabilities.supportedDailyRecords
            val complete = selectedProviderFixture(
                selectedProvider,
                longTermData(supportedRecords, temperatureBase = if (selectedProvider == WeatherProviderId.OPEN_WEATHER) 30 else 10),
            )
            assertTrue(complete.weather.refresh(requestFor(selectedProvider), cityDayStart) != null)

            val session = ForecastSessionCapability(complete.weather, FakePlatform(cityDayStart))
            listOf("Tomorrow", "Day-after").forEach {
                session.reset()
                val opened = session.openLongTerm(cityDayStart)
                assertEquals(ForecastSessionState.OPEN, opened.state)
                assertEquals(10, opened.longTermRows.flatten().size)
                assertEquals(
                    (0L..9L).map { LocalDate.of(2024, 1, 2).plusDays(it) },
                    opened.longTermRows.flatten().map { card -> card.date },
                )
                if (selectedProvider == WeatherProviderId.OPEN_WEATHER) {
                    val cards = opened.longTermRows.flatten()
                    assertTrue(cards.take(8).all {
                        it.temperatureCelsius != null &&
                            !it.temperatureText.isNullOrBlank() &&
                            it.backgroundHex?.startsWith("#") == true &&
                            it.illustration != null
                    })
                    assertTrue(cards.drop(8).all {
                        it.temperatureCelsius == null && it.temperatureText == null && it.backgroundHex == null && it.illustration == null
                    })
                } else {
                    assertTrue(opened.longTermRows.flatten().all {
                        it.temperatureCelsius != null &&
                            it.temperatureText?.isNotBlank() == true &&
                            it.backgroundHex?.startsWith("#") == true &&
                            it.illustration != null
                    })
                }
            }
            assertEquals(selectedProvider.storageId, complete.weather.snapshot()?.source)
            assertEquals(1, complete.selectedProvider.calls)
            assertEquals(0, complete.otherProvider.calls)

            val oneShort = selectedProviderFixture(
                selectedProvider,
                longTermData(supportedRecords - 1, temperatureBase = 40),
            )
            assertTrue(oneShort.weather.refresh(requestFor(selectedProvider), cityDayStart) != null)
            val rejectedSession = ForecastSessionCapability(oneShort.weather, FakePlatform(cityDayStart))
            listOf("Tomorrow", "Day-after").forEach {
                rejectedSession.reset()
                val rejected = rejectedSession.openLongTerm(cityDayStart)
                assertEquals(ForecastSessionState.CLOSED, rejected.state)
                assertEquals(ForecastSessionCapability.LONG_TERM_UNAVAILABLE_MESSAGE, rejected.message)
                assertTrue(rejected.longTermRows.isEmpty())
            }
            assertEquals(selectedProvider.storageId, oneShort.weather.snapshot()?.source)
            assertEquals(1, oneShort.selectedProvider.calls)
            assertEquals(0, oneShort.otherProvider.calls)
        }
    }

    @Test
    fun selectedProviderChangeDoesNotBorrowAnotherProviderLongTermCache() {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
        }
        val openMeteo = CountingProvider(WeatherProviderId.OPEN_METEO, longTermData(10, 10))
        val openWeather = CountingProvider(WeatherProviderId.OPEN_WEATHER, longTermData(8, 30))
        val weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteo, openWeather)

        assertTrue(weather.refresh(WeatherProviderRequest.withoutCredential(), cityDayStart) != null)
        assertTrue(weather.longTermProjection(cityDayStart) != null)
        assertTrue(settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER))

        val rejected = ForecastSessionCapability(weather, FakePlatform(cityDayStart)).openLongTerm(cityDayStart)

        assertEquals(ForecastSessionState.CLOSED, rejected.state)
        assertEquals(ForecastSessionCapability.LONG_TERM_UNAVAILABLE_MESSAGE, rejected.message)
        assertTrue(rejected.longTermRows.isEmpty())
        assertEquals(1, openMeteo.calls)
        assertEquals(0, openWeather.calls)
    }

    private fun weatherWith(data: ProviderWeatherData): WeatherCapability {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
        }
        val provider = SingleProvider(data)
        return WeatherCapability(settings, InMemoryWeatherCacheStore(), provider, provider)
    }

    private fun selectedProviderFixture(
        selectedProvider: WeatherProviderId,
        data: ProviderWeatherData,
    ): SelectedProviderFixture {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
            if (selectedProvider == WeatherProviderId.OPEN_WEATHER) {
                it.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
            }
        }
        val selected = CountingProvider(selectedProvider, data)
        val otherId = if (selectedProvider == WeatherProviderId.OPEN_METEO) {
            WeatherProviderId.OPEN_WEATHER
        } else {
            WeatherProviderId.OPEN_METEO
        }
        val other = CountingProvider(otherId, completeHourlyData())
        val openMeteo = if (selectedProvider == WeatherProviderId.OPEN_METEO) selected else other
        val openWeather = if (selectedProvider == WeatherProviderId.OPEN_WEATHER) selected else other
        return SelectedProviderFixture(
            weather = WeatherCapability(settings, InMemoryWeatherCacheStore(), openMeteo, openWeather),
            selectedProvider = selected,
            otherProvider = other,
        )
    }

    private fun requestFor(provider: WeatherProviderId): WeatherProviderRequest = if (
        provider == WeatherProviderId.OPEN_WEATHER
    ) {
        WeatherProviderRequest.fromSyntheticProbe()
    } else {
        WeatherProviderRequest.withoutCredential()
    }

    private fun completeHourlyData(): ProviderWeatherData {
        val today = LocalDate.of(2024, 1, 2)
        val times = listOf(6, 9, 12, 15, 18, 21)
            .map { today to LocalTime.of(it, 0) } +
            listOf(0, 3).map { today.plusDays(1) to LocalTime.of(it, 0) }
        return ProviderWeatherData(
            apiTimeZone = "Asia/Dushanbe",
            current = ProviderCurrentWeather(2, 100.0, "cloud"),
            daily = listOf(ProviderDailyWeather(today, 2, 1, "cloud", "cloud")),
            hourly = times.mapIndexed { index, (date, time) ->
                ProviderHourlyWeather(date, time, index - 2, "cloud")
            },
        )
    }

    private fun completeLongTermData(): ProviderWeatherData {
        return longTermData(10, 10)
    }

    private fun longTermData(recordCount: Int, temperatureBase: Int): ProviderWeatherData {
        val today = LocalDate.of(2024, 1, 2)
        return ProviderWeatherData(
            apiTimeZone = "Asia/Dushanbe",
            current = ProviderCurrentWeather(temperatureBase, 100.0, "cloud"),
            daily = (0L until recordCount.toLong()).map { offset ->
                ProviderDailyWeather(
                    date = today.plusDays(offset),
                    dayTemperatureCelsius = temperatureBase + offset.toInt(),
                    nightTemperatureCelsius = temperatureBase + 10 + offset.toInt(),
                    dayCondition = "clear",
                    nightCondition = "clear",
                )
            },
        )
    }

    private class SingleProvider(private val data: ProviderWeatherData) : WeatherProvider {
        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult =
            WeatherProviderResult(
                payload = RedactedProviderPayload(data.current.temperatureCelsius, "cloud"),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                weatherData = data,
            )
    }

    private class FakePlatform(private var currentMillis: Long) : PlatformRuntime {
        override fun nowMillis(): Long = currentMillis
        override fun deviceTimeText(nowMillis: Long): String = "12:00"
        override fun deviceZoneId(): ZoneId = ZoneId.of("UTC")
        override fun isNetworkAvailable(): Boolean = false
        override fun applyFoundationWindow(window: Window) = Unit
        override fun onActivityPaused() = Unit
        override fun onActivityResumed() = Unit
        override fun requestAlertAudio() = AudioProbeResult(false, false, "not_applicable")
        }
    }

    private data class SelectedProviderFixture(
        val weather: WeatherCapability,
        val selectedProvider: CountingProvider,
        val otherProvider: CountingProvider,
    )

    private class CountingProvider(
        override val providerId: WeatherProviderId,
        private val data: ProviderWeatherData,
    ) : WeatherProvider {
        var calls: Int = 0
            private set

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            calls += 1
            return WeatherProviderResult(
                payload = RedactedProviderPayload(data.current.temperatureCelsius, data.current.condition.orEmpty()),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                weatherData = data,
                provider = providerId,
            )
        }
    }
