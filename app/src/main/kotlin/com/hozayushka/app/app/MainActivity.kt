package com.hozayushka.app.app

import android.app.Activity
import android.os.Bundle
import com.hozayushka.app.forecast.ForecastEntryIntent

class MainActivity : Activity() {
    private val app: HozayushkaApplication
        get() = application as HozayushkaApplication

    private var settingsOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.runtime.platform.applyFoundationWindow(window)
        renderMainSurface()
    }

    override fun onResume() {
        super.onResume()
        app.runtime.platform.applyFoundationWindow(window)
        app.runtime.onActivityResumed()
        if (!settingsOpen) renderMainSurface()
    }

    override fun onPause() {
        app.runtime.onActivityPaused()
        super.onPause()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (settingsOpen) {
            renderMainSurface()
        } else {
            super.onBackPressed()
        }
    }

    private fun renderMainSurface() {
        settingsOpen = false
        if (intent.getBooleanExtra(EXTRA_FOUNDATION_PROBE, false)) {
            setContentView(app.runtime.display.createFoundationView(this, foundationProbe = true))
            return
        }
        setContentView(app.runtime.display.createMainView(this, ::renderSettingsSurface, ::renderForecastSurface))
    }

    private fun renderSettingsSurface() {
        settingsOpen = true
        setContentView(
            app.runtime.display.createSettingsView(this, ::renderMainSurface),
        )
    }

    private fun renderForecastSurface(intent: ForecastEntryIntent) {
        val forecastView = when (intent) {
            ForecastEntryIntent.HOURLY -> app.runtime.display.createHourlyForecastView(this, ::renderMainSurface)
            ForecastEntryIntent.LONG_TERM -> app.runtime.display.createLongTermForecastView(this, ::renderMainSurface)
        }
        if (forecastView == null) renderMainSurface() else setContentView(forecastView)
    }

    private companion object {
        const val EXTRA_FOUNDATION_PROBE = "foundation_probe"
    }
}
