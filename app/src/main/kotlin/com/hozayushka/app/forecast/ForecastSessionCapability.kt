package com.hozayushka.app.forecast

import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.weather.HourlyForecastCardProjection
import com.hozayushka.app.weather.LongTermForecastCardProjection
import com.hozayushka.app.weather.WeatherReadPort

enum class ForecastSessionState {
    CLOSED,
    OPEN,
    HINT,
}

data class ForecastSessionSnapshot(
    val state: ForecastSessionState,
    val rows: List<List<HourlyForecastCardProjection>> = emptyList(),
    val longTermRows: List<List<LongTermForecastCardProjection>> = emptyList(),
    val openedAtMillis: Long? = null,
    val message: String? = null,
)

enum class ForecastEntryIntent {
    HOURLY,
    LONG_TERM,
}

/** Owns forecast entry/rejection, transient session state and the shared exit flow. */
class ForecastSessionCapability(
    private val weather: WeatherReadPort,
    private val platform: PlatformRuntime,
) {
    private var current = ForecastSessionSnapshot(ForecastSessionState.CLOSED)
    private var holdActive = false

    fun foundationProbeAvailable(): Boolean =
        weather.snapshot() != null && platform.nowMillis() >= 0L

    fun openHourly(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        val projection = weather.hourlyProjection(nowMillis)
        holdActive = false
        current = if (projection == null) {
            ForecastSessionSnapshot(
                state = ForecastSessionState.CLOSED,
                message = HOURLY_UNAVAILABLE_MESSAGE,
            )
        } else {
            ForecastSessionSnapshot(
                state = ForecastSessionState.OPEN,
                rows = projection.rows,
                openedAtMillis = nowMillis,
            )
        }
        return current
    }

    fun openLongTerm(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        val projection = weather.longTermProjection(nowMillis)
        holdActive = false
        current = if (projection == null) {
            ForecastSessionSnapshot(
                state = ForecastSessionState.CLOSED,
                message = LONG_TERM_UNAVAILABLE_MESSAGE,
            )
        } else {
            ForecastSessionSnapshot(
                state = ForecastSessionState.OPEN,
                longTermRows = projection.rows,
                openedAtMillis = nowMillis,
            )
        }
        return current
    }

    fun snapshotAt(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        val openedAtMillis = current.openedAtMillis
        if (current.state == ForecastSessionState.OPEN &&
            !holdActive &&
            openedAtMillis != null &&
            nowMillis - openedAtMillis >= AUTO_CLOSE_MILLIS
        ) {
            close()
        }
        return current
    }

    fun singleTap(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        snapshotAt(nowMillis)
        if (current.state == ForecastSessionState.OPEN) {
            current = current.copy(state = ForecastSessionState.HINT)
        }
        return current
    }

    fun doubleTap(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        snapshotAt(nowMillis)
        close()
        return current
    }

    fun hold(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        snapshotAt(nowMillis)
        if (current.state == ForecastSessionState.OPEN) {
            holdActive = true
        }
        return current
    }

    fun release(nowMillis: Long = platform.nowMillis()): ForecastSessionSnapshot {
        snapshotAt(nowMillis)
        close()
        return current
    }

    fun reset() {
        close()
    }

    private fun close() {
        holdActive = false
        current = ForecastSessionSnapshot(ForecastSessionState.CLOSED)
    }

    companion object {
        const val AUTO_CLOSE_MILLIS = 3_000L
        const val HOURLY_UNAVAILABLE_MESSAGE = "Почасовой прогноз еще не подгрузился"
        const val LONG_TERM_UNAVAILABLE_MESSAGE = "Долгосрочный прогноз еще не подгрузился"
        const val SINGLE_TAP_HINT = "Дважды нажмите, чтобы закрыть"
    }
}
