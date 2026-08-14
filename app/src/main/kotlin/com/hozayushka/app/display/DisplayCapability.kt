package com.hozayushka.app.display

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.hozayushka.app.R
import com.hozayushka.app.adapters.platform.PlatformRuntime
import com.hozayushka.app.forecast.ForecastSessionCapability
import com.hozayushka.app.forecast.ForecastEntryIntent
import com.hozayushka.app.forecast.ForecastSessionSnapshot
import com.hozayushka.app.forecast.ForecastSessionState
import com.hozayushka.app.settings.LocationContext
import com.hozayushka.app.settings.SettingsCapability
import com.hozayushka.app.settings.TimerPresetDuration
import com.hozayushka.app.settings.TimerPresetSlot
import com.hozayushka.app.timer.TimerCapability
import com.hozayushka.app.timer.TimerGesture
import com.hozayushka.app.timer.TimerLifecycleState
import com.hozayushka.app.timer.TimerPresetPresentation
import com.hozayushka.app.weather.WeatherCapability
import com.hozayushka.app.weather.WeatherCardPresentation
import com.hozayushka.app.weather.WeatherCardSlot
import com.hozayushka.app.weather.WeatherCardProjection
import com.hozayushka.app.weather.WeatherIllustration
import com.hozayushka.app.weather.PressureDirection
import com.hozayushka.app.weather.TemperaturePalette
import com.hozayushka.app.weather.WeatherProjection
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class DisplayConnectivity {
    ONLINE,
    OFFLINE,
}

enum class ColonMode {
    ONLINE_PULSE,
    OFFLINE_FIXED,
    COUNTDOWN_BLINK,
}

enum class CityGesture {
    SHORT_TAP,
    LONG_HOLD,
}

enum class CityInteraction {
    OPEN_SETTINGS,
    NO_OP,
}

data class DisplayLayoutSpec(
    val weatherCardCount: Int = 4,
    val presetCount: Int = 3,
    val headerWeight: Float = 0f,
    val weatherRowWeight: Float = 1f,
    val outerHorizontalPaddingDp: Int = 32,
    val outerVerticalPaddingDp: Int = 24,
    val presetColumnWidthDp: Int = 220,
    val presetGapDp: Int = outerVerticalPaddingDp,
    val interCardGapDp: Int = outerVerticalPaddingDp,
    val smallerCardWeight: Float = 1f,
    val todayCardWeight: Float = 1.25f,
    val idleClockTextSize: Float = presetColumnWidthDp - outerVerticalPaddingDp.toFloat(),
)

internal fun mainDisplayClockTextSizeForRefresh(
    timerState: TimerLifecycleState,
    layoutSpec: DisplayLayoutSpec,
    availableWidth: Int = 0,
    availableHeight: Int = 0,
): Float = if (timerState == TimerLifecycleState.COUNTDOWN) {
    32f
} else if (availableWidth > 0 && availableHeight > 0) {
    adaptiveIdleClockTextSize(availableWidth, availableHeight, layoutSpec)
} else {
    layoutSpec.idleClockTextSize
}

internal fun adaptiveIdleClockTextSize(
    availableWidth: Int,
    availableHeight: Int,
    layoutSpec: DisplayLayoutSpec,
): Float {
    val safeWidth = availableWidth.coerceAtLeast(1)
    val safeHeight = availableHeight.coerceAtLeast(1)
    val widthBound = safeWidth.toFloat() / layoutSpec.weatherCardCount.coerceAtLeast(1)
    val heightBound = (safeHeight - layoutSpec.outerVerticalPaddingDp).coerceAtLeast(1).toFloat()
    return minOf(widthBound, heightBound)
}

internal data class MainClockTextMeasurement(
    val availableWidth: Int,
    val availableHeight: Int,
    val textSizePx: Float,
    val measuredWidth: Float,
    val measuredHeight: Float,
)

internal object MainClockGeometry {
    fun fitTextSizePx(
        availableWidth: Int,
        availableHeight: Int,
        textWidthAtUnitSize: Float,
        textHeightAtUnitSize: Float,
    ): Float {
        val safeWidth = availableWidth.coerceAtLeast(1).toFloat()
        val safeHeight = availableHeight.coerceAtLeast(1).toFloat()
        val safeTextWidth = textWidthAtUnitSize.coerceAtLeast(1f)
        val safeTextHeight = textHeightAtUnitSize.coerceAtLeast(1f)
        return minOf(safeWidth / safeTextWidth, safeHeight / safeTextHeight)
            .coerceAtLeast(1f)
    }

    fun measure(
        availableWidth: Int,
        availableHeight: Int,
        textWidthAtUnitSize: Float,
        textHeightAtUnitSize: Float,
    ): MainClockTextMeasurement {
        val textSizePx = fitTextSizePx(
            availableWidth = availableWidth,
            availableHeight = availableHeight,
            textWidthAtUnitSize = textWidthAtUnitSize,
            textHeightAtUnitSize = textHeightAtUnitSize,
        )
        return MainClockTextMeasurement(
            availableWidth = availableWidth.coerceAtLeast(1),
            availableHeight = availableHeight.coerceAtLeast(1),
            textSizePx = textSizePx,
            measuredWidth = textWidthAtUnitSize.coerceAtLeast(1f) * textSizePx,
            measuredHeight = textHeightAtUnitSize.coerceAtLeast(1f) * textSizePx,
        )
    }
}

internal data class DisplayBounds(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top

    fun intersects(other: DisplayBounds): Boolean =
        left < other.right && other.left < right && top < other.bottom && other.top < bottom
}

internal data class WeatherCardMeasuredGeometry(
    val illustrationBounds: DisplayBounds,
    val temperatureBounds: DisplayBounds,
    val dateBounds: DisplayBounds,
    val pressureBounds: DisplayBounds,
)

internal object WeatherCardContentGeometry {
    fun measure(width: Int, height: Int): WeatherCardMeasuredGeometry {
        val safeWidth = width.coerceAtLeast(1)
        val safeHeight = height.coerceAtLeast(1)
        val inset = (minOf(safeWidth, safeHeight) * 0.06f).toInt().coerceAtLeast(8)
        // Keep the illustration in a compact upper band so card content stays
        // primary even when the card is rendered on a high-density landscape
        // display. The band follows card height; it is not a fixed pixel size.
        val illustrationBottom = (safeHeight * 0.24f).toInt().coerceAtLeast(inset + 1)
        val contentTop = (safeHeight * 0.40f).toInt().coerceAtMost(safeHeight - 2)
        val contentBottom = (safeHeight * 0.74f).toInt().coerceAtLeast(contentTop + 1)
        val dateTop = (safeHeight * 0.82f).toInt().coerceAtMost(safeHeight - inset)
        val contentLeft = (safeWidth * 0.10f).toInt().coerceAtLeast(inset)
        val contentRight = (safeWidth * 0.90f).toInt().coerceAtLeast(contentLeft + 1)
        val pressureLeft = (safeWidth * 0.56f).toInt().coerceAtMost(contentRight - 1)
        return WeatherCardMeasuredGeometry(
            illustrationBounds = DisplayBounds(
                left = inset,
                top = inset,
                right = safeWidth - inset,
                bottom = illustrationBottom,
            ),
            temperatureBounds = DisplayBounds(
                left = contentLeft,
                top = contentTop,
                right = contentRight,
                bottom = contentBottom,
            ),
            dateBounds = DisplayBounds(
                left = inset,
                top = dateTop,
                right = (safeWidth * 0.45f).toInt().coerceAtLeast(inset + 1),
                bottom = safeHeight - inset,
            ),
            pressureBounds = DisplayBounds(
                left = pressureLeft,
                top = contentTop,
                right = safeWidth - inset,
                bottom = contentBottom,
            ),
        )
    }
}

internal object WeatherIllustrationCanvas {
    private const val REGULAR_MOON = "regular"
    private const val PI = 3.1415927f

    const val PAINT_SCALE = 0.70f
    const val CLEAR_SUN_RADIUS_FACTOR = 0.32f
    const val BASELINE_CLEAR_SUN_RADIUS_FACTOR = 0.19f

    fun moonPhaseFraction(value: String?): Float? {
        val phase = value?.trim()?.lowercase().orEmpty()
        return when (phase) {
            "", REGULAR_MOON -> null
            "new" -> 0f
            "waxing_crescent" -> 0.125f
            "first_quarter", "half" -> 0.25f
            "waxing_gibbous" -> 0.375f
            "full" -> 0.5f
            "waning_gibbous" -> 0.625f
            "last_quarter" -> 0.75f
            "waning_crescent" -> 0.875f
            else -> phase.toFloatOrNull()?.coerceIn(0f, 1f)
        }
    }

    fun draw(
        canvas: Canvas,
        illustration: WeatherIllustration,
        moonPhase: String?,
        width: Int,
        height: Int,
    ) {
        val fullBounds = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val bounds = scaledBounds(fullBounds, PAINT_SCALE)
        when (illustration) {
            WeatherIllustration.CLEAR -> drawSun(canvas, bounds)
            WeatherIllustration.CLOUD -> drawCloud(canvas, bounds, Color.rgb(237, 247, 252))
            WeatherIllustration.NEUTRAL_CLOUD -> drawCloud(canvas, bounds, Color.rgb(202, 222, 234))
            WeatherIllustration.RAIN -> {
                drawCloud(canvas, bounds, Color.rgb(222, 239, 249))
                drawRain(canvas, bounds)
            }
            WeatherIllustration.SNOW -> {
                drawCloud(canvas, bounds, Color.rgb(241, 247, 252))
                drawSnow(canvas, bounds)
            }
            WeatherIllustration.MOON -> drawMoon(canvas, bounds, moonPhase)
        }
    }

