package com.hozayushka.app

import com.hozayushka.app.adapters.weather.RedactedFixtureParser
import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.adapters.weather.RedactedWeatherFixtureAdapter
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerLifecycleState
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.WeatherCapability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FoundationProbesTest {
    @Test
    fun ownerLocalStateReloadsAndResetIsolated() {
        val firstStores = ProbeStores()
        val secondStores = ProbeStores()
        val first = firstStores.capabilities()
        val second = secondStores.capabilities()
        val location = LocationContext(
            cityLabel = "Тестовый город",
            latitude = 40.28,
            longitude = 69.62,
            apiTimeZone = "Asia/Dushanbe",
        )

        assertNull(second.settings.currentLocation())
        assertNull(second.weather.snapshot())
        assertEquals(TimerLifecycleState.IDLE, second.timer.snapshotAt(1_000L).state)

        first.settings.saveFoundationLocation(location)
        first.timer.start(startedAtMillis = 1_000L, durationMillis = 60_000L)
        val refresh = first.weather.refreshFoundationFixture()

        assertNotNull(refresh)
        assertEquals(location, first.settings.currentLocation())
        assertEquals(TimerLifecycleState.COUNTDOWN, first.timer.snapshotAt(2_000L).state)
        assertEquals(59_000L, first.timer.snapshotAt(2_000L).remainingMillis)
        assertEquals(location.cityLabel, first.weather.snapshot()?.cityLabel)

        val reloaded = firstStores.capabilities()
        assertEquals(location, reloaded.settings.currentLocation())
        assertEquals(location.cityLabel, reloaded.weather.snapshot()?.cityLabel)
        val rehydrated = reloaded.timer.rehydrateAt(61_000L)
        assertEquals(TimerLifecycleState.OVERDUE, rehydrated.state)
        assertEquals(60_000L, rehydrated.elapsedMillis)

        try {
            first.settings.resetFoundationState()
            first.weather.resetFoundationState()
            first.timer.resetFoundationState()

            assertNull(reloaded.settings.currentLocation())
            assertNull(reloaded.weather.snapshot())
            assertEquals(TimerLifecycleState.IDLE, reloaded.timer.snapshotAt(61_000L).state)
            assertNull(second.settings.currentLocation())
            assertNull(second.weather.snapshot())
            assertEquals(TimerLifecycleState.IDLE, second.timer.snapshotAt(61_000L).state)
        } finally {
            first.settings.resetFoundationState()
            first.weather.resetFoundationState()
            first.timer.resetFoundationState()
            second.settings.resetFoundationState()
            second.weather.resetFoundationState()
            second.timer.resetFoundationState()
        }
    }

    @Test
    fun redactedProviderFixtureReachesWeatherOwnerWithoutCredentialOutput() {
        val fixtureText = requireNotNull(
            javaClass.getResourceAsStream("/fixtures/redacted-weather.json"),
        ).bufferedReader().use { it.readText() }
        val payload: RedactedProviderPayload = RedactedFixtureParser.parse(fixtureText)
        val settings = SettingsCapability(InMemorySettingsStateStore())
        settings.saveFoundationLocation(
            LocationContext("Fixture city", 40.0, 69.0, "Asia/Dushanbe"),
        )
        val weather = WeatherCapability(
            locationReader = settings,
            cacheStore = InMemoryWeatherCacheStore(),
            provider = RedactedWeatherFixtureAdapter(payload),
        )

        val result = weather.refreshFoundationFixture()

        assertNotNull(result)
        assertTrue(result!!.credentialWasReceived)
        assertEquals("[REDACTED]", result.redactedCredential)
        assertEquals(21, result.snapshot.temperatureCelsius)
        assertEquals("cloud", result.snapshot.condition)
        assertTrue(fixtureText.contains("[REDACTED]"))
    }

    private data class ProbeStores(
        val settings: InMemorySettingsStateStore = InMemorySettingsStateStore(),
        val weather: InMemoryWeatherCacheStore = InMemoryWeatherCacheStore(),
        val timer: InMemoryTimerStateStore = InMemoryTimerStateStore(),
    ) {
        fun capabilities(): ProbeCapabilities {
            val settingsCapability = SettingsCapability(settings)
            return ProbeCapabilities(
                settings = settingsCapability,
                weather = WeatherCapability(
                    locationReader = settingsCapability,
                    cacheStore = weather,
                    provider = RedactedWeatherFixtureAdapter(),
                ),
                timer = TimerCapability(timer),
            )
        }
    }

    private data class ProbeCapabilities(
        val settings: SettingsCapability,
        val weather: WeatherCapability,
        val timer: TimerCapability,
    )
}
