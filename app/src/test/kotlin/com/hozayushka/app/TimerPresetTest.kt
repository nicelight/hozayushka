package com.hozayushka.app

import com.hozayushka.app.display.PresetPresentation
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.TimerPresetDuration
import com.hozayushka.app.settings.TimerPresetSlot
import com.hozayushka.app.settings.TimerPresetValidationError
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerLifecycleState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerPresetTest {
    @Test
    fun defaultsAreThreeIndependentPresetsAndReloadFromOwnerStore() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)

        assertEquals(3, settings.timerPresetProjection().presets.size)
        assertEquals(TimerPresetDuration(0, 3, 0), settings.timerPresetProjection().preset(TimerPresetSlot.FIRST).duration)
        assertEquals(TimerPresetDuration(0, 10, 0), settings.timerPresetProjection().preset(TimerPresetSlot.SECOND).duration)
        assertEquals(TimerPresetDuration(0, 30, 0), settings.timerPresetProjection().preset(TimerPresetSlot.THIRD).duration)

        assertTrue(settings.updateTimerPreset(TimerPresetSlot.SECOND, 1, 2, 3).accepted)
        settings.saveFoundationLocation(LocationContext("Test city", 40.0, 69.0, "UTC"))
        val reloaded = SettingsCapability(store)
        assertEquals(TimerPresetDuration(1, 2, 3), reloaded.timerPresetProjection().preset(TimerPresetSlot.SECOND).duration)
        assertEquals(TimerPresetDuration(0, 3, 0), reloaded.timerPresetProjection().preset(TimerPresetSlot.FIRST).duration)
        assertEquals(TimerPresetDuration(0, 30, 0), reloaded.timerPresetProjection().preset(TimerPresetSlot.THIRD).duration)
    }

    @Test
    fun everyFieldBoundaryPositiveTotalAndLastValidValueAreEnforced() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val slot = TimerPresetSlot.FIRST

        assertTrue(settings.updateTimerPreset(slot, 99, 59, 59).accepted)
        assertEquals(
            TimerPresetDuration(99, 59, 59),
            settings.timerPresetProjection().preset(slot).duration,
        )
        val invalid = listOf(
            Triple(100, 0, 0) to TimerPresetValidationError.HOURS_OUT_OF_RANGE,
            Triple(0, 60, 0) to TimerPresetValidationError.MINUTES_OUT_OF_RANGE,
            Triple(0, 0, 60) to TimerPresetValidationError.SECONDS_OUT_OF_RANGE,
            Triple(0, 0, 0) to TimerPresetValidationError.ZERO_TOTAL,
        )
        invalid.forEach { (input, expectedError) ->
            val result = settings.updateTimerPreset(slot, input.first, input.second, input.third)
            assertFalse(result.accepted)
            assertEquals(expectedError, result.error)
            assertEquals(TimerPresetDuration(99, 59, 59), result.duration)
            assertEquals(TimerPresetDuration(99, 59, 59), settings.timerPresetProjection().preset(slot).duration)
        }
    }

    @Test
    fun rejectedEditorUpdateRestoresLastValidDurationValues() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val slot = TimerPresetSlot.SECOND

        assertTrue(settings.updateTimerPreset(slot, 2, 4, 6).accepted)
        val rejected = settings.updateTimerPreset(slot, 2, 60, 6)

        assertFalse(rejected.accepted)
        assertEquals(listOf("2", "4", "6"), rejected.duration.editorFieldValues())
        assertEquals(
            TimerPresetDuration(2, 4, 6),
            settings.timerPresetProjection().preset(slot).duration,
        )
    }

    @Test
    fun labelsUseHighestNonZeroUnitWithFloorRounding() {
        assertEquals("1 ч", PresetPresentation.label(TimerPresetDuration(1, 59, 59)))
        assertEquals("59 м", PresetPresentation.label(TimerPresetDuration(0, 59, 59)))
        assertEquals("59 с", PresetPresentation.label(TimerPresetDuration(0, 0, 59)))
        assertEquals("3 м", PresetPresentation.label(TimerPresetDuration(0, 3, 0)))
    }

    @Test
    fun fixedColorsAndActiveProjectionUseTimerOwnerWithoutParallelState() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val timer = TimerCapability(
            stateStore = InMemoryTimerStateStore(),
            presetReader = settings,
        )

        val styles = PresetPresentation.styles(timer.presetPresentationAt(1_000L))
        assertEquals(listOf("#FF7A00", "#FF4FA3", "#A855F7"), styles.map { it.outlineHex })
        assertEquals(listOf("3 м", "10 м", "30 м"), styles.map { it.label })
        assertTrue(styles.none { it.isActive })

        timer.startPreset(TimerPresetSlot.FIRST, 1_000L)
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(1_000L).state)
        assertEquals(TimerPresetSlot.FIRST, timer.snapshotAt(1_000L).activePresetSlot)
        assertTrue(timer.presetPresentationAt(1_000L).first { it.slot == TimerPresetSlot.FIRST }.isActive)

        assertTrue(settings.updateTimerPreset(TimerPresetSlot.SECOND, 0, 20, 0).accepted)
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(2_000L).state)
        assertEquals(TimerPresetSlot.FIRST, timer.snapshotAt(2_000L).activePresetSlot)

        timer.startPreset(TimerPresetSlot.THIRD, 3_000L)
        val active = timer.presetPresentationAt(3_000L).filter { it.isActive }
        assertEquals(listOf(TimerPresetSlot.THIRD), active.map { it.slot })
    }
}
