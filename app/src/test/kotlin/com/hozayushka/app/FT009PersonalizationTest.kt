package com.hozayushka.app

import com.hozayushka.app.display.SettingsPreviewProjection
import com.hozayushka.app.settings.BuiltInAlertSignal
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.TimerAlertSettingsProjection
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerLifecycleState
import com.hozayushka.app.weather.PressureDirection
import com.hozayushka.app.weather.WeatherCardPresentation
import com.hozayushka.app.weather.WeatherCardProjection
import com.hozayushka.app.weather.WeatherCardSlot
import com.hozayushka.app.weather.WeatherFreshness
import com.hozayushka.app.weather.WeatherIllustration
import com.hozayushka.app.weather.WeatherProjection
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FT009PersonalizationTest {
    @Test
    fun defaultsValidChangesAndReloadUseOneValidatedSettingsProjection() {
        val store = InMemorySettingsStateStore()
        val settings = SettingsCapability(store)

        assertEquals(BuiltInAlertSignal.CLASSIC, settings.settingsPresentationProjection().signal)
        assertEquals(70, settings.settingsPresentationProjection().volumePercent)
        assertEquals(0.45f, settings.settingsPresentationProjection().glassIntensity, 0.0001f)

        settings.updateAlertSignal(BuiltInAlertSignal.ELECTRONIC)
        assertTrue(settings.updateAlertVolume(0).accepted)
        assertTrue(settings.updateGlassIntensity(1f).accepted)

        val reloaded = SettingsCapability(store)
        assertEquals(BuiltInAlertSignal.ELECTRONIC, reloaded.settingsPresentationProjection().signal)
        assertEquals(0, reloaded.settingsPresentationProjection().volumePercent)
        assertEquals(1f, reloaded.settingsPresentationProjection().glassIntensity, 0.0001f)
    }

    @Test
    fun invalidVolumeAndGlassValuesPreserveLastValidProjectionWithOwningErrors() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        settings.updateAlertVolume(35)
        settings.updateGlassIntensity(0.45f)

        val invalidVolume = settings.updateAlertVolume(101)
        val invalidGlassLow = settings.updateGlassIntensity(-0.01f)
        val invalidGlassHigh = settings.updateGlassIntensity(1.01f)
        val invalidGlassNan = settings.updateGlassIntensity(Float.NaN)

        assertFalse(invalidVolume.accepted)
        assertEquals("Громкость должна быть в диапазоне 0–100", invalidVolume.error?.message)
        assertEquals(35, invalidVolume.projection.volumePercent)
        assertFalse(invalidGlassLow.accepted)
        assertFalse(invalidGlassHigh.accepted)
        assertFalse(invalidGlassNan.accepted)
        assertEquals("Интенсивность стекла должна быть в диапазоне 0–1", invalidGlassNan.error?.message)
        assertEquals(35, settings.timerAlertSettingsProjection().volumePercent)
        assertEquals(0.45f, settings.settingsPresentationProjection().glassIntensity, 0.0001f)
    }

    @Test
    fun previewUsesTodayOrFallbackTemperatureTwoArrowsAndStaticMaterialAtGestureValues() {
        val today = WeatherCardProjection(
            slot = WeatherCardSlot.TODAY,
            date = LocalDate.of(2026, 8, 8),
            temperatureCelsius = 12,
            temperatureText = "12 °C",
            backgroundHex = "#98C1D1",
            illustration = WeatherIllustration.CLOUD,
            moonPhase = null,
            pressureArrowCount = 1,
            pressureDirection = PressureDirection.DOWN,
            isTodaySize = true,
        )
        val weather = WeatherProjection(
            cityLabel = "Тестовый город",
            apiTimeZone = "Asia/Dushanbe",
            freshness = WeatherFreshness.FRESH,
            cards = listOf(today),
        )

        val preview = SettingsPreviewProjection.from(
            weatherProjection = weather,
            nowMillis = 0L,
            zoneId = ZoneId.of("UTC"),
        )
        assertEquals(12, preview.temperatureCelsius)
        assertEquals("12 °C", preview.temperatureText)
        assertEquals(2, preview.pressureArrowCount)
        assertEquals(PressureDirection.DOWN, preview.pressureDirection)

        val fallback = SettingsPreviewProjection.from(
            weatherProjection = weather.copy(cards = listOf(today.copy(temperatureCelsius = null, temperatureText = null, backgroundHex = null))),
            nowMillis = 0L,
            zoneId = ZoneId.of("UTC"),
        )
        assertEquals(24, fallback.temperatureCelsius)
        assertEquals("24 °C", fallback.temperatureText)
        assertEquals(2, fallback.pressureArrowCount)

        val materials = listOf(0f, 0.45f, 1f).map(WeatherCardPresentation::pseudoGlass)
        assertTrue(materials.zipWithNext().all { (left, right) -> left != right })
        assertTrue(materials.all { it.isStatic })
    }

    @Test
    fun timerReadsProjectionAndVolumeZeroSuppressesOnlyAppAlertAudio() {
        val settings = SettingsCapability(InMemorySettingsStateStore())
        settings.updateAlertSignal(BuiltInAlertSignal.BELL)
        settings.updateAlertVolume(0)
        val before = settings.snapshot()
        val timer = TimerCapability(
            stateStore = InMemoryTimerStateStore(),
            presetReader = settings,
            alertSettingsReader = settings,
        )

        timer.start(1_000L, 1_000L)
        val decision = timer.advanceAt(2_000L)

        assertEquals(TimerLifecycleState.OVERDUE, timer.snapshotAt(2_000L).state)
        assertTrue(decision.visualOverdue)
        assertNull(decision.audioRequest)
        assertNull(decision.audioResult)
        assertEquals(before, settings.snapshot())
        assertEquals(TimerAlertSettingsProjection(BuiltInAlertSignal.BELL, 0), settings.timerAlertSettingsProjection())
    }
}
