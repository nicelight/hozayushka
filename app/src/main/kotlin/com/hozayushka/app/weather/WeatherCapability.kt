package com.hozayushka.app.weather

import android.content.SharedPreferences
import com.hozayushka.app.adapters.weather.ProviderDailyWeather
import com.hozayushka.app.adapters.weather.ProviderHourlyWeather
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.LocationReader
import com.hozayushka.app.settings.WeatherAccessReader
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val FRESHNESS_WINDOW_MILLIS = 24L * 60L * 60L * 1_000L
private const val REFRESH_INTERVAL_MILLIS = 30L * 60L * 1_000L
private const val HISTORY_WINDOW_MILLIS = 7L * 24L * 60L * 60L * 1_000L
private const val LONG_TERM_DAYS = 10L

data class WeatherSnapshot(
    val cityLabel: String,
    val temperatureCelsius: Int,
    val condition: String,
    val source: String,
    val updatedAtMillis: Long = 0L,
    val pressureMmHg: Double = 0.0,
    val apiTimeZone: String = "UTC",
)

data class NormalizedDay(
    val date: LocalDate,
    val dayTemperatureCelsius: Int?,
    val nightTemperatureCelsius: Int?,
    val dayCondition: String?,
    val nightCondition: String?,
    val moonPhase: String? = null,
)

data class PressureHistoryEntry(
    val recordedAtMillis: Long,
    val pressureMmHg: Double,
)

data class WeatherCacheRecord(
    val snapshot: WeatherSnapshot,
    val daily: List<NormalizedDay>,
    val history: List<PressureHistoryEntry>,
    val installedAtMillis: Long,
    val hourly: List<NormalizedHourly> = emptyList(),
)

data class NormalizedHourly(
    val date: LocalDate,
    val time: LocalTime,
    val temperatureCelsius: Int?,
    val illustration: WeatherIllustration?,
)

interface WeatherCacheStore {
    fun load(): WeatherSnapshot?

    fun save(snapshot: WeatherSnapshot)

    fun loadRecord(): WeatherCacheRecord? = load()?.let {
        WeatherCacheRecord(it, emptyList(), emptyList(), it.updatedAtMillis)
    }

    fun saveRecord(record: WeatherCacheRecord) = save(record.snapshot)

    fun reset()
}

class InMemoryWeatherCacheStore : WeatherCacheStore {
    private var record: WeatherCacheRecord? = null

    override fun load(): WeatherSnapshot? = record?.snapshot

    override fun save(snapshot: WeatherSnapshot) {
        record = WeatherCacheRecord(snapshot, emptyList(), emptyList(), snapshot.updatedAtMillis)
    }

    override fun loadRecord(): WeatherCacheRecord? = record

    override fun saveRecord(record: WeatherCacheRecord) {
        this.record = record
    }

    override fun reset() {
        record = null
    }
}

