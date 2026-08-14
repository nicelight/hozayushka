package com.hozayushka.app.settings

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import com.hozayushka.app.R
import kotlin.math.roundToInt

data class LocationContext(
    val cityLabel: String,
    val latitude: Double,
    val longitude: Double,
    val apiTimeZone: String,
    val countryCode: String = "TJ",
    val countryLabel: String = "Таджикистан",
    val cityId: String = "",
    val canonicalCityName: String = cityLabel,
    val asciiCityName: String = cityLabel,
)

/** In-memory secret wrapper; its value is never present in string output. */
class LocalWeatherApiKey private constructor(
    private val value: String,
) {
    internal fun <T> use(block: (String) -> T): T = block(value)

    override fun toString(): String = "[REDACTED]"

    companion object {
        internal fun fromUserInput(value: String): LocalWeatherApiKey = LocalWeatherApiKey(value)
    }
}

enum class WeatherProviderSelection(
    val storageId: String,
    val displayName: String,
) {
    OPEN_METEO("open_meteo", "Open-Meteo"),
    OPEN_WEATHER("open_weather", "OpenWeather"),
    ;

    companion object {
        fun fromStorage(value: String?): WeatherProviderSelection =
            entries.firstOrNull { it.storageId == value } ?: OPEN_METEO
    }
}

data class WeatherAccessSettingsProjection(
    val selectedProvider: WeatherProviderSelection,
    val hasApplicableOpenWeatherKey: Boolean,
)

data class WeatherRefreshAccessProjection(
    val selectedProvider: WeatherProviderSelection,
    val location: LocationContext,
)

interface WeatherRefreshAccessSnapshot {
    val projection: WeatherRefreshAccessProjection

    /** Keeps the raw value inside this Settings-owned ephemeral callback. */
    fun <T> withSelectedOpenWeatherApiKey(block: (String) -> T): T?
}

interface CoherentWeatherAccessReader {
    /** Loads Settings once and keeps provider, location and key authority in one immutable attempt. */
    fun <T> withWeatherRefreshAccessSnapshot(block: (WeatherRefreshAccessSnapshot) -> T): T?

    /** Loads provider and location together for post-fetch stale-response comparison. */
    fun currentWeatherRefreshAccessProjection(): WeatherRefreshAccessProjection?
}

enum class SettingsContentSection {
    WEATHER_PROVIDER,
    OPEN_WEATHER_KEY,
    LOCATION,
    OPEN_METEO_ATTRIBUTION,
    GEONAMES_ATTRIBUTION,
    ALERT,
    PERSONALIZATION,
    TIMER_PRESETS,
    BACK,
}

enum class ApiKeyValidationError(val message: String) {
    MISSING("API-ключ не указан"),
    INVALID("Неверный API-ключ"),
}

data class ApiKeyUpdateResult(
    val accepted: Boolean,
    val error: ApiKeyValidationError? = null,
)

enum class TimerPresetSlot(val index: Int, val label: String) {
    FIRST(0, "1"),
    SECOND(1, "2"),
    THIRD(2, "3"),
}

enum class BuiltInAlertSignal(val id: String, val label: String) {
    CLASSIC("classic", "Классический"),
    BELL("bell", "Колокольчик"),
    ELECTRONIC("electronic", "Электронный"),
}

data class TimerAlertSettingsProjection(
    val signal: BuiltInAlertSignal = BuiltInAlertSignal.CLASSIC,
    val volumePercent: Int = 70,
) {
    init {
        require(volumePercent in 0..100)
    }
}

object TimerAlertSettingsDefaults {
    fun projection(): TimerAlertSettingsProjection = TimerAlertSettingsProjection()
}

data class SettingsPresentationProjection(
    val signal: BuiltInAlertSignal = BuiltInAlertSignal.CLASSIC,
    val volumePercent: Int = 70,
    val glassIntensity: Float = 0.45f,
) {
    init {
        require(volumePercent in 0..100)
        require(glassIntensity.isFinite() && glassIntensity in 0f..1f)
    }
}

enum class AlertVolumeValidationError(val message: String) {
    OUT_OF_RANGE("Громкость должна быть в диапазоне 0–100"),
}

data class AlertVolumeUpdateResult(
    val accepted: Boolean,
    val projection: TimerAlertSettingsProjection,
    val error: AlertVolumeValidationError? = null,
)

enum class GlassIntensityValidationError(val message: String) {
    OUT_OF_RANGE("Интенсивность стекла должна быть в диапазоне 0–1"),
}

data class GlassIntensityUpdateResult(
    val accepted: Boolean,
    val intensity: Float,
    val error: GlassIntensityValidationError? = null,
)

