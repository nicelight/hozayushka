package com.hozayushka.app

import android.view.Window
import com.hozayushka.app.adapters.platform.AlertAudioRequest
import com.hozayushka.app.adapters.platform.AudioProbeResult
import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.display.DisplayFormatters
import com.hozayushka.app.display.OverduePresentation
import com.hozayushka.app.display.PresetPresentation
import com.hozayushka.app.settings.BuiltInAlertSignal
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.SettingsState
import com.hozayushka.app.settings.TimerAlertSettingsProjection
import com.hozayushka.app.settings.TimerPresetSlot
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerAlertPolicy
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OverdueAlertTest {
    @Test
    fun overdueProjectionUsesActivePresetColorBlinkSplitAndFullElapsedCounter() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        val timer = TimerCapability(InMemoryTimerStateStore(), presetReader = settings)
        val startedAt = 1_000L
        timer.startPreset(TimerPresetSlot.SECOND, startedAt)

        val snapshot = timer.snapshotAt(startedAt + 10L * 60L * 1_000L + 1_234L)

        assertEquals(TimerLifecycleState.OVERDUE, snapshot.state)
        assertEquals(601_234L, snapshot.elapsedMillis)
        assertEquals(1_234L, snapshot.overdueElapsedMillis)
        assertEquals("00:10:01", DisplayFormatters.elapsedText(snapshot.elapsedMillis))
        assertEquals("#FF4FA3", PresetPresentation.colorHex(TimerPresetSlot.SECOND))
        assertTrue(OverduePresentation.plusVisibleAt(0L))
        assertFalse(OverduePresentation.plusVisibleAt(382L))
        assertTrue(OverduePresentation.plusVisibleAt(764L))

        assertTrue(timer.advanceAt(startedAt + 601_234L).visualOverdue)
        val dismissed = timer.handleGesture(startedAt + 601_234L, TimerGesture.SINGLE_TAP)
        assertTrue(dismissed.dismissed)
        assertEquals(TimerLifecycleState.IDLE, dismissed.snapshot.state)
    }

    @Test
    fun selectedAndDefaultSignalsRampRepeatUntilDismissalAndStopAtAudioCap() {
        BuiltInAlertSignal.entries.forEach { signal ->
            val request = TimerAlertPolicy.requestAt(
                overdueElapsedMillis = 0L,
                settings = TimerAlertSettingsProjection(signal = signal),
            )
            assertNotNull(request)
            assertEquals(signal.id, request!!.signalId)
        }
        assertEquals("classic", TimerAlertPolicy.requestAt(0L, TimerAlertSettingsProjection())!!.signalId)
        assertEquals(10, TimerAlertPolicy.requestAt(0L, TimerAlertSettingsProjection())!!.rampPercent)
        assertEquals(100, TimerAlertPolicy.requestAt(TimerAlertPolicy.RAMP_DURATION_MILLIS, TimerAlertSettingsProjection())!!.rampPercent)
        assertTrue(TimerAlertPolicy.requestAt(TimerAlertPolicy.AUDIO_CAP_MILLIS - 1L, TimerAlertSettingsProjection()) != null)
        assertEquals(null, TimerAlertPolicy.requestAt(TimerAlertPolicy.AUDIO_CAP_MILLIS, TimerAlertSettingsProjection()))

        val platform = RecordingPlatform()
        val settingsStore = InMemorySettingsStateStore().also {
            it.save(SettingsState(timerAlert = TimerAlertSettingsProjection(BuiltInAlertSignal.ELECTRONIC, 70)))
        }
        val settings = SettingsCapability(settingsStore)
        val timer = TimerCapability(
            stateStore = InMemoryTimerStateStore(),
            platform = platform,
            presetReader = settings,
            alertSettingsReader = settings,
        )
        val startedAt = 1_000L
        timer.startPreset(TimerPresetSlot.FIRST, startedAt)
        val overdueAt = startedAt + 3L * 60L * 1_000L

        val first = timer.advanceAt(overdueAt)
        assertEquals("electronic", first.audioRequest!!.signalId)
        assertEquals(10, first.audioRequest.rampPercent)
        assertEquals(1, platform.requests.size)
        assertTrue(timer.advanceAt(overdueAt + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS - 1L).audioRequest == null)
        val repeated = timer.advanceAt(overdueAt + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS)
        assertEquals(100, repeated.audioRequest!!.rampPercent)
        assertEquals(2, platform.requests.size)

        val capped = timer.advanceAt(overdueAt + TimerAlertPolicy.AUDIO_CAP_MILLIS)
        assertTrue(capped.visualOverdue)
        assertTrue(capped.audioStopped)
        assertEquals(1, platform.stopCalls)
        assertEquals(2, platform.requests.size)
    }

    @Test
    fun silentDndAndUnavailableRouteSuppressAudioOnlyAndAnyTapStopsAlert() {
        listOf("silent", "dnd", "route").forEach { reason ->
            val platform = RecordingPlatform(suppressionReason = reason)
            val timer = TimerCapability(InMemoryTimerStateStore(), platform = platform)
            timer.start(1_000L, 1_000L)

            val decision = timer.advanceAt(2_000L)

            assertTrue(decision.visualOverdue)
            assertFalse(decision.audioResult!!.permitted)
            assertEquals(reason, decision.audioResult.reason)
            val dismissed = timer.handleGesture(2_000L, TimerGesture.DOUBLE_TAP)
            assertTrue(dismissed.dismissed)
            assertEquals(TimerLifecycleState.IDLE, dismissed.snapshot.state)
            assertEquals(1, platform.stopCalls)
        }
    }

    @Test
    fun persistedOverdueTimerReestablishesVisualAndPermittedAlertPathAfterResume() {
        val store = InMemoryTimerStateStore()
        val first = TimerCapability(store)
        first.start(5_000L, 10_000L)

        val platform = RecordingPlatform()
        val resumed = TimerCapability(store, platform = platform)
        val snapshot = resumed.rehydrateAt(15_001L)
        val decision = resumed.advanceAt(15_001L)

        assertEquals(TimerLifecycleState.OVERDUE, snapshot.state)
        assertEquals(10_001L, snapshot.elapsedMillis)
        assertTrue(decision.visualOverdue)
        assertEquals(1, platform.requests.size)
    }

    @Test
    fun sameRuntimeTemporaryResumeReRequestsReleasedAlertBeforeNormalRepeatInterval() {
        val platform = RecordingPlatform()
        val timer = TimerCapability(InMemoryTimerStateStore(), platform = platform)
        val overdueAt = 2_000L
        timer.start(1_000L, 1_000L)

        timer.advanceAt(overdueAt)
        assertEquals(1, platform.requests.size)
        assertTrue(platform.audioActive)

        platform.onActivityPaused()
        assertFalse(platform.audioActive)
        platform.onActivityResumed()

        val resumed = timer.rehydrateAt(overdueAt + 1L)

        assertEquals(TimerLifecycleState.OVERDUE, resumed.state)
        assertEquals(2, platform.requests.size)
        assertEquals(1L, platform.requests.last().overdueElapsedMillis)
        assertTrue(platform.audioActive)
        assertTrue(
            timer.advanceAt(overdueAt + 1L + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS - 1L)
                .audioRequest == null,
        )
        assertEquals(2, platform.requests.size)
    }

    private class RecordingPlatform(
        private val suppressionReason: String? = null,
    ) : PlatformRuntime {
        val requests = mutableListOf<AlertAudioRequest>()
        var stopCalls = 0
        var audioActive = false

        override fun nowMillis(): Long = 0L
        override fun deviceTimeText(nowMillis: Long): String = "00:00"
        override fun deviceZoneId(): ZoneId = ZoneId.of("UTC")
        override fun isNetworkAvailable(): Boolean = false
        override fun applyFoundationWindow(window: Window) = Unit
        override fun onActivityPaused() {
            audioActive = false
        }

        override fun onActivityResumed() = Unit
        override fun requestAlertAudio(): AudioProbeResult = AudioProbeResult(false, false, "not_applicable")
        override fun requestAlertAudio(request: AlertAudioRequest): AudioProbeResult {
            requests += request
            audioActive = suppressionReason == null
            return if (suppressionReason == null) {
                AudioProbeResult(
                    requested = true,
                    permitted = true,
                    reason = "accepted",
                    signalId = request.signalId,
                    volumePercent = request.volumePercent,
                    rampPercent = request.rampPercent,
                    overdueElapsedMillis = request.overdueElapsedMillis,
                )
            } else {
                AudioProbeResult(
                    requested = false,
                    permitted = false,
                    reason = suppressionReason,
                    signalId = request.signalId,
                    volumePercent = request.volumePercent,
                    rampPercent = request.rampPercent,
                    overdueElapsedMillis = request.overdueElapsedMillis,
                )
            }
        }

        override fun stopAlertAudio() {
            stopCalls += 1
            audioActive = false
        }
    }
}
