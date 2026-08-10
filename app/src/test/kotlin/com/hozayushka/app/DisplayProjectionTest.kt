package com.hozayushka.app

import android.view.MotionEvent
import com.hozayushka.app.display.ActiveCountdownTouchDispatcher
import com.hozayushka.app.display.CityGesture
import com.hozayushka.app.display.CityInteraction
import com.hozayushka.app.display.CityInteractionRouter
import com.hozayushka.app.display.ColonMode
import com.hozayushka.app.display.ColonProjection
import com.hozayushka.app.display.DisplayConnectivity
import com.hozayushka.app.display.DisplayFormatters
import com.hozayushka.app.display.DisplayLayoutSpec
import com.hozayushka.app.display.MainDisplayTickerOwner
import com.hozayushka.app.display.MainDisplayTickerScheduler
import com.hozayushka.app.display.MainDisplayWeatherCardRenderer
import com.hozayushka.app.display.MainDisplayWeatherRenderInput
import com.hozayushka.app.display.forecastEntryIntent
import com.hozayushka.app.forecast.ForecastEntryIntent
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import com.hozayushka.app.weather.WeatherCardSlot
import com.hozayushka.app.weather.WeatherFreshness
import com.hozayushka.app.weather.WeatherCardProjection
import com.hozayushka.app.weather.WeatherProjection
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
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
    fun activeCountdownDispatcherKeepsEveryCapturedSurfaceStreamToTerminalEvent() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        val dispatcher = ActiveCountdownTouchDispatcher()
        timer.start(100_000L, 60_000L)
        var pendingLongPressAt: Long? = null
        val delivered = mutableListOf<String>()

        fun dispatch(surface: String, action: Int, atMillis: Long): Boolean {
            val timerActiveAtDown = action == MotionEvent.ACTION_DOWN &&
                timer.snapshotAt(atMillis).state == TimerLifecycleState.COUNTDOWN
            val dispatched = dispatcher.shouldDispatch(action, timerActiveAtDown)
            if (dispatched) {
                delivered += "$surface:$action"
                when (action) {
                    MotionEvent.ACTION_DOWN -> if (surface == "city") {
                        pendingLongPressAt = atMillis + 600L
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> pendingLongPressAt = null
                }
            }
            return dispatched
        }

        assertTrue(dispatch("weather", MotionEvent.ACTION_DOWN, 101_000L))
        assertTrue(dispatch("weather", MotionEvent.ACTION_UP, 101_050L))
        val singleTap = timer.handleGesture(101_050L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, singleTap.snapshot.state)
        assertTrue(singleTap.singleTapHintVisible)

        assertTrue(dispatch("weather", MotionEvent.ACTION_DOWN, 101_150L))
        assertEquals(
            TimerLifecycleState.IDLE,
            timer.handleGesture(101_150L, TimerGesture.DOUBLE_TAP).snapshot.state,
        )
        assertTrue(dispatch("weather", MotionEvent.ACTION_UP, 101_200L))

        timer.start(102_000L, 60_000L)
        assertTrue(dispatch("city", MotionEvent.ACTION_DOWN, 102_000L))
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(true, CityGesture.LONG_HOLD))
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(102_700L).state)
        assertTrue(dispatch("city", MotionEvent.ACTION_UP, 102_800L))

        timer.start(103_000L, 60_000L)
        assertTrue(dispatch("city", MotionEvent.ACTION_DOWN, 103_000L))
        assertEquals(
            TimerLifecycleState.IDLE,
            timer.handleGesture(103_000L, TimerGesture.DOUBLE_TAP).snapshot.state,
        )
        assertTrue(dispatch("city", MotionEvent.ACTION_UP, 103_050L))

        timer.start(104_000L, 60_000L)
        assertTrue(dispatch("preset", MotionEvent.ACTION_DOWN, 104_000L))
        val presetSingleTap = timer.handleGesture(104_050L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, presetSingleTap.snapshot.state)
        assertTrue(presetSingleTap.singleTapHintVisible)
        assertTrue(dispatch("preset", MotionEvent.ACTION_UP, 104_100L))

        val beyondLongPressTimeout = 103_700L
        assertTrue(pendingLongPressAt == null || pendingLongPressAt!! > beyondLongPressTimeout)
        assertTrue(delivered.contains("weather:${MotionEvent.ACTION_UP}"))
        assertTrue(delivered.contains("city:${MotionEvent.ACTION_UP}"))
        assertTrue(delivered.contains("preset:${MotionEvent.ACTION_UP}"))
        assertTrue(!dispatcher.shouldDispatch(MotionEvent.ACTION_MOVE, timerActive = false))
    }

    @Test
    fun mainDisplayTickerCoalescesLifecycleStartsAndStopsWhilePausedOrDetached() {
        val scheduler = FakeMainDisplayTickerScheduler()
        var callbacks = 0
        val ticker = MainDisplayTickerOwner(scheduler, onTick = { callbacks++ })

        ticker.onActivityResumed()
        ticker.onViewAttachedToWindow()
        ticker.onViewAttachedToWindow()
        ticker.onActivityResumed()
        assertEquals(1, scheduler.pendingCount())

        scheduler.runNext()
        assertEquals(1, callbacks)
        assertEquals(1, scheduler.pendingCount())
        assertEquals(listOf(50L), scheduler.pendingDelays())

        ticker.onActivityPaused()
        ticker.onActivityPaused()
        assertEquals(0, scheduler.pendingCount())
        scheduler.runAll()
        assertEquals(1, callbacks)

        ticker.onViewDetachedFromWindow()
        ticker.onViewAttachedToWindow()
        assertEquals(0, scheduler.pendingCount())

        ticker.onActivityResumed()
        ticker.onActivityResumed()
        assertEquals(1, scheduler.pendingCount())
        scheduler.runNext()
        assertEquals(2, callbacks)
        assertEquals(1, scheduler.pendingCount())

        ticker.onViewDetachedFromWindow()
        assertEquals(0, scheduler.pendingCount())
        scheduler.runAll()
        assertEquals(2, callbacks)
        scheduler.reset()
    }

    @Test
    fun unchangedWeatherProjectionKeepsFourCardTreeAndChangedInputRebindsOnce() {
        val renderer = MainDisplayWeatherCardRenderer()
        var cardTree = List(4) { Any() }
        var rebinds = 0
        val initial = MainDisplayWeatherRenderInput(weatherProjection(), 0.45f)

        assertTrue(renderer.renderIfChanged(initial) {
            rebinds++
            cardTree = List(4) { Any() }
        })
        val initialTree = cardTree
        assertEquals(4, initialTree.size)
        assertEquals(1, rebinds)

        assertFalse(renderer.renderIfChanged(initial) {
            rebinds++
            cardTree = List(4) { Any() }
        })
        assertSame(initialTree, cardTree)
        assertEquals(1, rebinds)

        val changed = initial.copy(projection = weatherProjection(cityLabel = "changed"))
        assertTrue(renderer.renderIfChanged(changed) {
            rebinds++
            cardTree = List(4) { Any() }
        })
        assertEquals(4, cardTree.size)
        assertTrue(initialTree !== cardTree)
        assertEquals(2, rebinds)

        assertFalse(renderer.renderIfChanged(changed) { rebinds++ })
        assertEquals(2, rebinds)
    }

    @Test
    fun tomorrowAndDayAfterUseTheSameLongTermForecastIntent() {
        assertEquals(ForecastEntryIntent.HOURLY, forecastEntryIntent(WeatherCardSlot.TODAY))
        assertEquals(ForecastEntryIntent.LONG_TERM, forecastEntryIntent(WeatherCardSlot.TOMORROW))
        assertEquals(ForecastEntryIntent.LONG_TERM, forecastEntryIntent(WeatherCardSlot.DAY_AFTER))
    }

    private fun weatherProjection(cityLabel: String? = "Khujand"): WeatherProjection =
        WeatherProjection(
            cityLabel = cityLabel,
            apiTimeZone = "Asia/Dushanbe",
            freshness = WeatherFreshness.NO_DATA,
            cards = WeatherCardSlot.entries.mapIndexed { index, slot ->
                WeatherCardProjection(
                    slot = slot,
                    date = LocalDate.of(2024, 1, index + 1),
                    temperatureCelsius = null,
                    temperatureText = null,
                    backgroundHex = null,
                    illustration = null,
                    moonPhase = null,
                    pressureArrowCount = 0,
                    pressureDirection = null,
                    isTodaySize = slot == WeatherCardSlot.TODAY,
                )
            },
        )

    private class FakeMainDisplayTickerScheduler : MainDisplayTickerScheduler {
        private data class Pending(val runnable: Runnable, val delayMillis: Long)

        private val pending = ArrayDeque<Pending>()

        override fun post(runnable: Runnable) {
            pending.addLast(Pending(runnable, 0L))
        }

        override fun postDelayed(runnable: Runnable, delayMillis: Long) {
            pending.addLast(Pending(runnable, delayMillis))
        }

        override fun removeCallbacks(runnable: Runnable) {
            pending.removeAll { it.runnable === runnable }
        }

        fun pendingCount(): Int = pending.size

        fun pendingDelays(): List<Long> = pending.map { it.delayMillis }

        fun runNext() {
            pending.removeFirst().runnable.run()
        }

        fun runAll() {
            while (pending.isNotEmpty()) runNext()
        }

        fun reset() {
            pending.clear()
        }
    }
}