/** Private persistence owner for Weather Context. */
class SharedPreferencesWeatherCacheStore(
    private val preferences: SharedPreferences,
) : WeatherCacheStore {
    override fun load(): WeatherSnapshot? = loadRecord()?.snapshot

    override fun loadRecord(): WeatherCacheRecord? {
        if (!preferences.contains(KEY_CITY)) return null
        val snapshot = WeatherSnapshot(
            cityLabel = preferences.getString(KEY_CITY, null).orEmpty(),
            temperatureCelsius = preferences.getInt(KEY_TEMPERATURE, 0),
            condition = preferences.getString(KEY_CONDITION, null).orEmpty(),
            source = preferences.getString(KEY_SOURCE, null).orEmpty(),
            updatedAtMillis = preferences.getLong(KEY_UPDATED_AT, 0L),
            pressureMmHg = preferences.getString(KEY_PRESSURE, null)?.toDoubleOrNull() ?: 0.0,
            apiTimeZone = preferences.getString(KEY_TIME_ZONE, null).orEmpty().ifBlank { "UTC" },
        )
        val daily = preferences.getString(KEY_DAILY, null).orEmpty()
            .splitToSequence(';')
            .filter(String::isNotBlank)
            .mapNotNull(::decodeDay)
            .toList()
        val history = preferences.getString(KEY_HISTORY, null).orEmpty()
            .splitToSequence(';')
            .filter(String::isNotBlank)
            .mapNotNull(::decodeHistory)
            .toList()
        val hourly = preferences.getString(KEY_HOURLY, null).orEmpty()
            .splitToSequence(';')
            .filter(String::isNotBlank)
            .mapNotNull(::decodeHourly)
            .toList()
        return WeatherCacheRecord(
            snapshot = snapshot,
            daily = daily,
            history = history,
            installedAtMillis = preferences.getLong(KEY_INSTALLED_AT, snapshot.updatedAtMillis),
            hourly = hourly,
        )
    }

    override fun save(snapshot: WeatherSnapshot) {
        saveRecord(WeatherCacheRecord(snapshot, emptyList(), emptyList(), snapshot.updatedAtMillis))
    }

    override fun saveRecord(record: WeatherCacheRecord) {
        preferences.edit()
            .putString(KEY_CITY, record.snapshot.cityLabel)
            .putInt(KEY_TEMPERATURE, record.snapshot.temperatureCelsius)
            .putString(KEY_CONDITION, record.snapshot.condition)
            .putString(KEY_SOURCE, record.snapshot.source)
            .putLong(KEY_UPDATED_AT, record.snapshot.updatedAtMillis)
            .putString(KEY_PRESSURE, record.snapshot.pressureMmHg.toString())
            .putString(KEY_TIME_ZONE, record.snapshot.apiTimeZone)
            .putLong(KEY_INSTALLED_AT, record.installedAtMillis)
            .putString(KEY_DAILY, record.daily.joinToString(";", transform = ::encodeDay))
            .putString(KEY_HISTORY, record.history.joinToString(";", transform = ::encodeHistory))
            .putString(KEY_HOURLY, record.hourly.joinToString(";", transform = ::encodeHourly))
            .apply()
    }

    override fun reset() {
        preferences.edit()
            .remove(KEY_CITY)
            .remove(KEY_TEMPERATURE)
            .remove(KEY_CONDITION)
            .remove(KEY_SOURCE)
            .remove(KEY_UPDATED_AT)
            .remove(KEY_PRESSURE)
            .remove(KEY_TIME_ZONE)
            .remove(KEY_INSTALLED_AT)
            .remove(KEY_DAILY)
            .remove(KEY_HISTORY)
            .remove(KEY_HOURLY)
            .apply()
    }

    private companion object {
        const val KEY_CITY = "weather.city"
        const val KEY_TEMPERATURE = "weather.temperature"
        const val KEY_CONDITION = "weather.condition"
        const val KEY_SOURCE = "weather.source"
        const val KEY_UPDATED_AT = "weather.updated_at"
        const val KEY_PRESSURE = "weather.pressure"
        const val KEY_TIME_ZONE = "weather.time_zone"
        const val KEY_INSTALLED_AT = "weather.installed_at"
        const val KEY_DAILY = "weather.daily"
        const val KEY_HISTORY = "weather.history"
        const val KEY_HOURLY = "weather.hourly"

        fun encodeDay(day: NormalizedDay): String = listOf(
            day.date,
            day.dayTemperatureCelsius ?: "",
            day.nightTemperatureCelsius ?: "",
            day.dayCondition ?: "",
            day.nightCondition ?: "",
            day.moonPhase ?: "",
        ).joinToString(",")

        fun decodeDay(value: String): NormalizedDay? {
            val parts = value.split(',', limit = 6)
            if (parts.size != 6) return null
            return runCatching {
                NormalizedDay(
                    date = LocalDate.parse(parts[0]),
                    dayTemperatureCelsius = parts[1].toIntOrNull(),
                    nightTemperatureCelsius = parts[2].toIntOrNull(),
                    dayCondition = parts[3].ifBlank { null },
                    nightCondition = parts[4].ifBlank { null },
                    moonPhase = parts[5].ifBlank { null },
                )
            }.getOrNull()
        }

        fun encodeHistory(entry: PressureHistoryEntry): String =
            "${entry.recordedAtMillis},${entry.pressureMmHg}"

        fun decodeHistory(value: String): PressureHistoryEntry? {
            val parts = value.split(',', limit = 2)
            if (parts.size != 2) return null
            return PressureHistoryEntry(
                recordedAtMillis = parts[0].toLongOrNull() ?: return null,
                pressureMmHg = parts[1].toDoubleOrNull() ?: return null,
            )
        }

        fun encodeHourly(hourly: NormalizedHourly): String = listOf(
            hourly.date,
            hourly.time,
            hourly.temperatureCelsius ?: "",
            hourly.illustration?.name ?: "",
        ).joinToString(",")

        fun decodeHourly(value: String): NormalizedHourly? {
            val parts = value.split(',', limit = 4)
            if (parts.size != 4) return null
            return runCatching {
                NormalizedHourly(
                    date = LocalDate.parse(parts[0]),
                    time = LocalTime.parse(parts[1]),
                    temperatureCelsius = parts[2].toIntOrNull(),
                    illustration = parts[3].takeIf(String::isNotBlank)?.let(WeatherIllustration::valueOf),
                )
            }.getOrNull()
        }
    }
}

