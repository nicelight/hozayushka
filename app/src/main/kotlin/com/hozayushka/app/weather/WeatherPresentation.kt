package com.hozayushka.app.weather

data class PseudoGlassMaterial(
    val intensity: Float,
    val fillAlpha: Int,
    val lightEdgeAlpha: Int,
    val darkEdgeAlpha: Int,
    val isStatic: Boolean = true,
)

object WeatherCardPresentation {
    fun pseudoGlass(intensity: Float): PseudoGlassMaterial {
        val value = intensity.coerceIn(0f, 1f)
        return PseudoGlassMaterial(
            intensity = value,
            fillAlpha = (value * 92f).toInt(),
            lightEdgeAlpha = (value * 180f).toInt(),
            darkEdgeAlpha = (value * 150f).toInt(),
        )
    }

    fun illustrationText(illustration: WeatherIllustration): String = when (illustration) {
        WeatherIllustration.CLEAR -> "☀"
        WeatherIllustration.CLOUD,
        WeatherIllustration.NEUTRAL_CLOUD,
        -> "☁"
        WeatherIllustration.RAIN -> "☂"
        WeatherIllustration.SNOW -> "❄"
        WeatherIllustration.MOON -> "☾"
    }
}

/** Explicit compile-time Windy-derived table for every integer temperature -30..+47. */
object TemperaturePalette {
    private val entries = listOf(
        "#9653A4", "#965CA8", "#9665AB", "#966EAF", "#9778B3", "#9781B7",
        "#978ABA", "#9793BE", "#979CC2", "#97A5C5", "#97AEC9", "#98B8CD",
        "#98C1D1", "#98CAD4", "#98D3D8", "#94CFD6", "#8FCBD3", "#8BC6D1",
        "#87C2CF", "#82BECC", "#7EBACA", "#7AB5C8", "#76B1C6", "#71ADC3",
        "#6DA9C1", "#69A4BF", "#64A0BC", "#609CBA", "#5A92B3", "#5588AD",
        "#4F7EA6", "#47884D", "#448153", "#478750", "#5D8E30", "#5D8E2F",
        "#5D8D2E", "#6A9026", "#77921F", "#849517", "#798D18", "#839115",
        "#899514", "#979810", "#A79F0E", "#B2A30D", "#BAA60C", "#A39D0E",
        "#CAA808", "#D9AD07", "#E7B105", "#E4B105", "#E7B205", "#E49E04",
        "#DE8E06", "#D87D07", "#DA7908", "#DC7409", "#E76C0E", "#E68E06",
        "#DE5416", "#E05217", "#E15018", "#D34B15", "#CA4714", "#C04213",
        "#C84614", "#BA4012", "#AB390F", "#9D330D", "#932F0C", "#882A0A",
        "#7E2609", "#732107", "#5C1703", "#5E1804", "#541402", "#4A0F00",
    )

    val all: List<String> = entries

    fun colorFor(temperatureCelsius: Int): String =
        entries[(temperatureCelsius.coerceIn(-30, 47)) + 30]
}
