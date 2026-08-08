package com.hozayushka.app.display

import android.content.Context
import android.graphics.Color
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
)

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

internal class ActiveTimerCityTouchStream {
    private var captured = false

    fun shouldDispatch(actionMasked: Int, timerActive: Boolean): Boolean {
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            captured = timerActive
        }
        val shouldDispatch = captured
        if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
            captured = false
        }
        return shouldDispatch
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
    cardIndex: Int,
    onOpenForecast: (ForecastEntryIntent) -> Unit,
): (() -> Unit)? = forecastEntryIntent(WeatherCardSlot.entries[cardIndex])?.let { intent ->
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
            gravity = Gravity.CENTER
        }
        mainShell.addView(left, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        val right = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(20, 0, 0, 0)
        }
        mainShell.addView(right, LinearLayout.LayoutParams(220, LinearLayout.LayoutParams.MATCH_PARENT))

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        left.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                layoutSpec.headerWeight,
            ),
        )

        val clockRow = LinearLayout(context).apply {
            gravity = Gravity.CENTER
        }
        val hour = clockText(context)
        val colon = clockText(context).apply {
            text = ":"
            contentDescription = "Clock colon"
        }
        val minute = clockText(context)
        clockRow.addView(hour)
        clockRow.addView(colon)
        clockRow.addView(minute)
        header.addView(clockRow, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val countdown = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_primary))
            textSize = 132f
            setShadowLayer(12f, 0f, 0f, Color.WHITE)
            visibility = View.GONE
            contentDescription = "Timer countdown"
        }
        header.addView(countdown, 0, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val date = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 28f
            contentDescription = "Device date"
        }
        header.addView(date, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val city = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 22f
            setPadding(24, 12, 24, 12)
            isClickable = true
            isFocusable = true
            contentDescription = "City settings gesture"
        }
        header.addView(city, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val timerHint = TextView(context).apply {
            gravity = Gravity.CENTER
            setTextColor(context.getColor(R.color.display_secondary))
            textSize = 18f
            contentDescription = "Timer cancellation hint"
        }
        header.addView(timerHint, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

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
        left.addView(
            cards,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                layoutSpec.weatherRowWeight,
            ),
        )
        val initialProjection = weather.projection(platform.nowMillis())
        initialProjection.cards.forEachIndexed { index, projection ->
            cards.addView(weatherCard(context, projection, forecastClick(index, onOpenForecast), settings.settingsPresentationProjection().glassIntensity), LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                setMargins(if (index == 0) 0 else 8, 8, 0, 0)
            })
        }

        val presetButtons = TimerPresetSlot.entries.map { slot ->
            val presentation = timer.presetPresentationAt(platform.nowMillis()).first { it.slot == slot }
            presetButton(context, PresetPresentation.style(presentation))
        }
        presetButtons.forEach { button ->
            right.addView(
                button,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f).apply {
                    setMargins(0, 8, 0, 8)
                },
            )
        }

        var timerHintUntilMillis = 0L

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
            button.setOnTouchListener { _, event -> detector.onTouchEvent(event) }
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
        root.setOnTouchListener { _, event ->
            mainGestureDetector.onTouchEvent(event)
            false
        }

        val activeTimerTouchListener = View.OnTouchListener { _, event ->
            if (timer.snapshotAt(platform.nowMillis()).state == TimerLifecycleState.IDLE) {
                false
            } else {
                mainGestureDetector.onTouchEvent(event)
                true
            }
        }
        val activeTimerCityTouchStream = ActiveTimerCityTouchStream()
        val activeTimerCityTouchListener = View.OnTouchListener { _, event ->
            val timerActive = timer.snapshotAt(platform.nowMillis()).state != TimerLifecycleState.IDLE
            val shouldDispatch = activeTimerCityTouchStream.shouldDispatch(event.actionMasked, timerActive)
            if (shouldDispatch) {
                activeTimerCityGestureDetector.onTouchEvent(event)
            }
            shouldDispatch
        }

        val overdueOverlay = FrameLayout(context).apply {
            visibility = View.GONE
            isClickable = true
            contentDescription = "Fullscreen overdue state"
        }
        val overdueContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        val overduePlus = TextView(context).apply {
            gravity = Gravity.CENTER
            text = "+"
            textSize = 176f
            setTextColor(Color.BLACK)
            setShadowLayer(18f, 0f, 0f, Color.WHITE)
            contentDescription = "Blinking overdue plus"
        }
        val overdueCounter = TextView(context).apply {
            gravity = Gravity.CENTER
            textSize = 76f
            setTextColor(Color.BLACK)
            setShadowLayer(10f, 0f, 0f, Color.WHITE)
            contentDescription = "Stable full elapsed overdue counter"
        }
        overdueContent.addView(overduePlus, LinearLayout.LayoutParams.MATCH_PARENT, 220)
        overdueContent.addView(overdueCounter, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
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

        fun refresh() {
            val now = platform.nowMillis()
            val time = platform.deviceTimeText(now)
            val separator = time.indexOf(':')
            if (separator >= 0) {
                hour.text = time.substring(0, separator)
                minute.text = time.substring(separator + 1)
            } else {
                hour.text = time
                minute.text = ""
            }
            date.text = DisplayFormatters.dateText(now, platform.deviceZoneId())
            city.text = settings.currentLocation()?.cityLabel ?: context.getString(R.string.display_select_city)
            val timerState = timer.snapshotAt(now).state
            val timerSnapshot = timer.snapshotAt(now)
            timer.advanceAt(now)
            if (timerSnapshot.state == TimerLifecycleState.OVERDUE) {
                mainShell.visibility = View.GONE
                overdueOverlay.visibility = View.VISIBLE
                val activeSlot = timerSnapshot.activePresetSlot ?: TimerPresetSlot.FIRST
                overdueOverlay.setBackgroundColor(Color.parseColor(PresetPresentation.colorHex(activeSlot)))
                overdueCounter.text = DisplayFormatters.elapsedText(timerSnapshot.elapsedMillis)
                overduePlus.alpha = if (OverduePresentation.plusVisibleAt(timerSnapshot.overdueElapsedMillis)) 1f else 0f
            } else {
                mainShell.visibility = View.VISIBLE
                overdueOverlay.visibility = View.GONE
            }
            val isCountdown = timerSnapshot.state == TimerLifecycleState.COUNTDOWN
            countdown.visibility = if (isCountdown) View.VISIBLE else View.GONE
            if (isCountdown) {
                countdown.text = DisplayFormatters.countdownText(timerSnapshot.remainingMillis)
                clockRow.alpha = 0.72f
                hour.textSize = 32f
                colon.textSize = 32f
                minute.textSize = 32f
            } else {
                clockRow.alpha = 1f
                hour.textSize = 132f
                colon.textSize = 132f
                minute.textSize = 132f
            }
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
            colon.alpha = ColonProjection.brightness(mode, now)
            val projection = weather.projection(now)
            forecastMessage.text = forecast.snapshotAt(now).message.orEmpty()
            cards.removeAllViews()
            projection.cards.forEachIndexed { index, cardProjection ->
                val card = weatherCard(
                    context,
                    cardProjection,
                    forecastClick(index, onOpenForecast),
                    settings.settingsPresentationProjection().glassIntensity,
                )
                cards.addView(
                    card,
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                        setMargins(if (index == 0) 0 else 8, 8, 0, 0)
                    },
                )
                card.setOnTouchListener(activeTimerTouchListener)
            }
            val presetStyles = PresetPresentation.styles(timer.presetPresentationAt(now)).associateBy { it.slot }
            presetButtons.forEach { button ->
                val slot = button.tag as TimerPresetSlot
                presetStyles[slot]?.let { applyPresetStyle(button, it) }
            }
        }

        val ticker = object : Runnable {
            override fun run() {
                refresh()
                root.postDelayed(this, 50L)
            }
        }
        root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                view.post(ticker)
            }

            override fun onViewDetachedFromWindow(view: View) {
                view.removeCallbacks(ticker)
            }
        })
        root.post(ticker)
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

    private fun clockText(context: Context): TextView = TextView(context).apply {
        gravity = Gravity.CENTER
        setTextColor(context.getColor(R.color.display_primary))
        textSize = 132f
        setShadowLayer(12f, 0f, 0f, Color.WHITE)
        contentDescription = "Main display clock"
    }

    private fun weatherCard(
        context: Context,
        projection: WeatherCardProjection,
        onClick: (() -> Unit)? = null,
        glassIntensity: Float = 0.45f,
    ): View = FrameLayout(context).apply {
        tag = "weather-card-${projection.slot.name.lowercase()}"
        contentDescription = "Weather card ${projection.slot.name.lowercase()}"
        onClick?.let { setOnClickListener { it() } }
        val material = WeatherCardPresentation.pseudoGlass(glassIntensity)
        background = GradientDrawable().apply {
            setColor(projection.backgroundHex?.let(Color::parseColor) ?: Color.TRANSPARENT)
            setStroke(2, Color.argb(material.lightEdgeAlpha, 255, 255, 255))
            cornerRadius = 18f
        }
        scaleY = if (projection.isTodaySize) 1.04f else 0.98f
        addView(TextView(context).apply {
            text = projection.temperatureText.orEmpty()
            textSize = 28f
            gravity = Gravity.CENTER
            alpha = (0.65f + material.fillAlpha / 255f).coerceAtMost(1f)
            setTextColor(context.getColor(R.color.display_primary))
            contentDescription = "Weather temperature"
        }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(TextView(context).apply {
            text = projection.date.dayOfMonth.toString()
            textSize = 14f
            setTextColor(context.getColor(R.color.display_primary))
            gravity = Gravity.BOTTOM or Gravity.START
            setPadding(10, 0, 0, 8)
            contentDescription = "Weather calendar date"
        }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        repeat(projection.pressureArrowCount.coerceAtMost(2)) { index ->
            addView(TextView(context).apply {
                text = if (projection.pressureDirection == PressureDirection.UP) "↑" else "↓"
                textSize = 42f
                alpha = (0.65f + material.fillAlpha / 255f).coerceAtMost(1f)
                gravity = Gravity.CENTER
                translationX = (index - 0.5f) * 10f
                setTextColor(context.getColor(R.color.display_primary))
                contentDescription = "Pressure trend arrow"
            }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
    }

    private data class ForecastCardView(
        val label: String,
        val temperatureText: String,
        val backgroundHex: String,
        val illustration: WeatherIllustration,
    )

    private fun forecastCard(context: Context, projection: ForecastCardView): View = FrameLayout(context).apply {
        tag = "forecast-card-${projection.label}"
        contentDescription = "Forecast card ${projection.label}"
        val material = WeatherCardPresentation.pseudoGlass(0.45f)
        background = GradientDrawable().apply {
            setColor(Color.parseColor(projection.backgroundHex))
            setStroke(2, context.getColor(R.color.display_card_stroke))
            cornerRadius = 18f
        }
        addView(TextView(context).apply {
            text = WeatherCardPresentation.illustrationText(projection.illustration)
            textSize = 40f
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            setPadding(0, 10, 0, 0)
            setTextColor(context.getColor(R.color.display_primary))
            contentDescription = "Forecast weather illustration ${projection.illustration.name.lowercase()}"
        }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(TextView(context).apply {
            text = projection.temperatureText
            textSize = 28f
            gravity = Gravity.CENTER
            alpha = (0.65f + material.fillAlpha / 255f).coerceAtMost(1f)
            setTextColor(context.getColor(R.color.display_primary))
            contentDescription = "Forecast temperature"
        }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(TextView(context).apply {
            text = projection.label
            textSize = 16f
            setTextColor(context.getColor(R.color.display_primary))
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 10)
            contentDescription = "Forecast card date or time"
        }, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    private fun presetButton(context: Context, style: PresetButtonStyle): Button = Button(context).apply {
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
            setColor(button.context.getColor(R.color.display_button))
            setStroke(if (style.isActive) 7 else 4, Color.parseColor(style.outlineHex))
            cornerRadius = 18f
        }
        button.isActivated = style.isActive
        button.alpha = if (style.isSelected) 1f else 0.88f
    }

    private companion object {
        const val FOUNDATION_TIMER_DURATION_MILLIS = 1_000L
        const val HOLD_GESTURE_MILLIS = 600L
        const val SINGLE_TAP_HINT_MILLIS = 1_500L
        const val SINGLE_TAP_HINT = "Для отмены нажмите дважды"
    }
}