enum class WeatherFreshness {
    NO_DATA,
    FRESH,
    STALE_EMPTY,
}

enum class WeatherCardSlot {
    YESTERDAY,
    TODAY,
    TOMORROW,
    DAY_AFTER,
}

enum class WeatherIllustration {
    CLEAR,
    CLOUD,
    RAIN,
    SNOW,
    MOON,
    NEUTRAL_CLOUD,
}

enum class PressureDirection {
    UP,
    DOWN,
}

data class WeatherCardProjection(
    val slot: WeatherCardSlot,
    val date: LocalDate,
    val temperatureCelsius: Int?,
    val temperatureText: String?,
    val backgroundHex: String?,
    val illustration: WeatherIllustration?,
    val moonPhase: String?,
    val pressureArrowCount: Int,
    val pressureDirection: PressureDirection?,
    val isTodaySize: Boolean,
)

data class WeatherProjection(
    val cityLabel: String?,
    val apiTimeZone: String?,
    val freshness: WeatherFreshness,
    val cards: List<WeatherCardProjection>,
)

data class WeatherRefreshResult(
    val snapshot: WeatherSnapshot,
    val credentialWasReceived: Boolean,
    val redactedCredential: String,
    val projection: WeatherProjection,
)

data class HourlyForecastCardProjection(
    val date: LocalDate,
    val slotTime: LocalTime,
    val slotTimeText: String,
    val temperatureCelsius: Int,
    val temperatureText: String,
    val backgroundHex: String,
    val illustration: WeatherIllustration,
    val dayOffset: Long,
    val pressureArrowCount: Int = 0,
)

data class HourlyForecastProjection(
    val apiTimeZone: String,
    val cards: List<HourlyForecastCardProjection>,
) {
    val rows: List<List<HourlyForecastCardProjection>>
        get() = cards.chunked(4)
}

data class LongTermForecastCardProjection(
    val date: LocalDate,
    val dateDayText: String,
    val temperatureCelsius: Int,
    val temperatureText: String,
    val backgroundHex: String,
    val illustration: WeatherIllustration,
    val pressureArrowCount: Int = 0,
)

data class LongTermForecastProjection(
    val apiTimeZone: String,
    val cards: List<LongTermForecastCardProjection>,
) {
    val rows: List<List<LongTermForecastCardProjection>>
        get() = cards.chunked(5)
}

interface WeatherReadPort {
    fun snapshot(): WeatherSnapshot?

    fun projection(nowMillis: Long = System.currentTimeMillis()): WeatherProjection

    fun hourlyProjection(nowMillis: Long = System.currentTimeMillis()): HourlyForecastProjection?

    fun longTermProjection(nowMillis: Long = System.currentTimeMillis()): LongTermForecastProjection?
}

enum class WeatherRefreshTrigger {
    LAUNCH,
    LOCATION_CHANGE,
    SCHEDULED,
}

