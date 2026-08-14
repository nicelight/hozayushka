package com.hozayushka.app.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.hozayushka.app.adapters.platform.PlatformRuntimeAdapter
import com.hozayushka.app.adapters.weather.RedactedWeatherFixture
import com.hozayushka.app.adapters.weather.OpenMeteoWeatherAdapter
import com.hozayushka.app.adapters.weather.OpenWeatherWeatherAdapter
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
    private val weatherRefreshExecutor: ExecutorService,
) {
    private val weatherRefreshHandler = Handler(Looper.getMainLooper())
    private val scheduledWeatherRefresh = object : Runnable {
        override fun run() {
            enqueueWeatherRefresh(WeatherRefreshTrigger.SCHEDULED)
            weatherRefreshHandler.postDelayed(this, WEATHER_REFRESH_CADENCE_MILLIS)
        }
    }

    companion object {
        fun create(context: Context): FoundationRuntime {
            val platform = PlatformRuntimeAdapter(context)
            val catalog = BundledLocationCatalog.fromAsset(context)
            val weatherRefreshExecutor = Executors.newSingleThreadExecutor()
            var weatherCapability: WeatherCapability? = null
            val settings = SettingsCapability(
                SharedPreferencesSettingsStateStore(
                    context.getSharedPreferences(SETTINGS_STORE, Context.MODE_PRIVATE),
                ),
                onValidLocationChanged = {
                    weatherCapability?.let { capability ->
                        weatherRefreshExecutor.execute {
                            capability.refreshIfNeeded(
                                nowMillis = platform.nowMillis(),
                                networkAvailable = platform.isNetworkAvailable(),
                                trigger = WeatherRefreshTrigger.LOCATION_CHANGE,
                            )
                        }
                    }
                },
                catalog = catalog,
                onValidProviderChanged = {
                    weatherCapability?.let { capability ->
                        weatherRefreshExecutor.execute {
                            capability.refreshIfNeeded(
                                nowMillis = platform.nowMillis(),
                                networkAvailable = platform.isNetworkAvailable(),
                                trigger = WeatherRefreshTrigger.PROVIDER_CHANGE,
                            )
                        }
                    }
                },
                onValidOpenWeatherApiKeySaved = {
                    weatherCapability?.let { capability ->
                        weatherRefreshExecutor.execute {
                            capability.refreshIfNeeded(
                                nowMillis = platform.nowMillis(),
                                networkAvailable = platform.isNetworkAvailable(),
                                trigger = WeatherRefreshTrigger.PROVIDER_CHANGE,
                            )
                        }
                    }
                },
            )
            settings.ensureDefaultLocation()
            weatherCapability = WeatherCapability(
                locationReader = settings,
                cacheStore = SharedPreferencesWeatherCacheStore(
                    context.getSharedPreferences(WEATHER_STORE, Context.MODE_PRIVATE),
                ),
                openMeteoProvider = OpenMeteoWeatherAdapter(),
                openWeatherProvider = OpenWeatherWeatherAdapter(),
                fixtureProvider = RedactedWeatherFixture(),
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
                weatherRefreshExecutor = weatherRefreshExecutor,
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
        enqueueWeatherRefresh(WeatherRefreshTrigger.LAUNCH)
        weatherRefreshHandler.removeCallbacks(scheduledWeatherRefresh)
        weatherRefreshHandler.postDelayed(scheduledWeatherRefresh, WEATHER_REFRESH_CADENCE_MILLIS)
        timer.rehydrateAt(platform.nowMillis())
    }

    private fun enqueueWeatherRefresh(trigger: WeatherRefreshTrigger) {
        weatherRefreshExecutor.execute {
            weather.refreshIfNeeded(
                nowMillis = platform.nowMillis(),
                networkAvailable = platform.isNetworkAvailable(),
                trigger = trigger,
            )
        }
    }
}
