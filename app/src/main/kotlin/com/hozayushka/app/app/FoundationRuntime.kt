package com.hozayushka.app.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.hozayushka.app.adapters.platform.PlatformRuntimeAdapter
import com.hozayushka.app.adapters.weather.RedactedWeatherFixtureAdapter
import com.hozayushka.app.display.DisplayCapability
import com.hozayushka.app.forecast.ForecastSessionCapability
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.SharedPreferencesSettingsStateStore
import com.hozayushka.app.settings.BundledLocationCatalog
import com.hozayushka.app.timer.SharedPreferencesTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.weather.SharedPreferencesWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherRefreshTrigger

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
    private val weatherRefreshHandler = Handler(Looper.getMainLooper())
    private val scheduledWeatherRefresh = object : Runnable {
        override fun run() {
            weather.refreshIfNeeded(
                nowMillis = platform.nowMillis(),
                networkAvailable = platform.isNetworkAvailable(),
                trigger = WeatherRefreshTrigger.SCHEDULED,
                requireStoredCredential = true,
            )
            weatherRefreshHandler.postDelayed(this, WEATHER_REFRESH_CADENCE_MILLIS)
        }
    }

    companion object {
        fun create(context: Context): FoundationRuntime {
            val platform = PlatformRuntimeAdapter(context)
            val catalog = BundledLocationCatalog.fromAsset(context)
            var weatherCapability: WeatherCapability? = null
            val settings = SettingsCapability(
                SharedPreferencesSettingsStateStore(
                    context.getSharedPreferences(SETTINGS_STORE, Context.MODE_PRIVATE),
                ),
                onValidLocationChanged = {
                    weatherCapability?.refreshIfNeeded(
                        nowMillis = platform.nowMillis(),
                        networkAvailable = platform.isNetworkAvailable(),
                        trigger = WeatherRefreshTrigger.LOCATION_CHANGE,
                        requireStoredCredential = true,
                    )
                },
                catalog = catalog,
            )
            settings.ensureDefaultLocation()
            weatherCapability = WeatherCapability(
                locationReader = settings,
                cacheStore = SharedPreferencesWeatherCacheStore(
                    context.getSharedPreferences(WEATHER_STORE, Context.MODE_PRIVATE),
                ),
                provider = RedactedWeatherFixtureAdapter(),
            )
            val weather = requireNotNull(weatherCapability)
            val timer = TimerCapability(
                SharedPreferencesTimerStateStore(
                    context.getSharedPreferences(TIMER_STORE, Context.MODE_PRIVATE),
                ),
                platform = platform,
                presetReader = settings,
                alertSettingsReader = settings,
            )
            val forecast = ForecastSessionCapability(weather, platform)
            return FoundationRuntime(
                platform = platform,
                settings = settings,
                weather = weather,
                forecast = forecast,
                timer = timer,
                display = DisplayCapability(
                    platform = platform,
                    settings = settings,
                    weather = weather,
                    timer = timer,
                    forecast = forecast,
                ),
            )
        }

        private const val SETTINGS_STORE = "owner.settings"
        private const val WEATHER_STORE = "owner.weather"
        private const val TIMER_STORE = "owner.timer"
        private const val WEATHER_REFRESH_CADENCE_MILLIS = 30L * 60L * 1_000L
    }

    fun onActivityPaused() {
        weatherRefreshHandler.removeCallbacks(scheduledWeatherRefresh)
        platform.onActivityPaused()
    }

    fun onActivityResumed() {
        platform.onActivityResumed()
        weather.refreshIfNeeded(
            nowMillis = platform.nowMillis(),
            networkAvailable = platform.isNetworkAvailable(),
            trigger = WeatherRefreshTrigger.LAUNCH,
            requireStoredCredential = true,
        )
        weatherRefreshHandler.removeCallbacks(scheduledWeatherRefresh)
        weatherRefreshHandler.postDelayed(scheduledWeatherRefresh, WEATHER_REFRESH_CADENCE_MILLIS)
        timer.rehydrateAt(platform.nowMillis())
    }
}