    fun clearSunDiameterRatio(): Float =
        (PAINT_SCALE * CLEAR_SUN_RADIUS_FACTOR) / BASELINE_CLEAR_SUN_RADIUS_FACTOR

    private fun scaledBounds(bounds: RectF, scale: Float): RectF {
        val centerX = bounds.centerX()
        val centerY = bounds.centerY()
        val halfWidth = bounds.width() * scale / 2f
        val halfHeight = bounds.height() * scale / 2f
        return RectF(
            centerX - halfWidth,
            centerY - halfHeight,
            centerX + halfWidth,
            centerY + halfHeight,
        )
    }

    private fun drawSun(canvas: Canvas, bounds: RectF) {
        val centerX = bounds.centerX()
        val centerY = bounds.centerY() * 0.92f
        val radius = minOf(bounds.width(), bounds.height()) * CLEAR_SUN_RADIUS_FACTOR
        val rayStart = radius * 1.18f
        val rayEnd = radius * 1.45f
        val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 236, 145)
            style = Paint.Style.STROKE
            strokeWidth = maxOf(3f, radius * 0.12f)
            strokeCap = Paint.Cap.ROUND
        }
        for (index in 0 until 8) {
            val angle = (index * PI / 4f) - PI / 2f
            val startX = centerX + (kotlin.math.cos(angle.toDouble()) * rayStart).toFloat()
            val startY = centerY + (kotlin.math.sin(angle.toDouble()) * rayStart).toFloat()
            val endX = centerX + (kotlin.math.cos(angle.toDouble()) * rayEnd).toFloat()
            val endY = centerY + (kotlin.math.sin(angle.toDouble()) * rayEnd).toFloat()
            canvas.drawLine(startX, startY, endX, endY, rayPaint)
        }
        val sunPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 213, 84)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius, sunPaint)
        val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 246, 194)
            style = Paint.Style.STROKE
            strokeWidth = maxOf(2f, radius * 0.08f)
        }
        canvas.drawCircle(centerX, centerY, radius, outlinePaint)
    }

    private fun drawCloud(canvas: Canvas, bounds: RectF, color: Int) {
        val left = bounds.left + bounds.width() * 0.12f
        val right = bounds.right - bounds.width() * 0.12f
        val base = bounds.top + bounds.height() * 0.70f
        val top = bounds.top + bounds.height() * 0.25f
        val path = Path().apply {
            moveTo(left, base)
            lineTo(left, base - bounds.height() * 0.10f)
            cubicTo(
                left,
                base - bounds.height() * 0.25f,
                left + bounds.width() * 0.18f,
                base - bounds.height() * 0.34f,
                left + bounds.width() * 0.31f,
                base - bounds.height() * 0.26f,
            )
            cubicTo(
                left + bounds.width() * 0.35f,
                top,
                left + bounds.width() * 0.58f,
                top - bounds.height() * 0.02f,
                left + bounds.width() * 0.63f,
                base - bounds.height() * 0.24f,
            )
            cubicTo(
                left + bounds.width() * 0.78f,
                base - bounds.height() * 0.30f,
                right,
                base - bounds.height() * 0.20f,
                right,
                base - bounds.height() * 0.04f,
            )
            lineTo(right, base)
            close()
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
        }
        canvas.drawPath(path, fillPaint)
        val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.argb(210, 247, 252, 255)
            style = Paint.Style.STROKE
            strokeWidth = maxOf(2f, bounds.width() * 0.018f)
            strokeJoin = Paint.Join.ROUND
        }
        canvas.drawPath(path, outlinePaint)
    }

    private fun drawRain(canvas: Canvas, bounds: RectF) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(126, 211, 246)
            style = Paint.Style.STROKE
            strokeWidth = maxOf(3f, bounds.width() * 0.022f)
            strokeCap = Paint.Cap.ROUND
        }
        val y = bounds.top + bounds.height() * 0.79f
        val bottom = bounds.top + bounds.height() * 0.96f
        val x1 = bounds.left + bounds.width() * 0.30f
        val x2 = bounds.centerX()
        val x3 = bounds.left + bounds.width() * 0.70f
        canvas.drawLine(x1, y, x1 - bounds.width() * 0.035f, bottom, paint)
        canvas.drawLine(x2, y, x2 - bounds.width() * 0.035f, bottom, paint)
        canvas.drawLine(x3, y, x3 - bounds.width() * 0.035f, bottom, paint)
    }

    private fun drawSnow(canvas: Canvas, bounds: RectF) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = maxOf(2f, bounds.width() * 0.018f)
            strokeCap = Paint.Cap.ROUND
        }
        val y = bounds.top + bounds.height() * 0.84f
        val radius = bounds.width() * 0.055f
        listOf(
            bounds.left + bounds.width() * 0.32f,
            bounds.centerX(),
            bounds.left + bounds.width() * 0.68f,
        ).forEach { x ->
            canvas.drawLine(x - radius, y, x + radius, y, paint)
            canvas.drawLine(x, y - radius, x, y + radius, paint)
            canvas.drawLine(x - radius * 0.72f, y - radius * 0.72f, x + radius * 0.72f, y + radius * 0.72f, paint)
            canvas.drawLine(x - radius * 0.72f, y + radius * 0.72f, x + radius * 0.72f, y - radius * 0.72f, paint)
        }
    }

    private fun drawMoon(canvas: Canvas, bounds: RectF, moonPhase: String?) {
        val centerX = bounds.centerX()
        val centerY = bounds.centerY() * 0.94f
        val radius = minOf(bounds.width(), bounds.height()) * 0.24f
        val moonPath = Path().apply { addCircle(centerX, centerY, radius, Path.Direction.CW) }
        val fraction = moonPhaseFraction(moonPhase)
        if (fraction != null && fraction !in 0.45f..0.55f) {
            val illumination = kotlin.math.sin((fraction * PI).toDouble()).toFloat().coerceIn(0.08f, 1f)
            val shadowOffset = (1f - illumination) * radius * if (fraction < 0.5f) -1f else 1f
            val shadow = Path().apply {
                addOval(
                    RectF(
                        centerX - radius + shadowOffset,
                        centerY - radius,
                        centerX + radius + shadowOffset,
                        centerY + radius,
                    ),
                    Path.Direction.CW,
                )
            }
            moonPath.op(shadow, Path.Op.DIFFERENCE)
        }
        val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(248, 233, 161)
            style = Paint.Style.FILL
        }
        canvas.drawPath(moonPath, moonPaint)
        val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(255, 248, 207)
            style = Paint.Style.STROKE
            strokeWidth = maxOf(2f, radius * 0.08f)
        }
        canvas.drawPath(moonPath, outlinePaint)
    }
}

private class WeatherIllustrationView(
    context: Context,
    private val illustration: WeatherIllustration,
    private val moonPhase: String?,
) : View(context) {
    init {
        contentDescription = "Weather illustration"
        isFocusable = false
    }

    override fun onDraw(canvas: Canvas) {
        WeatherIllustrationCanvas.draw(canvas, illustration, moonPhase, width, height)
    }
}

private class DensitySafeClockView(context: Context) : View(context) {
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
        typeface = android.graphics.Typeface.DEFAULT
    }

    var textColor: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }

    var timeText: String = "00:00"
        set(value) {
            if (field == value) return
            field = value
            requestLayout()
            invalidate()
        }

    var colonAlpha: Float = 1f
        set(value) {
            val next = value.coerceIn(0f, 1f)
            if (field == next) return
            field = next
            invalidate()
        }

    var lastMeasurement: MainClockTextMeasurement? = null
        private set

    init {
        contentDescription = "Main display clock"
        isFocusable = false
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec).coerceAtLeast(1)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec).coerceAtLeast(1)
        textPaint.textSize = 1f
        val textWidthAtUnitSize = textPaint.measureText(timeText).coerceAtLeast(1f)
        val metrics = textPaint.fontMetrics
        val textHeightAtUnitSize = (metrics.bottom - metrics.top).coerceAtLeast(1f)
        val measurement = MainClockGeometry.measure(
            availableWidth = availableWidth,
            availableHeight = availableHeight,
            textWidthAtUnitSize = textWidthAtUnitSize,
            textHeightAtUnitSize = textHeightAtUnitSize,
        )
        lastMeasurement = measurement
        textPaint.textSize = measurement.textSizePx
        setMeasuredDimension(
            resolveSize(availableWidth, widthMeasureSpec),
            resolveSize(availableHeight, heightMeasureSpec),
        )
    }

    override fun onDraw(canvas: Canvas) {
        val measurement = lastMeasurement ?: return
        textPaint.textSize = measurement.textSizePx
        textPaint.textAlign = Paint.Align.LEFT
        val metrics = textPaint.fontMetrics
        val baseline = height / 2f - (metrics.ascent + metrics.descent) / 2f
        val textWidth = textPaint.measureText(timeText)
        var x = (width - textWidth) / 2f
        val colonIndex = timeText.indexOf(':')
        if (colonIndex < 0) {
            textPaint.color = textColor
            canvas.drawText(timeText, x, baseline, textPaint)
            return
        }
        val beforeColon = timeText.substring(0, colonIndex)
        val colon = ":"
        val afterColon = timeText.substring(colonIndex + 1)
        textPaint.color = textColor
        canvas.drawText(beforeColon, x, baseline, textPaint)
        x += textPaint.measureText(beforeColon)
        textPaint.color = Color.argb(
            (Color.alpha(textColor) * colonAlpha).toInt(),
            Color.red(textColor),
            Color.green(textColor),
            Color.blue(textColor),
        )
        canvas.drawText(colon, x, baseline, textPaint)
        x += textPaint.measureText(colon)
        textPaint.color = textColor
        canvas.drawText(afterColon, x, baseline, textPaint)
    }
}

