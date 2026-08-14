package com.hozayushka.app

import android.view.MotionEvent
import com.hozayushka.app.display.ActiveCountdownTouchDispatcher
import com.hozayushka.app.display.ActiveCountdownSurfaceGeometry
import com.hozayushka.app.display.CityGesture
import com.hozayushka.app.display.CityInteraction
import com.hozayushka.app.display.CityInteractionRouter
import com.hozayushka.app.display.ColonMode
import com.hozayushka.app.display.ColonProjection
import com.hozayushka.app.display.DisplayConnectivity
import com.hozayushka.app.display.DisplayFormatters
import com.hozayushka.app.display.DisplayLayoutSpec
import com.hozayushka.app.display.WeatherCardContentGeometry
import com.hozayushka.app.display.WeatherIllustrationCanvas
import com.hozayushka.app.display.MainDisplayGeometry
import com.hozayushka.app.display.MainClockGeometry
import com.hozayushka.app.display.MainDisplayTickerOwner
import com.hozayushka.app.display.MainDisplayTickerScheduler
import com.hozayushka.app.display.MainDisplayWeatherCardRenderer
import com.hozayushka.app.display.MainDisplayWeatherRenderInput
import com.hozayushka.app.display.OverduePresentation
import com.hozayushka.app.display.OverdueSurfaceGeometry
import com.hozayushka.app.display.PressureArrowCanvas
import com.hozayushka.app.display.PresetPresentation
import com.hozayushka.app.display.PresetVisualGeometry
import com.hozayushka.app.display.forecastEntryIntent
import com.hozayushka.app.display.mainDisplayClockTextSizeForRefresh
import com.hozayushka.app.display.orderedDisplayWeatherSlots
import com.hozayushka.app.forecast.ForecastEntryIntent
import com.hozayushka.app.timer.InMemoryTimerStateStore
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import com.hozayushka.app.timer.TimerPresetPresentation
import com.hozayushka.app.settings.TimerPresetDuration
import com.hozayushka.app.settings.TimerPresetSlot
import com.hozayushka.app.weather.WeatherCardSlot
import com.hozayushka.app.weather.WeatherFreshness
import com.hozayushka.app.weather.WeatherCardProjection
import com.hozayushka.app.weather.WeatherIllustration
import com.hozayushka.app.weather.WeatherProjection
import com.hozayushka.app.weather.PressureDirection
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayProjectionTest {
    @Test
    fun deviceTimezoneDrivesClockAndRussianGenitiveDate() {
        val instant = Instant.parse("2024-07-31T22:30:00Z").toEpochMilli()

        assertEquals("03:30", DisplayFormatters.timeText(instant, ZoneId.of("Asia/Dushanbe")))
        assertEquals("01 августа", DisplayFormatters.dateText(instant, ZoneId.of("Asia/Dushanbe")))
        assertEquals("31 июля", DisplayFormatters.dateText(instant, ZoneId.of("America/New_York")))
    }

    @Test
    fun stableShellReservesHeaderContentBeforeFourCardsAndThreePresets() {
        val emptyWeather = DisplayLayoutSpec()
        val projectedWeather = DisplayLayoutSpec(
            weatherCardCount = 4,
            presetCount = 3,
            headerWeight = 0f,
            weatherRowWeight = 1f,
        )

        assertEquals(4, emptyWeather.weatherCardCount)
        assertEquals(3, emptyWeather.presetCount)
        assertEquals(0f, emptyWeather.headerWeight, 0f)
        assertEquals(1f, emptyWeather.weatherRowWeight, 0f)
        assertTrue(emptyWeather.weatherRowWeight > emptyWeather.headerWeight)
        assertEquals(emptyWeather, projectedWeather)
    }

    @Test
    fun mainDisplayGeometryKeepsLeftCenterRightRegionsAndCardRelations() {
        val spec = DisplayLayoutSpec()
        val geometry = MainDisplayGeometry.measure(1280, 720, spec)
        val cards = geometry.weatherCardBounds

        assertEquals(
            listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
            geometry.weatherSlots,
        )
        assertTrue(geometry.cityBounds.right <= cards[0].right)
        assertTrue(geometry.dateBounds.right <= cards[0].right)
        assertTrue(geometry.cityBounds.bottom <= cards[0].top)
        assertTrue(geometry.dateBounds.bottom <= cards[0].top)
        assertTrue(geometry.clockBounds.left >= cards[1].left)
        assertTrue(geometry.clockBounds.right <= cards[3].right)
        assertTrue(geometry.clockBounds.bottom <= cards[1].top)
        assertTrue(geometry.presetBounds.all { it.left >= cards[3].right })

        assertEquals(listOf(217, 273, 217, 217), cards.map { it.width })
        assertEquals(listOf(32, 273, 570, 811), cards.map { it.left })
        assertEquals(listOf(249, 546, 787, 1028), cards.map { it.right })
        assertTrue(cards[1].width > cards[0].width)
        assertEquals(cards[0].width, cards[2].width)
        assertEquals(cards[0].width, cards[3].width)
        assertEquals(3, geometry.interCardGaps.size)
        assertTrue(geometry.interCardGaps.all { it == spec.interCardGapDp })
        assertTrue(geometry.interCardGaps.all { it > 8 })
    }

    @Test
    fun w24GreenGeometryMakesClockDominantAndPresetsCircular() {
        val spec = DisplayLayoutSpec()
        val geometry = MainDisplayGeometry.measure(1280, 720, spec)
        val cards = geometry.weatherCardBounds

        assertTrue(spec.idleClockTextSize > 132f)
        assertTrue(geometry.clockBounds.left >= cards[1].left)
        assertTrue(geometry.clockBounds.right <= cards[3].right)
        assertTrue(geometry.clockBounds.bottom <= cards[1].top)
        assertTrue(geometry.presetBounds.all { it.left >= cards[3].right })
        assertTrue(geometry.presetBounds.all { it.width == it.height })
        assertEquals(listOf(200, 200, 200), geometry.presetBounds.map { it.width })
        assertEquals(listOf(100f, 100f, 100f), geometry.presetCornerRadii)
        assertTrue(
            geometry.presetBounds.zip(geometry.presetCornerRadii).all { (bounds, radius) ->
                radius >= bounds.width / 2f
            },
        )
        assertEquals(listOf(24, 24), geometry.presetBounds.zipWithNext().map { (first, second) -> second.top - first.bottom })
        assertEquals(
            listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
            geometry.weatherSlots,
        )
        assertTrue(cards[1].width > cards[0].width)
        assertEquals(cards[0].width, cards[2].width)
        assertEquals(cards[0].width, cards[3].width)
        assertEquals(listOf(spec.interCardGapDp, spec.interCardGapDp, spec.interCardGapDp), geometry.interCardGaps)
        println(
            "W24 GREEN clock=${geometry.clockBounds.snapshot()} textSize=${spec.idleClockTextSize} " +
                "presets=${geometry.presetBounds.map { it.snapshot() }} radii=${geometry.presetCornerRadii} " +
                "cards=${cards.map { it.snapshot() }} gaps=${geometry.interCardGaps}",
        )
    }

    @Test
    fun w26ClaimProbeRequiresLargerAdaptiveClockAndExpandedSpacing() {
        val spec = DisplayLayoutSpec()
        val target = MainDisplayGeometry.measure(1280, 720, spec)
        val alternate = MainDisplayGeometry.measure(1024, 600, spec)

        assertTrue(
            "W26 GREEN: idle clock must advance beyond the W24 baseline",
            target.idleClockTextSize > 176f,
        )
        assertTrue(
            "W26 GREEN: alternate layout must adapt the idle clock to available space",
            alternate.idleClockTextSize in 0f..target.idleClockTextSize,
        )
        assertTrue(
            "W26 GREEN: common weather-card gap must exceed the W24 baseline",
            spec.interCardGapDp > 16,
        )
        assertTrue(
            "W26 GREEN: preset-circle spacing must exceed the W24 baseline",
            spec.presetGapDp > 4,
        )
        assertTrue(target.presetBounds.all { it.width == it.height })
        assertTrue(target.presetBounds.zipWithNext().all { (first, second) -> second.top - first.bottom > 4 })
        assertTrue(target.weatherCardBounds[1].width > target.weatherCardBounds[0].width)
        assertEquals(target.weatherCardBounds[0].width, target.weatherCardBounds[2].width)
        assertEquals(target.weatherCardBounds[0].width, target.weatherCardBounds[3].width)
        assertEquals(listOf(spec.interCardGapDp, spec.interCardGapDp, spec.interCardGapDp), target.interCardGaps)
        println(
            "W26 GREEN targetClock=${target.clockBounds.snapshot()} targetClockTextSize=${target.idleClockTextSize} " +
                "alternateClockTextSize=${alternate.idleClockTextSize} presets=${target.presetBounds.map { it.snapshot() }} " +
                "radii=${target.presetCornerRadii} cards=${target.weatherCardBounds.map { it.snapshot() }} " +
                "gaps=${target.interCardGaps}",
        )
    }

    @Test
    fun w29FullClockMeasurementFitsBothRequiredLandscapeSizes() {
        val measurements = listOf(2460 to 1080, 1280 to 720).map { (width, height) ->
            val geometry = MainDisplayGeometry.measure(width, height)
            MainClockGeometry.measure(
                availableWidth = geometry.clockBounds.width,
                availableHeight = geometry.clockBounds.height,
                textWidthAtUnitSize = 3f,
                textHeightAtUnitSize = 1.2f,
            )
        }

        measurements.forEach { measurement ->
            assertTrue(measurement.measuredWidth <= measurement.availableWidth + 0.01f)
            assertTrue(measurement.measuredHeight <= measurement.availableHeight + 0.01f)
            assertTrue(measurement.textSizePx > 0f)
        }
        assertTrue(measurements.first().textSizePx > measurements.last().textSizePx)
        println(
            "W29 GREEN clock measurements=" + measurements.map {
                "available=${it.availableWidth}x${it.availableHeight},sizePx=${it.textSizePx}," +
                    "measured=${it.measuredWidth}x${it.measuredHeight}"
            },
        )
    }

    @Test
    fun w29WeatherSlotsStayOrderedAndShellsSurviveNoDataAsyncAndPopulatedInputs() {
        val noData = weatherProjection()
        val async = noData.copy(cards = listOf(noData.cards[1]))
        val populated = noData.copy(
            freshness = WeatherFreshness.FRESH,
            cards = noData.cards.reversed().map { card ->
                card.copy(
                    temperatureCelsius = 21,
                    temperatureText = "21 °C",
                    backgroundHex = "#FFE0A3",
                    illustration = WeatherIllustration.CLOUD,
                    pressureArrowCount = 1,
                    pressureDirection = PressureDirection.UP,
                )
            },
        )

        listOf(noData, async, populated).forEach { projection ->
            val slots = orderedDisplayWeatherSlots(projection)
            assertEquals(WeatherCardSlot.entries.toList(), slots.map { it.slot })
            assertEquals(4, slots.size)
        }
        assertNull(orderedDisplayWeatherSlots(async).first { it.slot == WeatherCardSlot.YESTERDAY }.projection)
        assertEquals(21, orderedDisplayWeatherSlots(populated).first { it.slot == WeatherCardSlot.TODAY }.projection?.temperatureCelsius)
        assertEquals(
            listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
            orderedDisplayWeatherSlots(async).map { it.slot },
        )
        println(
            "W29 GREEN slots noData=${orderedDisplayWeatherSlots(noData).map { it.slot }} " +
                "async=${orderedDisplayWeatherSlots(async).map { it.slot to (it.projection != null) }} " +
                "populated=${orderedDisplayWeatherSlots(populated).map { it.slot to it.projection?.temperatureText }}",
        )
    }

    @Test
    fun w29PresetRimIsWiderRadialAndHasStaticOutwardGlowLayers() {
        val side = 200
        val presetColors = listOf(0xFFFF7A00.toInt(), 0xFFFF4FA3.toInt(), 0xFFA855F7.toInt())
        TimerPresetSlot.entries.zip(presetColors).forEach { (_, presetColor) ->
            val radialColors = PresetVisualGeometry.radialShadeColors(
                presetColor,
            )
            assertEquals(3, radialColors.size)
            assertTrue(PresetVisualGeometry.rimWidthPx(side, false) > 7f)
            assertTrue(PresetVisualGeometry.rimWidthPx(side, true) > PresetVisualGeometry.rimWidthPx(side, false))
            assertTrue(PresetVisualGeometry.glowLayerCount() >= 3)
            assertTrue(PresetVisualGeometry.glowSpreadPx(side, PresetVisualGeometry.glowLayerCount()) > 0f)
        }
        assertEquals(
            listOf("#FF7A00", "#FF4FA3", "#A855F7"),
            TimerPresetSlot.entries.map(PresetPresentation::colorHex),
        )
        println(
            "W29 GREEN presets colors=${TimerPresetSlot.entries.map(PresetPresentation::colorHex)} " +
                "rim=${PresetVisualGeometry.rimWidthPx(side, false)} activeRim=${PresetVisualGeometry.rimWidthPx(side, true)} " +
                "glowLayers=${PresetVisualGeometry.glowLayerCount()} outerGlow=${PresetVisualGeometry.glowSpreadPx(side, PresetVisualGeometry.glowLayerCount())}",
        )
    }

    @Test
    fun reachableMainDisplayRefreshKeepsIdleClock176AndCountdown32() {
        val spec = DisplayLayoutSpec()

        assertEquals(196f, mainDisplayClockTextSizeForRefresh(TimerLifecycleState.IDLE, spec), 0f)
        assertEquals(32f, mainDisplayClockTextSizeForRefresh(TimerLifecycleState.COUNTDOWN, spec), 0f)
        assertTrue(
            mainDisplayClockTextSizeForRefresh(
                TimerLifecycleState.IDLE,
                spec,
                availableWidth = 755,
                availableHeight = 228,
        ) > 176f,
        )
    }

    @Test
    fun w27GreenCountdownSurfaceIsDedicatedLargerAndPresetIdentified() {
        val spec = DisplayLayoutSpec()
        val idle = MainDisplayGeometry.measure(1280, 720, spec)
        val active = ActiveCountdownSurfaceGeometry.measure(1280, 720, spec)
        val activePreset = TimerPresetPresentation(
            slot = TimerPresetSlot.SECOND,
            duration = TimerPresetDuration(hours = 0, minutes = 10, seconds = 0),
            isSelected = true,
            isActive = true,
        )
        val style = PresetPresentation.style(activePreset)

        assertEquals(idle.clockBounds, active.surfaceBounds)
        assertTrue(active.surfaceBounds.bottom <= idle.weatherCardBounds.first().top)
        assertTrue(active.surfaceBounds.left >= idle.weatherCardBounds[1].left)
        assertTrue(active.surfaceBounds.right <= idle.weatherCardBounds.last().right)
        assertTrue(active.surfaceBounds.left >= idle.cityBounds.right)
        assertTrue(active.surfaceBounds.left >= idle.dateBounds.right)
        assertTrue(active.backdropBounds.width == active.backdropBounds.height)
        assertTrue(active.backdropBounds.left >= active.surfaceBounds.left)
        assertTrue(active.backdropBounds.top >= active.surfaceBounds.top)
        assertTrue(active.backdropBounds.right <= active.surfaceBounds.right)
        assertTrue(active.backdropBounds.bottom <= active.surfaceBounds.bottom)
        assertTrue(active.countdownTextSize > idle.idleClockTextSize)
        assertEquals(PresetPresentation.colorHex(activePreset.slot), style.outlineHex)
        assertTrue(style.isSelected)
        assertTrue(style.isActive)
        println(
            "W27 GREEN surface=${active.surfaceBounds.snapshot()} backdrop=${active.backdropBounds.snapshot()} " +
                "idleTextSize=${idle.idleClockTextSize} countdownTextSize=${active.countdownTextSize} " +
                "preset=${activePreset.slot} color=${style.outlineHex} selected=${style.isSelected} active=${style.isActive}",
        )
    }

    @Test
    fun w28GreenOverdueSurfaceIsContentFreeAdaptiveAndHierarchyFirst() {
        val spec = DisplayLayoutSpec()
        val idle = MainDisplayGeometry.measure(1280, 720, spec)
        val active = ActiveCountdownSurfaceGeometry.measure(1280, 720, spec)
        val overdue = OverdueSurfaceGeometry.measure(1280, 720)
        val activePreset = TimerPresetPresentation(
            slot = TimerPresetSlot.SECOND,
            duration = TimerPresetDuration(hours = 0, minutes = 10, seconds = 0),
            isSelected = true,
            isActive = true,
        )
        val style = PresetPresentation.style(activePreset)

        assertEquals(1280, overdue.surfaceBounds.width)
        assertEquals(720, overdue.surfaceBounds.height)
        assertEquals(overdue.backdropBounds.width, overdue.backdropBounds.height)
        assertTrue(overdue.backdropBounds.left >= overdue.surfaceBounds.left)
        assertTrue(overdue.backdropBounds.right <= overdue.surfaceBounds.right)
        assertTrue(overdue.backdropBounds.top >= overdue.surfaceBounds.top)
        assertTrue(overdue.backdropBounds.bottom <= overdue.surfaceBounds.bottom)
        assertFalse(overdue.plusBounds.intersects(overdue.elapsedBounds))
        assertTrue(overdue.plusTextSize <= overdue.plusBounds.height - 24f)
        assertTrue(overdue.elapsedTextSize <= overdue.elapsedBounds.height - 24f)
        assertTrue(overdue.elapsedTextSize * 5f <= overdue.elapsedBounds.width)
        assertTrue(overdue.elapsedTextSize > idle.idleClockTextSize)
        assertTrue(overdue.elapsedTextSize > active.countdownTextSize)
        assertTrue(overdue.plusTextSize > active.countdownTextSize)
        assertEquals(PresetPresentation.colorHex(activePreset.slot), style.outlineHex)
        assertTrue(style.isActive)
        assertTrue(style.isSelected)
        println(
            "W28 GREEN surface=${overdue.surfaceBounds.snapshot()} backdrop=${overdue.backdropBounds.snapshot()} " +
                "plus=${overdue.plusBounds.snapshot()} elapsed=${overdue.elapsedBounds.snapshot()} " +
                "idleTextSize=${idle.idleClockTextSize} activeTextSize=${active.countdownTextSize} " +
                "elapsedTextSize=${overdue.elapsedTextSize} plusTextSize=${overdue.plusTextSize} " +
                "preset=${activePreset.slot} color=${style.outlineHex}",
        )
    }

    @Test
    fun w28GreenKeepsElapsedNumericStableAndPlusBlinkingSeparate() {
        assertEquals("00:10:00", DisplayFormatters.elapsedText(600_000L))
        assertEquals(DisplayFormatters.elapsedText(600_000L), DisplayFormatters.elapsedText(600_999L))
        assertEquals("00:10:01", DisplayFormatters.elapsedText(601_000L))
        assertTrue(OverduePresentation.plusVisibleAt(0L))
        assertFalse(OverduePresentation.plusVisibleAt(382L))
        assertTrue(OverduePresentation.plusVisibleAt(764L))
        println(
            "W28 GREEN elapsed=stable:00:10:00 plusVisibleAt=[0:${OverduePresentation.plusVisibleAt(0L)}," +
                "382:${OverduePresentation.plusVisibleAt(382L)},764:${OverduePresentation.plusVisibleAt(764L)}]",
        )
    }

    @Test
    fun w28ReadOnlyOverdueAnyTapStillDismissesThroughTimerContract() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        timer.start(100_000L, 1_000L)

        assertEquals(TimerLifecycleState.OVERDUE, timer.snapshotAt(101_000L).state)
        val dismissal = timer.handleGesture(101_000L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.IDLE, dismissal.snapshot.state)
        assertTrue(dismissal.dismissed)
    }

    @Test
    fun weatherIllustrationBoundsStaySeparateFromCardContentAtRowGeometry() {
        val cardGeometry = WeatherCardContentGeometry.measure(width = 223, height = 444)

        assertFalse(cardGeometry.illustrationBounds.intersects(cardGeometry.temperatureBounds))
        assertFalse(cardGeometry.illustrationBounds.intersects(cardGeometry.dateBounds))
        assertFalse(cardGeometry.illustrationBounds.intersects(cardGeometry.pressureBounds))
        assertTrue(cardGeometry.illustrationBounds.bottom <= cardGeometry.temperatureBounds.top)
        assertTrue(cardGeometry.illustrationBounds.bottom <= cardGeometry.pressureBounds.top)
        assertTrue(cardGeometry.illustrationBounds.bottom <= cardGeometry.dateBounds.top)
        assertTrue(cardGeometry.illustrationBounds.width > 0)
        assertTrue(cardGeometry.illustrationBounds.height > 0)
        assertTrue(cardGeometry.illustrationBounds.height < cardGeometry.temperatureBounds.height)
    }

    @Test
    fun weatherIllustrationsCoverSixNonTextStatesAndMoonFallback() {
        assertEquals(
            setOf(
                WeatherIllustration.CLEAR,
                WeatherIllustration.CLOUD,
                WeatherIllustration.NEUTRAL_CLOUD,
                WeatherIllustration.RAIN,
                WeatherIllustration.SNOW,
                WeatherIllustration.MOON,
            ),
            WeatherIllustration.entries.toSet(),
        )
        assertNull(WeatherIllustrationCanvas.moonPhaseFraction(null))
        assertNull(WeatherIllustrationCanvas.moonPhaseFraction("regular"))
        assertEquals(0.125f, WeatherIllustrationCanvas.moonPhaseFraction("waxing_crescent"))
        assertEquals(0.5f, WeatherIllustrationCanvas.moonPhaseFraction("0.50"))
        assertEquals(0.5f, WeatherIllustrationCanvas.moonPhaseFraction("full"))
        assertNull(WeatherIllustrationCanvas.moonPhaseFraction("unknown-phase"))
    }

    @Test
    fun w31ShrinksIllustrationBandAndKeepsClearDiskModeratelyLarger() {
        assertEquals(0.70f, WeatherIllustrationCanvas.PAINT_SCALE, 0f)
        assertTrue(WeatherIllustrationCanvas.PAINT_SCALE <= 0.90f)
        assertTrue(WeatherIllustrationCanvas.clearSunDiameterRatio() in 1.15f..1.30f)

        val rowGeometry = WeatherCardContentGeometry.measure(width = 223, height = 444)
        val todayGeometry = WeatherCardContentGeometry.measure(width = 279, height = 444)
        assertEquals(197, rowGeometry.illustrationBounds.width)
        assertEquals(93, rowGeometry.illustrationBounds.height)
        assertEquals(247, todayGeometry.illustrationBounds.width)
        assertEquals(90, todayGeometry.illustrationBounds.height)
        assertFalse(rowGeometry.illustrationBounds.intersects(rowGeometry.temperatureBounds))
        assertFalse(todayGeometry.illustrationBounds.intersects(todayGeometry.pressureBounds))
    }

    @Test
    fun w31AdaptiveGeometryExpandsClockAndKeepsFourCardsAndTimersSeparated() {
        listOf(2460 to 1080, 1280 to 720).forEach { (width, height) ->
            val geometry = MainDisplayGeometry.measure(width, height)
            val cards = geometry.weatherCardBounds
            assertEquals(4, cards.size)
            assertEquals(
                listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
                geometry.weatherSlots,
            )
            assertTrue(geometry.clockBounds.bottom <= cards.first().top)
            assertTrue(geometry.clockBounds.left >= cards[1].left)
            assertTrue(geometry.clockBounds.right <= cards.last().right)
            assertTrue(geometry.presetBounds.all { it.left >= cards.last().right })
            assertTrue(geometry.presetBounds.zipWithNext().all { (first, second) -> second.top > first.bottom })
            assertTrue(cards[1].width > cards[0].width)
            assertEquals(cards[0].width, cards[2].width)
            assertEquals(cards[0].width, cards[3].width)
            val clockMeasurement = MainClockGeometry.measure(
                availableWidth = geometry.clockBounds.width,
                availableHeight = geometry.clockBounds.height,
                textWidthAtUnitSize = 3f,
                textHeightAtUnitSize = 1.2f,
            )
            println(
                "W31 GREEN host size=${width}x${height} clock=${geometry.clockBounds.snapshot()} " +
                    "clockMeasurement=${clockMeasurement.measuredWidth}x${clockMeasurement.measuredHeight} " +
                    "cards=${cards.map { it.snapshot() }} illustrations=" +
                    "${cards.map { WeatherCardContentGeometry.measure(it.width, it.height).illustrationBounds.snapshot() }} " +
                    "presets=${geometry.presetBounds.map { it.snapshot() }}",
            )
        }
    }

    @Test
    fun w32CompositionContractFitsBandAndClockZoneAtBothHostSizes() {
        val bandRatios = mutableListOf<Float>()
        listOf(2460 to 1080, 1280 to 720).forEach { (width, height) ->
            val geometry = MainDisplayGeometry.measure(width, height)
            val bandTop = geometry.weatherCardBounds.minOf { it.top }
            val bandBottom = geometry.weatherCardBounds.maxOf { it.bottom }
            val bandRatio = (bandBottom - bandTop).toFloat() / height
            val clockZoneRatio = 1f - bandRatio
            bandRatios += bandRatio
            println(
                "W32 GREEN host size=${width}x${height} bandBounds=${bandTop}..${bandBottom} " +
                    "bandRatio=$bandRatio clockZoneRatio=$clockZoneRatio " +
                    "clock=${geometry.clockBounds.snapshot()} cards=${geometry.weatherCardBounds.map { it.snapshot() }} " +
                    "presets=${geometry.presetBounds.map { it.snapshot() }}",
            )
        }
        assertTrue(
            "W32 GREEN: band ratio must stay inside the accepted macro contract",
            bandRatios.all { it in 0.25f..0.30f },
        )
    }

    @Test
    fun w32GreenMacroGeometryKeepsEqualCardsCompleteClockAndSeparateRail() {
        listOf(2460 to 1080, 1280 to 720).forEach { (width, height) ->
            val geometry = MainDisplayGeometry.measure(width, height)
            val cards = geometry.weatherCardBounds
            val bandTop = cards.minOf { it.top }
            val bandBottom = cards.maxOf { it.bottom }
            val bandRatio = (bandBottom - bandTop).toFloat() / height
            val clockZoneRatio = 1f - bandRatio
            val rasterTolerance = maxOf(2, (height * 0.005f).toInt())
            val cardHeights = cards.map { it.height }
            val cardBottoms = cards.map { it.bottom }
            val clockMeasurement = MainClockGeometry.measure(
                availableWidth = geometry.clockBounds.width,
                availableHeight = geometry.clockBounds.height,
                textWidthAtUnitSize = 3f,
                textHeightAtUnitSize = 1.2f,
            )

            assertTrue(bandRatio in 0.25f..0.30f)
            assertTrue(clockZoneRatio in 0.70f..0.75f)
            assertEquals(listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER), geometry.weatherSlots)
            assertEquals(1, cardHeights.distinct().size)
            assertTrue(cardBottoms.maxOrNull()!! - cardBottoms.minOrNull()!! <= rasterTolerance)
            assertTrue(geometry.cityBounds.bottom <= cards.first().top)
            assertTrue(geometry.dateBounds.bottom <= cards.first().top)
            assertTrue(geometry.clockBounds.bottom <= cards.first().top)
            assertTrue(geometry.clockBounds.left >= cards[1].left)
            assertTrue(geometry.clockBounds.right <= cards.last().right)
            assertTrue(clockMeasurement.measuredWidth <= clockMeasurement.availableWidth)
            assertTrue(clockMeasurement.measuredHeight <= clockMeasurement.availableHeight)
            assertTrue(clockMeasurement.textSizePx > 0f)
            assertTrue(geometry.presetBounds.all { it.width == it.height })
            assertTrue(geometry.presetBounds.zipWithNext().all { (first, second) -> second.top > first.bottom })
            assertTrue(geometry.presetBounds.all { it.left >= cards.last().right })

            println(
                "W32 GREEN host size=${width}x${height} bandBounds=${bandTop}..${bandBottom} " +
                    "bandRatio=$bandRatio clockZoneRatio=$clockZoneRatio rasterTolerance=$rasterTolerance " +
                    "clock=${geometry.clockBounds.snapshot()} clockMeasurement=${clockMeasurement.measuredWidth}x${clockMeasurement.measuredHeight} " +
                    "cards=${cards.map { it.snapshot() }} cardHeights=$cardHeights cardBottoms=$cardBottoms " +
                    "city=${geometry.cityBounds.snapshot()} date=${geometry.dateBounds.snapshot()} " +
                    "presets=${geometry.presetBounds.map { it.snapshot() }}",
            )
        }
    }

    @Test
    fun w32GreenStateMatrixPreservesFourOrderedShellSlots() {
        val noData = weatherProjection()
        val partial = noData.copy(cards = listOf(noData.cards.first()))
        val populated = noData.copy(
            freshness = WeatherFreshness.FRESH,
            cards = noData.cards.map { card ->
                card.copy(
                    temperatureCelsius = 21,
                    temperatureText = "21 °C",
                    backgroundHex = "#FFE0A3",
                    illustration = WeatherIllustration.CLOUD,
                    pressureArrowCount = 1,
                    pressureDirection = PressureDirection.UP,
                )
            },
        )
        listOf("NO_DATA" to noData, "PARTIAL_ASYNC" to partial, "POPULATED_REDACTED" to populated).forEach { (state, projection) ->
            val slots = orderedDisplayWeatherSlots(projection)
            assertEquals(WeatherCardSlot.entries.toList(), slots.map { it.slot })
            assertEquals(4, slots.size)
            assertEquals(4, MainDisplayGeometry.measure(1280, 720).weatherCardBounds.size)
            println("W32 GREEN state=$state slots=${slots.map { it.slot to (it.projection != null) }}")
        }
    }

    @Test
    fun w34MixedStateUsesOneSharedBandForEmptyYesterdayAndPopulatedCards() {
        val mixed = weatherProjection().copy(
            freshness = WeatherFreshness.FRESH,
            cards = WeatherCardSlot.entries.mapIndexed { index, slot ->
                val base = weatherProjection().cards[index]
                if (slot == WeatherCardSlot.YESTERDAY) {
                    base.copy(date = LocalDate.of(2024, 8, 13))
                } else {
                    base.copy(
                        date = LocalDate.of(2024, 8, 13 + index),
                        temperatureCelsius = 23 + (index - 1),
                        temperatureText = "${23 + (index - 1)}°",
                        backgroundHex = "#E49E04",
                        illustration = WeatherIllustration.CLOUD,
                        pressureArrowCount = 1,
                        pressureDirection = PressureDirection.UP,
                    )
                }
            },
        )

        val slots = orderedDisplayWeatherSlots(mixed)
        assertEquals(
            listOf(WeatherCardSlot.YESTERDAY, WeatherCardSlot.TODAY, WeatherCardSlot.TOMORROW, WeatherCardSlot.DAY_AFTER),
            slots.map { it.slot },
        )
        assertNull(slots.first().projection?.temperatureText)
        assertEquals(listOf("23°", "24°", "25°"), slots.drop(1).map { it.projection?.temperatureText })

        listOf(2460 to 1080, 1280 to 720).forEach { (width, height) ->
            val geometry = MainDisplayGeometry.measure(width, height)
            val cards = geometry.weatherCardBounds
            val bandTop = cards.minOf { it.top }
            val bandBottom = cards.maxOf { it.bottom }
            val bandRatio = (bandBottom - bandTop).toFloat() / height
            val clockZoneRatio = 1f - bandRatio
            val rasterTolerance = maxOf(2, (height * 0.005f).toInt())

            assertTrue(bandRatio in 0.25f..0.30f)
            assertTrue(clockZoneRatio in 0.70f..0.75f)
            assertEquals(1, cards.map { it.height }.distinct().size)
            assertTrue(cards.maxOf { it.bottom } - cards.minOf { it.bottom } <= rasterTolerance)
            assertEquals(cards.first().height, cards.drop(1).first().height)

            println(
                "W34 GREEN mixed fixture size=${width}x${height} " +
                    "slots=${slots.map { it.slot to (it.projection?.temperatureText != null) }} " +
                    "band=${bandTop}..${bandBottom} bandRatio=$bandRatio clockZoneRatio=$clockZoneRatio " +
                    "cardBounds=${cards.map { it.snapshot() }} cardHeights=${cards.map { it.height }} " +
                    "cardBottoms=${cards.map { it.bottom }} rasterTolerance=$rasterTolerance",
            )
        }
    }

    @Test
    fun w32GreenVisualRubricKeepsIllustrationsSecondaryAndPresetRailRadial() {
        val geometry = MainDisplayGeometry.measure(1280, 720)
        val cardContent = geometry.weatherCardBounds.map { WeatherCardContentGeometry.measure(it.width, it.height) }
        assertTrue(cardContent.all { content ->
            !content.illustrationBounds.intersects(content.temperatureBounds) &&
                content.illustrationBounds.bottom <= content.temperatureBounds.top &&
                content.illustrationBounds.height < content.temperatureBounds.height
        })
        assertTrue(geometry.presetBounds.all { it.width == it.height })
        assertTrue(geometry.presetBounds.zipWithNext().all { (first, second) -> second.top - first.bottom > 0 })
        assertEquals(listOf("#FF7A00", "#FF4FA3", "#A855F7"), TimerPresetSlot.entries.map(PresetPresentation::colorHex))
        assertEquals(3, PresetVisualGeometry.glowLayerCount())
        TimerPresetSlot.entries.zip(listOf(0xFFFF7A00.toInt(), 0xFFFF4FA3.toInt(), 0xFFA855F7.toInt())).forEach { (_, color) ->
            assertEquals(3, PresetVisualGeometry.radialShadeColors(color).size)
        }
        println(
            "W32 GREEN rubric cards=${geometry.weatherCardBounds.map { it.snapshot() }} " +
                "illustrations=${cardContent.map { it.illustrationBounds.snapshot() }} " +
                "temperatures=${cardContent.map { it.temperatureBounds.snapshot() }} " +
                "presets=${geometry.presetBounds.map { it.snapshot() }} " +
                "gaps=${geometry.presetBounds.zipWithNext().map { (first, second) -> second.top - first.bottom }}",
        )
    }

    @Test
    fun w25PressureArrowsUseMeasuredVisiblePathContractAndZeroSuppressesViews() {
        assertTrue(PressureArrowCanvas.STROKE_WIDTH_PX in 4f..8f)
        assertEquals(2, PressureArrowCanvas.PATH_SEGMENT_COUNT)
        assertEquals(0, PressureArrowCanvas.visibleCount(0))
        assertEquals(1, PressureArrowCanvas.visibleCount(1))
        assertEquals(2, PressureArrowCanvas.visibleCount(2))
        assertEquals(0, PressureArrowCanvas.visibleCount(-1))
        assertEquals(2, PressureArrowCanvas.visibleCount(4))
        assertEquals(PressureDirection.UP, PressureArrowCanvas.effectiveDirection(PressureDirection.UP))
        assertEquals(PressureDirection.DOWN, PressureArrowCanvas.effectiveDirection(PressureDirection.DOWN))
        assertEquals(PressureDirection.DOWN, PressureArrowCanvas.effectiveDirection(null))
    }

    @Test
    fun emptyWeatherProjectionKeepsNoIllustrationsAndOrderedSlots() {
        val projection = weatherProjection()

        assertEquals(WeatherFreshness.NO_DATA, projection.freshness)
        assertEquals(
            listOf(
                WeatherCardSlot.YESTERDAY,
                WeatherCardSlot.TODAY,
                WeatherCardSlot.TOMORROW,
                WeatherCardSlot.DAY_AFTER,
            ),
            projection.cards.map { it.slot },
        )
        assertTrue(projection.cards.all { it.illustration == null })
        assertTrue(projection.cards.none { it.temperatureText != null })
        assertTrue(projection.cards.none { it.pressureArrowCount != 0 })
    }

    @Test
    fun colonModesUseAcceptedConnectivityAndCountdownValues() {
        assertEquals(ColonMode.OFFLINE_FIXED, ColonProjection.mode(DisplayConnectivity.OFFLINE, TimerLifecycleState.IDLE))
        assertEquals(0.38f, ColonProjection.brightness(ColonMode.OFFLINE_FIXED, 4_000L), 0.001f)
        assertEquals(ColonMode.COUNTDOWN_BLINK, ColonProjection.mode(DisplayConnectivity.ONLINE, TimerLifecycleState.COUNTDOWN))
        assertEquals(1f, ColonProjection.brightness(ColonMode.COUNTDOWN_BLINK, 381L), 0.001f)
        assertEquals(0f, ColonProjection.brightness(ColonMode.COUNTDOWN_BLINK, 382L), 0.001f)
        assertEquals(1f, ColonProjection.brightness(ColonMode.ONLINE_PULSE, 3_000L), 0.001f)
        assertEquals(0.02f, ColonProjection.brightness(ColonMode.ONLINE_PULSE, 5_999L), 0.002f)
    }

    @Test
    fun cityGesturesRespectEmptyAndSelectedCityRules() {
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(false, CityGesture.SHORT_TAP))
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(false, CityGesture.LONG_HOLD))
        assertEquals(CityInteraction.NO_OP, CityInteractionRouter.route(true, CityGesture.SHORT_TAP))
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(true, CityGesture.LONG_HOLD))
        assertTrue(CityInteractionRouter.route(true, CityGesture.LONG_HOLD) != CityInteraction.NO_OP)
    }

    @Test
    fun activeCountdownKeepsCityHoldAlongsideProtectedTimerTaps() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        timer.start(100_000L, 60_000L)

        val singleTap = timer.handleGesture(101_000L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, singleTap.snapshot.state)
        assertTrue(singleTap.singleTapHintVisible)
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(true, CityGesture.LONG_HOLD))
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(101_500L).state)

        val doubleTap = timer.handleGesture(102_000L, TimerGesture.DOUBLE_TAP)
        assertEquals(TimerLifecycleState.IDLE, doubleTap.snapshot.state)
        assertTrue(doubleTap.dismissed)
    }

    @Test
    fun activeCountdownDispatcherKeepsEveryCapturedSurfaceStreamToTerminalEvent() {
        val timer = TimerCapability(InMemoryTimerStateStore())
        val dispatcher = ActiveCountdownTouchDispatcher()
        timer.start(100_000L, 60_000L)
        var pendingLongPressAt: Long? = null
        val delivered = mutableListOf<String>()

        fun dispatch(surface: String, action: Int, atMillis: Long): Boolean {
            val timerActiveAtDown = action == MotionEvent.ACTION_DOWN &&
                timer.snapshotAt(atMillis).state == TimerLifecycleState.COUNTDOWN
            val dispatched = dispatcher.shouldDispatch(action, timerActiveAtDown)
            if (dispatched) {
                delivered += "$surface:$action"
                when (action) {
                    MotionEvent.ACTION_DOWN -> if (surface == "city") {
                        pendingLongPressAt = atMillis + 600L
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> pendingLongPressAt = null
                }
            }
            return dispatched
        }

        assertTrue(dispatch("weather", MotionEvent.ACTION_DOWN, 101_000L))
        assertTrue(dispatch("weather", MotionEvent.ACTION_UP, 101_050L))
        val singleTap = timer.handleGesture(101_050L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, singleTap.snapshot.state)
        assertTrue(singleTap.singleTapHintVisible)

        assertTrue(dispatch("weather", MotionEvent.ACTION_DOWN, 101_150L))
        assertEquals(
            TimerLifecycleState.IDLE,
            timer.handleGesture(101_150L, TimerGesture.DOUBLE_TAP).snapshot.state,
        )
        assertTrue(dispatch("weather", MotionEvent.ACTION_UP, 101_200L))

        timer.start(102_000L, 60_000L)
        assertTrue(dispatch("city", MotionEvent.ACTION_DOWN, 102_000L))
        assertEquals(CityInteraction.OPEN_SETTINGS, CityInteractionRouter.route(true, CityGesture.LONG_HOLD))
        assertEquals(TimerLifecycleState.COUNTDOWN, timer.snapshotAt(102_700L).state)
        assertTrue(dispatch("city", MotionEvent.ACTION_UP, 102_800L))

        timer.start(103_000L, 60_000L)
        assertTrue(dispatch("city", MotionEvent.ACTION_DOWN, 103_000L))
        assertEquals(
            TimerLifecycleState.IDLE,
            timer.handleGesture(103_000L, TimerGesture.DOUBLE_TAP).snapshot.state,
        )
        assertTrue(dispatch("city", MotionEvent.ACTION_UP, 103_050L))

        timer.start(104_000L, 60_000L)
        assertTrue(dispatch("preset", MotionEvent.ACTION_DOWN, 104_000L))
        val presetSingleTap = timer.handleGesture(104_050L, TimerGesture.SINGLE_TAP)
        assertEquals(TimerLifecycleState.COUNTDOWN, presetSingleTap.snapshot.state)
        assertTrue(presetSingleTap.singleTapHintVisible)
        assertTrue(dispatch("preset", MotionEvent.ACTION_UP, 104_100L))

        val beyondLongPressTimeout = 103_700L
        assertTrue(pendingLongPressAt == null || pendingLongPressAt!! > beyondLongPressTimeout)
        assertTrue(delivered.contains("weather:${MotionEvent.ACTION_UP}"))
        assertTrue(delivered.contains("city:${MotionEvent.ACTION_UP}"))
        assertTrue(delivered.contains("preset:${MotionEvent.ACTION_UP}"))
        assertTrue(!dispatcher.shouldDispatch(MotionEvent.ACTION_MOVE, timerActive = false))
    }

    @Test
    fun mainDisplayTickerCoalescesLifecycleStartsAndStopsWhilePausedOrDetached() {
        val scheduler = FakeMainDisplayTickerScheduler()
        var callbacks = 0
        val ticker = MainDisplayTickerOwner(scheduler, onTick = { callbacks++ })

        ticker.onActivityResumed()
        ticker.onViewAttachedToWindow()
        ticker.onViewAttachedToWindow()
        ticker.onActivityResumed()
        assertEquals(1, scheduler.pendingCount())

        scheduler.runNext()
        assertEquals(1, callbacks)
        assertEquals(1, scheduler.pendingCount())
        assertEquals(listOf(50L), scheduler.pendingDelays())

        ticker.onActivityPaused()
        ticker.onActivityPaused()
        assertEquals(0, scheduler.pendingCount())
        scheduler.runAll()
        assertEquals(1, callbacks)

        ticker.onViewDetachedFromWindow()
        ticker.onViewAttachedToWindow()
        assertEquals(0, scheduler.pendingCount())

        ticker.onActivityResumed()
        ticker.onActivityResumed()
        assertEquals(1, scheduler.pendingCount())
        scheduler.runNext()
        assertEquals(2, callbacks)
        assertEquals(1, scheduler.pendingCount())

        ticker.onViewDetachedFromWindow()
        assertEquals(0, scheduler.pendingCount())
        scheduler.runAll()
        assertEquals(2, callbacks)
        scheduler.reset()
    }

    @Test
    fun unchangedWeatherProjectionKeepsFourCardTreeAndChangedInputRebindsOnce() {
        val renderer = MainDisplayWeatherCardRenderer()
        var cardTree = List(4) { Any() }
        var rebinds = 0
        val initial = MainDisplayWeatherRenderInput(weatherProjection(), 0.45f)

        assertTrue(renderer.renderIfChanged(initial) {
            rebinds++
            cardTree = List(4) { Any() }
        })
        val initialTree = cardTree
        assertEquals(4, initialTree.size)
        assertEquals(1, rebinds)

        assertFalse(renderer.renderIfChanged(initial) {
            rebinds++
            cardTree = List(4) { Any() }
        })
        assertSame(initialTree, cardTree)
        assertEquals(1, rebinds)

        val changed = initial.copy(projection = weatherProjection(cityLabel = "changed"))
        assertTrue(renderer.renderIfChanged(changed) {
            rebinds++
            cardTree = List(4) { Any() }
        })
        assertEquals(4, cardTree.size)
        assertTrue(initialTree !== cardTree)
        assertEquals(2, rebinds)

        assertFalse(renderer.renderIfChanged(changed) { rebinds++ })
        assertEquals(2, rebinds)
    }

    @Test
    fun tomorrowAndDayAfterUseTheSameLongTermForecastIntent() {
        assertEquals(ForecastEntryIntent.HOURLY, forecastEntryIntent(WeatherCardSlot.TODAY))
        assertEquals(ForecastEntryIntent.LONG_TERM, forecastEntryIntent(WeatherCardSlot.TOMORROW))
        assertEquals(ForecastEntryIntent.LONG_TERM, forecastEntryIntent(WeatherCardSlot.DAY_AFTER))
    }

    private fun weatherProjection(cityLabel: String? = "Khujand"): WeatherProjection =
        WeatherProjection(
            cityLabel = cityLabel,
            apiTimeZone = "Asia/Dushanbe",
            freshness = WeatherFreshness.NO_DATA,
            cards = WeatherCardSlot.entries.mapIndexed { index, slot ->
                WeatherCardProjection(
                    slot = slot,
                    date = LocalDate.of(2024, 1, index + 1),
                    temperatureCelsius = null,
                    temperatureText = null,
                    backgroundHex = null,
                    illustration = null,
                    moonPhase = null,
                    pressureArrowCount = 0,
                    pressureDirection = null,
                    isTodaySize = slot == WeatherCardSlot.TODAY,
                )
            },
        )

    private data class DisplayBoundsSnapshot(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
    )

    private fun com.hozayushka.app.display.DisplayBounds.snapshot(): DisplayBoundsSnapshot =
        DisplayBoundsSnapshot(left, top, right, bottom)

    private class FakeMainDisplayTickerScheduler : MainDisplayTickerScheduler {
        private data class Pending(val runnable: Runnable, val delayMillis: Long)

        private val pending = ArrayDeque<Pending>()

        override fun post(runnable: Runnable) {
            pending.addLast(Pending(runnable, 0L))
        }

        override fun postDelayed(runnable: Runnable, delayMillis: Long) {
            pending.addLast(Pending(runnable, delayMillis))
        }

        override fun removeCallbacks(runnable: Runnable) {
            pending.removeAll { it.runnable === runnable }
        }

        fun pendingCount(): Int = pending.size

        fun pendingDelays(): List<Long> = pending.map { it.delayMillis }

        fun runNext() {
            pending.removeFirst().runnable.run()
        }

        fun runAll() {
            while (pending.isNotEmpty()) runNext()
        }

        fun reset() {
            pending.clear()
        }
    }
}
