package com.hozayushka.app.timer

import com.hozayushka.app.adapters.platform.AlertAudioRequest
import com.hozayushka.app.settings.TimerAlertSettingsProjection

object TimerAlertPolicy {
    const val RAMP_DURATION_MILLIS = 5_000L
    const val AUDIO_CAP_MILLIS = 30L * 60L * 1_000L
    const val REPEAT_INTERVAL_MILLIS = 5_000L

    fun isRepeatDue(nowMillis: Long, lastRequestAtMillis: Long?): Boolean =
        lastRequestAtMillis == null || nowMillis - lastRequestAtMillis >= REPEAT_INTERVAL_MILLIS

    fun requestAt(
        overdueElapsedMillis: Long,
        settings: TimerAlertSettingsProjection,
    ): AlertAudioRequest? {
        if (overdueElapsedMillis !in 0 until AUDIO_CAP_MILLIS || settings.volumePercent == 0) return null
        val rampPercent = ((overdueElapsedMillis * 100L) / RAMP_DURATION_MILLIS)
            .coerceIn(10L, 100L)
            .toInt()
        return AlertAudioRequest(
            signalId = settings.signal.id,
            volumePercent = settings.volumePercent,
            rampPercent = rampPercent,
            overdueElapsedMillis = overdueElapsedMillis,
        )
    }
}
