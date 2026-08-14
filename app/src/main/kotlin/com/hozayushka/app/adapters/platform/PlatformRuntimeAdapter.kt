package com.hozayushka.app.adapters.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
    val signalId: String? = null,
    val volumePercent: Int? = null,
    val rampPercent: Int? = null,
    val overdueElapsedMillis: Long? = null,
)

data class AlertAudioRequest(
    val signalId: String,
    val volumePercent: Int,
    val rampPercent: Int,
    val overdueElapsedMillis: Long,
)

interface PlatformRuntime {
    fun nowMillis(): Long

    fun deviceTimeText(nowMillis: Long = nowMillis()): String

    fun deviceZoneId(): ZoneId

    fun isNetworkAvailable(): Boolean

    fun applyFoundationWindow(window: Window)

    fun onActivityPaused()

    fun onActivityResumed()

    fun requestAlertAudio(): AudioProbeResult

    fun requestAlertAudio(request: AlertAudioRequest): AudioProbeResult = requestAlertAudio()

    fun stopAlertAudio() = Unit
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

    override fun deviceZoneId(): ZoneId = ZoneId.systemDefault()

    override fun isNetworkAvailable(): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = connectivity.activeNetwork ?: return false
        val capabilities = connectivity.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

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

    override fun requestAlertAudio(): AudioProbeResult = requestAlertAudio(
        AlertAudioRequest(
            signalId = "classic",
            volumePercent = 70,
            rampPercent = 100,
            overdueElapsedMillis = 0L,
        ),
    )

    override fun requestAlertAudio(request: AlertAudioRequest): AudioProbeResult {
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
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }
        if (request.volumePercent == 0) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "app_volume_suppressed",
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }
        if (ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "ringer_mode_suppressed",
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }
        if (dndSuppressed) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "dnd_suppressed",
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }
        if (audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).isEmpty()) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "audio_route_unavailable",
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }

        toneGenerator?.release()
        val rampedVolume = (request.volumePercent * request.rampPercent / 100).coerceIn(1, 100)
        val tone = try {
            ToneGenerator(AudioManager.STREAM_ALARM, rampedVolume)
        } catch (_: RuntimeException) {
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "audio_start_error",
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }
        val started = try {
            tone.startTone(toneFor(request.signalId), 500)
        } catch (_: RuntimeException) {
            false
        }
        if (!started) {
            tone.release()
            return AudioProbeResult(
                requested = false,
                permitted = false,
                reason = "audio_start_error",
                signalId = request.signalId,
                volumePercent = request.volumePercent,
                rampPercent = request.rampPercent,
                overdueElapsedMillis = request.overdueElapsedMillis,
            )
        }
        toneGenerator = tone
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
            signalId = request.signalId,
            volumePercent = request.volumePercent,
            rampPercent = request.rampPercent,
            overdueElapsedMillis = request.overdueElapsedMillis,
        )
    }

    override fun stopAlertAudio() {
        toneGenerator?.release()
        toneGenerator = null
    }

    private fun toneFor(signalId: String): Int = when (signalId) {
        "bell" -> ToneGenerator.TONE_PROP_ACK
        "electronic" -> ToneGenerator.TONE_PROP_BEEP2
        else -> ToneGenerator.TONE_PROP_BEEP
    }
}
