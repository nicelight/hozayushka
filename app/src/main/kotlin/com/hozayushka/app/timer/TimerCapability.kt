package com.hozayushka.app.timer

import android.content.SharedPreferences
import com.hozayushka.app.adapters.platform.AudioProbeResult
import com.hozayushka.app.adapters.platform.AlertAudioRequest
import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.settings.TimerPresetDuration
import com.hozayushka.app.settings.TimerAlertSettingsDefaults
import com.hozayushka.app.settings.TimerAlertSettingsProjection
import com.hozayushka.app.settings.TimerAlertSettingsReader
import com.hozayushka.app.settings.TimerPresetReader
import com.hozayushka.app.settings.TimerPresetSlot

enum class TimerLifecycleState {
    IDLE,
    COUNTDOWN,
    OVERDUE,
}

data class TimerRecord(
    val startedAtMillis: Long,
    val durationMillis: Long,
    val presetSlot: TimerPresetSlot? = null,
)

data class TimerSnapshot(
    val state: TimerLifecycleState,
    val elapsedMillis: Long,
    val remainingMillis: Long,
    val activePresetSlot: TimerPresetSlot? = null,
    val durationMillis: Long = 0L,
    val overdueElapsedMillis: Long = 0L,
)

enum class TimerGesture {
    SINGLE_TAP,
    DOUBLE_TAP,
}

data class TimerGestureResult(
    val snapshot: TimerSnapshot,
    val singleTapHintVisible: Boolean = false,
    val dismissed: Boolean = false,
)

data class TimerAlertDecision(
    val visualOverdue: Boolean,
    val overdueElapsedMillis: Long = 0L,
    val audioRequest: AlertAudioRequest? = null,
    val audioResult: AudioProbeResult? = null,
    val audioStopped: Boolean = false,
)

data class TimerPresetPresentation(
    val slot: TimerPresetSlot,
    val duration: TimerPresetDuration,
    val isSelected: Boolean,
    val isActive: Boolean,
)

interface TimerStateStore {
    fun load(): TimerRecord?

    fun save(record: TimerRecord)

    fun reset()
}

class InMemoryTimerStateStore : TimerStateStore {
    private var record: TimerRecord? = null

    override fun load(): TimerRecord? = record

    override fun save(record: TimerRecord) {
        this.record = record
    }

    override fun reset() {
        record = null
    }
}

/** Private persistence owner for Timer & Alert. */
class SharedPreferencesTimerStateStore(
    private val preferences: SharedPreferences,
) : TimerStateStore {
    override fun load(): TimerRecord? {
        if (!preferences.contains(KEY_STARTED_AT)) return null
        return TimerRecord(
            startedAtMillis = preferences.getLong(KEY_STARTED_AT, 0L),
            durationMillis = preferences.getLong(KEY_DURATION, 0L),
            presetSlot = preferences.getString(KEY_PRESET_SLOT, null)?.let { value ->
                TimerPresetSlot.entries.firstOrNull { it.name == value }
            },
        )
    }

    override fun save(record: TimerRecord) {
        preferences.edit()
            .putLong(KEY_STARTED_AT, record.startedAtMillis)
            .putLong(KEY_DURATION, record.durationMillis)
            .apply {
                if (record.presetSlot == null) remove(KEY_PRESET_SLOT)
                else putString(KEY_PRESET_SLOT, record.presetSlot.name)
            }
            .apply()
    }

    override fun reset() {
        preferences.edit()
            .remove(KEY_STARTED_AT)
            .remove(KEY_DURATION)
            .remove(KEY_PRESET_SLOT)
            .apply()
    }

    private companion object {
        const val KEY_STARTED_AT = "foundation.started_at"
        const val KEY_DURATION = "foundation.duration"
        const val KEY_PRESET_SLOT = "timer.active_preset"
    }
}

