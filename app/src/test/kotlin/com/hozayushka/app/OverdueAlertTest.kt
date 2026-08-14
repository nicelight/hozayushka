package com.hozayushka.app

import android.view.Window
import com.hozayushka.app.adapters.platform.AlertAudioRequest
import com.hozayushka.app.adapters.platform.AudioProbeResult
import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.display.DisplayFormatters
import com.hozayushka.app.display.MainDisplayTickerOwner
import com.hozayushka.app.display.MainDisplayTickerScheduler
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
import com.hozayushka.app.timer.TimerAlertDecision
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import java.time.ZoneId
import java.util.PriorityQueue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OverdueAlertTest {
    @Test
    fun overdueDisplayTicksEmitStartRepeatStopAndThirtyMinuteCap() {
        val scheduler = DeterministicDisplayTickScheduler()
        val platform = RecordingPlatform()
        val timer = TimerCapability(InMemoryTimerStateStore(), platform = platform)
        timer.start(startedAtMillis = 0L, durationMillis = 1_000L)
        val ticker = tickerFor(timer, scheduler)

        scheduler.advanceTo(999L)
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(scheduler.nowMillis).state)
        scheduler.advanceTo(1_000L)

        assertEquals(1, platform.requests.size)
        assertEquals(1, platform.startCalls)
        assertTrue(platform.audioActive)
        assertEquals("classic", platform.requests.first().signalId)
        assertEquals(10, platform.requests.first().rampPercent)
        assertEquals(0L, platform.requests.first().overdueElapsedMillis)
        assertEquals(TimerLifecycleState.OVERDUE, timer.snapshotAt(scheduler.nowMillis).state)

        scheduler.advanceTo(1_000L + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS - 1L)
        assertEquals(1, platform.requests.size)
        scheduler.advanceTo(1_000L + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS)
        assertEquals(2, platform.requests.size)
        assertEquals(100, platform.requests.last().rampPercent)

        val dismissed = timer.handleGesture(scheduler.nowMillis, TimerGesture.SINGLE_TAP)
        assertTrue(dismissed.dismissed)
        assertEquals(TimerLifecycleState.IDLE, dismissed.snapshot.state)
        assertFalse(platform.audioActive)
        val requestsAtDismissal = platform.requests.size
        scheduler.advanceTo(scheduler.nowMillis + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS)
        assertEquals(requestsAtDismissal, platform.requests.size)
        ticker.dispose()

        val capScheduler = DeterministicDisplayTickScheduler()
        val capPlatform = RecordingPlatform()
        val cappedTimer = TimerCapability(InMemoryTimerStateStore(), platform = capPlatform)
        cappedTimer.start(startedAtMillis = 0L, durationMillis = 1_000L)
        val capTicker = tickerFor(cappedTimer, capScheduler)
        val capAt = 1_000L + TimerAlertPolicy.AUDIO_CAP_MILLIS
        capScheduler.advanceTo(capAt)

        assertEquals(TimerLifecycleState.OVERDUE, cappedTimer.snapshotAt(capAt).state)
        assertTrue(cappedTimer.advanceAt(capAt).visualOverdue)
        assertTrue(capPlatform.stopCalls >= 1)
        assertFalse(capPlatform.audioActive)
        val requestsAtCap = capPlatform.requests.size
        capScheduler.advanceTo(capAt + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS)
        assertEquals(requestsAtCap, capPlatform.requests.size)
        capTicker.dispose()
    }

    @Test
    fun overdueSchedulerDenialAndErrorMatrixPreservesVisualAndDismissal() {
        data class Case(
            val name: String,
            val expectedReason: String,
            val volumePercent: Int = 70,
            val platform: () -> RecordingPlatform,
        )

        val cases = listOf(
            Case("VOLUME_0", "app_volume_suppressed", volumePercent = 0, platform = { RecordingPlatform() }),
            Case("SILENT_NON_NORMAL_RINGER", "ringer_mode_suppressed", platform = { RecordingPlatform("ringer_mode_suppressed") }),
            Case("DND", "dnd_suppressed", platform = { RecordingPlatform("dnd_suppressed") }),
            Case("UNAVAILABLE_ROUTE", "audio_route_unavailable", platform = { RecordingPlatform("audio_route_unavailable") }),
            Case("UNAVAILABLE_SERVICE", "audio_service_unavailable", platform = { RecordingPlatform("audio_service_unavailable") }),
            Case("AUDIO_START_ERROR", "audio_start_error", platform = { RecordingPlatform(startError = true) }),
        )

        cases.forEach { case ->
            val scheduler = DeterministicDisplayTickScheduler()
            val platform = case.platform()
            val settingsStore = InMemorySettingsStateStore().also {
                it.save(SettingsState(timerAlert = TimerAlertSettingsProjection(
                    signal = BuiltInAlertSignal.CLASSIC,
                    volumePercent = case.volumePercent,
                )))
            }
            val settings = SettingsCapability(settingsStore)
            val timer = TimerCapability(
                stateStore = InMemoryTimerStateStore(),
                platform = platform,
                alertSettingsReader = settings,
            )
            timer.start(startedAtMillis = 0L, durationMillis = 1L)
            val ticker = tickerFor(timer, scheduler)

            scheduler.advanceTo(50L)
            val decision = scheduler.lastDecision
            assertTrue(case.name, decision?.visualOverdue == true)
            val audioResult = decision?.audioResult
            assertNotNull(case.name, audioResult)
            assertFalse(case.name, audioResult!!.permitted)
            assertEquals(case.name, case.expectedReason, audioResult.reason)
            assertEquals(case.name, TimerLifecycleState.OVERDUE, timer.snapshotAt(scheduler.nowMillis).state)

            val dismissed = timer.handleGesture(scheduler.nowMillis, TimerGesture.DOUBLE_TAP)
            assertTrue(case.name, dismissed.dismissed)
            assertEquals(case.name, TimerLifecycleState.IDLE, dismissed.snapshot.state)
            val requestsAtDismissal = platform.requests.size
            scheduler.advanceTo(scheduler.nowMillis + TimerAlertPolicy.REPEAT_INTERVAL_MILLIS)
            assertEquals(case.name, requestsAtDismissal, platform.requests.size)
            assertFalse(case.name, platform.audioActive)
            ticker.dispose()
        }
    }

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
        private val startError: Boolean = false,
    ) : PlatformRuntime {
        val requests = mutableListOf<AlertAudioRequest>()
        var stopCalls = 0
        var audioActive = false
        var startCalls = 0

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
            val result = if (startError) {
                audioActive = false
                AudioProbeResult(
                    requested = false,
                    permitted = false,
                    reason = "audio_start_error",
                    signalId = request.signalId,
                    volumePercent = request.volumePercent,
                    rampPercent = request.rampPercent,
                    overdueElapsedMillis = request.overdueElapsedMillis,
                )
            } else if (suppressionReason == null) {
                startCalls += 1
                audioActive = true
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
                audioActive = false
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
            return result
        }

        override fun stopAlertAudio() {
            stopCalls += 1
            audioActive = false
        }
    }

    private fun tickerFor(
        timer: TimerCapability,
        scheduler: DeterministicDisplayTickScheduler,
    ): MainDisplayTickerOwner {
        val ticker = MainDisplayTickerOwner(
            scheduler = scheduler,
            onTick = {
                scheduler.lastDecision = timer.advanceAt(scheduler.nowMillis)
            },
        )
        ticker.onViewAttachedToWindow()
        ticker.onActivityResumed()
        return ticker
    }

    private class DeterministicDisplayTickScheduler : MainDisplayTickerScheduler {
        private data class Entry(
            val atMillis: Long,
            val sequence: Long,
            val runnable: Runnable,
        )

        private val queue = PriorityQueue<Entry>(compareBy<Entry> { it.atMillis }.thenBy { it.sequence })
        private var sequence = 0L
        var nowMillis: Long = 0L
            private set
        var lastDecision: TimerAlertDecision? = null

        override fun post(runnable: Runnable) = enqueue(runnable, nowMillis)

        override fun postDelayed(runnable: Runnable, delayMillis: Long) =
            enqueue(runnable, nowMillis + delayMillis)

        override fun removeCallbacks(runnable: Runnable) {
            queue.removeIf { it.runnable === runnable }
        }

        fun advanceTo(targetMillis: Long) {
            require(targetMillis >= nowMillis)
            while (queue.peek()?.atMillis?.let { it <= targetMillis } == true) {
                val next = queue.remove()
                nowMillis = next.atMillis
                next.runnable.run()
            }
            nowMillis = targetMillis
        }

        private fun enqueue(runnable: Runnable, atMillis: Long) {
            queue.add(Entry(atMillis, sequence++, runnable))
        }
    }
}