class WeatherCapability(
    private val locationReader: LocationReader,
    private val cacheStore: WeatherCacheStore,
    private val provider: WeatherProvider,
    private val fixtureProvider: WeatherProvider? = null,
) : WeatherReadPort {
    private var lastRefreshFailure: WeatherProviderFailure? = null
    private var projectionSnapshot: ProjectionSnapshot? = null

    private data class ProjectionSnapshot(
        val location: LocationContext?,
        val record: WeatherCacheRecord?,
        val projection: WeatherProjection,
        val builtAtMillis: Long,
        val localDate: LocalDate,
        val daytime: Boolean,
        val nextPressureBoundaryMillis: Long?,
    ) {
        fun canReuse(currentLocation: LocationContext?, nowMillis: Long): Boolean {
            if (location != currentLocation || nowMillis < builtAtMillis) return false
            val zoneId = record?.snapshot?.apiTimeZone?.toZoneIdOrUtc()
                ?: currentLocation?.apiTimeZone?.toZoneIdOrUtc()
                ?: ZoneId.of("UTC")
            val zonedNow = Instant.ofEpochMilli(nowMillis).atZone(zoneId)
            if (zonedNow.toLocalDate() != localDate || isDaytime(nowMillis, zoneId) != daytime) return false
            val updatedAt = record?.snapshot?.updatedAtMillis
            if (updatedAt != null && nowMillis > updatedAt + FRESHNESS_WINDOW_MILLIS) return false
            return nextPressureBoundaryMillis == null || nowMillis < nextPressureBoundaryMillis
        }
    }

    fun inlineErrorMessage(): String? = when (lastRefreshFailure) {
        WeatherProviderFailure.INVALID_CREDENTIAL -> "Неверный API-ключ"
        WeatherProviderFailure.NETWORK -> "Нет подключения"
        WeatherProviderFailure.UNKNOWN_CITY -> "Город не найден"
        null -> if (locationReader is WeatherAccessReader && !locationReader.hasWeatherApiKey()) {
            "API-ключ не указан"
        } else {
            null
        }
    }
    override fun snapshot(): WeatherSnapshot? = cacheStore.loadRecord()?.snapshot

    override fun projection(nowMillis: Long): WeatherProjection {
        val location = locationReader.currentLocation()
        val cached = projectionSnapshot
        if (cached != null && cached.canReuse(location, nowMillis)) return cached.projection
        val record = cacheStore.loadRecord()
        return rebuildProjection(location, record, nowMillis)
    }

    private fun rebuildProjection(
        location: LocationContext?,
        record: WeatherCacheRecord?,
        nowMillis: Long,
    ): WeatherProjection {
        val zoneId = record?.snapshot?.apiTimeZone?.toZoneIdOrUtc()
            ?: location?.apiTimeZone?.toZoneIdOrUtc()
            ?: ZoneId.of("UTC")
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val daytime = isDaytime(nowMillis, zoneId)
        val dates = WeatherCardSlot.entries.mapIndexed { index, slot ->
            slot to today.plusDays(index - 1L)
        }
        val projection = when {
            record == null || location == null -> WeatherProjection(
                cityLabel = location?.cityLabel,
                apiTimeZone = location?.apiTimeZone,
                freshness = WeatherFreshness.NO_DATA,
                cards = dates.map { (slot, date) -> emptyCard(slot, date) },
            )
            (nowMillis - record.snapshot.updatedAtMillis).coerceAtLeast(0L) > FRESHNESS_WINDOW_MILLIS -> WeatherProjection(
                cityLabel = record.snapshot.cityLabel,
                apiTimeZone = record.snapshot.apiTimeZone,
                freshness = WeatherFreshness.STALE_EMPTY,
                cards = dates.map { (slot, date) -> emptyCard(slot, date) },
            )
            else -> {
                val byDate = record.daily.associateBy { it.date }
                WeatherProjection(
                    cityLabel = record.snapshot.cityLabel,
                    apiTimeZone = record.snapshot.apiTimeZone,
                    freshness = WeatherFreshness.FRESH,
                    cards = dates.map { (slot, date) ->
                        val day = byDate[date]
                        val temperature = when {
                            slot == WeatherCardSlot.TODAY && daytime -> record.snapshot.temperatureCelsius
                            slot == WeatherCardSlot.TODAY -> day?.nightTemperatureCelsius ?: record.snapshot.temperatureCelsius
                            daytime -> day?.dayTemperatureCelsius
                            else -> day?.nightTemperatureCelsius
                        }
                        val condition = when {
                            slot == WeatherCardSlot.TODAY && daytime -> record.snapshot.condition
                            slot == WeatherCardSlot.TODAY -> day?.nightCondition ?: record.snapshot.condition
                            daytime -> day?.dayCondition
                            else -> day?.nightCondition
                        }
                        val moon = if (!daytime && day != null) day.moonPhase ?: "regular" else null
                        val trend = when (slot) {
                            WeatherCardSlot.TODAY -> pressureTrend(record.history, nowMillis)
                            WeatherCardSlot.YESTERDAY -> yesterdayTrend(record.history, date, zoneId)
                            else -> PressureTrend(0, null)
                        }
                        WeatherCardProjection(
                            slot = slot,
                            date = date,
                            temperatureCelsius = temperature,
                            temperatureText = temperature?.let(::temperatureText),
                            backgroundHex = temperature?.let(TemperaturePalette::colorFor),
                            illustration = temperature?.let { conditionIllustration(condition, daytime) },
                            moonPhase = moon,
                            pressureArrowCount = trend.count,
                            pressureDirection = trend.direction,
                            isTodaySize = slot == WeatherCardSlot.TODAY,
                        )
                    },
                )
            }
        }
        projectionSnapshot = ProjectionSnapshot(
            location = location,
            record = record,
            projection = projection,
            builtAtMillis = nowMillis,
            localDate = today,
            daytime = daytime,
            nextPressureBoundaryMillis = if (projection.freshness == WeatherFreshness.FRESH) {
                nextPressureBoundaryMillis(record?.history.orEmpty(), nowMillis)
            } else {
                null
            },
        )
        return projection
    }

    override fun hourlyProjection(nowMillis: Long): HourlyForecastProjection? {
        val record = cacheStore.loadRecord() ?: return null
        val zoneId = record.snapshot.apiTimeZone.toZoneIdOrUtc()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val expected = hourlyKeys(today)
        val byKey = record.hourly.associateBy { it.date to it.time }
        if (record.hourly.size != expected.size || expected.any { it !in byKey }) return null
        val age = (nowMillis - record.snapshot.updatedAtMillis).coerceAtLeast(0L)
        if (age > FRESHNESS_WINDOW_MILLIS) return null
        val cards = expected.map { (date, time) ->
            val value = byKey.getValue(date to time)
            val temperature = value.temperatureCelsius ?: return null
            val illustration = value.illustration ?: return null
            HourlyForecastCardProjection(
                date = date,
                slotTime = time,
                slotTimeText = HOURLY_TIME_FORMAT.format(time),
                temperatureCelsius = temperature,
                temperatureText = temperatureText(temperature),
                backgroundHex = TemperaturePalette.colorFor(temperature),
                illustration = illustration,
                dayOffset = java.time.temporal.ChronoUnit.DAYS.between(today, date),
            )
        }
        return HourlyForecastProjection(record.snapshot.apiTimeZone, cards)
    }

    override fun longTermProjection(nowMillis: Long): LongTermForecastProjection? {
        val record = cacheStore.loadRecord() ?: return null
        val zoneId = record.snapshot.apiTimeZone.toZoneIdOrUtc()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val expectedDates = (0L until LONG_TERM_DAYS).map(today::plusDays)
        if (record.daily.size != LONG_TERM_DAYS.toInt() || record.daily.map { it.date } != expectedDates) return null
        val age = (nowMillis - record.snapshot.updatedAtMillis).coerceAtLeast(0L)
        if (age > FRESHNESS_WINDOW_MILLIS) return null

        val daytime = isDaytime(nowMillis, zoneId)
        val cards = record.daily.map { day ->
            val temperature = if (daytime) day.dayTemperatureCelsius else day.nightTemperatureCelsius
            val condition = if (daytime) day.dayCondition else day.nightCondition
            if (temperature == null || condition.isNullOrBlank()) return null
            LongTermForecastCardProjection(
                date = day.date,
                dateDayText = "%02d".format(day.date.dayOfMonth),
                temperatureCelsius = temperature,
                temperatureText = temperatureText(temperature),
                backgroundHex = TemperaturePalette.colorFor(temperature),
                illustration = conditionIllustration(condition, daytime),
            )
        }
        return LongTermForecastProjection(record.snapshot.apiTimeZone, cards)
    }

    /** Normalizes one successful provider response and atomically replaces the owner cache. */
    fun refresh(
        request: WeatherProviderRequest,
        nowMillis: Long,
    ): WeatherRefreshResult? = refreshWithProvider(provider, request, nowMillis)

    private fun refreshWithProvider(
        sourceProvider: WeatherProvider,
        request: WeatherProviderRequest,
        nowMillis: Long,
    ): WeatherRefreshResult? {
        val location = locationReader.currentLocation() ?: return null
        val result = sourceProvider.fetch(request)
        if (result.failure != null) {
            lastRefreshFailure = result.failure
            return null
        }
        val structuredData = result.weatherData
        val data = structuredData ?: ProviderWeatherData(
            apiTimeZone = location.apiTimeZone,
            current = com.hozayushka.app.adapters.weather.ProviderCurrentWeather(
                temperatureCelsius = result.payload.temperatureCelsius,
                pressureMmHg = 0.0,
                condition = result.payload.condition,
            ),
            daily = emptyList(),
        )
        val previous = cacheStore.loadRecord()
        if (structuredData != null) {
            val expectedCityDate = Instant.ofEpochMilli(nowMillis)
                .atZone(data.apiTimeZone.toZoneIdOrUtc())
                .toLocalDate()
            if (!data.current.pressureMmHg.isFinite() || data.daily.none {
                    it.date == expectedCityDate &&
                        it.dayTemperatureCelsius != null &&
                        it.nightTemperatureCelsius != null
                } ||
                (data.daily.size >= LONG_TERM_DAYS.toInt() && !hasCompleteDaily(data.daily, expectedCityDate)) ||
                (data.hourly.isNotEmpty() && !hasCompleteHourly(data.hourly, expectedCityDate)) ||
                (data.hourly.isEmpty() && previous?.hourly?.isNotEmpty() == true)) {
                return null
            }
        }
        val normalized = normalize(data, location, nowMillis)
        val history = (previous?.history.orEmpty() + PressureHistoryEntry(nowMillis, data.current.pressureMmHg))
            .filter { it.recordedAtMillis >= nowMillis - HISTORY_WINDOW_MILLIS }
            .distinctBy { it.recordedAtMillis }
            .sortedBy { it.recordedAtMillis }
        val daily = if (data.daily.size >= LONG_TERM_DAYS.toInt()) {
            normalized.daily
        } else {
            previous?.daily?.takeIf { it.size == LONG_TERM_DAYS.toInt() } ?: normalized.daily
        }
        val record = WeatherCacheRecord(
            snapshot = normalized.snapshot,
            daily = daily,
            history = history,
            installedAtMillis = previous?.installedAtMillis ?: nowMillis,
            hourly = normalized.hourly,
        )
        cacheStore.saveRecord(record)
        lastRefreshFailure = null
        val projection = rebuildProjection(location, record, nowMillis)
        return WeatherRefreshResult(
            snapshot = record.snapshot,
            credentialWasReceived = result.credentialWasReceived,
            redactedCredential = result.redactedCredential,
            projection = projection,
        )
    }

    fun refreshIfNeeded(
        nowMillis: Long,
        networkAvailable: Boolean,
        trigger: WeatherRefreshTrigger,
        requireStoredCredential: Boolean = false,
    ): WeatherRefreshResult? {
        val access = locationReader as? WeatherAccessReader
        if (requireStoredCredential && (access == null || !access.hasWeatherApiKey())) {
            lastRefreshFailure = null
            return null
        }
        if (!networkAvailable) {
            lastRefreshFailure = WeatherProviderFailure.NETWORK
            return null
        }
        val last = cacheStore.loadRecord()?.snapshot?.updatedAtMillis
        if (trigger == WeatherRefreshTrigger.SCHEDULED && last != null && nowMillis - last < REFRESH_INTERVAL_MILLIS) {
            return null
        }
        val location = locationReader.currentLocation() ?: return null
        val request = if (requireStoredCredential) {
            access?.withWeatherApiKey { key ->
                WeatherProviderRequest.fromUserInput(key, location.latitude, location.longitude)
            }
        } else {
            storedRequest(location) ?: WeatherProviderRequest.fromSyntheticProbe(location.latitude, location.longitude)
        }
        if (request == null) {
            lastRefreshFailure = WeatherProviderFailure.INVALID_CREDENTIAL
            return null
        }
        return refresh(request, nowMillis)
    }

    /** Existing Foundation probe route; request construction remains inside Weather Context. */
    fun refreshFoundationFixture(nowMillis: Long = System.currentTimeMillis()): WeatherRefreshResult? {
        val location = locationReader.currentLocation() ?: return null
        return refreshWithProvider(
            sourceProvider = fixtureProvider ?: provider,
            request = WeatherProviderRequest.fromSyntheticProbe(location.latitude, location.longitude),
            nowMillis = nowMillis,
        )
    }

    fun resetFoundationState() {
        cacheStore.reset()
        lastRefreshFailure = null
        projectionSnapshot = null
    }

    private fun storedRequest(location: LocationContext): WeatherProviderRequest? {
        val access = locationReader as? WeatherAccessReader ?: return null
        return access.withWeatherApiKey { key ->
            WeatherProviderRequest.fromUserInput(key, location.latitude, location.longitude)
        }
    }

    private fun normalize(
        data: ProviderWeatherData,
        location: LocationContext,
        nowMillis: Long,
    ): NormalizedWeather {
        val currentCondition = normalizeCondition(data.current.condition)
        return NormalizedWeather(
            snapshot = WeatherSnapshot(
                cityLabel = location.cityLabel,
                temperatureCelsius = data.current.temperatureCelsius,
                condition = currentCondition,
                source = "redacted-provider",
                updatedAtMillis = nowMillis,
                pressureMmHg = data.current.pressureMmHg,
                apiTimeZone = data.apiTimeZone.ifBlank { location.apiTimeZone },
            ),
            daily = data.daily.map(::normalizeDay),
            hourly = normalizeSelectedHourly(data.hourly, data.apiTimeZone, nowMillis),
        )
    }

    private fun normalizeDay(day: ProviderDailyWeather): NormalizedDay = NormalizedDay(
        date = day.date,
        dayTemperatureCelsius = day.dayTemperatureCelsius,
        nightTemperatureCelsius = day.nightTemperatureCelsius,
        dayCondition = normalizeCondition(day.dayCondition),
        nightCondition = normalizeCondition(day.nightCondition),
        moonPhase = day.moonPhase?.takeIf(String::isNotBlank),
    )

    private data class NormalizedWeather(
        val snapshot: WeatherSnapshot,
        val daily: List<NormalizedDay>,
        val hourly: List<NormalizedHourly>,
    )
}

