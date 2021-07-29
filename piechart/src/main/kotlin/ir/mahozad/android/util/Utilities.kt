package ir.mahozad.android.util

import ir.mahozad.android.PieChart
import ir.mahozad.android.PieChart.LegendPosition.*
import ir.mahozad.android.component.Paddings
import kotlin.math.min

internal fun parseBorderDashArray(string: String?): List<Float> {
    if (string == null || string.isBlank()) return emptyList()
    return string
        .replace(Regex("""[,;]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .split(" ")
        .map { it.toFloat() }
}

internal fun calculatePieDimensions(
    viewWidth: Int,
    viewHeight: Int,
    viewPaddings: Paddings,
    isLegendEnabled: Boolean,
    legendBoxMargin: Float,
    legendPosition: PieChart.LegendPosition,
    legendBoxWidth: Float,
    legendBoxHeight: Float
): Pair<Float, Float> {
    var pieWidth = viewWidth.toFloat() - viewPaddings.horizontal
    var pieHeight = viewHeight.toFloat() - viewPaddings.vertical
    if (!isLegendEnabled) return Pair(pieWidth, pieHeight)
    val legendsRectHeight: Float
    val legendsRectWidth: Float
    if (legendPosition == TOP || legendPosition == BOTTOM) {
        val maxAvailableHeight = (viewHeight - viewPaddings.vertical) / 2f // Arbitrary
        legendsRectHeight = min(maxAvailableHeight, legendBoxHeight)
        pieHeight = pieHeight - legendsRectHeight - legendBoxMargin
    } else if (legendPosition == START || legendPosition == END) {
        val maxAvailableWidth = (viewWidth - viewPaddings.horizontal) / 2f // Arbitrary
        legendsRectWidth = min(maxAvailableWidth, legendBoxWidth)
        pieWidth = pieWidth - legendsRectWidth - legendBoxMargin
    }
    return Pair(pieWidth, pieHeight)
}
