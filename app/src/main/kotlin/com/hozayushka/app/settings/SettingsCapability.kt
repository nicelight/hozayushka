package com.hozayushka.app.settings

import android.content.SharedPreferences

data class LocationContext(
    val cityLabel: String,
    val latitude: Double,
    val longitude: Double,
    val apiTimeZone: String,
)

data class SettingsState(
    val location: LocationContext? = null,
)

interface LocationReader {
    fun currentLocation(): LocationContext?
}

interface SettingsStateStore {
    fun load(): SettingsState

    fun save(state: SettingsState)

    fun reset()
}

class InMemorySettingsStateStore : SettingsStateStore {
    private var state = SettingsState()

    override fun load(): SettingsState = state

    override fun save(state: SettingsState) {
        this.state = state
    }

    override fun reset() {
        state = SettingsState()
    }
}

/** Private persistence owner for Settings & Location. */
class SharedPreferencesSettingsStateStore(
    private val preferences: SharedPreferences,
) : SettingsStateStore {
    override fun load(): SettingsState {
        if (!preferences.contains(KEY_CITY)) return SettingsState()
        return SettingsState(
            location = LocationContext(
                cityLabel = preferences.getString(KEY_CITY, null).orEmpty(),
                latitude = preferences.getString(KEY_LATITUDE, null)?.toDoubleOrNull() ?: 0.0,
                longitude = preferences.getString(KEY_LONGITUDE, null)?.toDoubleOrNull() ?: 0.0,
                apiTimeZone = preferences.getString(KEY_TIME_ZONE, null).orEmpty(),
            ),
        )
    }

    override fun save(state: SettingsState) {
        val location = state.location
        preferences.edit().apply {
            if (location == null) {
                remove(KEY_CITY)
                remove(KEY_LATITUDE)
                remove(KEY_LONGITUDE)
                remove(KEY_TIME_ZONE)
            } else {
                putString(KEY_CITY, location.cityLabel)
                putString(KEY_LATITUDE, location.latitude.toString())
                putString(KEY_LONGITUDE, location.longitude.toString())
                putString(KEY_TIME_ZONE, location.apiTimeZone)
            }
        }.apply()
    }

    override fun reset() {
        preferences.edit()
            .remove(KEY_CITY)
            .remove(KEY_LATITUDE)
            .remove(KEY_LONGITUDE)
            .remove(KEY_TIME_ZONE)
            .apply()
    }

    private companion object {
        const val KEY_CITY = "foundation.city"
        const val KEY_LATITUDE = "foundation.latitude"
        const val KEY_LONGITUDE = "foundation.longitude"
        const val KEY_TIME_ZONE = "foundation.time_zone"
    }
}

class SettingsCapability(
    private val stateStore: SettingsStateStore,
) : LocationReader {
    fun snapshot(): SettingsState = stateStore.load()

    override fun currentLocation(): LocationContext? = snapshot().location

    fun saveFoundationLocation(location: LocationContext) {
        stateStore.save(SettingsState(location))
    }

    fun resetFoundationState() {
        stateStore.reset()
    }
}
