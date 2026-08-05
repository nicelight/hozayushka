package com.hozayushka.app.forecast

import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.weather.WeatherReadPort

/**
 * Foundation seam for forecast sessions. Completeness, slot/day mapping and
 * gesture behavior remain downstream feature work.
 */
class ForecastSessionCapability(
    private val weather: WeatherReadPort,
    private val platform: PlatformRuntime,
) {
    fun foundationProbeAvailable(): Boolean =
        weather.snapshot() != null && platform.nowMillis() >= 0L
}