internal object PressureArrowCanvas {
    const val STROKE_WIDTH_PX = 5f
    const val PATH_SEGMENT_COUNT = 2
    private const val MAX_VISIBLE_ARROWS = 2

    fun visibleCount(count: Int): Int = count.coerceIn(0, MAX_VISIBLE_ARROWS)

    fun effectiveDirection(direction: PressureDirection?): PressureDirection =
        direction ?: PressureDirection.DOWN
}

private class PressureArrowView(
    context: Context,
    private val direction: PressureDirection,
    private val paintColor: Int,
    private val paintAlpha: Float,
) : View(context) {
    private val shaftPath = Path()
    private val headPath = Path()
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = paintColor
        style = Paint.Style.STROKE
        strokeWidth = PressureArrowCanvas.STROKE_WIDTH_PX
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    init {
        contentDescription = "Pressure trend arrow"
        isFocusable = false
        alpha = paintAlpha
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val tipY = if (direction == PressureDirection.UP) height * 0.24f else height * 0.76f
        val shaftStartY = if (direction == PressureDirection.UP) height * 0.72f else height * 0.28f
        val shaftEndY = if (direction == PressureDirection.UP) height * 0.34f else height * 0.66f
        val headHalfWidth = minOf(width * 0.22f, height * 0.20f)
        val headBaseY = if (direction == PressureDirection.UP) height * 0.42f else height * 0.58f

        shaftPath.reset()
        shaftPath.moveTo(centerX, shaftStartY)
        shaftPath.lineTo(centerX, shaftEndY)

        headPath.reset()
        headPath.moveTo(centerX - headHalfWidth, headBaseY)
        headPath.lineTo(centerX, tipY)
        headPath.lineTo(centerX + headHalfWidth, headBaseY)

        canvas.drawPath(shaftPath, arrowPaint)
        canvas.drawPath(headPath, arrowPaint)
    }
}

private class WeatherCardLayout(context: Context) : FrameLayout(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth <= 0 || measuredHeight <= 0) return
        val geometry = WeatherCardContentGeometry.measure(measuredWidth, measuredHeight)
        var changed = false
        fun place(view: View, bounds: DisplayBounds) {
            val params = view.layoutParams as FrameLayout.LayoutParams
            if (params.width != bounds.width || params.height != bounds.height ||
                params.leftMargin != bounds.left || params.topMargin != bounds.top
            ) {
                params.width = bounds.width
                params.height = bounds.height
                params.leftMargin = bounds.left
                params.topMargin = bounds.top
                view.layoutParams = params
                changed = true
            }
        }
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            when (child.tag) {
                "weather-illustration" -> place(child, geometry.illustrationBounds)
                "weather-temperature" -> place(child, geometry.temperatureBounds)
                "weather-date" -> place(child, geometry.dateBounds)
                "weather-pressure" -> {
                    val pressureViews = (0 until childCount)
                        .map { getChildAt(it) }
                        .filter { it.tag == "weather-pressure" }
                    val slotWidth = (geometry.pressureBounds.width / pressureViews.size.coerceAtLeast(1)).coerceAtLeast(1)
                    val pressureIndex = pressureViews.indexOf(child)
                    place(
                        child,
                        DisplayBounds(
                            left = geometry.pressureBounds.left + pressureIndex * slotWidth,
                            top = geometry.pressureBounds.top,
                            right = if (pressureIndex == pressureViews.lastIndex) geometry.pressureBounds.right
                            else geometry.pressureBounds.left + (pressureIndex + 1) * slotWidth,
                            bottom = geometry.pressureBounds.bottom,
                        ),
                    )
                }
            }
        }
        if (changed) super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

internal data class MainDisplayMeasuredGeometry(
    val weatherSlots: List<WeatherCardSlot>,
    val cityBounds: DisplayBounds,
    val dateBounds: DisplayBounds,
    val clockBounds: DisplayBounds,
    val idleClockTextSize: Float,
    val weatherCardBounds: List<DisplayBounds>,
    val presetBounds: List<DisplayBounds>,
    val presetCornerRadii: List<Float>,
    val interCardGaps: List<Int>,
)

internal object MainDisplayGeometry {
    private const val WEATHER_BAND_RATIO = 0.28f

    private data class CardWidths(val smaller: Int, val today: Int)

    private fun cardWidths(
        availableWeatherWidth: Int,
        spec: DisplayLayoutSpec,
    ): CardWidths {
        val distributableWidth = availableWeatherWidth - (spec.interCardGapDp * 3)
        val totalWeight = (spec.smallerCardWeight * 3f) + spec.todayCardWeight
        val smaller = (distributableWidth * spec.smallerCardWeight / totalWeight)
            .toInt()
            .coerceAtLeast(1)
        val today = (distributableWidth - (smaller * 3)).coerceAtLeast(1)
        return CardWidths(smaller = smaller, today = today)
    }

    fun measure(
        width: Int,
        height: Int,
        spec: DisplayLayoutSpec = DisplayLayoutSpec(),
    ): MainDisplayMeasuredGeometry {
        val contentLeft = spec.outerHorizontalPaddingDp
        val contentTop = spec.outerVerticalPaddingDp
        val contentRight = width - spec.outerHorizontalPaddingDp
        val contentBottom = height - spec.outerVerticalPaddingDp
        val contentWidth = contentRight - contentLeft
        val availableWeatherWidth = contentWidth - spec.presetColumnWidthDp
        val widths = cardWidths(availableWeatherWidth, spec)
        val leftColumnRight = contentLeft + widths.smaller
        val centralLeft = leftColumnRight + spec.interCardGapDp
        val centralRight = contentLeft + availableWeatherWidth
        val weatherBandHeight = (height * WEATHER_BAND_RATIO).toInt().coerceAtLeast(1)
        val weatherTop = (contentBottom - weatherBandHeight).coerceAtLeast(contentTop + 1)
        val headerHeight = weatherTop - contentTop
        val cardBottom = contentBottom
        val rightColumnLeft = centralRight

        val yesterday = DisplayBounds(contentLeft, weatherTop, leftColumnRight, cardBottom)
        val today = DisplayBounds(
            centralLeft,
            weatherTop,
            centralLeft + widths.today,
            cardBottom,
        )
        val tomorrow = DisplayBounds(
            today.right + spec.interCardGapDp,
            weatherTop,
            today.right + spec.interCardGapDp + widths.smaller,
            cardBottom,
        )
        val dayAfter = DisplayBounds(
            tomorrow.right + spec.interCardGapDp,
            weatherTop,
            centralRight,
            cardBottom,
        )
        val leftBounds = DisplayBounds(contentLeft, contentTop, leftColumnRight, weatherTop)
        val centralBounds = DisplayBounds(centralLeft, contentTop, centralRight, weatherTop)
        val presetSide = spec.presetColumnWidthDp
        val presetGap = spec.presetGapDp
        val presetCount = TimerPresetSlot.entries.size
        val presetButtonSide = minOf(
            presetSide,
            ((contentBottom - contentTop - (presetGap * presetCount)) / presetCount)
                .coerceAtLeast(1),
        )
        val presetGroupHeight = (presetButtonSide * presetCount) + (presetGap * presetCount)
        val presetTop = contentTop + ((contentBottom - contentTop - presetGroupHeight) / 2) + (presetGap / 2)
        val presetLeft = rightColumnLeft + ((presetSide - presetButtonSide) / 2)
        return MainDisplayMeasuredGeometry(
            weatherSlots = listOf(
                WeatherCardSlot.YESTERDAY,
                WeatherCardSlot.TODAY,
                WeatherCardSlot.TOMORROW,
                WeatherCardSlot.DAY_AFTER,
            ),
            cityBounds = DisplayBounds(leftBounds.left, leftBounds.top, leftBounds.right, leftBounds.top + headerHeight / 2),
            dateBounds = DisplayBounds(leftBounds.left, leftBounds.top + headerHeight / 2, leftBounds.right, leftBounds.bottom),
            clockBounds = centralBounds,
            idleClockTextSize = adaptiveIdleClockTextSize(centralBounds.width, centralBounds.height, spec),
            weatherCardBounds = listOf(yesterday, today, tomorrow, dayAfter),
            presetBounds = TimerPresetSlot.entries.mapIndexed { index, _ ->
                DisplayBounds(
                    presetLeft,
                    presetTop + (index * (presetButtonSide + presetGap)),
                    presetLeft + presetButtonSide,
                    presetTop + (index * (presetButtonSide + presetGap)) + presetButtonSide,
                )
            },
            presetCornerRadii = List(TimerPresetSlot.entries.size) { presetButtonSide / 2f },
            interCardGaps = listOf(
                spec.interCardGapDp,
                spec.interCardGapDp,
                spec.interCardGapDp,
            ),
        )
    }
}

internal data class ActiveCountdownMeasuredGeometry(
    val surfaceBounds: DisplayBounds,
    val backdropBounds: DisplayBounds,
    val countdownTextSize: Float,
)