data class TimerPresetDuration(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
) {
    fun totalMillis(): Long =
        ((hours * 60L + minutes) * 60L + seconds) * 1_000L

    internal fun editorFieldValues(): List<String> =
        listOf(hours.toString(), minutes.toString(), seconds.toString())
}

data class TimerPreset(
    val slot: TimerPresetSlot,
    val duration: TimerPresetDuration,
)

object TimerPresetDefaults {
    fun projection(): TimerPresetProjection = TimerPresetProjection(
        listOf(
            TimerPreset(TimerPresetSlot.FIRST, TimerPresetDuration(0, 3, 0)),
            TimerPreset(TimerPresetSlot.SECOND, TimerPresetDuration(0, 10, 0)),
            TimerPreset(TimerPresetSlot.THIRD, TimerPresetDuration(0, 30, 0)),
        ),
    )
}

data class TimerPresetProjection(
    val presets: List<TimerPreset>,
) {
    init {
        require(presets.size == TimerPresetSlot.entries.size)
        require(presets.map { it.slot } == TimerPresetSlot.entries)
    }

    fun preset(slot: TimerPresetSlot): TimerPreset = presets[slot.index]
}

enum class TimerPresetValidationError(val message: String) {
    HOURS_OUT_OF_RANGE("Часы должны быть в диапазоне 0–99"),
    MINUTES_OUT_OF_RANGE("Минуты должны быть в диапазоне 0–59"),
    SECONDS_OUT_OF_RANGE("Секунды должны быть в диапазоне 0–59"),
    ZERO_TOTAL("Укажите время больше нуля"),
}

data class TimerPresetUpdateResult(
    val accepted: Boolean,
    val duration: TimerPresetDuration,
    val error: TimerPresetValidationError? = null,
)

data class SettingsState(
    val location: LocationContext? = null,
    val weatherProvider: WeatherProviderSelection = WeatherProviderSelection.OPEN_METEO,
    val apiKey: LocalWeatherApiKey? = null,
    val timerPresets: TimerPresetProjection = TimerPresetDefaults.projection(),
    val timerAlert: TimerAlertSettingsProjection = TimerAlertSettingsDefaults.projection(),
    val glassIntensity: Float = 0.45f,
) {
    init {
        require(glassIntensity.isFinite() && glassIntensity in 0f..1f)
    }
}

interface LocationReader {
    fun currentLocation(): LocationContext?
}

interface WeatherAccessReader : LocationReader {
    fun selectedWeatherProvider(): WeatherProviderSelection

    /** Authorizes one ephemeral read only while OpenWeather is the current explicit selection. */
    fun <T> withSelectedOpenWeatherApiKey(block: (String) -> T): T?

