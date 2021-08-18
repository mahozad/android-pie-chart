package ir.mahozad.android.util

import android.content.res.Resources
import android.content.res.TypedArray
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import ir.mahozad.android.PieChart
import ir.mahozad.android.PieChart.LegendPosition.*
import ir.mahozad.android.component.Paddings
import ir.mahozad.android.dp
import ir.mahozad.android.px
import ir.mahozad.android.sp
import ir.mahozad.android.unit.Dimension

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

@ColorInt
internal fun getIconTint(typedArray: TypedArray, @StyleableRes attrName: Int): Int? {
    // Do not use -1 as no color; -1 is white: https://stackoverflow.com/a/30430194
    val tint = typedArray.getColor(attrName, /* if user specified no value or @null */ Int.MAX_VALUE)
    return if (tint == Int.MAX_VALUE) null else tint
}

/**
 * Gets an attribute of type *reference|color*.
 *
 * The attribute can be a reference to a color array, a reference to a color, or a color literal.
 */
@ColorInt
internal fun getColorArray(typedArray: TypedArray, @StyleableRes attrName: Int): IntArray? {
    return try {
        val arrayId = typedArray.getResourceId(attrName, -1)
        typedArray.resources.getIntArray(arrayId)
    } catch (e: Resources.NotFoundException) {
        /* It was not an array; try as a single color */
        val color = getIconTint(typedArray, attrName)
        if (color != null) intArrayOf(color) else null
    }
}

internal fun parseBorderDashArray(string: String?): List<Dimension>? {
    if (string == null || string.isBlank()) return null
    return string
        .replace(Regex("""[,;]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .split(" ")
        .map {
            if (it.endsWith("dp")) {
                it.removeSuffix("dp").toFloat().dp
            } else if (it.endsWith("px")) {
                it.removeSuffix("px").toFloat().px
            } else if (it.endsWith("sp")) {
                it.removeSuffix("sp").toFloat().sp
            } else throw IllegalArgumentException("The specified dash array string is not valid.")
        }
}

/**
 * Gets an attribute of type *reference|string*.
 *
 * See https://developer.android.com/guide/topics/resources/more-resources#TypedArray.
 *
 * The attribute can be a reference to a dimension array,
 * a reference to a string with the required format, or a string literal with the required format.
 */
// internal fun getDimensionArray(context: Context, @ArrayRes resId: Int): Array<Dimension>? {
//     return try {
//         // val arrayId = typedArray.getResourceId(attrName, -1)
//         // val joinedDimensions = typedArray.resources.getIntArray(arrayId).joinToString()
//         // parseBorderDashArray(joinedDimensions)?.toTypedArray()
//
//         /* OR */
//
//         val dimens = context.resources.obtainTypedArray(resId)
//         dimens.use {
//             val dimenStrings = mutableListOf<String>()
//             val entryCount = it.length()
//             for (i in 0 until entryCount) {
//                 val dimen = it.getDimension(i, 0f)
//                 dimenStrings.add(dimen.toString())
//             }
//             return parseBorderDashArray(dimenStrings.joinToString()).toTypedArray()
//         }
//     } catch (e: Resources.NotFoundException) {
//         /* It was not an array; try as a string literal */
//         val string = typedArray.getString(attrName)
//         val dimensions = parseBorderDashArray(string)
//         dimensions?.toTypedArray()
//     }
// }
