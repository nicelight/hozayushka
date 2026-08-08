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
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.forecast.ForecastSessionCapability
import com.hozayushka.app.forecast.ForecastSessionState
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
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
        assertTrue(weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), now) != null)

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
        weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), now)
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
        assertNull(weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), now))

        val session = ForecastSessionCapability(weather, FakePlatform(now))
        val rejected = session.openHourly(now)

        assertEquals(ForecastSessionState.CLOSED, rejected.state)
        assertEquals(ForecastSessionCapability.HOURLY_UNAVAILABLE_MESSAGE, rejected.message)
        assertTrue(rejected.rows.isEmpty())
    }

    @Test
    fun sharedSessionTimingAndGesturesFollowAcceptedTransitions() {
        val weather = weatherWith(completeHourlyData())
        weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), now)
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
        weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), now)
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
        val weather = WeatherCapability(settings, store, provider)

        assertTrue(weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), cityDayStart) != null)
        val beforeReload = requireNotNull(weather.longTermProjection(cityDayStart))
        val reloadedWeather = WeatherCapability(settings, store, provider)
        val afterReload = requireNotNull(reloadedWeather.longTermProjection(cityDayStart))

        assertEquals(beforeReload, afterReload)
        assertEquals((0L..9L).map { LocalDate.of(2024, 1, 2).plusDays(it) }, afterReload.cards.map { it.date })
        assertEquals(listOf(5, 5), afterReload.rows.map { it.size })
        assertEquals("02", afterReload.cards.first().dateDayText)
        assertTrue(afterReload.cards.all {
            it.temperatureText.isNotBlank() &&
                it.backgroundHex.startsWith("#") &&
                WeatherCardPresentation.illustrationText(it.illustration).isNotBlank() &&
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
        val weather = WeatherCapability(settings, store, SingleProvider(complete))
        weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), cityDayStart)

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
        val failedRefreshWeather = WeatherCapability(settings, store, SingleProvider(incomplete))
        assertNull(failedRefreshWeather.refresh(WeatherProviderRequest.fromSyntheticProbe(), cityDayStart))
        assertEquals(day, requireNotNull(failedRefreshWeather.longTermProjection(cityDayStart)))

        val brokenWeather = WeatherCapability(settings, InMemoryWeatherCacheStore(), SingleProvider(incomplete))
        assertNull(brokenWeather.refresh(WeatherProviderRequest.fromSyntheticProbe(), cityDayStart))
        val rejected = ForecastSessionCapability(brokenWeather, FakePlatform(cityDayStart)).openLongTerm(cityDayStart)
        assertEquals(ForecastSessionState.CLOSED, rejected.state)
        assertEquals(ForecastSessionCapability.LONG_TERM_UNAVAILABLE_MESSAGE, rejected.message)
        assertTrue(rejected.longTermRows.isEmpty())
    }

    @Test
    fun longTermSessionUsesSharedTimingAndGestureContract() {
        val weather = weatherWith(completeLongTermData())
        weather.refresh(WeatherProviderRequest.fromSyntheticProbe(), cityDayStart)
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

    private fun weatherWith(data: ProviderWeatherData): WeatherCapability {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "Asia/Dushanbe"))
        }
        return WeatherCapability(settings, InMemoryWeatherCacheStore(), SingleProvider(data))
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
        val today = LocalDate.of(2024, 1, 2)
        return ProviderWeatherData(
            apiTimeZone = "Asia/Dushanbe",
            current = ProviderCurrentWeather(10, 100.0, "cloud"),
            daily = (0L..9L).map { offset ->
                ProviderDailyWeather(
                    date = today.plusDays(offset),
                    dayTemperatureCelsius = 10 + offset.toInt(),
                    nightTemperatureCelsius = 20 + offset.toInt(),
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
