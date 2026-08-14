package com.hozayushka.app.weather

import android.content.SharedPreferences
import com.hozayushka.app.adapters.weather.ProviderDailyWeather
import com.hozayushka.app.adapters.weather.ProviderHourlyWeather
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.adapters.weather.WeatherProviderCapabilities
import com.hozayushka.app.adapters.weather.WeatherProviderFailure
import com.hozayushka.app.adapters.weather.WeatherProviderId
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.adapters.weather.WeatherFixture
import com.hozayushka.app.settings.CoherentWeatherAccessReader
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.LocationReader
import com.hozayushka.app.settings.WeatherAccessReader
import com.hozayushka.app.settings.WeatherProviderSelection
import com.hozayushka.app.settings.WeatherRefreshAccessProjection
import com.hozayushka.app.settings.WeatherRefreshAccessSnapshot
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val FRESHNESS_WINDOW_MILLIS = 24L * 60L * 60L * 1_000L
private const val REFRESH_INTERVAL_MILLIS = 30L * 60L * 1_000L
private const val HISTORY_WINDOW_MILLIS = 7L * 24L * 60L * 60L * 1_000L
private const val LONG_TERM_DAYS = 10L
private const val HPA_TO_MMHG = 0.75006157584566

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
    val sunrise: LocalTime? = null,
    val sunset: LocalTime? = null,
)

data class PressureHistoryEntry(
    val recordedAtMillis: Long,
    val pressureMmHg: Double,
    val provider: WeatherProviderId,
    val locationIdentity: String,
)

data class WeatherCacheRecord(
    val snapshot: WeatherSnapshot,
    val daily: List<NormalizedDay>,
    val history: List<PressureHistoryEntry>,
    val installedAtMillis: Long,
    val hourly: List<NormalizedHourly> = emptyList(),
    val provider: WeatherProviderId,
    val locationIdentity: String,
    val providerCapabilities: WeatherProviderCapabilities = provider.capabilities,
)

data class NormalizedHourly(
    val date: LocalDate,
    val time: LocalTime,
    val temperatureCelsius: Int?,
    val illustration: WeatherIllustration?,
)

interface WeatherCacheStore {
    fun loadRecord(): WeatherCacheRecord?

    fun saveRecord(record: WeatherCacheRecord)

    fun reset()
}

class InMemoryWeatherCacheStore : WeatherCacheStore {
    private var record: WeatherCacheRecord? = null

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
    override fun loadRecord(): WeatherCacheRecord? {
        if (!preferences.contains(KEY_CITY)) return null
        val provider = WeatherProviderId.fromStorage(preferences.getString(KEY_PROVIDER, null)) ?: return null
        val locationIdentity = preferences.getString(KEY_LOCATION_ID, null)?.takeIf(String::isNotBlank) ?: return null
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
            provider = provider,
            locationIdentity = locationIdentity,
            providerCapabilities = provider.capabilities,
        )
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
            .putString(KEY_PROVIDER, record.provider.storageId)
            .putString(KEY_LOCATION_ID, record.locationIdentity)
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
            .remove(KEY_PROVIDER)
            .remove(KEY_LOCATION_ID)
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
        const val KEY_PROVIDER = "weather.provider"
        const val KEY_LOCATION_ID = "weather.location_identity"

        fun encodeDay(day: NormalizedDay): String = listOf(
            day.date,
            day.dayTemperatureCelsius ?: "",
            day.nightTemperatureCelsius ?: "",
            day.dayCondition ?: "",
            day.nightCondition ?: "",
            day.moonPhase ?: "",
            day.sunrise ?: "",
            day.sunset ?: "",
        ).joinToString(",")