internal object ActiveCountdownSurfaceGeometry {
    fun measure(
        width: Int,
        height: Int,
        spec: DisplayLayoutSpec = DisplayLayoutSpec(),
    ): ActiveCountdownMeasuredGeometry {
        val idleGeometry = MainDisplayGeometry.measure(width, height, spec)
        val surface = idleGeometry.clockBounds
        val backdropSide = minOf(surface.width, surface.height)
        val backdrop = DisplayBounds(
            left = surface.left + (surface.width - backdropSide) / 2,
            top = surface.top + (surface.height - backdropSide) / 2,
            right = surface.left + (surface.width - backdropSide) / 2 + backdropSide,
            bottom = surface.top + (surface.height - backdropSide) / 2 + backdropSide,
        )
        return ActiveCountdownMeasuredGeometry(
            surfaceBounds = surface,
            backdropBounds = backdrop,
            countdownTextSize = maxOf(
                idleGeometry.idleClockTextSize,
                minOf(
                    surface.height.toFloat(),
                    OverdueSurfaceGeometry.measure(width, height).elapsedTextSize - 1f,
                ),
            ),
        )
    }
}

internal data class OverdueMeasuredGeometry(
    val surfaceBounds: DisplayBounds,
    val backdropBounds: DisplayBounds,
    val plusBounds: DisplayBounds,
    val elapsedBounds: DisplayBounds,
    val plusTextSize: Float,
    val elapsedTextSize: Float,
)

internal object OverdueSurfaceGeometry {
    fun measure(
        width: Int,
        height: Int,
    ): OverdueMeasuredGeometry {
        val safeWidth = width.coerceAtLeast(1)
        val safeHeight = height.coerceAtLeast(1)
        val surface = DisplayBounds(0, 0, safeWidth, safeHeight)
        val backdropSide = minOf(safeWidth, safeHeight)
        val backdrop = DisplayBounds(
            left = (safeWidth - backdropSide) / 2,
            top = (safeHeight - backdropSide) / 2,
            right = (safeWidth - backdropSide) / 2 + backdropSide,
            bottom = (safeHeight - backdropSide) / 2 + backdropSide,
        )
        val splitTop = safeHeight / 2
        val plusBounds = DisplayBounds(0, 0, safeWidth, splitTop)
        val elapsedBounds = DisplayBounds(0, splitTop, safeWidth, safeHeight)
        val elapsedTextSize = minOf(
            elapsedBounds.width.toFloat() / 5f,
            (elapsedBounds.height - 24).coerceAtLeast(1).toFloat(),
        )
        val plusTextSize = minOf(
            (plusBounds.height - 24).coerceAtLeast(1).toFloat(),
            elapsedTextSize + 24f,
        )
        return OverdueMeasuredGeometry(
            surfaceBounds = surface,
            backdropBounds = backdrop,
            plusBounds = plusBounds,
            elapsedBounds = elapsedBounds,
            plusTextSize = plusTextSize,
            elapsedTextSize = elapsedTextSize,
        )
    }
}

data class PresetButtonStyle(
    val slot: TimerPresetSlot,
    val label: String,
    val outlineHex: String,
    val isSelected: Boolean,
    val isActive: Boolean,
)

object PresetPresentation {
    private val outlineColors = mapOf(
        TimerPresetSlot.FIRST to "#FF7A00",
        TimerPresetSlot.SECOND to "#FF4FA3",
        TimerPresetSlot.THIRD to "#A855F7",
    )

    fun label(duration: TimerPresetDuration): String = when {
        duration.hours > 0 -> "${duration.hours} ч"
        duration.minutes > 0 -> "${duration.minutes} м"
        else -> "${duration.seconds} с"
    }

    fun style(presentation: TimerPresetPresentation): PresetButtonStyle = PresetButtonStyle(
        slot = presentation.slot,
        label = label(presentation.duration),
        outlineHex = outlineColors.getValue(presentation.slot),
        isSelected = presentation.isSelected,
        isActive = presentation.isActive,
    )

    fun styles(presentations: List<TimerPresetPresentation>): List<PresetButtonStyle> =
        presentations.map(::style)

    fun colorHex(slot: TimerPresetSlot): String = outlineColors.getValue(slot)
}

internal object PresetVisualGeometry {
    private const val RIM_WIDTH_FRACTION = 0.05f
    private const val ACTIVE_RIM_MULTIPLIER = 1.2f
    private const val GLOW_LAYER_COUNT = 3

    fun rimWidthPx(buttonSide: Int, isActive: Boolean): Float {
        val base = (buttonSide.coerceAtLeast(1) * RIM_WIDTH_FRACTION).coerceAtLeast(8f)
        return if (isActive) base * ACTIVE_RIM_MULTIPLIER else base
    }

    fun glowLayerCount(): Int = GLOW_LAYER_COUNT

    fun glowSpreadPx(buttonSide: Int, layer: Int): Float =
        (buttonSide.coerceAtLeast(1) * 0.025f * layer.coerceIn(1, GLOW_LAYER_COUNT))

    fun radialShadeColors(baseColor: Int): IntArray {
        val hsv = FloatArray(3)
        Color.colorToHSV(baseColor, hsv)
        val highlight = hsv.copyOf().apply {
            this[1] = (this[1] * 0.72f).coerceIn(0f, 1f)
            this[2] = (this[2] * 1.22f).coerceIn(0f, 1f)
        }
        val shade = hsv.copyOf().apply {
            this[1] = (this[1] * 1.08f).coerceIn(0f, 1f)
            this[2] = (this[2] * 0.68f).coerceIn(0f, 1f)
        }
        return intArrayOf(
            Color.HSVToColor(255, highlight),
            Color.rgb(Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor)),
            Color.HSVToColor(255, shade),
        )
    }
}

object DisplayFormatters {
    private val clockFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val russianGenitiveMonths = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря",
    )

    fun timeText(nowMillis: Long, zoneId: ZoneId): String =
        clockFormatter.withZone(zoneId).format(Instant.ofEpochMilli(nowMillis))

    fun dateText(nowMillis: Long, zoneId: ZoneId): String {
        val date = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        return "%02d %s".format(date.dayOfMonth, russianGenitiveMonths[date.monthValue - 1])
    }

    fun countdownText(remainingMillis: Long): String {
        val totalSeconds = ((remainingMillis.coerceAtLeast(0L) + 999L) / 1_000L)
        val hours = totalSeconds / 3_600L
        val minutes = (totalSeconds % 3_600L) / 60L
        val seconds = totalSeconds % 60L
        return if (hours > 0L) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    fun elapsedText(elapsedMillis: Long): String {
        val totalSeconds = elapsedMillis.coerceAtLeast(0L) / 1_000L
        val hours = totalSeconds / 3_600L
        val minutes = (totalSeconds % 3_600L) / 60L
        val seconds = totalSeconds % 60L
        return "%02d:%02d:%02d".format(hours, minutes, seconds)
    }
}

object OverduePresentation {
    private const val BLINK_PERIOD_MILLIS = 764L
    private const val BLINK_VISIBLE_MILLIS = BLINK_PERIOD_MILLIS / 2L

    fun plusVisibleAt(overdueElapsedMillis: Long): Boolean =
        overdueElapsedMillis.coerceAtLeast(0L).mod(BLINK_PERIOD_MILLIS) < BLINK_VISIBLE_MILLIS
}

object ColonProjection {
    fun mode(
        connectivity: DisplayConnectivity,
        timerState: TimerLifecycleState,
    ): ColonMode = when (timerState) {
        TimerLifecycleState.COUNTDOWN -> ColonMode.COUNTDOWN_BLINK
        TimerLifecycleState.IDLE,
        TimerLifecycleState.OVERDUE,
        -> if (connectivity == DisplayConnectivity.ONLINE) {
            ColonMode.ONLINE_PULSE
        } else {
            ColonMode.OFFLINE_FIXED
        }
    }

    fun brightness(mode: ColonMode, elapsedMillis: Long): Float = when (mode) {
        ColonMode.OFFLINE_FIXED -> 0.38f
        ColonMode.COUNTDOWN_BLINK -> if (elapsedMillis.mod(1_000L) < 382L) 1f else 0f
        ColonMode.ONLINE_PULSE -> {
            val phase = elapsedMillis.mod(6_000L).toFloat()
            if (phase <= 3_000f) {
                phase / 3_000f
            } else {
                1f - ((phase - 3_000f) / 3_000f * 0.98f)
            }
        }
    }
}

object CityInteractionRouter {
    fun route(citySelected: Boolean, gesture: CityGesture): CityInteraction = when (gesture) {
        CityGesture.LONG_HOLD -> CityInteraction.OPEN_SETTINGS
        CityGesture.SHORT_TAP -> if (citySelected) {
            CityInteraction.NO_OP
        } else {
            CityInteraction.OPEN_SETTINGS
        }
    }
}

internal class ActiveCountdownTouchDispatcher {
    private var captured = false

    fun shouldDispatch(actionMasked: Int, timerActive: Boolean): Boolean {
        if (actionMasked == MotionEvent.ACTION_DOWN) captured = timerActive
        val shouldDispatch = captured
        if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
            captured = false
        }
        return shouldDispatch
    }
}

internal data class MainDisplayWeatherRenderInput(
    val projection: WeatherProjection,
    val glassIntensity: Float,
)

internal data class DisplayWeatherSlot(
    val slot: WeatherCardSlot,
    val projection: WeatherCardProjection?,
)

internal fun orderedDisplayWeatherSlots(projection: WeatherProjection): List<DisplayWeatherSlot> =
    WeatherCardSlot.entries.map { slot ->
        DisplayWeatherSlot(
            slot = slot,
            projection = projection.cards.firstOrNull { it.slot == slot },
        )
    }

internal class MainDisplayWeatherCardRenderer {
    private var lastRenderedInput: MainDisplayWeatherRenderInput? = null

    fun renderIfChanged(
        input: MainDisplayWeatherRenderInput,
        render: () -> Unit,
    ): Boolean {
        if (input == lastRenderedInput) return false
        lastRenderedInput = input
        render()
        return true
    }
}

internal interface MainDisplayTickerScheduler {
    fun post(runnable: Runnable)

