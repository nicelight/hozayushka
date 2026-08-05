package com.hozayushka.app.app

import android.app.Application

class HozayushkaApplication : Application() {
    lateinit var runtime: FoundationRuntime
        private set

    override fun onCreate() {
        super.onCreate()
        runtime = FoundationRuntime.create(this)
    }
}
