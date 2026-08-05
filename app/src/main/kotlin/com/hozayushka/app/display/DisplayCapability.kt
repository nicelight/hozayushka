package com.hozayushka.app.display

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Button
import android.widget.TextView
import com.hozayushka.app.R
import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.weather.WeatherCapability

/**
 * Minimal visible shell plus an explicit Foundation-only probe mode. Product
 * card, forecast and full timer/Settings behavior remain downstream.
 */
class DisplayCapability(
    private val platform: PlatformRuntime,
    private val settings: SettingsCapability,
    private val weather: WeatherCapability,
    private val timer: TimerCapability,
) {
    fun createFoundationView(context: Context, foundationProbe: Boolean = false): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(context.getColor(R.color.foundation_background))
            setPadding(48, 32, 48, 32)
        }

        val time = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_primary))
            textSize = 96f
            text = platform.deviceTimeText()
            contentDescription = "Foundation device time"
        }
        val status = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_secondary))
            textSize = 18f
            text = context.getString(R.string.foundation_status)
        }
        val hint = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_secondary))
            textSize = 14f
            text = context.getString(R.string.foundation_hint)
        }

        root.addView(time, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        root.addView(status, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        root.addView(hint, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        if (foundationProbe) {
            addFoundationProbe(context, root, time, status, hint)
        }
        return root
    }

    private fun addFoundationProbe(
        context: Context,
        root: LinearLayout,
        time: TextView,
        status: TextView,
        hint: TextView,
    ) {
        status.text = "Foundation probe mode"
        hint.text = "ADB: --ez foundation_probe true"

        val probeStatus = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_secondary))
            textSize = 14f
            setPadding(0, 12, 0, 12)
        }
        root.addView(probeStatus, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        fun render(message: String) {
            val now = platform.nowMillis()
            val timerSnapshot = timer.snapshotAt(now)
            val location = settings.currentLocation()?.cityLabel ?: "empty"
            val weatherSnapshot = weather.snapshot()
            time.text = platform.deviceTimeText(now)
            probeStatus.text = buildString {
                append(message)
                append("\nSettings: ")
                append(location)
                append("\nTimer: ")
                append(timerSnapshot.state.name)
                append(" elapsed=")
                append(timerSnapshot.elapsedMillis)
                append(" remaining=")
                append(timerSnapshot.remainingMillis)
                append("\nWeather: ")
                append(weatherSnapshot?.let { "${it.temperatureCelsius}C/${it.condition}" } ?: "empty")
            }
        }

        fun addProbeButton(label: String, action: () -> String) {
            root.addView(
                Button(context).apply {
                    text = label
                    contentDescription = "Foundation probe: $label"
                    setOnClickListener { render(action()) }
                },
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        }

        addProbeButton("Seed Settings") {
            settings.saveFoundationLocation(
                LocationContext(
                    cityLabel = "Khujand",
                    latitude = 40.2833,
                    longitude = 69.6167,
                    apiTimeZone = "Asia/Dushanbe",
                ),
            )
            "settings_seeded"
        }
        addProbeButton("Refresh Weather Fixture") {
            if (weather.refreshFoundationFixture() == null) {
                "weather_requires_settings"
            } else {
                "weather_refreshed_redacted"
            }
        }
        addProbeButton("Start 1s Timer") {
            timer.start(platform.nowMillis(), FOUNDATION_TIMER_DURATION_MILLIS)
            root.postDelayed({ render("timer_observed") }, FOUNDATION_TIMER_DURATION_MILLIS + 250L)
            "timer_started"
        }
        addProbeButton("Rehydrate Timer") {
            timer.rehydrateAt(platform.nowMillis())
            "timer_rehydrated"
        }
        addProbeButton("Audio Probe") {
            val result = timer.requestAudioProbeAt(platform.nowMillis())
            "audio_${result.reason}_permitted=${result.permitted}"
        }
        addProbeButton("Cancel and Reset") {
            settings.resetFoundationState()
            weather.resetFoundationState()
            timer.resetFoundationState()
            "foundation_state_reset"
        }

        render("probe_ready")
    }

    private companion object {
        const val FOUNDATION_TIMER_DURATION_MILLIS = 1_000L
    }
}