    fun postDelayed(runnable: Runnable, delayMillis: Long)

    fun removeCallbacks(runnable: Runnable)
}

internal class MainDisplayTickerOwner(
    private val scheduler: MainDisplayTickerScheduler,
    private val onTick: () -> Unit,
    private val tickIntervalMillis: Long = 50L,
) {
    private var attached = false
    private var activityResumed = false
    private var scheduled = false
    private var running = false

    private val ticker = object : Runnable {
        override fun run() {
            scheduled = false
            if (!canRun()) return
            running = true
            try {
                onTick()
            } finally {
                running = false
                scheduleNextIfNeeded()
            }
        }
    }

    fun onViewAttachedToWindow() {
        attached = true
        scheduleIfNeeded()
    }

    fun onViewDetachedFromWindow() {
        attached = false
        stop()
    }

    fun onActivityResumed() {
        activityResumed = true
        scheduleIfNeeded()
    }

    fun onActivityPaused() {
        activityResumed = false
        stop()
    }

    fun dispose() {
        attached = false
        activityResumed = false
        stop()
    }

    private fun canRun(): Boolean = attached && activityResumed

    private fun scheduleIfNeeded() {
        if (!canRun() || scheduled || running) return
        scheduled = true
        scheduler.post(ticker)
    }

    private fun scheduleNextIfNeeded() {
        if (!canRun() || scheduled) return
        scheduled = true
        scheduler.postDelayed(ticker, tickIntervalMillis)
    }

    private fun stop() {
        scheduler.removeCallbacks(ticker)
        scheduled = false
    }
}

fun forecastEntryIntent(slot: WeatherCardSlot): ForecastEntryIntent? = when (slot) {
    WeatherCardSlot.TODAY -> ForecastEntryIntent.HOURLY
    WeatherCardSlot.TOMORROW,
    WeatherCardSlot.DAY_AFTER,
    -> ForecastEntryIntent.LONG_TERM
    WeatherCardSlot.YESTERDAY -> null
}

private fun forecastClick(
    slot: WeatherCardSlot,
    onOpenForecast: (ForecastEntryIntent) -> Unit,
): (() -> Unit)? = forecastEntryIntent(slot)?.let { intent ->
    { onOpenForecast(intent) }
}

/** Main Display-owned projection for the Settings preview; it never refreshes weather. */
object SettingsPreviewProjection {
    fun from(
        weatherProjection: WeatherProjection,
        nowMillis: Long,
        zoneId: ZoneId,
    ): WeatherCardProjection {
        val today = weatherProjection.cards.firstOrNull { it.slot == WeatherCardSlot.TODAY }
        val temperature = today?.temperatureCelsius ?: 24
        return (today ?: WeatherCardProjection(
            slot = WeatherCardSlot.TODAY,
            date = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate(),
            temperatureCelsius = null,
            temperatureText = null,
            backgroundHex = null,
            illustration = null,
            moonPhase = null,
            pressureArrowCount = 0,
            pressureDirection = null,
            isTodaySize = true,
        )).copy(
            temperatureCelsius = temperature,
            temperatureText = today?.temperatureCelsius?.let {
                today.temperatureText ?: "$it °C"
            } ?: "24 °C",
            backgroundHex = today?.backgroundHex ?: TemperaturePalette.colorFor(temperature),
            illustration = today?.illustration ?: WeatherIllustration.NEUTRAL_CLOUD,
            pressureArrowCount = 2,
            pressureDirection = today?.pressureDirection ?: PressureDirection.UP,
            isTodaySize = true,
        )
    }
}

