package com.hozayushka.app.timer

import android.content.SharedPreferences
import com.hozayushka.app.adapters.platform.AudioProbeResult
import com.hozayushka.app.adapters.platform.PlatformRuntime

enum class TimerLifecycleState {
    IDLE,
    COUNTDOWN,
    OVERDUE,
}

data class TimerRecord(
    val startedAtMillis: Long,
    val durationMillis: Long,
)

data class TimerSnapshot(
    val state: TimerLifecycleState,
    val elapsedMillis: Long,
    val remainingMillis: Long,
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
        )
    }

    override fun save(record: TimerRecord) {
        preferences.edit()
            .putLong(KEY_STARTED_AT, record.startedAtMillis)
            .putLong(KEY_DURATION, record.durationMillis)
            .apply()
    }

    override fun reset() {
        preferences.edit()
            .remove(KEY_STARTED_AT)
            .remove(KEY_DURATION)
            .apply()
    }

    private companion object {
        const val KEY_STARTED_AT = "foundation.started_at"
        const val KEY_DURATION = "foundation.duration"
    }
}

class TimerCapability(
    private val stateStore: TimerStateStore,
    private val platform: PlatformRuntime? = null,
) {
    fun start(startedAtMillis: Long, durationMillis: Long) {
        require(startedAtMillis >= 0L) { "startedAtMillis must be non-negative" }
        require(durationMillis > 0L) { "durationMillis must be positive" }
        stateStore.save(TimerRecord(startedAtMillis, durationMillis))
    }

    fun snapshotAt(nowMillis: Long): TimerSnapshot {
        val record = stateStore.load() ?: return TimerSnapshot(
            state = TimerLifecycleState.IDLE,
            elapsedMillis = 0L,
            remainingMillis = 0L,
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
        )
    }

    /** Rehydrates the persisted timer after an Activity/process interruption. */
    fun rehydrateAt(nowMillis: Long): TimerSnapshot = snapshotAt(nowMillis)

    /** Foundation-only route for verifying Android silent/DND audio policy. */
    fun requestAudioProbeAt(nowMillis: Long): AudioProbeResult {
        val snapshot = snapshotAt(nowMillis)
        if (snapshot.state != TimerLifecycleState.OVERDUE) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "timer_not_overdue",
            )
        }
        return platform?.requestAlertAudio() ?: AudioProbeResult(
            requested = false,
            permitted = false,
            reason = "platform_unavailable",
        )
    }

    fun cancel() {
        stateStore.reset()
    }

    fun resetFoundationState() {
        stateStore.reset()
    }
}