private val HOURLY_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private val ACCEPTED_HOURLY_TIMES = listOf(
    LocalTime.of(6, 0),
    LocalTime.of(9, 0),
    LocalTime.of(12, 0),
    LocalTime.of(15, 0),
    LocalTime.of(18, 0),
    LocalTime.of(21, 0),
    LocalTime.MIDNIGHT,
    LocalTime.of(3, 0),
)

private fun hourlyKeys(today: LocalDate): List<Pair<LocalDate, LocalTime>> =
    ACCEPTED_HOURLY_TIMES.mapIndexed { index, time ->
        today.plusDays(if (index >= 6) 1L else 0L) to time
    }

private fun normalizeSelectedHourly(
    hourly: List<ProviderHourlyWeather>,
    apiTimeZone: String,
    nowMillis: Long,
): List<NormalizedHourly> {
    if (hourly.isEmpty()) return emptyList()
    val today = Instant.ofEpochMilli(nowMillis)
        .atZone(apiTimeZone.toZoneIdOrUtc())
        .toLocalDate()
    val byKey = hourly.associateBy { it.date to it.time }
    return hourlyKeys(today).map { key -> normalizeHourly(byKey.getValue(key)) }
}

private fun normalizeHourly(hourly: ProviderHourlyWeather): NormalizedHourly = NormalizedHourly(
    date = hourly.date,
    time = hourly.time,
    temperatureCelsius = hourly.temperatureCelsius,
    illustration = hourlyIllustration(normalizeCondition(hourly.condition)),
)

