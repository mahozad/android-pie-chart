package ir.mahozad.android.util

import android.content.res.Resources
import android.content.res.TypedArray
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
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

internal fun getIconTint(typedArray: TypedArray, @StyleableRes attrName: Int): Int? {
    // Do not use -1 as no color; -1 is white: https://stackoverflow.com/a/30430194
    val tint = typedArray.getColor(attrName, /* if user specified no value or @null */ Int.MAX_VALUE)
    return if (tint == Int.MAX_VALUE) null else tint
}

@ColorInt
internal fun getColorArray(typedArray: TypedArray, resources: Resources, @StyleableRes attrName: Int): IntArray? {
    val arrayId = typedArray.getResourceId(attrName, -1)
    if (arrayId != -1) {
        // If the attribute value is a reference to a resource...
        try {
            return resources.getIntArray(arrayId)
        } catch (e: Resources.NotFoundException) {
            // If it didn't reference a color array, it is a reference to a color
            val color = getIconTint(typedArray, attrName)
            return when {
                color != null -> intArrayOf(color)
                else -> null
            }
        }
    } else {
        // If the attribute value is a literal color value...
        val color = getIconTint(typedArray, attrName)
        return when {
            color != null -> intArrayOf(color)
            else -> null
        }
    }
}