        fun decodeDay(value: String): NormalizedDay? {
            val parts = value.split(',', limit = 8)
            if (parts.size !in 6..8) return null
            return runCatching {
                NormalizedDay(
                    date = LocalDate.parse(parts[0]),
                    dayTemperatureCelsius = parts[1].toIntOrNull(),
                    nightTemperatureCelsius = parts[2].toIntOrNull(),
                    dayCondition = parts[3].ifBlank { null },
                    nightCondition = parts[4].ifBlank { null },
                    moonPhase = parts[5].ifBlank { null },
                    sunrise = parts.getOrNull(6)?.takeIf(String::isNotBlank)?.let(LocalTime::parse),
                    sunset = parts.getOrNull(7)?.takeIf(String::isNotBlank)?.let(LocalTime::parse),
                )
            }.getOrNull()
        }

        fun encodeHistory(entry: PressureHistoryEntry): String = listOf(
            entry.provider.storageId,
            entry.locationIdentity,
            entry.recordedAtMillis,
            entry.pressureMmHg,
        ).joinToString(",")

        fun decodeHistory(value: String): PressureHistoryEntry? {
            val parts = value.split(',', limit = 4)
            if (parts.size != 4) return null
            return PressureHistoryEntry(
                provider = WeatherProviderId.fromStorage(parts[0]) ?: return null,
                locationIdentity = parts[1],
                recordedAtMillis = parts[2].toLongOrNull() ?: return null,
                pressureMmHg = parts[3].toDoubleOrNull() ?: return null,
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
    val provider: WeatherProviderId,
    val credentialWasReceived: Boolean,
    val redactedCredential: String?,
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
    val temperatureCelsius: Int?,
    val temperatureText: String?,
    val backgroundHex: String?,
    val illustration: WeatherIllustration?,
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
    PROVIDER_CHANGE,
    SCHEDULED,
}

class WeatherCapability(
    private val locationReader: LocationReader,
    private val cacheStore: WeatherCacheStore,
    private val openMeteoProvider: WeatherProvider,
    private val openWeatherProvider: WeatherProvider,
    private val fixtureProvider: WeatherFixture? = null,
) : WeatherReadPort {
    private data class AttributedProviderFailure(
        val provider: WeatherProviderId,
        val reason: WeatherProviderFailure,
    )

    private data class ProviderRequestIdentity(
        val provider: WeatherProviderId,
        val location: LocationContext,
        val locationIdentity: String,
    )

    private var lastRefreshFailure: AttributedProviderFailure? = null
    private var projectionSnapshot: ProjectionSnapshot? = null

    private data class ProjectionSnapshot(
        val provider: WeatherProviderId,
        val location: LocationContext?,
        val locationIdentity: String?,
        val record: WeatherCacheRecord?,
        val projection: WeatherProjection,
        val builtAtMillis: Long,
        val localDate: LocalDate,
        val daytime: Boolean,
        val nextPressureBoundaryMillis: Long?,
    ) {
        fun canReuse(
            currentProvider: WeatherProviderId,
            currentLocation: LocationContext?,
            currentLocationIdentity: String?,
            nowMillis: Long,
        ): Boolean {
            if (provider != currentProvider || location != currentLocation || locationIdentity != currentLocationIdentity) {
                return false
            }
            if (nowMillis < builtAtMillis) return false
            val zoneId = record?.snapshot?.apiTimeZone?.toZoneIdOrUtc()
                ?: currentLocation?.apiTimeZone?.toZoneIdOrUtc()
                ?: ZoneId.of("UTC")
            val zonedNow = Instant.ofEpochMilli(nowMillis).atZone(zoneId)
            if (zonedNow.toLocalDate() != localDate || isDaytime(nowMillis, zoneId, record?.daily.orEmpty()) != daytime) {
                return false
            }
            val updatedAt = record?.snapshot?.updatedAtMillis
            if (updatedAt != null && nowMillis > updatedAt + FRESHNESS_WINDOW_MILLIS) return false
            return nextPressureBoundaryMillis == null || nowMillis < nextPressureBoundaryMillis
        }
    }

    fun inlineErrorMessage(): String? {
        val selectedProvider = selectedProvider()
        val failure = lastRefreshFailure?.takeIf { it.provider == selectedProvider }
        if (failure != null) {
            val message = when (failure.reason) {
                WeatherProviderFailure.MISSING_CREDENTIAL -> "API-ключ не указан"
                WeatherProviderFailure.INVALID_CREDENTIAL -> "Неверный API-ключ"
                WeatherProviderFailure.UNKNOWN_CITY -> "Город не найден"
                WeatherProviderFailure.NETWORK,
                WeatherProviderFailure.TIMEOUT,
                WeatherProviderFailure.MALFORMED_RESPONSE,
                WeatherProviderFailure.PROVIDER_MISMATCH,
                -> "Нет подключения"
            }
            return "${failure.provider.displayName}: $message"
        }
        val access = locationReader as? WeatherAccessReader
        return if (selectedProvider == WeatherProviderId.OPEN_WEATHER && access?.hasWeatherApiKey() != true) {
            "${WeatherProviderId.OPEN_WEATHER.displayName}: API-ключ не указан"
        } else {
            null
        }
    }

    override fun snapshot(): WeatherSnapshot? = matchingRecord()?.snapshot

    override fun projection(nowMillis: Long): WeatherProjection {
        val provider = selectedProvider()
        val location = locationReader.currentLocation()
        val locationIdentity = location?.weatherLocationIdentity()
        val cached = projectionSnapshot
        if (cached != null && cached.canReuse(provider, location, locationIdentity, nowMillis)) return cached.projection
        val record = matchingRecord(provider, locationIdentity)
        return rebuildProjection(provider, location, locationIdentity, record, nowMillis)
    }

    private fun rebuildProjection(
        provider: WeatherProviderId,
        location: LocationContext?,
        locationIdentity: String?,
        record: WeatherCacheRecord?,
        nowMillis: Long,
    ): WeatherProjection {
        val zoneId = record?.snapshot?.apiTimeZone?.toZoneIdOrUtc()
            ?: location?.apiTimeZone?.toZoneIdOrUtc()
            ?: ZoneId.of("UTC")
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val daytime = isDaytime(nowMillis, zoneId, record?.daily.orEmpty())
        val dates = WeatherCardSlot.entries.mapIndexed { index, slot ->
            slot to today.plusDays(index - 1L)
        }
        val matchingHistory = record?.history.orEmpty().filter {
            it.provider == provider && it.locationIdentity == locationIdentity
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
                            WeatherCardSlot.TODAY -> pressureTrend(matchingHistory, nowMillis)
                            WeatherCardSlot.YESTERDAY -> yesterdayTrend(matchingHistory, date, zoneId)
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
            provider = provider,
            location = location,
            locationIdentity = locationIdentity,
            record = record,
            projection = projection,
            builtAtMillis = nowMillis,
            localDate = today,
            daytime = daytime,
            nextPressureBoundaryMillis = if (projection.freshness == WeatherFreshness.FRESH) {
                nextPressureBoundaryMillis(matchingHistory, nowMillis)
            } else {
                null
            },
        )
        return projection
    }

    override fun hourlyProjection(nowMillis: Long): HourlyForecastProjection? {
        val record = matchingRecord() ?: return null
        val zoneId = record.snapshot.apiTimeZone.toZoneIdOrUtc()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val expected = hourlyKeys(today)
        val byKey = record.hourly.associateBy { it.date to it.time }
        if (expected.any { it !in byKey }) return null
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
        val record = matchingRecord() ?: return null
        val zoneId = record.snapshot.apiTimeZone.toZoneIdOrUtc()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val expectedDates = (0L until LONG_TERM_DAYS).map(today::plusDays)
        val supportedRecords = record.provider.capabilities.supportedDailyRecords
        if (record.daily.size != supportedRecords || record.daily.map { it.date } != expectedDates.take(supportedRecords)) return null
        if (record.daily.any {
                it.dayTemperatureCelsius == null ||
                    it.nightTemperatureCelsius == null ||
                    it.dayCondition.isNullOrBlank() ||
                    it.nightCondition.isNullOrBlank()
            }) return null
        val age = (nowMillis - record.snapshot.updatedAtMillis).coerceAtLeast(0L)
        if (age > FRESHNESS_WINDOW_MILLIS) return null

        val daytime = isDaytime(nowMillis, zoneId, record.daily)
        val cards = expectedDates.mapIndexed { index, date ->
            val day = record.daily.getOrNull(index)
            val temperature = day?.let { if (daytime) it.dayTemperatureCelsius else it.nightTemperatureCelsius }
            val condition = day?.let { if (daytime) it.dayCondition else it.nightCondition }
            LongTermForecastCardProjection(
                date = date,
                dateDayText = "%02d".format(date.dayOfMonth),
                temperatureCelsius = temperature,
                temperatureText = temperature?.let(::temperatureText),
                backgroundHex = temperature?.let(TemperaturePalette::colorFor),
                illustration = condition?.takeIf(String::isNotBlank)?.let { conditionIllustration(it, daytime) },
            )
        }
        return LongTermForecastProjection(record.snapshot.apiTimeZone, cards)
    }

    /** Test/fixture seam that still obeys the currently selected provider identity. */
    fun refresh(
        request: WeatherProviderRequest,
        nowMillis: Long,
    ): WeatherRefreshResult? = withRefreshAccessSnapshot { accessSnapshot ->
        val requestIdentity = accessSnapshot.projection.toRequestIdentity()
        val provider = requestIdentity.provider
        val adapter = adapterFor(provider) ?: return@withRefreshAccessSnapshot rejectMismatchedAdapter(provider)
        refreshWithProvider(requestIdentity, adapter, request, nowMillis)
    }

    private fun refreshWithProvider(
        requestIdentity: ProviderRequestIdentity,
        sourceProvider: WeatherProvider,
        request: WeatherProviderRequest,
        nowMillis: Long,
    ): WeatherRefreshResult? {
        val expectedProvider = requestIdentity.provider
        if (sourceProvider.providerId != expectedProvider) return rejectMismatchedAdapter(expectedProvider)
        if (expectedProvider == WeatherProviderId.OPEN_METEO && request.hasCredential()) {
            lastRefreshFailure = AttributedProviderFailure(expectedProvider, WeatherProviderFailure.PROVIDER_MISMATCH)
            return null
        }
        val result = sourceProvider.fetch(request)
        return acceptProviderResultIfCurrent(requestIdentity, result, nowMillis)
    }

    private fun acceptProviderResultIfCurrent(
        requestIdentity: ProviderRequestIdentity,
        result: WeatherProviderResult,
        nowMillis: Long,
    ): WeatherRefreshResult? {
        val currentAccess = currentWeatherRefreshAccessProjection() ?: return null
        if (
            currentAccess.selectedProvider.toProviderId() != requestIdentity.provider ||
            currentAccess.location.weatherLocationIdentity() != requestIdentity.locationIdentity
        ) {
            return null
        }
        return acceptProviderResult(requestIdentity, result, nowMillis)
    }

    private fun acceptProviderResult(
        requestIdentity: ProviderRequestIdentity,
        result: WeatherProviderResult,
        nowMillis: Long,
    ): WeatherRefreshResult? {
        val expectedProvider = requestIdentity.provider
        val location = requestIdentity.location
        if (result.provider != expectedProvider) {
            lastRefreshFailure = AttributedProviderFailure(expectedProvider, WeatherProviderFailure.PROVIDER_MISMATCH)
            return null
        }
        if (result.failure != null) {
            lastRefreshFailure = AttributedProviderFailure(expectedProvider, result.failure)
            return null
        }
        val structuredData = result.weatherData
        val data = structuredData ?: ProviderWeatherData(
            apiTimeZone = location.apiTimeZone,
            current = com.hozayushka.app.adapters.weather.ProviderCurrentWeather(
                temperatureCelsius = result.payload.temperatureCelsius,
                pressureHpa = 0.0,
                condition = result.payload.condition,
            ),
            daily = emptyList(),
        )
        if (structuredData != null && !validStructuredData(data, nowMillis)) {
            lastRefreshFailure = AttributedProviderFailure(expectedProvider, WeatherProviderFailure.MALFORMED_RESPONSE)
            return null
        }

        val locationIdentity = requestIdentity.locationIdentity
        val previous = cacheStore.loadRecord()
        val matchingPrevious = previous?.takeIf {
            it.provider == expectedProvider && it.locationIdentity == locationIdentity
        }
        val normalized = normalize(expectedProvider, data, location, nowMillis)
        val historyEntry = PressureHistoryEntry(
            recordedAtMillis = nowMillis,
            pressureMmHg = data.current.pressureHpa * HPA_TO_MMHG,
            provider = expectedProvider,
            locationIdentity = locationIdentity,
        )
        val history = (previous?.history.orEmpty() + historyEntry)
            .filter { it.recordedAtMillis >= nowMillis - HISTORY_WINDOW_MILLIS }
            .distinctBy { listOf(it.provider, it.locationIdentity, it.recordedAtMillis) }
            .sortedBy { it.recordedAtMillis }
        val daily = retainCompleteDailySubset(expectedProvider, normalized.daily, matchingPrevious?.daily)
        val hourly = retainCompleteHourlySubset(normalized.hourly, matchingPrevious?.hourly, data.apiTimeZone, nowMillis)
        val record = WeatherCacheRecord(
            snapshot = normalized.snapshot,
            daily = daily,
            history = history,
            installedAtMillis = previous?.installedAtMillis ?: nowMillis,
            hourly = hourly,
            provider = expectedProvider,
            locationIdentity = locationIdentity,
            providerCapabilities = expectedProvider.capabilities,
        )
        cacheStore.saveRecord(record)
        lastRefreshFailure = null
        val projection = rebuildProjection(expectedProvider, location, locationIdentity, record, nowMillis)
        return WeatherRefreshResult(
            snapshot = record.snapshot,
            provider = expectedProvider,
            credentialWasReceived = result.credentialWasReceived,
            redactedCredential = result.redactedCredential,
            projection = projection,
        )
    }

    fun refreshIfNeeded(
        nowMillis: Long,
        networkAvailable: Boolean,
        trigger: WeatherRefreshTrigger,
    ): WeatherRefreshResult? = withRefreshAccessSnapshot { accessSnapshot ->
        val requestIdentity = accessSnapshot.projection.toRequestIdentity()
        val provider = requestIdentity.provider
        if (!networkAvailable) {
            lastRefreshFailure = AttributedProviderFailure(provider, WeatherProviderFailure.NETWORK)
            return@withRefreshAccessSnapshot null
        }
        val last = matchingRecord(provider, requestIdentity.locationIdentity)?.snapshot?.updatedAtMillis
        if (trigger == WeatherRefreshTrigger.SCHEDULED && last != null && nowMillis - last < REFRESH_INTERVAL_MILLIS) {
            return@withRefreshAccessSnapshot null
        }
        val adapter = adapterFor(provider) ?: return@withRefreshAccessSnapshot rejectMismatchedAdapter(provider)
        val request = when (provider) {
            WeatherProviderId.OPEN_METEO -> WeatherProviderRequest.withoutCredential(
                requestIdentity.location.latitude,
                requestIdentity.location.longitude,
            )
            WeatherProviderId.OPEN_WEATHER ->
                accessSnapshot.withSelectedOpenWeatherApiKey { key ->
                    WeatherProviderRequest.fromUserInput(
                        key,
                        requestIdentity.location.latitude,
                        requestIdentity.location.longitude,
                    )
                }
        }
        if (request == null) {
            lastRefreshFailure = AttributedProviderFailure(provider, WeatherProviderFailure.MISSING_CREDENTIAL)
            return@withRefreshAccessSnapshot null
        }
        refreshWithProvider(requestIdentity, adapter, request, nowMillis)
    }

    /** Existing Foundation fake route; request construction remains inside Weather Context. */
    fun refreshFoundationFixture(nowMillis: Long = System.currentTimeMillis()): WeatherRefreshResult? {
        val location = locationReader.currentLocation() ?: return null
        val request = WeatherProviderRequest.withoutCredential(location.latitude, location.longitude)
        val fixture = fixtureProvider
        return if (fixture == null) {
            val requestIdentity = ProviderRequestIdentity(
                provider = WeatherProviderId.OPEN_METEO,
                location = location,
                locationIdentity = location.weatherLocationIdentity(),
            )
            refreshWithProvider(requestIdentity, openMeteoProvider, request, nowMillis)
        } else {
            val requestIdentity = ProviderRequestIdentity(
                provider = WeatherProviderId.OPEN_METEO,
                location = location,
                locationIdentity = location.weatherLocationIdentity(),
            )
            val result = fixture.fetch(request)
            acceptProviderResultIfCurrent(requestIdentity, result, nowMillis)
        }
    }

    fun resetFoundationState() {
        cacheStore.reset()
        lastRefreshFailure = null
        projectionSnapshot = null
    }

    private fun selectedProvider(): WeatherProviderId = when (
        (locationReader as? WeatherAccessReader)?.selectedWeatherProvider() ?: WeatherProviderSelection.OPEN_METEO
    ) {
        WeatherProviderSelection.OPEN_METEO -> WeatherProviderId.OPEN_METEO
        WeatherProviderSelection.OPEN_WEATHER -> WeatherProviderId.OPEN_WEATHER
    }

    private fun <T> withRefreshAccessSnapshot(
        block: (WeatherRefreshAccessSnapshot) -> T,
    ): T? {
        val access = locationReader as? WeatherAccessReader
        val coherentAccess = access as? CoherentWeatherAccessReader
        if (coherentAccess != null) {
            return coherentAccess.withWeatherRefreshAccessSnapshot(block)
        }
        val projection = WeatherRefreshAccessProjection(
            selectedProvider = access?.selectedWeatherProvider() ?: WeatherProviderSelection.OPEN_METEO,
            location = locationReader.currentLocation() ?: return null,
        )
        return block(object : WeatherRefreshAccessSnapshot {
            override val projection: WeatherRefreshAccessProjection = projection

            override fun <R> withSelectedOpenWeatherApiKey(block: (String) -> R): R? {
                if (projection.selectedProvider != WeatherProviderSelection.OPEN_WEATHER) return null
                return access?.withSelectedOpenWeatherApiKey(block)
            }
        })
    }

    private fun currentWeatherRefreshAccessProjection(): WeatherRefreshAccessProjection? {
        val access = locationReader as? WeatherAccessReader
        val coherentAccess = access as? CoherentWeatherAccessReader
        if (coherentAccess != null) return coherentAccess.currentWeatherRefreshAccessProjection()
        return WeatherRefreshAccessProjection(
            selectedProvider = access?.selectedWeatherProvider() ?: WeatherProviderSelection.OPEN_METEO,
            location = locationReader.currentLocation() ?: return null,
        )
    }

    private fun WeatherRefreshAccessProjection.toRequestIdentity(): ProviderRequestIdentity =
        ProviderRequestIdentity(
            provider = selectedProvider.toProviderId(),
            location = location,
            locationIdentity = location.weatherLocationIdentity(),
        )

    private fun WeatherProviderSelection.toProviderId(): WeatherProviderId = when (this) {
        WeatherProviderSelection.OPEN_METEO -> WeatherProviderId.OPEN_METEO
        WeatherProviderSelection.OPEN_WEATHER -> WeatherProviderId.OPEN_WEATHER
    }

    private fun adapterFor(provider: WeatherProviderId): WeatherProvider? = when (provider) {
        WeatherProviderId.OPEN_METEO -> openMeteoProvider
        WeatherProviderId.OPEN_WEATHER -> openWeatherProvider
    }.takeIf { it.providerId == provider }

    private fun rejectMismatchedAdapter(provider: WeatherProviderId): WeatherRefreshResult? {
        lastRefreshFailure = AttributedProviderFailure(provider, WeatherProviderFailure.PROVIDER_MISMATCH)
        return null
    }

    private fun matchingRecord(
        provider: WeatherProviderId = selectedProvider(),
        locationIdentity: String? = locationReader.currentLocation()?.weatherLocationIdentity(),
    ): WeatherCacheRecord? = cacheStore.loadRecord()?.takeIf {
        locationIdentity != null && it.provider == provider && it.locationIdentity == locationIdentity
    }

    private fun validStructuredData(data: ProviderWeatherData, nowMillis: Long): Boolean {
        if (!data.current.pressureHpa.isFinite()) return false
        val zoneId = runCatching { ZoneId.of(data.apiTimeZone) }.getOrNull() ?: return false
        val expectedCityDate = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        return data.daily.any { it.date == expectedCityDate }
    }

    private fun normalize(
        provider: WeatherProviderId,
        data: ProviderWeatherData,
        location: LocationContext,
        nowMillis: Long,
    ): NormalizedWeather = NormalizedWeather(
        snapshot = WeatherSnapshot(
            cityLabel = location.cityLabel,
            temperatureCelsius = data.current.temperatureCelsius,
            condition = normalizeCondition(provider, data.current.condition),
            source = provider.storageId,
            updatedAtMillis = nowMillis,
            pressureMmHg = data.current.pressureHpa * HPA_TO_MMHG,
            apiTimeZone = data.apiTimeZone,
        ),
        daily = data.daily.map { normalizeDay(provider, it) },
        hourly = data.hourly.map { normalizeHourly(provider, it) }
            .distinctBy { it.date to it.time }
            .sortedWith(compareBy(NormalizedHourly::date, NormalizedHourly::time)),
    )

    private fun normalizeDay(provider: WeatherProviderId, day: ProviderDailyWeather): NormalizedDay = NormalizedDay(
        date = day.date,
        dayTemperatureCelsius = day.dayTemperatureCelsius,
        nightTemperatureCelsius = day.nightTemperatureCelsius,
        dayCondition = day.dayCondition?.takeIf(String::isNotBlank)?.let { normalizeCondition(provider, it) },
        nightCondition = day.nightCondition?.takeIf(String::isNotBlank)?.let { normalizeCondition(provider, it) },
        moonPhase = day.moonPhase?.takeIf(String::isNotBlank),
        sunrise = day.sunrise,
        sunset = day.sunset,
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

private fun normalizeHourly(
    provider: WeatherProviderId,
    hourly: ProviderHourlyWeather,
): NormalizedHourly = NormalizedHourly(
    date = hourly.date,
    time = hourly.time,
    temperatureCelsius = hourly.temperatureCelsius,
    illustration = hourly.condition?.takeIf(String::isNotBlank)?.let {
        hourlyIllustration(normalizeCondition(provider, it))
    },
)

private fun hasCompleteHourly(hourly: List<NormalizedHourly>, today: LocalDate): Boolean {
    val expected = hourlyKeys(today)
    val byKey = hourly.associateBy { it.date to it.time }
    return expected.all { key ->
        val value = byKey[key]
        value?.temperatureCelsius != null && value.illustration != null
    }
}

private fun hasCompleteDaily(provider: WeatherProviderId, daily: List<NormalizedDay>): Boolean {
    val expectedCount = provider.capabilities.supportedDailyRecords
    if (daily.size != expectedCount || daily.zipWithNext().any { (first, second) -> second.date != first.date.plusDays(1) }) {
        return false
    }
    return daily.all { day ->
        day.dayTemperatureCelsius != null &&
            day.nightTemperatureCelsius != null &&
            !day.dayCondition.isNullOrBlank() &&
            !day.nightCondition.isNullOrBlank()
    }
}

private fun retainCompleteDailySubset(
    provider: WeatherProviderId,
    incoming: List<NormalizedDay>,
    previous: List<NormalizedDay>?,
): List<NormalizedDay> = when {
    hasCompleteDaily(provider, incoming) -> incoming
    previous != null && hasCompleteDaily(provider, previous) -> previous
    else -> incoming
}

private fun retainCompleteHourlySubset(
    incoming: List<NormalizedHourly>,
    previous: List<NormalizedHourly>?,
    apiTimeZone: String,
    nowMillis: Long,
): List<NormalizedHourly> {
    val today = Instant.ofEpochMilli(nowMillis).atZone(apiTimeZone.toZoneIdOrUtc()).toLocalDate()
    return when {
        hasCompleteHourly(incoming, today) -> incoming
        previous != null && hasCompleteHourly(previous, today) -> previous
        else -> incoming
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

private fun normalizeCondition(provider: WeatherProviderId, condition: String?): String {
    val normalized = condition?.lowercase()
    return when {
        normalized == "clear" || normalized == "sunny" -> "clear"
        normalized == "cloud" || normalized == "partly-cloudy" || normalized == "overcast" -> "cloud"
        normalized == "rain" || normalized == "drizzle" || normalized == "showers" -> "rain"
        normalized == "snow" -> "snow"
        provider == WeatherProviderId.OPEN_METEO && normalized?.startsWith("wmo:") == true ->
            normalizeWmoCode(normalized.substringAfter(':').toIntOrNull())
        provider == WeatherProviderId.OPEN_WEATHER && normalized?.startsWith("owm:") == true ->
            normalizeOpenWeatherId(normalized.substringAfter(':').toIntOrNull())
        else -> "neutral-cloud"
    }
}

private fun normalizeWmoCode(code: Int?): String = when (code) {
    0 -> "clear"
    1, 2, 3, 45, 48 -> "cloud"
    71, 73, 75, 77, 85, 86 -> "snow"
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99 -> "rain"
    else -> "neutral-cloud"
}

private fun normalizeOpenWeatherId(id: Int?): String = when {
    id == 800 -> "clear"
    id != null && id in 600..699 -> "snow"
    id != null && id in 200..599 -> "rain"
    id != null && id in 700..804 -> "cloud"
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

private fun isDaytime(
    nowMillis: Long,
    zoneId: ZoneId,
    daily: List<NormalizedDay> = emptyList(),
): Boolean {
    val local = Instant.ofEpochMilli(nowMillis).atZone(zoneId)
    val today = daily.firstOrNull { it.date == local.toLocalDate() }
    val sunrise = today?.sunrise
    val sunset = today?.sunset
    return if (sunrise != null && sunset != null && sunrise < sunset) {
        !local.toLocalTime().isBefore(sunrise) && local.toLocalTime().isBefore(sunset)
    } else {
        local.hour in 6..17
    }
}

private fun String.toZoneIdOrUtc(): ZoneId = runCatching { ZoneId.of(this) }.getOrDefault(ZoneId.of("UTC"))

private fun LocationContext.weatherLocationIdentity(): String = String.format(
    Locale.US,
    "%s|%s|%.6f|%.6f|%s",
    countryCode,
    cityId.ifBlank { "coordinates" },
    latitude,
    longitude,
    apiTimeZone,
)
