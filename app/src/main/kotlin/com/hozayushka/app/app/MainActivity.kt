package com.hozayushka.app.app

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity() {
    private val app: HozayushkaApplication
        get() = application as HozayushkaApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.runtime.platform.applyFoundationWindow(window)
        renderFoundationSurface()
    }

    override fun onResume() {
        super.onResume()
        app.runtime.platform.applyFoundationWindow(window)
        app.runtime.onActivityResumed()
        renderFoundationSurface()
    }

    override fun onPause() {
        app.runtime.onActivityPaused()
        super.onPause()
    }

    private fun renderFoundationSurface() {
        setContentView(
            app.runtime.display.createFoundationView(
                context = this,
                foundationProbe = intent.getBooleanExtra(EXTRA_FOUNDATION_PROBE, false),
            ),
        )
    }

    private companion object {
        const val EXTRA_FOUNDATION_PROBE = "foundation_probe"
    }
}