private fun hasCompleteHourly(hourly: List<ProviderHourlyWeather>, today: LocalDate): Boolean {
    val expected = hourlyKeys(today)
    val byKey = hourly.associateBy { it.date to it.time }
    return expected.all { key ->
        val value = byKey[key]
        value?.temperatureCelsius != null && !value.condition.isNullOrBlank()
    }
}

private fun hasCompleteDaily(daily: List<ProviderDailyWeather>, today: LocalDate): Boolean {
    val expected = (0L until LONG_TERM_DAYS).map(today::plusDays)
    if (daily.size != LONG_TERM_DAYS.toInt() || daily.map { it.date } != expected) return false
    return daily.all { day ->
        day.dayTemperatureCelsius != null &&
            day.nightTemperatureCelsius != null &&
            !day.dayCondition.isNullOrBlank() &&
            !day.nightCondition.isNullOrBlank()
    }
}

private data class PressureTrend(
    val count: Int,
    val direction: PressureDirection?,
)

private fun nextPressureBoundaryMillis(
    history: List<PressureHistoryEntry>,
    nowMillis: Long,
): Long? = history.asSequence()
    .flatMap { entry ->
        sequenceOf(
            entry.recordedAtMillis + 3L * 60L * 60L * 1_000L,
            entry.recordedAtMillis + 12L * 60L * 60L * 1_000L,
        )
    }
    .filter { it > nowMillis }
    .minOrNull()

