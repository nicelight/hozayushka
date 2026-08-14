package com.hozayushka.app

import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.WeatherProviderSelection
import com.hozayushka.app.weather.PressureHistoryEntry
import com.hozayushka.app.weather.WeatherCacheRecord
import com.hozayushka.app.weather.WeatherCapability
import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Initial target-state probe for TASK-020. It intentionally uses only current,
 * compilable public/reflection surfaces so the pre-migration result is an
 * honest behavior/shape RED rather than a missing-import or syntax failure.
 */
class ProviderMigrationClaimProbeTest {
    @Test
    fun ac002TargetAdaptersAndProviderIdentifiedEnvelopeExist() {
        val adapterFiles = weatherAdapterSources().map(File::getName).toSet()
        val providerImplementations = weatherAdapterSources()
            .filter { it.readText().contains(") : WeatherProvider") }
            .map(File::getName)
            .toSet()
        val resultFields = WeatherProviderResult::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(
            "Expected exactly the two target adapter sources and provider identity in the envelope; " +
                "adapterFiles=$adapterFiles resultFields=$resultFields",
            "OpenMeteoWeatherAdapter.kt" in adapterFiles &&
                "OpenWeatherWeatherAdapter.kt" in adapterFiles &&
                "YandexWeatherAdapter.kt" !in adapterFiles &&
                providerImplementations == setOf(
                    "OpenMeteoWeatherAdapter.kt",
                    "OpenWeatherWeatherAdapter.kt",
                ) &&
                "provider" in resultFields,
        )
    }

    @Test
    fun ac004CacheCarriesProviderAndLocationIdentity() {
        val fields = WeatherCacheRecord::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(
            "Weather cache record must carry provider plus location identity; fields=$fields",
            "provider" in fields && "locationIdentity" in fields,
        )
    }

    @Test
    fun ac005HistoryCarriesProviderAndLocationIdentity() {
        val fields = PressureHistoryEntry::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(
            "Weather history must carry provider plus location identity; fields=$fields",
            "provider" in fields && "locationIdentity" in fields,
        )
    }

    @Test
    fun ac006BothTargetAdaptersOwnConditionMappingInputs() {
        val sources = weatherAdapterSources().associateBy(File::getName)

        assertTrue(
            "Both target provider decoders must exist for two-provider unknown/optional mapping; " +
                "adapterFiles=${sources.keys}",
            sources.keys.containsAll(
                setOf("OpenMeteoWeatherAdapter.kt", "OpenWeatherWeatherAdapter.kt"),
            ),
        )
    }

    @Test
    fun ac007OnlySelectedOpenWeatherAuthorizesOwnerKeyAccess() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        var callbackInvoked = false

        val openMeteoAccess = settings.withSelectedOpenWeatherApiKey {
            callbackInvoked = true
            true
        }
        assertFalse(openMeteoAccess ?: false)
        assertFalse(callbackInvoked)

        settings.updateWeatherProvider(WeatherProviderSelection.OPEN_WEATHER)
        assertTrue(settings.updateOpenWeatherApiKey(syntheticKey()).accepted)
        val selectedAccess = settings.withSelectedOpenWeatherApiKey {
            callbackInvoked = true
            true
        }

        assertTrue("Selected OpenWeather must atomically replace the W16 blanket deny", selectedAccess == true)
        assertTrue(callbackInvoked)
    }

    @Test
    fun ac008WeatherContextOwnsTwoExplicitSelectedProviderLeaves() {
        val fields = WeatherCapability::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(
            "Weather Context must own explicit Open-Meteo/OpenWeather dispatch fields; fields=$fields",
            "openMeteoProvider" in fields && "openWeatherProvider" in fields && "provider" !in fields,
        )
    }

    private fun weatherAdapterSources(): List<File> {
        val directory = listOf(
            File("app/src/main/kotlin/com/hozayushka/app/adapters/weather"),
            File("src/main/kotlin/com/hozayushka/app/adapters/weather"),
        ).first(File::isDirectory)
        return directory.listFiles().orEmpty().filter { it.extension == "kt" }
    }

    private fun syntheticKey(): String = buildString {
        append(ProviderMigrationClaimProbeTest::class.java.name.hashCode().toUInt().toString(16))
        append('-')
        append(System.nanoTime().toString(16))
    }
}
