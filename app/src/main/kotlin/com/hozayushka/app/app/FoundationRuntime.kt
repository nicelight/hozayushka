package com.hozayushka.app.app

import android.content.Context
import com.hozayushka.app.adapters.platform.PlatformRuntimeAdapter
import com.hozayushka.app.adapters.weather.RedactedWeatherFixtureAdapter
import com.hozayushka.app.display.DisplayCapability
import com.hozayushka.app.forecast.ForecastSessionCapability
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.SharedPreferencesSettingsStateStore
import com.hozayushka.app.timer.SharedPreferencesTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.weather.SharedPreferencesWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability

/**
 * Composition root: creates owners/adapters and wires lifecycle-facing seams.
 * Product business state remains inside the capability owners.
 */
class FoundationRuntime private constructor(
    val platform: PlatformRuntimeAdapter,
    val settings: SettingsCapability,
    val weather: WeatherCapability,
    val forecast: ForecastSessionCapability,
    val timer: TimerCapability,
    val display: DisplayCapability,
) {
    companion object {
        fun create(context: Context): FoundationRuntime {
            val platform = PlatformRuntimeAdapter(context)
            val settings = SettingsCapability(
                SharedPreferencesSettingsStateStore(
                    context.getSharedPreferences(SETTINGS_STORE, Context.MODE_PRIVATE),
                ),
            )
            val weather = WeatherCapability(
                locationReader = settings,
                cacheStore = SharedPreferencesWeatherCacheStore(
                    context.getSharedPreferences(WEATHER_STORE, Context.MODE_PRIVATE),
                ),
                provider = RedactedWeatherFixtureAdapter(),
            )
            val timer = TimerCapability(
                SharedPreferencesTimerStateStore(
                    context.getSharedPreferences(TIMER_STORE, Context.MODE_PRIVATE),
                ),
                platform = platform,
            )
            return FoundationRuntime(
                platform = platform,
                settings = settings,
                weather = weather,
                forecast = ForecastSessionCapability(weather, platform),
                timer = timer,
                display = DisplayCapability(
                    platform = platform,
                    settings = settings,
                    weather = weather,
                    timer = timer,
                ),
            )
        }

        private const val SETTINGS_STORE = "owner.settings"
        private const val WEATHER_STORE = "owner.weather"
        private const val TIMER_STORE = "owner.timer"
    }

    fun onActivityPaused() {
        platform.onActivityPaused()
    }

    fun onActivityResumed() {
        platform.onActivityResumed()
        timer.rehydrateAt(platform.nowMillis())
    }
}