class TimerCapability(
    private val stateStore: TimerStateStore,
    private val platform: PlatformRuntime? = null,
    private val presetReader: TimerPresetReader? = null,
    private val alertSettingsReader: TimerAlertSettingsReader? = null,
) {
    private var lastAlertRequestAtMillis: Long? = null
    private var audioCapStopIssued = false

    fun start(startedAtMillis: Long, durationMillis: Long) {
        start(startedAtMillis, durationMillis, presetSlot = null)
    }

    private fun start(
        startedAtMillis: Long,
        durationMillis: Long,
        presetSlot: TimerPresetSlot?,
    ) {
        require(startedAtMillis >= 0L) { "startedAtMillis must be non-negative" }
        require(durationMillis > 0L) { "durationMillis must be positive" }
        resetAlertRuntime(stopAudio = lastAlertRequestAtMillis != null && !audioCapStopIssued)
        stateStore.save(TimerRecord(startedAtMillis, durationMillis, presetSlot))
    }

    /** Reads validated Settings data through the registered Timer & Alert edge. */
    fun presetPresentationAt(nowMillis: Long): List<TimerPresetPresentation> {
        val active = stateStore.load()?.takeIf { snapshotAt(nowMillis).state != TimerLifecycleState.IDLE }?.presetSlot
        val projection = presetReader?.timerPresetProjection()
            ?: com.hozayushka.app.settings.TimerPresetDefaults.projection()
        return projection.presets.map { preset ->
            TimerPresetPresentation(
                slot = preset.slot,
                duration = preset.duration,
                isSelected = preset.slot == active,
                isActive = preset.slot == active,
            )
        }
    }

    /** Selects one validated preset while preserving the single active timer record. */
    fun startPreset(slot: TimerPresetSlot, startedAtMillis: Long) {
        val preset = (presetReader?.timerPresetProjection()
            ?: com.hozayushka.app.settings.TimerPresetDefaults.projection()).preset(slot)
        start(startedAtMillis, preset.duration.totalMillis(), slot)
    }

    fun snapshotAt(nowMillis: Long): TimerSnapshot {
        val record = stateStore.load() ?: return TimerSnapshot(
            state = TimerLifecycleState.IDLE,
            elapsedMillis = 0L,
            remainingMillis = 0L,
            activePresetSlot = null,
            durationMillis = 0L,
            overdueElapsedMillis = 0L,
        )
        val elapsed = (nowMillis - record.startedAtMillis).coerceAtLeast(0L)
        val remaining = (record.durationMillis - elapsed).coerceAtLeast(0L)
        return TimerSnapshot(
            state = if (elapsed < record.durationMillis) {
                TimerLifecycleState.COUNTDOWN
            } else {
                TimerLifecycleState.OVERDUE
            },
            elapsedMillis = elapsed,
            remainingMillis = remaining,
            activePresetSlot = record.presetSlot,
            durationMillis = record.durationMillis,
            overdueElapsedMillis = (elapsed - record.durationMillis).coerceAtLeast(0L),
        )
    }

    /**
     * Rehydrates the persisted timer after an Activity/process interruption and
     * re-establishes an overdue alert after the platform released its audio.
     */
    fun rehydrateAt(nowMillis: Long): TimerSnapshot {
        val snapshot = snapshotAt(nowMillis)
        if (snapshot.state == TimerLifecycleState.OVERDUE) {
            lastAlertRequestAtMillis = null
            advanceAt(nowMillis)
        }
        return snapshot
    }

    /** Applies only the accepted timer gestures; Main Display owns the input surface. */
    fun handleGesture(nowMillis: Long, gesture: TimerGesture): TimerGestureResult {
        val current = snapshotAt(nowMillis)
        return when (current.state) {
            TimerLifecycleState.COUNTDOWN -> when (gesture) {
                TimerGesture.SINGLE_TAP -> TimerGestureResult(
                    snapshot = current,
                    singleTapHintVisible = true,
                )
                TimerGesture.DOUBLE_TAP -> {
                    cancel()
                    TimerGestureResult(snapshotAt(nowMillis), dismissed = true)
                }
            }
            TimerLifecycleState.OVERDUE -> {
                cancel()
                TimerGestureResult(snapshotAt(nowMillis), dismissed = true)
            }
            TimerLifecycleState.IDLE -> TimerGestureResult(snapshot = current)
        }
    }

    /** Foundation-only route for verifying Android silent/DND audio policy. */
    fun requestAudioProbeAt(nowMillis: Long): AudioProbeResult {
        return advanceAt(nowMillis).audioResult ?: AudioProbeResult(
            requested = false,
            permitted = false,
            reason = "timer_not_overdue",
        )
    }

    /** Advances the owner-controlled alert request without changing visual lifecycle state. */
    fun advanceAt(nowMillis: Long): TimerAlertDecision {
        val snapshot = snapshotAt(nowMillis)
        if (snapshot.state != TimerLifecycleState.OVERDUE) {
            return TimerAlertDecision(visualOverdue = false)
        }

        val settings = alertSettingsReader?.timerAlertSettingsProjection()
            ?: TimerAlertSettingsDefaults.projection()
        if (snapshot.overdueElapsedMillis >= TimerAlertPolicy.AUDIO_CAP_MILLIS) {
            if (!audioCapStopIssued) {
                platform?.stopAlertAudio()
                audioCapStopIssued = true
                return TimerAlertDecision(
                    visualOverdue = true,
                    overdueElapsedMillis = snapshot.overdueElapsedMillis,
                    audioStopped = true,
                )
            }
            return TimerAlertDecision(
                visualOverdue = true,
                overdueElapsedMillis = snapshot.overdueElapsedMillis,
            )
        }

        if (!TimerAlertPolicy.isRepeatDue(nowMillis, lastAlertRequestAtMillis)) {
            return TimerAlertDecision(
                visualOverdue = true,
                overdueElapsedMillis = snapshot.overdueElapsedMillis,
            )
        }

        val request = TimerAlertPolicy.requestAt(snapshot.overdueElapsedMillis, settings)
            ?: if (settings.volumePercent == 0) {
                if (platform == null) {
                    return TimerAlertDecision(
                        visualOverdue = true,
                        overdueElapsedMillis = snapshot.overdueElapsedMillis,
                    )
                }
                val suppressedRequest = TimerAlertPolicy.requestAt(
                    snapshot.overdueElapsedMillis,
                    settings.copy(volumePercent = 1),
                )
                return TimerAlertDecision(
                    visualOverdue = true,
                    overdueElapsedMillis = snapshot.overdueElapsedMillis,
                    audioResult = AudioProbeResult(
                        requested = false,
                        permitted = false,
                        reason = "app_volume_suppressed",
                        signalId = suppressedRequest?.signalId ?: settings.signal.id,
                        volumePercent = settings.volumePercent,
                        rampPercent = suppressedRequest?.rampPercent,
                        overdueElapsedMillis = snapshot.overdueElapsedMillis,
                    ),
                )
            } else {
                return TimerAlertDecision(
                    visualOverdue = true,
                    overdueElapsedMillis = snapshot.overdueElapsedMillis,
                )
            }
        val result = platform?.requestAlertAudio(request) ?: AudioProbeResult(
            requested = false,
            permitted = false,
            reason = "platform_unavailable",
            signalId = request.signalId,
            volumePercent = request.volumePercent,
            rampPercent = request.rampPercent,
            overdueElapsedMillis = request.overdueElapsedMillis,
        )
        lastAlertRequestAtMillis = nowMillis
        return TimerAlertDecision(
            visualOverdue = true,
            overdueElapsedMillis = snapshot.overdueElapsedMillis,
            audioRequest = request,
            audioResult = result,
        )
    }

    fun cancel() {
        resetAlertRuntime(stopAudio = lastAlertRequestAtMillis != null && !audioCapStopIssued)
        stateStore.reset()
    }

    fun resetFoundationState() {
        cancel()
    }

    private fun resetAlertRuntime(stopAudio: Boolean) {
        if (stopAudio) platform?.stopAlertAudio()
        lastAlertRequestAtMillis = null
        audioCapStopIssued = false
    }
}
