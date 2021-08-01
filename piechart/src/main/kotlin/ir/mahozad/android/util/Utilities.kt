package ir.mahozad.android.util

import ir.mahozad.android.PieChart
import ir.mahozad.android.PieChart.LegendPosition.*
import ir.mahozad.android.component.Paddings

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
    if (legendPosition == TOP || legendPosition == BOTTOM) {
        pieHeight = pieHeight - legendBoxHeight - legendBoxMargin
    } else if (legendPosition == START || legendPosition == END) {
        pieWidth = pieWidth - legendBoxWidth - legendBoxMargin
    }
    return Pair(pieWidth, pieHeight)
}

internal fun IntArray?.getElementCircular(index: Int): Int? {
    return this?.toTypedArray().getElementCircular(index)
}

internal fun <T> Array<T>?.getElementCircular(index: Int) = when {
    this == null || isEmpty() -> null
    else -> this[index % size]
}