    fun hasWeatherApiKey(): Boolean
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
        return SettingsState(
            location = if (preferences.contains(KEY_CITY)) LocationContext(
                countryCode = preferences.getString(KEY_COUNTRY_CODE, "TJ").orEmpty(),
                countryLabel = preferences.getString(KEY_COUNTRY_LABEL, "Таджикистан").orEmpty(),
                cityId = preferences.getString(KEY_CITY_ID, null).orEmpty(),
                cityLabel = preferences.getString(KEY_CITY, null).orEmpty(),
                canonicalCityName = preferences.getString(KEY_CITY_CANONICAL, null).orEmpty(),
                asciiCityName = preferences.getString(KEY_CITY_ASCII, null).orEmpty(),
                latitude = preferences.getString(KEY_LATITUDE, null)?.toDoubleOrNull() ?: 0.0,
                longitude = preferences.getString(KEY_LONGITUDE, null)?.toDoubleOrNull() ?: 0.0,
                apiTimeZone = preferences.getString(KEY_TIME_ZONE, null).orEmpty(),
            ) else null,
            weatherProvider = WeatherProviderSelection.fromStorage(
                preferences.getString(KEY_WEATHER_PROVIDER, null),
            ),
            apiKey = preferences.getString(KEY_OPEN_WEATHER_API_KEY, null)
                ?.takeIf(String::isNotBlank)
                ?.let(LocalWeatherApiKey::fromUserInput),
            timerPresets = TimerPresetProjection(
                TimerPresetSlot.entries.map { slot ->
                    val defaults = TimerPresetDefaults.projection().preset(slot).duration
                    TimerPreset(
                        slot = slot,
                        duration = TimerPresetDuration(
                            hours = preferences.getInt(key(slot, "hours"), defaults.hours),
                            minutes = preferences.getInt(key(slot, "minutes"), defaults.minutes),
                            seconds = preferences.getInt(key(slot, "seconds"), defaults.seconds),
                        ),
                    )
                },
            ),
            timerAlert = TimerAlertSettingsProjection(
                signal = preferences.getString(KEY_ALERT_SIGNAL, null)
                    ?.let { value -> BuiltInAlertSignal.entries.firstOrNull { it.id == value } }
                    ?: BuiltInAlertSignal.CLASSIC,
                volumePercent = preferences.getInt(KEY_ALERT_VOLUME, 70).coerceIn(0, 100),
            ),
            glassIntensity = preferences.getString(KEY_GLASS_INTENSITY, null)
                ?.toFloatOrNull()
                ?.takeIf { it.isFinite() && it in 0f..1f }
                ?: 0.45f,
        )
    }

    override fun save(state: SettingsState) {
        val location = state.location
        preferences.edit().apply {
            if (location == null) {
                remove(KEY_COUNTRY_CODE)
                remove(KEY_COUNTRY_LABEL)
                remove(KEY_CITY_ID)
                remove(KEY_CITY)
                remove(KEY_CITY_CANONICAL)
                remove(KEY_CITY_ASCII)
                remove(KEY_LATITUDE)
                remove(KEY_LONGITUDE)
                remove(KEY_TIME_ZONE)
            } else {
                putString(KEY_COUNTRY_CODE, location.countryCode)
                putString(KEY_COUNTRY_LABEL, location.countryLabel)
                putString(KEY_CITY_ID, location.cityId)
                putString(KEY_CITY, location.cityLabel)
                putString(KEY_CITY_CANONICAL, location.canonicalCityName)
                putString(KEY_CITY_ASCII, location.asciiCityName)
                putString(KEY_LATITUDE, location.latitude.toString())
                putString(KEY_LONGITUDE, location.longitude.toString())
                putString(KEY_TIME_ZONE, location.apiTimeZone)
            }
            putString(KEY_WEATHER_PROVIDER, state.weatherProvider.storageId)
            state.apiKey?.use { putString(KEY_OPEN_WEATHER_API_KEY, it) }
                ?: remove(KEY_OPEN_WEATHER_API_KEY)
            remove(KEY_LEGACY_WEATHER_API_KEY)
            state.timerPresets.presets.forEach { preset ->
                putInt(key(preset.slot, "hours"), preset.duration.hours)
                putInt(key(preset.slot, "minutes"), preset.duration.minutes)
                putInt(key(preset.slot, "seconds"), preset.duration.seconds)
            }
            putString(KEY_ALERT_SIGNAL, state.timerAlert.signal.id)
            putInt(KEY_ALERT_VOLUME, state.timerAlert.volumePercent)
            putString(KEY_GLASS_INTENSITY, state.glassIntensity.toString())
        }.apply()
    }

    override fun reset() {
        preferences.edit()
            .remove(KEY_COUNTRY_CODE)
            .remove(KEY_COUNTRY_LABEL)
            .remove(KEY_CITY_ID)
            .remove(KEY_CITY)
            .remove(KEY_CITY_CANONICAL)
            .remove(KEY_CITY_ASCII)
            .remove(KEY_LATITUDE)
            .remove(KEY_LONGITUDE)
            .remove(KEY_TIME_ZONE)
            .remove(KEY_WEATHER_PROVIDER)
            .remove(KEY_OPEN_WEATHER_API_KEY)
            .remove(KEY_LEGACY_WEATHER_API_KEY)
            .also { editor ->
                TimerPresetSlot.entries.forEach { slot ->
                    editor.remove(key(slot, "hours"))
                    editor.remove(key(slot, "minutes"))
                    editor.remove(key(slot, "seconds"))
                }
                editor.remove(KEY_ALERT_SIGNAL)
                editor.remove(KEY_ALERT_VOLUME)
                editor.remove(KEY_GLASS_INTENSITY)
            }
            .apply()
    }

    private companion object {
        const val KEY_COUNTRY_CODE = "settings.country.code"
        const val KEY_COUNTRY_LABEL = "settings.country.label"
        const val KEY_CITY_ID = "settings.city.id"
        const val KEY_CITY = "foundation.city"
        const val KEY_CITY_CANONICAL = "settings.city.canonical"
        const val KEY_CITY_ASCII = "settings.city.ascii"
        const val KEY_LATITUDE = "foundation.latitude"
        const val KEY_LONGITUDE = "foundation.longitude"
        const val KEY_TIME_ZONE = "foundation.time_zone"
        const val KEY_WEATHER_PROVIDER = "settings.weather.provider"
        const val KEY_OPEN_WEATHER_API_KEY = "settings.weather.open_weather.api_key"
        const val KEY_LEGACY_WEATHER_API_KEY = "settings.weather.api_key"
        const val KEY_ALERT_SIGNAL = "settings.alert.signal"
        const val KEY_ALERT_VOLUME = "settings.alert.volume"
        const val KEY_GLASS_INTENSITY = "settings.display.glass_intensity"

        fun key(slot: TimerPresetSlot, field: String): String =
            "settings.timer_preset.${slot.index}.$field"
    }
}