private fun pressureTrend(history: List<PressureHistoryEntry>, nowMillis: Long): PressureTrend {
    val current = history.maxByOrNull { it.recordedAtMillis } ?: return PressureTrend(0, null)
    val threeHour = history.filter { it.recordedAtMillis <= nowMillis - 3L * 60L * 60L * 1_000L }
        .maxByOrNull { it.recordedAtMillis }
    val threeDelta = threeHour?.let { current.pressureMmHg - it.pressureMmHg } ?: return PressureTrend(0, null)
    if (threeDelta == 0.0) {
        val twelveHour = history.filter { it.recordedAtMillis <= nowMillis - 12L * 60L * 60L * 1_000L }
            .maxByOrNull { it.recordedAtMillis }
        val twelveDelta = twelveHour?.let { current.pressureMmHg - it.pressureMmHg } ?: return PressureTrend(0, null)
        return if (twelveDelta == 0.0) PressureTrend(0, null)
        else PressureTrend(1, if (twelveDelta > 0) PressureDirection.UP else PressureDirection.DOWN)
    }
    val absolute = kotlin.math.abs(threeDelta)
    val count = when {
        absolute <= 1.5 -> 0
        absolute <= 3.0 -> 1
        else -> 2
    }
    return PressureTrend(count, if (count == 0) null else if (threeDelta > 0) PressureDirection.UP else PressureDirection.DOWN)
}

