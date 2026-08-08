package com.hozayushka.app

import com.hozayushka.app.adapters.weather.ProviderCurrentWeather
import com.hozayushka.app.adapters.weather.ProviderDailyWeather
import com.hozayushka.app.adapters.weather.ProviderHourlyWeather
import com.hozayushka.app.adapters.weather.ProviderWeatherData
import com.hozayushka.app.adapters.weather.WeatherProvider
import com.hozayushka.app.adapters.weather.WeatherProviderRequest
import com.hozayushka.app.adapters.weather.WeatherProviderResult
import com.hozayushka.app.adapters.weather.RedactedProviderPayload
import com.hozayushka.app.settings.InMemorySettingsStateStore
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.weather.InMemoryWeatherCacheStore
import com.hozayushka.app.weather.PressureDirection
import com.hozayushka.app.weather.TemperaturePalette
import com.hozayushka.app.weather.WeatherCardPresentation
import com.hozayushka.app.weather.WeatherCardSlot
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherFreshness
import com.hozayushka.app.weather.WeatherIllustration
import com.hozayushka.app.weather.WeatherRefreshTrigger
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherContextTest {
    private val midday = 1_704_196_800_000L // 2024-01-02T12:00:00Z
    private val location = LocationContext("Test city", 40.0, 69.0, "UTC")

    @Test
    fun projectionKeepsAcceptedOrderSizingAndCardFields() {
        val weather = weatherWith(QueueProvider(sampleData()))
        weather.refresh(request(), midday)

        val projection = weather.projection(midday)

        assertEquals(
            listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
            projection.cards.map { it.slot },
        )
        assertFalse(projection.cards[0].isTodaySize)
        assertTrue(projection.cards[1].isTodaySize)
        assertFalse(projection.cards[2].isTodaySize)
        assertEquals("+2°", projection.cards[1].temperatureText)
        assertEquals("#448153", projection.cards[1].backgroundHex)
        assertEquals(WeatherIllustration.CLOUD, projection.cards[1].illustration)
        assertEquals(2, projection.cards[1].date.dayOfMonth)
    }

    @Test
    fun timezoneAndNightMoonFallbackUseSelectedCityData() {
        val nightMillis = 1_704_238_200_000L // 2024-01-03T00:30:00Z
        val settings = SettingsCapability(InMemorySettingsStateStore()).also {
            it.saveFoundationLocation(location.copy(apiTimeZone = "Asia/Dushanbe"))
        }
        val weather = WeatherCapability(
            locationReader = settings,
            cacheStore = InMemoryWeatherCacheStore(),
            provider = QueueProvider(sampleData(apiTimeZone = "Asia/Dushanbe", moonPhase = null)),
        )
        weather.refresh(request(), nightMillis)

        val today = weather.projection(nightMillis).cards[1]

        assertEquals(LocalDate.of(2024, 1, 3), today.date)
        assertEquals(4, today.temperatureCelsius)
        assertEquals(WeatherIllustration.MOON, today.illustration)
        assertEquals("regular", today.moonPhase)
    }

    @Test
    fun paletteIsExplicitAndSignClampAndGlassRulesAreDeterministic() {
        assertEquals(78, TemperaturePalette.all.size)
        assertEquals("#9653A4", TemperaturePalette.colorFor(-100))
        assertEquals("#4A0F00", TemperaturePalette.colorFor(100))
        assertEquals("#4F7EA6", TemperaturePalette.colorFor(0))
        assertEquals(0, WeatherCardPresentation.pseudoGlass(-1f).intensity.toInt())
        assertEquals(1f, WeatherCardPresentation.pseudoGlass(2f).intensity)
        assertTrue(WeatherCardPresentation.pseudoGlass(0.45f).isStatic)
        assertTrue(WeatherCardPresentation.pseudoGlass(0.45f).lightEdgeAlpha > 0)
    }

    @Test
    fun refreshTriggersCacheFreshnessAndStaleContours() {
        val provider = QueueProvider(sampleData())
        val weather = weatherWith(provider)

        assertNull(weather.refreshIfNeeded(midday, networkAvailable = false, WeatherRefreshTrigger.LAUNCH))
        assertTrue(weather.refreshIfNeeded(midday, networkAvailable = true, WeatherRefreshTrigger.LAUNCH) != null)
        assertNull(weather.refreshIfNeeded(midday + 1_000L, networkAvailable = true, WeatherRefreshTrigger.SCHEDULED))
        val scheduledAt = midday + 30L * 60L * 1_000L
        assertTrue(
            weather.refreshIfNeeded(
                scheduledAt,
                networkAvailable = true,
                WeatherRefreshTrigger.SCHEDULED,
            ) != null,
        )
        assertTrue(
            weather.refreshIfNeeded(
                scheduledAt + 1_000L,
                networkAvailable = true,
                WeatherRefreshTrigger.LOCATION_CHANGE,
            ) != null,
        )
        assertEquals(WeatherFreshness.FRESH, weather.projection(midday + 23L * 60L * 60L * 1_000L).freshness)
        val stale = weather.projection(scheduledAt + 1_000L + 24L * 60L * 60L * 1_000L + 1L)
        assertEquals(WeatherFreshness.STALE_EMPTY, stale.freshness)
        assertEquals(4, stale.cards.size)
        assertTrue(stale.cards.all { it.temperatureCelsius == null && it.pressureArrowCount == 0 })
        assertEquals(3, provider.calls)
    }

    @Test
    fun pressureThresholdsAndTwelveHourZeroFallbackAreOwnedByWeatherContext() {
        val provider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData(pressure = 104.1),
        )
        val weather = weatherWith(provider)
        weather.refresh(request(), midday - 3L * 60L * 60L * 1_000L)
        weather.refresh(request(), midday)
        val trend = weather.projection(midday).cards[1]
        assertEquals(2, trend.pressureArrowCount)
        assertEquals(PressureDirection.UP, trend.pressureDirection)

        val fallbackProvider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData(pressure = 101.0),
            sampleData(pressure = 101.0),
        )
        val fallbackWeather = weatherWith(fallbackProvider)
        fallbackWeather.refresh(request(), midday - 12L * 60L * 60L * 1_000L)
        fallbackWeather.refresh(request(), midday - 3L * 60L * 60L * 1_000L)
        fallbackWeather.refresh(request(), midday)
        val fallback = fallbackWeather.projection(midday).cards[1]
        assertEquals(1, fallback.pressureArrowCount)
        assertEquals(PressureDirection.UP, fallback.pressureDirection)
    }

    @Test
    fun unknownConditionAndMissingOptionalFieldsUseNeutralFallbackWithoutText() {
        val data = sampleData(currentCondition = "alien-weather", nightCondition = null, moonPhase = null)
        val weather = weatherWith(QueueProvider(data))
        weather.refresh(request(), midday)

        val today = weather.projection(midday).cards[1]
        assertEquals(WeatherIllustration.NEUTRAL_CLOUD, today.illustration)
        assertEquals(2, today.temperatureCelsius)
        assertTrue(today.temperatureText!!.contains("°"))
        assertFalse(today.temperatureText!!.contains("cloud", ignoreCase = true))
    }

    @Test
    fun incompleteStructuredRefreshDoesNotReplaceSuccessfulCache() {
        val provider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData().copy(
                current = ProviderCurrentWeather(40, Double.NaN, "rain"),
                daily = emptyList(),
            ),
        )
        val weather = weatherWith(provider)
        assertTrue(weather.refresh(request(), midday) != null)
        assertNull(weather.refresh(request(), midday + 30L * 60L * 1_000L))
        assertEquals(2, weather.snapshot()!!.temperatureCelsius)
        assertEquals("cloud", weather.snapshot()!!.condition)
    }

    @Test
    fun supportedFullDayHourlyPayloadNormalizesAcceptedCityLocalSlots() {
        val now = 1_704_196_800_000L // 2024-01-02T12:00:00Z
        val weather = weatherWith(
            QueueProvider(
                sampleData(
                    apiTimeZone = "Asia/Dushanbe",
                    hourly = fullDayHourly(LocalDate.of(2024, 1, 2)),
                ),
            ),
        )

        assertNotNull(weather.refresh(request(), now))

        val projection = weather.hourlyProjection(now)
        assertNotNull(projection)
        assertEquals("Asia/Dushanbe", projection!!.apiTimeZone)
        assertEquals(
            listOf(
                LocalTime.of(6, 0),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(15, 0),
                LocalTime.of(18, 0),
                LocalTime.of(21, 0),
                LocalTime.MIDNIGHT,
                LocalTime.of(3, 0),
            ),
            projection.cards.map { it.slotTime },
        )
        assertEquals(
            listOf(
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 2),
                LocalDate.of(2024, 1, 3),
                LocalDate.of(2024, 1, 3),
            ),
            projection.cards.map { it.date },
        )
    }

    @Test
    fun selectedHourlyRequiredFieldMissingKeepsProjectionUnavailable() {
        val now = 1_704_196_800_000L // 2024-01-02T12:00:00Z
        val base = fullDayHourly(LocalDate.of(2024, 1, 2))
        val variants = listOf(
            base.filterNot { it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(9, 0) }
                .plus(ProviderHourlyWeather(LocalDate.of(2024, 1, 2), LocalTime.of(10, 0), 5, "cloud")),
            base.map {
                if (it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(12, 0)) {
                    it.copy(temperatureCelsius = null)
                } else {
                    it
                }
            },
            base.map {
                if (it.date == LocalDate.of(2024, 1, 2) && it.time == LocalTime.of(15, 0)) {
                    it.copy(condition = null)
                } else {
                    it
                }
            },
        )

        variants.forEach { hourly ->
            val weather = weatherWith(
                QueueProvider(
                    sampleData(
                        apiTimeZone = "Asia/Dushanbe",
                        hourly = hourly,
                    ),
                ),
            )

            assertNull(weather.refresh(request(), now))
            assertNull(weather.hourlyProjection(now))
        }
    }

    @Test
    fun yesterdayUsesLargestChangeAndHistoryIsInstallationRelativeSevenDays() {
        val provider = QueueProvider(
            sampleData(pressure = 100.0),
            sampleData(pressure = 104.1),
            sampleData(pressure = 102.0),
            sampleData(baseDate = LocalDate.of(2024, 1, 10), pressure = 100.0),
        )
        val weather = weatherWith(provider)
        val yesterdayFirst = midday - 20L * 60L * 60L * 1_000L
        val yesterdayLargest = midday - 18L * 60L * 60L * 1_000L
        weather.refresh(request(), yesterdayFirst)
        weather.refresh(request(), yesterdayLargest)
        weather.refresh(request(), midday)

        val yesterday = weather.projection(midday).cards[0]
        assertEquals(2, yesterday.pressureArrowCount)
        assertEquals(PressureDirection.UP, yesterday.pressureDirection)

        val eightDaysLater = midday + 8L * 24L * 60L * 60L * 1_000L
        weather.refresh(request(), eightDaysLater)
        assertEquals(0, weather.projection(eightDaysLater).cards[1].pressureArrowCount)
    }

    private fun weatherWith(provider: WeatherProvider = QueueProvider(sampleData())): WeatherCapability {
        val settings = SettingsCapability(InMemorySettingsStateStore()).also { it.saveFoundationLocation(location) }
        return WeatherCapability(settings, InMemoryWeatherCacheStore(), provider)
    }

    private fun request(): WeatherProviderRequest = WeatherProviderRequest.fromSyntheticProbe()

    private fun sampleData(
        apiTimeZone: String = "UTC",
        baseDate: LocalDate = LocalDate.of(2024, 1, 2),
        pressure: Double = 100.0,
        currentCondition: String? = "cloud",
        nightCondition: String? = "clear",
        moonPhase: String? = "half",
        hourly: List<ProviderHourlyWeather> = emptyList(),
    ): ProviderWeatherData {
        return ProviderWeatherData(
            apiTimeZone = apiTimeZone,
            current = ProviderCurrentWeather(2, pressure, currentCondition),
            daily = (-1..2).map { offset ->
                ProviderDailyWeather(
                    date = baseDate.plusDays(offset.toLong()),
                    dayTemperatureCelsius = 2 + offset,
                    nightTemperatureCelsius = 3 + offset,
                    dayCondition = "cloud",
                    nightCondition = nightCondition,
                    moonPhase = moonPhase,
                )
            },
            hourly = hourly,
        )
    }

    private fun fullDayHourly(startDate: LocalDate): List<ProviderHourlyWeather> =
        (0..1).flatMap { dayOffset ->
            (0..23).map { hour ->
                ProviderHourlyWeather(
                    date = startDate.plusDays(dayOffset.toLong()),
                    time = LocalTime.of(hour, 0),
                    temperatureCelsius = hour - 4,
                    condition = "cloud",
                )
            }
        }

    private class QueueProvider(vararg private val values: ProviderWeatherData) : WeatherProvider {
        var calls: Int = 0
            private set

        override fun fetch(request: WeatherProviderRequest): WeatherProviderResult {
            val value = values[minOf(calls, values.lastIndex)]
            calls += 1
            return WeatherProviderResult(
                payload = RedactedProviderPayload(value.current.temperatureCelsius, value.current.condition.orEmpty()),
                credentialWasReceived = request.hasCredential(),
                redactedCredential = request.redactedCredential(),
                weatherData = value,
            )
        }
    }
}