class SettingsCapability(
    private val stateStore: SettingsStateStore,
    private val onValidLocationChanged: () -> Unit = {},
    private val catalog: BundledLocationCatalog? = null,
    private val onValidProviderChanged: () -> Unit = onValidLocationChanged,
    private val onValidOpenWeatherApiKeySaved: () -> Unit = {},
) : WeatherAccessReader, CoherentWeatherAccessReader, TimerPresetReader, TimerAlertSettingsReader {
    fun snapshot(): SettingsState = stateStore.load()

    override fun currentLocation(): LocationContext? = snapshot().location

    override fun selectedWeatherProvider(): WeatherProviderSelection = snapshot().weatherProvider

    override fun <T> withSelectedOpenWeatherApiKey(block: (String) -> T): T? {
        val state = snapshot()
        if (state.weatherProvider != WeatherProviderSelection.OPEN_WEATHER) return null
        return state.apiKey?.use(block)
    }

    override fun <T> withWeatherRefreshAccessSnapshot(
        block: (WeatherRefreshAccessSnapshot) -> T,
    ): T? {
        val state = snapshot()
        val location = state.location ?: return null
        val projection = WeatherRefreshAccessProjection(
            selectedProvider = state.weatherProvider,
            location = location,
        )
        return block(object : WeatherRefreshAccessSnapshot {
            override val projection: WeatherRefreshAccessProjection = projection

            override fun <R> withSelectedOpenWeatherApiKey(block: (String) -> R): R? {
                if (state.weatherProvider != WeatherProviderSelection.OPEN_WEATHER) return null
                return state.apiKey?.use(block)
            }
        })
    }

    override fun currentWeatherRefreshAccessProjection(): WeatherRefreshAccessProjection? {
        val state = snapshot()
        val location = state.location ?: return null
        return WeatherRefreshAccessProjection(
            selectedProvider = state.weatherProvider,
            location = location,
        )
    }

    fun hasStoredOpenWeatherApiKey(): Boolean = snapshot().apiKey != null

    override fun hasWeatherApiKey(): Boolean = weatherAccessSettingsProjection().hasApplicableOpenWeatherKey

    fun weatherAccessSettingsProjection(): WeatherAccessSettingsProjection {
        val state = snapshot()
        return WeatherAccessSettingsProjection(
            selectedProvider = state.weatherProvider,
            hasApplicableOpenWeatherKey =
                state.weatherProvider == WeatherProviderSelection.OPEN_WEATHER && state.apiKey != null,
        )
    }

    fun updateWeatherProvider(provider: WeatherProviderSelection): Boolean {
        val state = snapshot()
        if (state.weatherProvider == provider) return false
        stateStore.save(state.copy(weatherProvider = provider))
        onValidProviderChanged()
        return true
    }

    private fun validateOpenWeatherApiKey(input: String): ApiKeyValidationError? = when {
        input.isBlank() -> ApiKeyValidationError.MISSING
        input != input.trim() || input.any(Char::isWhitespace) || input.any(Char::isISOControl) ->
            ApiKeyValidationError.INVALID
        else -> null
    }

    fun updateOpenWeatherApiKey(input: String): ApiKeyUpdateResult {
        if (selectedWeatherProvider() != WeatherProviderSelection.OPEN_WEATHER) {
            return ApiKeyUpdateResult(accepted = false)
        }
        val error = validateOpenWeatherApiKey(input)
        if (error != null) return ApiKeyUpdateResult(accepted = false, error = error)
        stateStore.save(snapshot().copy(apiKey = LocalWeatherApiKey.fromUserInput(input)))
        onValidOpenWeatherApiKeySaved()
        return ApiKeyUpdateResult(accepted = true)
    }

    fun contextualizeWeatherError(message: String?): String? {
        val normalized = message?.takeIf(String::isNotBlank) ?: return null
        // Local key validation is rendered by Settings; this input has no trustworthy provider tag.
        if (ApiKeyValidationError.entries.any { it.message == normalized }) {
            return null
        }
        return normalized
    }

    fun settingsContentOrder(): List<SettingsContentSection> = buildList {
        add(SettingsContentSection.WEATHER_PROVIDER)
        if (selectedWeatherProvider() == WeatherProviderSelection.OPEN_WEATHER) {
            add(SettingsContentSection.OPEN_WEATHER_KEY)
        }
        add(SettingsContentSection.LOCATION)
        add(SettingsContentSection.OPEN_METEO_ATTRIBUTION)
        add(SettingsContentSection.GEONAMES_ATTRIBUTION)
        add(SettingsContentSection.ALERT)
        add(SettingsContentSection.PERSONALIZATION)
        add(SettingsContentSection.TIMER_PRESETS)
        add(SettingsContentSection.BACK)
    }

    fun ensureDefaultLocation() {
        if (currentLocation() == null) {
            catalog?.defaultLocation()?.toLocationContext()?.let(::saveFoundationLocation)
        }
    }

    override fun timerPresetProjection(): TimerPresetProjection = snapshot().timerPresets

    /** Validated read-only projection consumed by Timer & Alert; no Settings UI is added here. */
    override fun timerAlertSettingsProjection(): TimerAlertSettingsProjection = snapshot().timerAlert

    /** Validated read-only projection consumed by Main Display and its preview. */
    fun settingsPresentationProjection(): SettingsPresentationProjection {
        val state = snapshot()
        return SettingsPresentationProjection(
            signal = state.timerAlert.signal,
            volumePercent = state.timerAlert.volumePercent,
            glassIntensity = state.glassIntensity,
        )
    }

    fun updateAlertSignal(signal: BuiltInAlertSignal) {
        stateStore.save(snapshot().copy(timerAlert = timerAlertSettingsProjection().copy(signal = signal)))
    }

    fun updateAlertVolume(volumePercent: Int): AlertVolumeUpdateResult {
        val current = timerAlertSettingsProjection()
        if (volumePercent !in 0..100) {
            return AlertVolumeUpdateResult(
                accepted = false,
                projection = current,
                error = AlertVolumeValidationError.OUT_OF_RANGE,
            )
        }
        val updated = current.copy(volumePercent = volumePercent)
        stateStore.save(snapshot().copy(timerAlert = updated))
        return AlertVolumeUpdateResult(accepted = true, projection = updated)
    }

    fun updateGlassIntensity(intensity: Float): GlassIntensityUpdateResult {
        val current = snapshot().glassIntensity
        if (!intensity.isFinite() || intensity !in 0f..1f) {
            return GlassIntensityUpdateResult(
                accepted = false,
                intensity = current,
                error = GlassIntensityValidationError.OUT_OF_RANGE,
            )
        }
        stateStore.save(snapshot().copy(glassIntensity = intensity))
        return GlassIntensityUpdateResult(accepted = true, intensity = intensity)
    }

    fun saveFoundationLocation(location: LocationContext) {
        val previousLocation = currentLocation()
        stateStore.save(snapshot().copy(location = location))
        if (previousLocation != location) onValidLocationChanged()
    }

    fun saveCatalogLocation(entry: LocationCatalogEntry) {
        saveFoundationLocation(entry.toLocationContext())
    }

    fun resetFoundationState() {
        stateStore.reset()
    }

    fun updateTimerPreset(
        slot: TimerPresetSlot,
        hours: Int,
        minutes: Int,
        seconds: Int,
    ): TimerPresetUpdateResult {
        val current = timerPresetProjection().preset(slot).duration
        val error = when {
            hours !in 0..99 -> TimerPresetValidationError.HOURS_OUT_OF_RANGE
            minutes !in 0..59 -> TimerPresetValidationError.MINUTES_OUT_OF_RANGE
            seconds !in 0..59 -> TimerPresetValidationError.SECONDS_OUT_OF_RANGE
            hours == 0 && minutes == 0 && seconds == 0 -> TimerPresetValidationError.ZERO_TOTAL
            else -> null
        }
        if (error != null) {
            return TimerPresetUpdateResult(accepted = false, duration = current, error = error)
        }

        val duration = TimerPresetDuration(hours, minutes, seconds)
        val updated = timerPresetProjection().presets.map { preset ->
            if (preset.slot == slot) preset.copy(duration = duration) else preset
        }
        stateStore.save(snapshot().copy(timerPresets = TimerPresetProjection(updated)))
        return TimerPresetUpdateResult(accepted = true, duration = duration)
    }

    /** Settings & Location owns the validated surface and its inline errors. */
    fun createDestinationView(
        context: Context,
        onBack: () -> Unit,
        weatherErrorProvider: () -> String? = { null },
        previewFactory: ((Context, Float) -> View)? = null,
    ): View {
        ensureDefaultLocation()
        val scroll = ScrollView(context).apply {
            setBackgroundColor(context.getColor(R.color.display_background))
        }
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 32, 48, 32)
        }
        scroll.addView(content)

        content.addView(TextView(context).apply {
            text = context.getString(R.string.settings_title)
            textSize = 40f
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_primary))
        })

        val providerGroup = RadioGroup(context).apply {
            orientation = RadioGroup.HORIZONTAL
            contentDescription = context.getString(R.string.settings_weather_provider_description)
            tag = "settings-weather-provider"
        }
        WeatherProviderSelection.entries.forEach { provider ->
            providerGroup.addView(RadioButton(context).apply {
                id = View.generateViewId()
                text = provider.displayName
                tag = provider
                isChecked = provider == selectedWeatherProvider()
                contentDescription = context.getString(
                    R.string.settings_weather_provider_option_description,
                    provider.displayName,
                )
            })
        }
        val keyLabel = fieldLabel(context, context.getString(R.string.settings_open_weather_key))
        val keyError = inlineError(context).apply { tag = "settings-open-weather-key-error" }
        val keyInput = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imeOptions = EditorInfo.IME_ACTION_DONE
            contentDescription = context.getString(R.string.settings_open_weather_key_description)
            tag = "settings-open-weather-api-key"
        }
        fun renderKeyValidation(input: String) {
            val error = validateOpenWeatherApiKey(input)
            keyError.text = error?.let {
                "${WeatherProviderSelection.OPEN_WEATHER.displayName}: ${it.message}"
            }.orEmpty()
        }
        fun commitOpenWeatherApiKey() {
            val result = updateOpenWeatherApiKey(keyInput.text?.toString().orEmpty())
            keyError.text = result.error?.let {
                "${WeatherProviderSelection.OPEN_WEATHER.displayName}: ${it.message}"
            }.orEmpty()
        }
        keyInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderKeyValidation(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        fun renderProviderContext() {
            val openWeatherSelected = selectedWeatherProvider() == WeatherProviderSelection.OPEN_WEATHER
            val visibility = if (openWeatherSelected) View.VISIBLE else View.GONE
            keyLabel.visibility = visibility
            keyInput.visibility = visibility
            keyError.visibility = visibility
            if (openWeatherSelected) {
                keyInput.hint = context.getString(
                    if (hasStoredOpenWeatherApiKey()) {
                        R.string.settings_open_weather_key_saved
                    } else {
                        R.string.settings_open_weather_key_hint
                    },
                )
                keyError.text = if (hasStoredOpenWeatherApiKey()) {
                    ""
                } else {
                    "${WeatherProviderSelection.OPEN_WEATHER.displayName}: ${ApiKeyValidationError.MISSING.message}"
                }
            } else {
                keyInput.text.clear()
                keyError.text = ""
            }
        }
        keyInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                false
            } else {
                keyInput.clearFocus()
                true
            }
        }
        keyInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) commitOpenWeatherApiKey()
        }
        providerGroup.setOnCheckedChangeListener { group, checkedId ->
            (group.findViewById<RadioButton>(checkedId)?.tag as? WeatherProviderSelection)?.let { provider ->
                updateWeatherProvider(provider)
                renderProviderContext()
            }
        }
        content.addView(fieldLabel(context, context.getString(R.string.settings_weather_provider)))
        content.addView(providerGroup, fieldParams())
        content.addView(keyLabel)
        content.addView(keyInput, fieldParams())
        content.addView(keyError, fieldParams())
        renderProviderContext()

        val selectedLocation = TextView(context).apply {
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            text = currentLocation()?.cityLabel ?: context.getString(R.string.settings_no_city)
        }
        content.addView(selectedLocation, fieldParams())

        val countryInput = EditText(context).apply {
            hint = "Страна"
            inputType = InputType.TYPE_CLASS_TEXT
            contentDescription = "Поиск страны"
            tag = "settings-country-search"
        }
        val countryResults = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        val cityInput = EditText(context).apply {
            hint = "Город выбранной страны"
            inputType = InputType.TYPE_CLASS_TEXT
            contentDescription = "Поиск города выбранной страны"
            tag = "settings-city-search"
        }
        val cityResults = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
        val locationError = inlineError(context)
        var selectedCountryCode: String? = currentLocation()?.countryCode
        lateinit var showCities: () -> Unit

        fun showCountries() {
            countryResults.removeAllViews()
            catalog?.searchCountries(countryInput.text.toString()).orEmpty().take(MAX_RESULTS).forEach { country ->
                countryResults.addView(Button(context).apply {
                    text = country.displayName
                    contentDescription = "Страна ${country.displayName}"
                    setOnClickListener {
                        selectedCountryCode = country.code
                        countryInput.setText(country.displayName)
                        cityInput.text.clear()
                        locationError.text = ""
                        showCities()
                    }
                })
            }
        }

        showCities = {
            cityResults.removeAllViews()
            val countryCode = selectedCountryCode
            if (countryCode != null) {
                val cities = catalog?.searchCities(countryCode, cityInput.text.toString()).orEmpty()
                if (cityInput.text.isNotBlank() && cities.isEmpty()) {
                    locationError.text = "Город не найден"
                }
                cities.take(MAX_RESULTS).forEach { city ->
                    cityResults.addView(Button(context).apply {
                        text = city.cityDisplayName
                        contentDescription = "Город ${city.cityDisplayName}"
                        setOnClickListener {
                            saveCatalogLocation(city)
                            selectedLocation.text = city.cityDisplayName
                            locationError.text = contextualizeWeatherError(weatherErrorProvider()).orEmpty()
                        }
                    })
                }
            }
        }

        countryInput.addTextChangedListener(simpleWatcher { showCountries() })
        cityInput.addTextChangedListener(simpleWatcher { showCities() })
        content.addView(fieldLabel(context, "Страна и город"))
        content.addView(countryInput, fieldParams())
        content.addView(countryResults, fieldParams())
        content.addView(cityInput, fieldParams())
        content.addView(cityResults, fieldParams())
        content.addView(locationError, fieldParams())

        showCountries()
        showCities()

        content.addView(TextView(context).apply {
            autoLinkMask = Linkify.WEB_URLS
            text = context.getString(R.string.settings_open_meteo_attribution)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 8)
            setTextColor(context.getColor(R.color.display_secondary))
            contentDescription = "Open-Meteo attribution"
            linksClickable = true
            movementMethod = LinkMovementMethod.getInstance()
            tag = "settings-open-meteo-attribution"
        }, fieldParams())

        content.addView(TextView(context).apply {
            text = context.getString(R.string.settings_geonames_attribution)
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 16)
            setTextColor(context.getColor(R.color.display_secondary))
            contentDescription = "GeoNames attribution"
            tag = "settings-geonames-attribution"
        }, fieldParams())

        content.addView(TextView(context).apply {
            text = context.getString(R.string.settings_alert_title)
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 8)
            setTextColor(context.getColor(R.color.display_primary))
        })
        content.addView(fieldLabel(context, context.getString(R.string.settings_alert_sound)))
        val signalGroup = RadioGroup(context).apply {
            orientation = RadioGroup.VERTICAL
            contentDescription = context.getString(R.string.settings_alert_sound_description)
            tag = "settings-alert-sound"
        }
        val currentSignal = timerAlertSettingsProjection().signal
        BuiltInAlertSignal.entries.forEach { signal ->
            signalGroup.addView(RadioButton(context).apply {
                id = View.generateViewId()
                text = signal.label
                textSize = 20f
                tag = signal
                contentDescription = "${context.getString(R.string.settings_alert_sound_description)}: ${signal.label}"
                isChecked = signal == currentSignal
            })
        }
        signalGroup.setOnCheckedChangeListener { group, checkedId ->
            (group.findViewById<RadioButton>(checkedId)?.tag as? BuiltInAlertSignal)
                ?.let(::updateAlertSignal)
        }
        content.addView(signalGroup, fieldParams())

        val alertVolume = timerAlertSettingsProjection().volumePercent
        val volumeValue = TextView(context).apply {
            textSize = 18f
            setTextColor(context.getColor(R.color.display_secondary))
            tag = "settings-alert-volume-value"
            contentDescription = context.getString(R.string.settings_alert_volume_description)
        }
        fun setVolumeLabel(value: Int) {
            volumeValue.text = context.getString(R.string.settings_alert_volume_value, value)
        }
        setVolumeLabel(alertVolume)
        content.addView(fieldLabel(context, context.getString(R.string.settings_alert_volume)))
        content.addView(volumeValue, fieldParams())
        val volumeError = inlineError(context).apply { tag = "settings-alert-volume-error" }
        val volumeSlider = SeekBar(context).apply {
            max = 100
            progress = alertVolume
            contentDescription = context.getString(R.string.settings_alert_volume_description)
            tag = "settings-alert-volume"
        }
        volumeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val result = updateAlertVolume(progress)
                setVolumeLabel(result.projection.volumePercent)
                volumeError.text = result.error?.message.orEmpty()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        content.addView(volumeSlider, fieldParams())
        content.addView(volumeError, fieldParams())

        val glassValue = TextView(context).apply {
            textSize = 18f
            setTextColor(context.getColor(R.color.display_secondary))
            tag = "settings-glass-value"
            contentDescription = context.getString(R.string.settings_glass_description)
        }
        fun setGlassLabel(value: Float) {
            glassValue.text = context.getString(R.string.settings_glass_value, value)
        }
        val initialGlass = snapshot().glassIntensity
        setGlassLabel(initialGlass)
        content.addView(fieldLabel(context, context.getString(R.string.settings_glass_title)))
        content.addView(glassValue, fieldParams())
        val glassSlider = SeekBar(context).apply {
            max = 100
            progress = (initialGlass * 100f).roundToInt()
            contentDescription = context.getString(R.string.settings_glass_description)
            tag = "settings-glass-intensity"
        }
        val glassError = inlineError(context).apply { tag = "settings-glass-error" }
        val previewContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            tag = "settings-glass-preview"
        }
        fun renderPreview(intensity: Float) {
            previewContainer.removeAllViews()
            previewFactory?.invoke(context, intensity)?.let {
                previewContainer.addView(it, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    220,
                ))
            }
        }
        renderPreview(initialGlass)
        glassSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val result = updateGlassIntensity(progress / 100f)
                setGlassLabel(result.intensity)
                glassError.text = result.error?.message.orEmpty()
                if (result.accepted) renderPreview(result.intensity)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        content.addView(glassSlider, fieldParams())
        content.addView(glassError, fieldParams())
        content.addView(TextView(context).apply {
            text = context.getString(R.string.settings_glass_preview)
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_primary))
        }, fieldParams())
        content.addView(previewContainer, fieldParams())

        content.addView(TextView(context).apply {
            text = context.getString(R.string.settings_timer_title)
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 8)
            setTextColor(context.getColor(R.color.display_primary))
        })
        TimerPresetSlot.entries.forEach { slot -> content.addView(timerPresetEditor(context, slot)) }
        content.addView(Button(context).apply {
            text = context.getString(R.string.settings_back_icon)
            contentDescription = context.getString(R.string.settings_back)
            setOnClickListener {
                keyInput.clearFocus()
                onBack()
            }
        }, fieldParams())
        return scroll
    }

    private fun fieldLabel(context: Context, text: String): TextView = TextView(context).apply {
        this.text = text
        textSize = 20f
        setTextColor(context.getColor(R.color.display_primary))
        setPadding(0, 16, 0, 4)
    }

    private fun inlineError(context: Context): TextView = TextView(context).apply {
        textSize = 14f
        setTextColor(context.getColor(R.color.display_secondary))
    }

    private fun fieldParams(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
    )

    private fun simpleWatcher(onChange: () -> Unit): TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = onChange()
        override fun afterTextChanged(s: Editable?) = Unit
    }

    private companion object {
        const val MAX_RESULTS = 12
    }

    private fun timerPresetEditor(context: Context, slot: TimerPresetSlot): View {
        val current = timerPresetProjection().preset(slot).duration
        val editor = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 8)
        }
        editor.addView(TextView(context).apply {
            text = context.getString(R.string.settings_preset_label, slot.label)
            textSize = 22f
            setTextColor(context.getColor(R.color.display_primary))
        })
        val fields = listOf(
            Triple(current.hours, "ч", "hours"),
            Triple(current.minutes, "м", "minutes"),
            Triple(current.seconds, "с", "seconds"),
        ).map { (value, unit, field) ->
            EditText(context).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(value.toString())
                hint = unit
                contentDescription = context.getString(R.string.settings_preset_field_description, slot.label, unit)
                tag = "preset-${slot.index}-$field"
            }
        }
        val row = LinearLayout(context).apply {
            gravity = Gravity.CENTER
        }
        fields.forEach { field ->
            row.addView(field, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        }
        editor.addView(row)
        val error = TextView(context).apply {
            textSize = 14f
            setTextColor(context.getColor(R.color.display_secondary))
        }
        editor.addView(error)

        var restoringFields = false

        fun restoreFields(duration: TimerPresetDuration) {
            restoringFields = true
            duration.editorFieldValues().forEachIndexed { index, value ->
                fields[index].setText(value)
            }
            restoringFields = false
        }

        fun persistFields() {
            if (restoringFields) return
            val result = updateTimerPreset(
                slot = slot,
                hours = fields[0].text.toString().toIntOrNull() ?: -1,
                minutes = fields[1].text.toString().toIntOrNull() ?: -1,
                seconds = fields[2].text.toString().toIntOrNull() ?: -1,
            )
            if (!result.accepted) restoreFields(result.duration)
            error.text = result.error?.message.orEmpty()
        }
        fields.forEach { field ->
            field.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = persistFields()
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        return editor
    }
}

interface TimerPresetReader {
    fun timerPresetProjection(): TimerPresetProjection
}

interface TimerAlertSettingsReader {
    fun timerAlertSettingsProjection(): TimerAlertSettingsProjection
}