/** Main Display owns composition and gesture intent; neighbor state stays behind capability contracts. */
class DisplayCapability(
    private val platform: PlatformRuntime,
    private val settings: SettingsCapability,
    private val weather: WeatherCapability,
    private val timer: TimerCapability,
    private val forecast: ForecastSessionCapability,
) {
    private var activityResumed = false
    private var activeMainDisplayTicker: MainDisplayTickerOwner? = null

    internal fun onActivityResumed() {
        activityResumed = true
        activeMainDisplayTicker?.onActivityResumed()
    }

    internal fun onActivityPaused() {
        activityResumed = false
        activeMainDisplayTicker?.onActivityPaused()
    }

    /** Main Display composes the Settings surface and supplies its local preview. */
    fun createSettingsView(context: Context, onBack: () -> Unit): View =
        settings.createDestinationView(
            context = context,
            onBack = onBack,
            weatherErrorProvider = weather::inlineErrorMessage,
            previewFactory = { previewContext, intensity ->
                val projection = SettingsPreviewProjection.from(
                    weatherProjection = weather.projection(platform.nowMillis()),
                    nowMillis = platform.nowMillis(),
                    zoneId = platform.deviceZoneId(),
                )
                weatherCard(
                    context = previewContext,
                    projection = projection,
                    glassIntensity = intensity,
                )
            },
        )

    fun createMainView(
        context: Context,
        onOpenSettings: () -> Unit,
        onOpenForecast: (ForecastEntryIntent) -> Unit = {},
    ): View {
        val layoutSpec = DisplayLayoutSpec()
        val root = FrameLayout(context).apply {
            setBackgroundColor(context.getColor(R.color.display_background))
        }
        val mainShell = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(context.getColor(R.color.display_background))
            setPadding(32, 24, 32, 24)
        }
        root.addView(mainShell, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        val left = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.BOTTOM
        }

        val center = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val right = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            clipChildren = false
            clipToPadding = false
        }
        mainShell.clipChildren = false
        mainShell.clipToPadding = false
        mainShell.addView(left, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))
        mainShell.addView(
            center,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.25f).apply {
                setMargins(layoutSpec.interCardGapDp, 0, 0, 0)
            },
        )
        mainShell.addView(right, LinearLayout.LayoutParams(layoutSpec.presetColumnWidthDp, LinearLayout.LayoutParams.MATCH_PARENT))

        val leftHeader = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        left.addView(
            leftHeader,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                layoutSpec.headerWeight,
            ),
        )

        val date = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 28f
            contentDescription = "Device date"
        }
        leftHeader.addView(date, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val city = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 22f
            setPadding(24, 12, 24, 12)
            isClickable = true
            isFocusable = true
            contentDescription = "City settings gesture"
        }
        leftHeader.addView(city, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val yesterdayCard = FrameLayout(context)
        left.addView(
            yesterdayCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0f,
            ),
        )

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        center.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                layoutSpec.headerWeight,
            ),
        )

        val clockRow = FrameLayout(context).apply {
            clipChildren = false
            clipToPadding = false
        }
        val clock = DensitySafeClockView(context).apply {
            textColor = context.getColor(R.color.display_primary)
        }
        clockRow.addView(
            clock,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )
        header.addView(
            clockRow,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ),
        )

        val countdown = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_primary))
            textSize = layoutSpec.idleClockTextSize
            setShadowLayer(12f, 0f, 0f, Color.WHITE)
            contentDescription = "Timer countdown"
        }

        val timerHint = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 18f
            contentDescription = "Timer cancellation hint"
        }

        val forecastMessage = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 16f
            contentDescription = "Hourly forecast availability"
        }
        header.addView(forecastMessage, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val cards = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
        }
        center.addView(
            cards,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                layoutSpec.weatherRowWeight,
            ),
        )
        val activeCountdownSurface = FrameLayout(context).apply {
            visibility = View.GONE
            isClickable = true
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = "Dedicated active countdown surface"
        }
        val activeCountdownBackdrop = NeonCountdownBackdropView(context)
        activeCountdownSurface.addView(
            activeCountdownBackdrop,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
        )
        activeCountdownSurface.addView(
            countdown,
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT,
        )
        activeCountdownSurface.addView(
            timerHint,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM,
            ),
        )
        center.addView(
            activeCountdownSurface,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                layoutSpec.weatherRowWeight,
            ),
        )
        val presetButtons = TimerPresetSlot.entries.map { slot ->
            val presentation = timer.presetPresentationAt(platform.nowMillis()).first { it.slot == slot }
            presetButton(context, PresetPresentation.style(presentation))
        }
        presetButtons.forEach { button ->
            right.addView(
                button,
                LinearLayout.LayoutParams(0, 0),
            )
        }

        var timerHintUntilMillis = 0L
        val activeCountdownTouchDispatcher = ActiveCountdownTouchDispatcher()

        fun applyTimerGesture(gesture: TimerGesture) {
            val now = platform.nowMillis()
            val result = timer.handleGesture(now, gesture)
            if (result.singleTapHintVisible) {
                timerHintUntilMillis = now + SINGLE_TAP_HINT_MILLIS
            }
        }

        fun handlePresetTap(slot: TimerPresetSlot) {
            val now = platform.nowMillis()
            if (timer.snapshotAt(now).state == TimerLifecycleState.IDLE) {
                timer.startPreset(slot, now)
            } else {
                applyTimerGesture(TimerGesture.SINGLE_TAP)
            }
        }

        fun route(gesture: CityGesture) {
            val citySelected = settings.currentLocation() != null
            if (CityInteractionRouter.route(citySelected, gesture) == CityInteraction.OPEN_SETTINGS) {
                onOpenSettings()
            }
        }

        val mainGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                applyTimerGesture(TimerGesture.SINGLE_TAP)
                return true
            }

            override fun onDoubleTap(event: MotionEvent): Boolean {
                applyTimerGesture(TimerGesture.DOUBLE_TAP)
                return true
            }
        })
        val activeTimerCityGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                applyTimerGesture(TimerGesture.SINGLE_TAP)
                return true
            }

            override fun onDoubleTap(event: MotionEvent): Boolean {
                applyTimerGesture(TimerGesture.DOUBLE_TAP)
                return true
            }

            override fun onLongPress(event: MotionEvent) {
                route(CityGesture.LONG_HOLD)
            }
        })
        fun dispatchActiveCountdownTouch(
            event: MotionEvent,
            detector: GestureDetector,
        ): Boolean {
            val timerActiveAtDown = event.actionMasked == MotionEvent.ACTION_DOWN &&
                timer.snapshotAt(platform.nowMillis()).state == TimerLifecycleState.COUNTDOWN
            val shouldDispatch = activeCountdownTouchDispatcher.shouldDispatch(
                actionMasked = event.actionMasked,
                timerActive = timerActiveAtDown,
            )
            if (shouldDispatch) detector.onTouchEvent(event)
            return shouldDispatch
        }

        root.setOnTouchListener { _, event ->
            if (dispatchActiveCountdownTouch(event, mainGestureDetector)) {
                true
            } else {
                mainGestureDetector.onTouchEvent(event)
                false
            }
        }
        activeCountdownSurface.setOnTouchListener { _, event ->
            dispatchActiveCountdownTouch(event, mainGestureDetector)
        }

        val activeTimerTouchListener = View.OnTouchListener { _, event ->
            dispatchActiveCountdownTouch(event, mainGestureDetector)
        }
        val activeTimerCityTouchListener = View.OnTouchListener { _, event ->
            dispatchActiveCountdownTouch(event, activeTimerCityGestureDetector)
        }

        val weatherCardRenderer = MainDisplayWeatherCardRenderer()

        fun bindWeatherCards(input: MainDisplayWeatherRenderInput) {
            yesterdayCard.removeAllViews()
            cards.removeAllViews()
            orderedDisplayWeatherSlots(input.projection).forEachIndexed { index, slotInput ->
                val card = weatherCard(
                    context,
                    slotInput.projection,
                    slotInput.slot,
                    forecastClick(slotInput.slot, onOpenForecast),
                    input.glassIntensity,
                )
                if (slotInput.slot == WeatherCardSlot.YESTERDAY) {
                    yesterdayCard.addView(
                        card,
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        ),
                    )
                } else {
                    val centralIndex = cards.childCount
                    cards.addView(
                        card,
                        LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            if (slotInput.slot == WeatherCardSlot.TODAY) layoutSpec.todayCardWeight
                            else layoutSpec.smallerCardWeight,
                        ).apply {
                            setMargins(if (centralIndex == 0) 0 else layoutSpec.interCardGapDp, 0, 0, 0)
                        },
                    )
                }
                card.setOnTouchListener(activeTimerTouchListener)
            }
        }

        fun renderWeatherCardsIfChanged(now: Long) {
            val input = MainDisplayWeatherRenderInput(
                projection = weather.projection(now),
                glassIntensity = settings.settingsPresentationProjection().glassIntensity,
            )
            weatherCardRenderer.renderIfChanged(input) {
                bindWeatherCards(input)
            }
        }

        renderWeatherCardsIfChanged(platform.nowMillis())

        fun alignMainDisplayGeometry() {
            if (mainShell.width <= 0 || mainShell.height <= 0) return
            val geometry = MainDisplayGeometry.measure(mainShell.width, mainShell.height, layoutSpec)
            val leftWidth = geometry.weatherCardBounds.first().width
            val centerWidth = geometry.weatherCardBounds.last().right - geometry.weatherCardBounds[1].left
            val gap = layoutSpec.interCardGapDp

            val leftParams = left.layoutParams as LinearLayout.LayoutParams
            val centerParams = center.layoutParams as LinearLayout.LayoutParams
            val rightParams = right.layoutParams as LinearLayout.LayoutParams
            val headerParams = header.layoutParams as LinearLayout.LayoutParams
            val yesterdayParams = yesterdayCard.layoutParams as LinearLayout.LayoutParams
            val weatherCardHeight = geometry.weatherCardBounds.first().height
            var changed = false
            if (leftParams.width != leftWidth || leftParams.weight != 0f) {
                leftParams.width = leftWidth
                leftParams.weight = 0f
                changed = true
            }
            if (centerParams.width != centerWidth || centerParams.weight != 0f || centerParams.leftMargin != gap) {
                centerParams.width = centerWidth
                centerParams.weight = 0f
                centerParams.leftMargin = gap
                changed = true
            }
            if (rightParams.width != layoutSpec.presetColumnWidthDp || rightParams.weight != 0f) {
                rightParams.width = layoutSpec.presetColumnWidthDp
                rightParams.weight = 0f
                changed = true
            }
            if (headerParams.height != geometry.clockBounds.height || headerParams.weight != 0f) {
                headerParams.height = geometry.clockBounds.height
                headerParams.weight = 0f
                changed = true
            }
            if (yesterdayParams.height != weatherCardHeight || yesterdayParams.weight != 0f) {
                yesterdayParams.height = weatherCardHeight
                yesterdayParams.weight = 0f
                changed = true
            }
            geometry.weatherCardBounds.drop(1).forEachIndexed { index, bounds ->
                val cardParams = cards.getChildAt(index).layoutParams as LinearLayout.LayoutParams
                val expectedLeftMargin = if (index == 0) 0 else gap
                if (cardParams.width != bounds.width || cardParams.weight != 0f || cardParams.leftMargin != expectedLeftMargin) {
                    cardParams.width = bounds.width
                    cardParams.weight = 0f
                    cardParams.leftMargin = expectedLeftMargin
                    changed = true
                }
            }
            val presetGap = geometry.presetBounds.zipWithNext()
                .firstOrNull()
                ?.let { (first, second) -> second.top - first.bottom }
                ?: layoutSpec.presetGapDp
            val presetMargin = presetGap / 2
            geometry.presetBounds.forEachIndexed { index, bounds ->
                val buttonParams = presetButtons[index].layoutParams as LinearLayout.LayoutParams
                if (
                    buttonParams.width != bounds.width ||
                    buttonParams.height != bounds.height ||
                    buttonParams.topMargin != presetMargin ||
                    buttonParams.bottomMargin != presetMargin
                ) {
                    buttonParams.width = bounds.width
                    buttonParams.height = bounds.height
                    buttonParams.topMargin = presetMargin
                    buttonParams.bottomMargin = presetMargin
                    changed = true
                }
            }
            if (changed) {
                left.layoutParams = leftParams
                center.layoutParams = centerParams
                right.layoutParams = rightParams
                header.layoutParams = headerParams
                yesterdayCard.layoutParams = yesterdayParams
                cards.requestLayout()
            }
        }

        mainShell.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            alignMainDisplayGeometry()
        }
        mainShell.post { alignMainDisplayGeometry() }

        presetButtons.forEach { button ->
            val slot = button.tag as TimerPresetSlot
            val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(event: MotionEvent): Boolean = true

                override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                    handlePresetTap(slot)
                    return true
                }

                override fun onDoubleTap(event: MotionEvent): Boolean {
                    applyTimerGesture(TimerGesture.DOUBLE_TAP)
                    return true
                }
            })
            button.setOnTouchListener { _, event ->
                val captured = dispatchActiveCountdownTouch(event, detector)
                if (captured) true else detector.onTouchEvent(event)
            }
        }

        val overdueOverlay = FrameLayout(context).apply {
            visibility = View.GONE
            isClickable = true
            contentDescription = "Fullscreen overdue state"
        }
        val overdueContent = FrameLayout(context)
        val overdueBackdrop = NeonCountdownBackdropView(context).apply {
            contentDescription = "Transparent neon overdue backdrop"
        }
        val overduePlus = TextView(context).apply {
            gravity = Gravity.CENTER
            text = "+"
            textSize = 176f
            setTextColor(context.getColor(R.color.display_primary))
            setShadowLayer(18f, 0f, 0f, Color.WHITE)
            contentDescription = "Blinking overdue plus"
        }
        val overdueCounter = TextView(context).apply {
            gravity = Gravity.CENTER
            textSize = 76f
            setTextColor(context.getColor(R.color.display_primary))
            setShadowLayer(10f, 0f, 0f, Color.WHITE)
            contentDescription = "Stable full elapsed overdue counter"
        }
        overdueOverlay.setBackgroundColor(Color.TRANSPARENT)
        overdueContent.addView(overduePlus, FrameLayout.LayoutParams.MATCH_PARENT, 0)
        overdueContent.addView(overdueCounter, FrameLayout.LayoutParams.MATCH_PARENT, 0)
        overdueOverlay.addView(overdueBackdrop, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        overdueOverlay.addView(overdueContent, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        root.addView(overdueOverlay, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        val overdueGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                applyTimerGesture(TimerGesture.SINGLE_TAP)
                return true
            }

            override fun onDoubleTap(event: MotionEvent): Boolean {
                applyTimerGesture(TimerGesture.DOUBLE_TAP)
                return true
            }
        })
        overdueOverlay.setOnTouchListener { _, event ->
            overdueGestureDetector.onTouchEvent(event)
            true
        }

        city.setOnClickListener { route(CityGesture.SHORT_TAP) }
        city.setOnLongClickListener {
            route(CityGesture.LONG_HOLD)
            true
        }
        city.setOnTouchListener(activeTimerCityTouchListener)
        for (index in 0 until cards.childCount) {
            cards.getChildAt(index).setOnTouchListener(activeTimerTouchListener)
        }

        fun placeOverdueContent(view: View, bounds: DisplayBounds) {
            val params = view.layoutParams as FrameLayout.LayoutParams
            params.width = bounds.width
            params.height = bounds.height
            params.leftMargin = bounds.left
            params.topMargin = bounds.top
            view.layoutParams = params
        }

        fun refresh() {
            val now = platform.nowMillis()
            val time = platform.deviceTimeText(now)
            clock.timeText = time
            date.text = DisplayFormatters.dateText(now, platform.deviceZoneId())
            city.text = settings.currentLocation()?.cityLabel ?: context.getString(R.string.display_select_city)
            val timerState = timer.snapshotAt(now).state
            val timerSnapshot = timer.snapshotAt(now)
            timer.advanceAt(now)
            if (timerSnapshot.state == TimerLifecycleState.OVERDUE) {
                mainShell.visibility = View.GONE
                overdueOverlay.visibility = View.VISIBLE
                val activeSlot = timerSnapshot.activePresetSlot ?: TimerPresetSlot.FIRST
                overdueBackdrop.borderColor = Color.parseColor(PresetPresentation.colorHex(activeSlot))
                overdueCounter.text = DisplayFormatters.elapsedText(timerSnapshot.elapsedMillis)
                overduePlus.alpha = if (OverduePresentation.plusVisibleAt(timerSnapshot.overdueElapsedMillis)) 1f else 0f
                if (overdueOverlay.width > 0 && overdueOverlay.height > 0) {
                    val overdueGeometry = OverdueSurfaceGeometry.measure(
                        overdueOverlay.width,
                        overdueOverlay.height,
                    )
                    placeOverdueContent(overduePlus, overdueGeometry.plusBounds)
                    placeOverdueContent(overdueCounter, overdueGeometry.elapsedBounds)
                    overduePlus.textSize = overdueGeometry.plusTextSize
                    overdueCounter.textSize = overdueGeometry.elapsedTextSize
                }
            } else {
                mainShell.visibility = View.VISIBLE
                overdueOverlay.visibility = View.GONE
            }
            val isCountdown = timerSnapshot.state == TimerLifecycleState.COUNTDOWN
            left.visibility = if (isCountdown) View.GONE else View.VISIBLE
            header.visibility = if (isCountdown) View.GONE else View.VISIBLE
            cards.visibility = if (isCountdown) View.GONE else View.VISIBLE
            activeCountdownSurface.visibility = if (isCountdown) View.VISIBLE else View.GONE
            countdown.visibility = if (isCountdown) View.VISIBLE else View.GONE
            if (isCountdown) {
                val activeGeometry = if (mainShell.width > 0 && mainShell.height > 0) {
                    ActiveCountdownSurfaceGeometry.measure(mainShell.width, mainShell.height, layoutSpec)
                } else {
                    null
                }
                countdown.text = DisplayFormatters.countdownText(timerSnapshot.remainingMillis)
                countdown.textSize = activeGeometry?.countdownTextSize ?: layoutSpec.idleClockTextSize
                activeCountdownBackdrop.borderColor = Color.parseColor(
                    PresetPresentation.colorHex(timerSnapshot.activePresetSlot ?: TimerPresetSlot.FIRST),
                )
            } else {
                countdown.textSize = layoutSpec.idleClockTextSize
            }
            clockRow.alpha = 1f
            timerHint.text = if (timerHintUntilMillis > now) {
                SINGLE_TAP_HINT
            } else {
                ""
            }
            val connectivity = if (platform.isNetworkAvailable()) {
                DisplayConnectivity.ONLINE
            } else {
                DisplayConnectivity.OFFLINE
            }
            val mode = ColonProjection.mode(connectivity, timerState)
            clock.colonAlpha = ColonProjection.brightness(mode, now)
            val forecastText = forecast.snapshotAt(now).message.orEmpty()
            forecastMessage.text = forecastText
            forecastMessage.visibility = if (forecastText.isBlank()) View.GONE else View.VISIBLE
            if (!isCountdown) renderWeatherCardsIfChanged(now)
            val presetStyles = PresetPresentation.styles(timer.presetPresentationAt(now)).associateBy { it.slot }
            presetButtons.forEach { button ->
                val slot = button.tag as TimerPresetSlot
                presetStyles[slot]?.let { applyPresetStyle(button, it) }
            }
        }

        val ticker = MainDisplayTickerOwner(
            scheduler = object : MainDisplayTickerScheduler {
                override fun post(runnable: Runnable) {
                    root.post(runnable)
                }

                override fun postDelayed(runnable: Runnable, delayMillis: Long) {
                    root.postDelayed(runnable, delayMillis)
                }

                override fun removeCallbacks(runnable: Runnable) {
                    root.removeCallbacks(runnable)
                }
            },
            onTick = ::refresh,
        )
        activeMainDisplayTicker?.dispose()
        activeMainDisplayTicker = ticker
        root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                ticker.onViewAttachedToWindow()
            }

            override fun onViewDetachedFromWindow(view: View) {
                ticker.onViewDetachedFromWindow()
            }
        })
        if (activityResumed) ticker.onActivityResumed()
        return root
    }

    fun createHourlyForecastView(context: Context, onClose: () -> Unit): View? =
        createForecastView(
            context = context,
            onClose = onClose,
            open = { forecast.openHourly(platform.nowMillis()) },
            rows = { snapshot -> snapshot.rows.map { row -> row.map { card ->
                ForecastCardView(card.slotTimeText, card.temperatureText, card.backgroundHex, card.illustration)
            } } },
        )

    fun createLongTermForecastView(context: Context, onClose: () -> Unit): View? =
        createForecastView(
            context = context,
            onClose = onClose,
            open = { forecast.openLongTerm(platform.nowMillis()) },
            rows = { snapshot -> snapshot.longTermRows.map { row -> row.map { card ->
                ForecastCardView(card.dateDayText, card.temperatureText, card.backgroundHex, card.illustration)
            } } },
        )

    private fun createForecastView(
        context: Context,
        onClose: () -> Unit,
        open: () -> ForecastSessionSnapshot,
        rows: (ForecastSessionSnapshot) -> List<List<ForecastCardView>>,
    ): View? {
        if (open().state != ForecastSessionState.OPEN) return null
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(context.getColor(R.color.display_background))
            setPadding(28, 20, 28, 20)
        }
        val hint = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 18f
        }
        root.addView(hint, LinearLayout.LayoutParams.MATCH_PARENT, 42)
        val grid = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        root.addView(grid, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))

        fun update() {
            val snapshot = forecast.snapshotAt(platform.nowMillis())
            if (snapshot.state == ForecastSessionState.CLOSED) {
                onClose()
                return
            }
            hint.text = if (snapshot.state == ForecastSessionState.HINT) {
                ForecastSessionCapability.SINGLE_TAP_HINT
            } else {
                ""
            }
            grid.removeAllViews()
            rows(snapshot).forEach { row ->
                val rowView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }
                row.forEachIndexed { index, card ->
                    rowView.addView(
                        forecastCard(context, card),
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                            setMargins(if (index == 0) 0 else 8, 8, 0, 8)
                        },
                    )
                }
                grid.addView(rowView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
            }
        }

        var held = false
        val holdRunnable = Runnable {
            held = true
            forecast.hold(platform.nowMillis())
        }
        val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                forecast.singleTap(platform.nowMillis())
                update()
                return true
            }

            override fun onDoubleTap(event: MotionEvent): Boolean {
                forecast.doubleTap(platform.nowMillis())
                onClose()
                return true
            }

            override fun onLongPress(event: MotionEvent) {
                held = true
                forecast.hold(platform.nowMillis())
            }
        })
        root.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    held = false
                    view.postDelayed(holdRunnable, HOLD_GESTURE_MILLIS)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.removeCallbacks(holdRunnable)
                    if (held && event.actionMasked == MotionEvent.ACTION_UP) {
                        forecast.release(platform.nowMillis())
                        onClose()
                    }
                }
            }
            detector.onTouchEvent(event)
            true
        }

        val ticker = object : Runnable {
            override fun run() {
                if (root.isAttachedToWindow) {
                    update()
                    root.postDelayed(this, 50L)
                }
            }
        }
        root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                update()
                view.post(ticker)
            }

            override fun onViewDetachedFromWindow(view: View) {
                view.removeCallbacks(ticker)
                view.removeCallbacks(holdRunnable)
            }
        })
        root.post { update() }
        return root
    }

    /** Kept for the Foundation's explicit probe route; the product entry uses [createMainView]. */
    fun createFoundationView(context: Context, foundationProbe: Boolean = false): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(context.getColor(R.color.foundation_background))
            setPadding(48, 32, 48, 32)
        }
        val time = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_primary))
            textSize = 96f
            text = platform.deviceTimeText()
            contentDescription = "Foundation device time"
        }
        val status = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_secondary))
            textSize = 18f
            text = context.getString(R.string.foundation_status)
        }
        val hint = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_secondary))
            textSize = 14f
            text = context.getString(R.string.foundation_hint)
        }
        root.addView(time, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        root.addView(status, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        root.addView(hint, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if (foundationProbe) addFoundationProbe(context, root, time, status, hint)
        return root
    }

    private fun addFoundationProbe(
        context: Context,
        root: LinearLayout,
        time: TextView,
        status: TextView,
        hint: TextView,
    ) {
        status.text = "Foundation probe mode"
        hint.text = "ADB: --ez foundation_probe true"

        val probeStatus = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.foundation_secondary))
            textSize = 14f
            setPadding(0, 12, 0, 12)
        }
        root.addView(probeStatus, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        fun render(message: String) {
            val now = platform.nowMillis()
            val timerSnapshot = timer.snapshotAt(now)
            val location = settings.currentLocation()?.cityLabel ?: "empty"
            val weatherSnapshot = weather.snapshot()
            time.text = platform.deviceTimeText(now)
            probeStatus.text = buildString {
                append(message)
                append("\nSettings: ")
                append(location)
                append("\nTimer: ")
                append(timerSnapshot.state.name)
                append(" elapsed=")
                append(timerSnapshot.elapsedMillis)
                append(" remaining=")
                append(timerSnapshot.remainingMillis)
                append("\nWeather: ")
                append(weatherSnapshot?.let { "${it.temperatureCelsius}C/${it.condition}" } ?: "empty")
            }
        }

        fun addProbeButton(label: String, action: () -> String) {
            root.addView(
                Button(context).apply {
                    text = label
                    contentDescription = "Foundation probe: $label"
                    setOnClickListener { render(action()) }
                },
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
        }

        addProbeButton("Seed Settings") {
            settings.saveFoundationLocation(
                LocationContext(
                    cityLabel = "Khujand",
                    latitude = 40.2833,
                    longitude = 69.6167,
                    apiTimeZone = "Asia/Dushanbe",
                ),
            )
            "settings_seeded"
        }
        addProbeButton("Refresh Weather Fixture") {
            if (weather.refreshFoundationFixture() == null) "weather_requires_settings" else "weather_refreshed_redacted"
        }
        addProbeButton("Start 1s Timer") {
            timer.start(platform.nowMillis(), FOUNDATION_TIMER_DURATION_MILLIS)
            root.postDelayed({ render("timer_observed") }, FOUNDATION_TIMER_DURATION_MILLIS + 250L)
            "timer_started"
        }
        addProbeButton("Rehydrate Timer") {
            timer.rehydrateAt(platform.nowMillis())
            "timer_rehydrated"
        }
        addProbeButton("Audio Probe") {
            val result = timer.requestAudioProbeAt(platform.nowMillis())
            "audio_${result.reason}_permitted=${result.permitted}"
        }
        addProbeButton("Cancel and Reset") {
            settings.resetFoundationState()
            weather.resetFoundationState()
            timer.resetFoundationState()
            "foundation_state_reset"
        }
        render("probe_ready")
    }

    private fun clockText(context: Context, textSize: Float): TextView = TextView(context).apply {
        gravity = Gravity.CENTER
        setTextColor(context.getColor(R.color.display_primary))
        this.textSize = textSize
        setShadowLayer(12f, 0f, 0f, Color.WHITE)
        contentDescription = "Main display clock"
    }

    private fun weatherCard(
        context: Context,
        projection: WeatherCardProjection?,
        slot: WeatherCardSlot = projection?.slot ?: WeatherCardSlot.YESTERDAY,
        onClick: (() -> Unit)? = null,
        glassIntensity: Float = 0.45f,
    ): View = WeatherCardLayout(context).apply {
        tag = "weather-card-${slot.name.lowercase()}"
        contentDescription = "Weather card ${slot.name.lowercase()}"
        onClick?.let { setOnClickListener { it() } }
        val material = WeatherCardPresentation.pseudoGlass(glassIntensity)
        background = GradientDrawable().apply {
            setColor(projection?.backgroundHex?.let(Color::parseColor) ?: Color.TRANSPARENT)
            setStroke(2, Color.argb(material.lightEdgeAlpha, 255, 255, 255))
            cornerRadius = 18f
        }
        scaleY = if (projection?.isTodaySize == true) 1.04f else 0.98f
        projection?.illustration?.let { illustration ->
            addView(
                WeatherIllustrationView(context, illustration, projection.moonPhase).apply {
                    tag = "weather-illustration"
                },
                FrameLayout.LayoutParams(1, 1),
            )
        }
        addView(TextView(context).apply {
            tag = "weather-temperature"
            text = projection?.temperatureText.orEmpty()
            textSize = 28f
            gravity = Gravity.CENTER
            alpha = (0.65f + material.fillAlpha / 255f).coerceAtMost(1f)
            setTextColor(context.getColor(R.color.display_primary))
            contentDescription = "Weather temperature"
        }, FrameLayout.LayoutParams(1, 1))
        projection?.let { cardProjection ->
            addView(TextView(context).apply {
                tag = "weather-date"
                text = cardProjection.date.dayOfMonth.toString()
                textSize = 14f
                setTextColor(context.getColor(R.color.display_primary))
                gravity = Gravity.BOTTOM or Gravity.START
                setPadding(10, 0, 0, 8)
                contentDescription = "Weather calendar date"
            }, FrameLayout.LayoutParams(1, 1))
        }
        repeat(PressureArrowCanvas.visibleCount(projection?.pressureArrowCount ?: 0)) {
            addView(
                PressureArrowView(
                    context = context,
                    direction = PressureArrowCanvas.effectiveDirection(projection?.pressureDirection),
                    paintColor = context.getColor(R.color.display_primary),
                    paintAlpha = (0.65f + material.fillAlpha / 255f).coerceAtMost(1f),
                ).apply {
                    tag = "weather-pressure"
                },
                FrameLayout.LayoutParams(1, 1),
            )
        }
    }

    private data class ForecastCardView(
        val label: String,
        val temperatureText: String?,
        val backgroundHex: String?,
        val illustration: WeatherIllustration?,
    )

    private fun forecastCard(context: Context, projection: ForecastCardView): View = FrameLayout(context).apply {
        tag = "forecast-card-${projection.label}"
        contentDescription = "Forecast card ${projection.label}"
        val material = WeatherCardPresentation.pseudoGlass(0.45f)
        background = GradientDrawable().apply {
            setColor(projection.backgroundHex?.let(Color::parseColor) ?: context.getColor(R.color.display_background))
            setStroke(2, context.getColor(R.color.display_card_stroke))
            cornerRadius = 18f
        }
        projection.illustration?.let { illustration ->
            addView(TextView(context).apply {
                text = WeatherCardPresentation.illustrationText(illustration)
                textSize = 40f
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                setPadding(0, 10, 0, 0)
                setTextColor(context.getColor(R.color.display_primary))
                contentDescription = "Forecast weather illustration ${illustration.name.lowercase()}"
            }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        projection.temperatureText?.let { temperatureText ->
            addView(TextView(context).apply {
                text = temperatureText
                textSize = 28f
                gravity = Gravity.CENTER
                alpha = (0.65f + material.fillAlpha / 255f).coerceAtMost(1f)
                setTextColor(context.getColor(R.color.display_primary))
                contentDescription = "Forecast temperature"
            }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        addView(TextView(context).apply {
            text = projection.label
            textSize = 16f
            setTextColor(context.getColor(R.color.display_primary))
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 10)
            contentDescription = "Forecast card date or time"
        }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    private class NeonCountdownBackdropView(context: Context) : View(context) {
        var borderColor: Int = Color.WHITE
            set(value) {
                field = value
                invalidate()
            }

        private val borderWidthPx = 7f

        init {
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = "Transparent neon countdown backdrop"
            isFocusable = false
        }

        override fun onDraw(canvas: Canvas) {
            if (width <= 0 || height <= 0) return
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = borderWidthPx
                shader = LinearGradient(
                    0f,
                    0f,
                    width.toFloat(),
                    height.toFloat(),
                    Color.WHITE,
                    borderColor,
                    Shader.TileMode.MIRROR,
                )
            }
            val radius = minOf(width, height) / 2f - (borderWidthPx / 2f)
            canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        }
    }

    private class NeonPresetButton(context: Context) : Button(context) {
        var borderColor: Int = Color.WHITE
        var isActivePreset: Boolean = false

        override fun onDraw(canvas: Canvas) {
            if (width <= 0 || height <= 0) return
            val side = minOf(width, height)
            val centerX = width / 2f
            val centerY = height / 2f
            val rimWidth = PresetVisualGeometry.rimWidthPx(side, isActivePreset)
            val rimRadius = side / 2f - (rimWidth / 2f)
            for (layer in PresetVisualGeometry.glowLayerCount() downTo 1) {
                val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(
                        (if (isActivePreset) 44 else 32) / layer,
                        Color.red(borderColor),
                        Color.green(borderColor),
                        Color.blue(borderColor),
                    )
                    style = Paint.Style.STROKE
                    strokeWidth = (rimWidth * 0.55f).coerceAtLeast(2f)
                }
                canvas.drawCircle(
                    centerX,
                    centerY,
                    rimRadius + PresetVisualGeometry.glowSpreadPx(side, layer),
                    glowPaint,
                )
            }
            val rimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = rimWidth
                shader = RadialGradient(
                    centerX,
                    centerY,
                    side / 2f,
                    PresetVisualGeometry.radialShadeColors(borderColor),
                    null,
                    Shader.TileMode.CLAMP,
                )
            }
            canvas.drawCircle(centerX, centerY, rimRadius, rimPaint)
            super.onDraw(canvas)
        }
    }

    private fun presetButton(context: Context, style: PresetButtonStyle): Button = NeonPresetButton(context).apply {
        tag = style.slot
        applyPresetStyle(this, style)
        isAllCaps = false
    }

    private fun applyPresetStyle(button: Button, style: PresetButtonStyle) {
        button.text = style.label
        button.textSize = 30f
        button.contentDescription = "Preset ${style.slot.label}: ${style.label}"
        button.setTextColor(button.context.getColor(R.color.display_primary))
        button.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.TRANSPARENT)
        }
        (button as? NeonPresetButton)?.apply {
            borderColor = Color.parseColor(style.outlineHex)
            isActivePreset = style.isActive
        }
        button.isActivated = style.isActive
        button.alpha = if (style.isSelected) 1f else 0.88f
        button.invalidate()
    }

    private companion object {
        const val FOUNDATION_TIMER_DURATION_MILLIS = 1_000L
        const val HOLD_GESTURE_MILLIS = 600L
        const val SINGLE_TAP_HINT_MILLIS = 1_500L
        const val SINGLE_TAP_HINT = "Для отмены нажмите дважды"
    }
}
