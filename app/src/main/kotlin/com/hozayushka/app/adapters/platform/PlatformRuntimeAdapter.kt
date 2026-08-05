package com.hozayushka.app.adapters.platform

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.app.NotificationManager
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Narrow platform boundary used by the Foundation shell and capability seams.
 * Android remains the authority for device time, window policy and lifecycle.
 */
data class AudioProbeResult(
    val requested: Boolean,
    val permitted: Boolean,
    val reason: String,
)

interface PlatformRuntime {
    fun nowMillis(): Long

    fun deviceTimeText(nowMillis: Long = nowMillis()): String

    fun applyFoundationWindow(window: Window)

    fun onActivityPaused()

    fun onActivityResumed()

    fun requestAlertAudio(): AudioProbeResult
}

class PlatformRuntimeAdapter(
    private val context: Context,
) : PlatformRuntime {
    private val clockFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val mainHandler = Handler(Looper.getMainLooper())
    private var toneGenerator: ToneGenerator? = null

    override fun nowMillis(): Long = System.currentTimeMillis()

    override fun deviceTimeText(nowMillis: Long): String =
        clockFormatter
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(nowMillis))

    override fun applyFoundationWindow(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars(),
            )
            window.insetsController?.systemBarsBehavior =
                android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onActivityPaused() {
        toneGenerator?.release()
        toneGenerator = null
    }

    override fun onActivityResumed() = Unit

    override fun requestAlertAudio(): AudioProbeResult {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val ringerMode = audioManager?.ringerMode
        val dndSuppressed =
            notificationManager?.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_NONE

        if (audioManager == null) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "audio_service_unavailable",
            )
        }
        if (ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "ringer_mode_suppressed",
            )
        }
        if (dndSuppressed) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "dnd_suppressed",
            )
        }

        toneGenerator?.release()
        val tone = ToneGenerator(AudioManager.STREAM_ALARM, 80)
        toneGenerator = tone
        tone.startTone(ToneGenerator.TONE_PROP_BEEP, 250)
        mainHandler.postDelayed({
            if (toneGenerator === tone) {
                tone.release()
                toneGenerator = null
            }
        }, 350L)
        return AudioProbeResult(
            requested = true,
            permitted = true,
            reason = "tone_requested",
        )
    }
}
