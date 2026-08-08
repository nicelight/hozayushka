package com.hozayushka.app

import com.hozayushka.app.display.DisplayFormatters
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.TimerPresetSlot
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerLifecycleTest {
    @Test
    fun selectedPresetStartsImmediatelyProjectsCountdownAndHighlightsOrigin() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val timer = TimerCapability(InMemoryTimerStateStore(), presetReader = settings)
        val startedAt = 1_700_000_000_000L

        timer.startPreset(TimerPresetSlot.SECOND, startedAt)

        val snapshot = timer.snapshotAt(startedAt + 1_000L)
        assertEquals(TimerLifecycleState.COUNTDOWN, snapshot.state)
        assertEquals(599_000L, snapshot.remainingMillis)
        assertEquals(TimerPresetSlot.SECOND, snapshot.activePresetSlot)
        assertEquals(
            listOf(TimerPresetSlot.SECOND),
            timer.presetPresentationAt(startedAt + 1_000L).filter { it.isActive }.map { it.slot },
        )
        assertEquals("09:59", DisplayFormatters.countdownText(snapshot.remainingMillis))
    }

    @Test
    fun startingAnotherValidatedPresetReplacesTheSingleActiveRecord() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val timer = TimerCapability(InMemoryTimerStateStore(), presetReader = settings)

        timer.startPreset(TimerPresetSlot.FIRST, 10_000L)
        timer.startPreset(TimerPresetSlot.THIRD, 20_000L)

        val snapshot = timer.snapshotAt(20_000L)
        assertEquals(TimerLifecycleState.COUNTDOWN, snapshot.state)
        assertEquals(TimerPresetSlot.THIRD, snapshot.activePresetSlot)
        assertEquals(1, timer.presetPresentationAt(20_000L).count { it.isActive })
    }

    @Test
    fun singleTapPreservesCountdownAndDoubleTapCancels() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        timer.start(100_000L, 60_000L)

        val single = timer.handleGesture(101_000L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, single.snapshot.state)
        assertTrue(single.singleTapHintVisible)

        val double = timer.handleGesture(102_000L, TimerGesture.DOUBLE_TAP)
        assertEquals(TimerLifecycleState.IDLE, double.snapshot.state)
        assertTrue(double.dismissed)
        assertFalse(timer.snapshotAt(102_000L).state == TimerLifecycleState.COUNTDOWN)
    }

    @Test
    fun persistedStartAndDurationRehydrateCountdownOrOverdueAfterTemporaryInterruption() {
        val store = InMemoryTimerStateStore()
        val startedAt = 500_000L
        TimerCapability(store).start(startedAt, 10_000L)

        val resumed = TimerCapability(store)
        assertEquals(TimerLifecycleState.COUNTDOWN, resumed.rehydrateAt(startedAt + 9_000L).state)
        assertEquals(1_000L, resumed.rehydrateAt(startedAt + 9_000L).remainingMillis)
        assertEquals(TimerLifecycleState.OVERDUE, resumed.rehydrateAt(startedAt + 10_001L).state)
        assertEquals(10_001L, resumed.rehydrateAt(startedAt + 10_001L).elapsedMillis)
    }

    @Test
    fun noProviderInputDoesNotAffectTimerAndAnyTapDismissesOverdue() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        timer.start(1_000L, 1_000L)

        val overdue = timer.rehydrateAt(2_000L)
        assertEquals(TimerLifecycleState.OVERDUE, overdue.state)
        val dismissed = timer.handleGesture(2_000L, TimerGesture.SINGLE_TAP)

        assertEquals(TimerLifecycleState.IDLE, dismissed.snapshot.state)
        assertTrue(dismissed.dismissed)
    }
}
