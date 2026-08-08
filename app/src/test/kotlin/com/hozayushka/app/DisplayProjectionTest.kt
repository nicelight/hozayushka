package com.hozayushka.app

import android.view.MotionEvent
import com.hozayushka.app.display.ActiveTimerCityTouchStream
import com.hozayushka.app.display.CityGesture
import com.hozayushka.app.display.CityInteraction
import com.hozayushka.app.display.CityInteractionRouter
import com.hozayushka.app.display.ColonMode
import com.hozayushka.app.display.ColonProjection
import com.hozayushka.app.display.DisplayConnectivity
import com.hozayushka.app.display.DisplayFormatters
import com.hozayushka.app.display.DisplayLayoutSpec
import com.hozayushka.app.display.forecastEntryIntent
import com.hozayushka.app.forecast.ForecastEntryIntent
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import com.hozayushka.app.weather.WeatherCardSlot
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayProjectionTest {
    @Test
    fun deviceTimezoneDrivesClockAndRussianGenitiveDate() {
        val instant = Instant.parse("2024-07-31T22:30:00Z").toEpochMilli()

        assertEquals("03:30", DisplayFormatters.timeText(instant, ZoneId.of("Asia/Dushanbe")))
        assertEquals("01 августа", DisplayFormatters.dateText(instant, ZoneId.of("Asia/Dushanbe")))
        assertEquals("31 июля", DisplayFormatters.dateText(instant, ZoneId.of("America/New_York")))
    }

    @Test
    fun stableShellReservesHeaderContentBeforeFourCardsAndThreePresets() {
        val emptyWeather = DisplayLayoutSpec()
        val projectedWeather = DisplayLayoutSpec(
            weatherCardCount = 4,
            presetCount = 3,
            headerWeight = 0f,
            weatherRowWeight = 1f,
        )

        assertEquals(4, emptyWeather.weatherCardCount)
        assertEquals(3, emptyWeather.presetCount)
        assertEquals(0f, emptyWeather.headerWeight, 0f)
        assertEquals(1f, emptyWeather.weatherRowWeight, 0f)
        assertTrue(emptyWeather.weatherRowWeight > emptyWeather.headerWeight)
        assertEquals(emptyWeather, projectedWeather)
    }

    @Test
    fun colonModesUseAcceptedConnectivityAndCountdownValues() {
        assertEquals(ColonMode.OFFLINE_FIXED, ColonProjection.mode(DisplayConnectivity.OFFLINE, TimerLifecycleState.IDLE))
        assertEquals(0.38f, ColonProjection.brightness(ColonMode.OFFLINE_FIXED, 4_000L), 0.001f)
        assertEquals(ColonMode.COUNTDOWN_BLINK, ColonProjection.mode(DisplayConnectivity.ONLINE, TimerLifecycleState.COUNTDOWN))
        assertEquals(1f, ColonProjection.brightness(ColonMode.COUNTDOWN_BLINK, 381L), 0.001f)
        assertEquals(0f, ColonProjection.brightness(ColonMode.COUNTDOWN_BLINK, 382L), 0.001f)
        assertEquals(1f, ColonProjection.brightness(ColonMode.ONLINE_PULSE, 3_000L), 0.001f)
        assertEquals(0.02f, ColonProjection.brightness(ColonMode.ONLINE_PULSE, 5_999L), 0.002f)
    }

    @Test
    fun cityGesturesRespectEmptyAndSelectedCityRules() {
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(false, CityGesture.SHORT_TAP))
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(false, CityGesture.LONG_HOLD))
        assertEquals(CityInteraction.NO_OP, CityInteractionRouter.route(true, CityGesture.SHORT_TAP))
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(true, CityGesture.LONG_HOLD))
        assertTrue(CityInteractionRouter.route(true, CityGesture.LONG_HOLD) != CityInteraction.NO_OP)
    }

    @Test
    fun activeCountdownKeepsCityHoldAlongsideProtectedTimerTaps() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        timer.start(100_000L, 60_000L)

        val singleTap = timer.handleGesture(101_000L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, singleTap.snapshot.state)
        assertTrue(singleTap.singleTapHintVisible)
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(true, CityGesture.LONG_HOLD))
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(101_500L).state)

        val doubleTap = timer.handleGesture(102_000L, TimerGesture.DOUBLE_TAP)
        assertEquals(TimerLifecycleState.IDLE, doubleTap.snapshot.state)
        assertTrue(doubleTap.dismissed)
    }

    @Test
    fun cityDoubleTapCannotLeaveDelayedSettingsAfterLongPressTimeout() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        val stream = ActiveTimerCityTouchStream()
        timer.start(100_000L, 60_000L)
        var pendingLongPressAt: Long? = null

        fun dispatch(action: Int, atMillis: Long): Boolean {
            val timerActive = timer.snapshotAt(atMillis).state != TimerLifecycleState.IDLE
            val dispatched = stream.shouldDispatch(action, timerActive)
            if (dispatched) {
                when (action) {
                    MotionEvent.ACTION_DOWN -> pendingLongPressAt = atMillis + 600L
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> pendingLongPressAt = null
                }
            }
            return dispatched
        }

        assertTrue(dispatch(MotionEvent.ACTION_DOWN, 101_000L))
        assertTrue(dispatch(MotionEvent.ACTION_UP, 101_050L))
        assertTrue(dispatch(MotionEvent.ACTION_DOWN, 101_150L))
        assertEquals(
            TimerLifecycleState.IDLE,
            timer.handleGesture(101_150L, TimerGesture.DOUBLE_TAP).snapshot.state,
        )
        assertTrue(dispatch(MotionEvent.ACTION_UP, 101_200L))

        val beyondLongPressTimeout = 101_900L
        assertTrue(pendingLongPressAt == null || pendingLongPressAt!! > beyondLongPressTimeout)
        assertTrue(!stream.shouldDispatch(MotionEvent.ACTION_MOVE, timerActive = false))
    }

    @Test
    fun tomorrowAndDayAfterUseTheSameLongTermForecastIntent() {
        assertEquals(ForecastEntryIntent.HOURLY, forecastEntryIntent(WeatherCardSlot.TODAY))
        assertEquals(ForecastEntryIntent.LONG_TERM, forecastEntryIntent(WeatherCardSlot.TOMORROW))
        assertEquals(ForecastEntryIntent.LONG_TERM, forecastEntryIntent(WeatherCardSlot.DAY_AFTER))
    }
}