private fun yesterdayTrend(history: List<PressureHistoryEntry>, yesterday: LocalDate, zoneId: ZoneId): PressureTrend {
    val dayEntries = history.filter {
        Instant.ofEpochMilli(it.recordedAtMillis).atZone(zoneId).toLocalDate() == yesterday
    }.sortedBy { it.recordedAtMillis }
    if (dayEntries.size < 2) return PressureTrend(0, null)
    val delta = dayEntries.zipWithNext()
        .maxByOrNull { (previous, next) -> kotlin.math.abs(next.pressureMmHg - previous.pressureMmHg) }
        ?.let { (previous, next) -> next.pressureMmHg - previous.pressureMmHg }
        ?: return PressureTrend(0, null)
    val absolute = kotlin.math.abs(delta)
    val count = when {
        absolute <= 1.5 -> 0
        absolute <= 3.0 -> 1
        else -> 2
    }
    return PressureTrend(count, if (count == 0) null else if (delta > 0) PressureDirection.UP else PressureDirection.DOWN)
}

private fun emptyCard(slot: WeatherCardSlot, date: LocalDate) = WeatherCardProjection(
    slot = slot,
    date = date,
    temperatureCelsius = null,
    temperatureText = null,
    backgroundHex = null,
    illustration = null,
    moonPhase = null,
    pressureArrowCount = 0,
    pressureDirection = null,
    isTodaySize = slot == WeatherCardSlot.TODAY,
)

private fun temperatureText(temperature: Int): String = when {
    temperature in -4..4 && temperature > 0 -> "+${temperature}°"
    temperature in -4..4 -> "${temperature}°"
    else -> "${kotlin.math.abs(temperature)}°"
}

private fun normalizeCondition(condition: String?): String = when (condition?.lowercase()) {
    "clear", "sunny" -> "clear"
    "cloud", "partly-cloudy", "overcast" -> "cloud"
    "rain", "drizzle", "showers" -> "rain"
    "snow" -> "snow"
    else -> "neutral-cloud"
}

private fun conditionIllustration(condition: String?, daytime: Boolean): WeatherIllustration = when {
    !daytime -> WeatherIllustration.MOON
    condition == "clear" -> WeatherIllustration.CLEAR
    condition == "rain" -> WeatherIllustration.RAIN
    condition == "snow" -> WeatherIllustration.SNOW
    condition == "cloud" -> WeatherIllustration.CLOUD
    else -> WeatherIllustration.NEUTRAL_CLOUD
}

private fun hourlyIllustration(condition: String): WeatherIllustration = when (condition) {
    "clear" -> WeatherIllustration.CLEAR
    "rain" -> WeatherIllustration.RAIN
    "snow" -> WeatherIllustration.SNOW
    "cloud" -> WeatherIllustration.CLOUD
    else -> WeatherIllustration.NEUTRAL_CLOUD
}

private fun isDaytime(nowMillis: Long, zoneId: ZoneId): Boolean {
    val hour = Instant.ofEpochMilli(nowMillis).atZone(zoneId).hour
    return hour in 6..17
}

private fun String.toZoneIdOrUtc(): ZoneId = runCatching { ZoneId.of(this) }.getOrDefault(ZoneId.of("UTC"))
