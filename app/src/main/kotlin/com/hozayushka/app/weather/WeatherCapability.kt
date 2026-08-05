package com.hozayushka.app.weather

import android.content.SharedPreferences
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.settings.LocationReader

data class WeatherSnapshot(
    val cityLabel: String,
    val temperatureCelsius: Int,
    val condition: String,
    val source: String,
)

interface WeatherCacheStore {
    fun load(): WeatherSnapshot?

    fun save(snapshot: WeatherSnapshot)

    fun reset()
}

class InMemoryWeatherCacheStore : WeatherCacheStore {
    private var snapshot: WeatherSnapshot? = null

    override fun load(): WeatherSnapshot? = snapshot

    override fun save(snapshot: WeatherSnapshot) {
        this.snapshot = snapshot
    }

    override fun reset() {
        snapshot = null
    }
}

/** Private persistence owner for Weather Context. */
class SharedPreferencesWeatherCacheStore(
    private val preferences: SharedPreferences,
) : WeatherCacheStore {
    override fun load(): WeatherSnapshot? {
        if (!preferences.contains(KEY_CITY)) return null
        return WeatherSnapshot(
            cityLabel = preferences.getString(KEY_CITY, null).orEmpty(),
            temperatureCelsius = preferences.getInt(KEY_TEMPERATURE, 0),
            condition = preferences.getString(KEY_CONDITION, null).orEmpty(),
            source = preferences.getString(KEY_SOURCE, null).orEmpty(),
        )
    }

    override fun save(snapshot: WeatherSnapshot) {
        preferences.edit()
            .putString(KEY_CITY, snapshot.cityLabel)
            .putInt(KEY_TEMPERATURE, snapshot.temperatureCelsius)
            .putString(KEY_CONDITION, snapshot.condition)
            .putString(KEY_SOURCE, snapshot.source)
            .apply()
    }

    override fun reset() {
        preferences.edit()
            .remove(KEY_CITY)
            .remove(KEY_TEMPERATURE)
            .remove(KEY_CONDITION)
            .remove(KEY_SOURCE)
            .apply()
    }

    private companion object {
        const val KEY_CITY = "foundation.city"
        const val KEY_TEMPERATURE = "foundation.temperature"
        const val KEY_CONDITION = "foundation.condition"
        const val KEY_SOURCE = "foundation.source"
    }
}

data class WeatherRefreshResult(
    val snapshot: WeatherSnapshot,
    val credentialWasReceived: Boolean,
    val redactedCredential: String,
)

interface WeatherReadPort {
    fun snapshot(): WeatherSnapshot?
}

class WeatherCapability(
    private val locationReader: LocationReader,
    private val cacheStore: WeatherCacheStore,
    private val provider: WeatherProvider,
) : WeatherReadPort {
    override fun snapshot(): WeatherSnapshot? = cacheStore.load()

    /** Foundation refresh path; feature-level provider mapping remains downstream. */
    fun refresh(request: WeatherProviderRequest): WeatherRefreshResult? {
        val location = locationReader.currentLocation() ?: return null
        val providerResult = provider.fetch(request)
        val snapshot = WeatherSnapshot(
            cityLabel = location.cityLabel,
            temperatureCelsius = providerResult.payload.temperatureCelsius,
            condition = providerResult.payload.condition,
            source = "foundation-fixture",
        )
        cacheStore.save(snapshot)
        return WeatherRefreshResult(
            snapshot = snapshot,
            credentialWasReceived = providerResult.credentialWasReceived,
            redactedCredential = providerResult.redactedCredential,
        )
    }

    /** Foundation-only fixture path; request construction stays inside Weather Context. */
    fun refreshFoundationFixture(): WeatherRefreshResult? =
        refresh(WeatherProviderRequest.fromSyntheticProbe())

    fun resetFoundationState() {
        cacheStore.reset()
    }
}
