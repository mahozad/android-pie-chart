package ir.mahozad.android

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.view.View
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import ir.mahozad.android.unit.Dimension
import kotlin.math.PI

internal fun View.spToPx(sp: Float) = sp * resources.displayMetrics.scaledDensity

internal fun View.pxToSp(px: Float) = px / resources.displayMetrics.scaledDensity

internal fun View.dpToPx(dp: Float) = dp * resources.displayMetrics.density

internal fun View.pxToDp(px: Float) = px / resources.displayMetrics.density

inline val Int.dp: Dimension get() = toFloat().dp
inline val Int.sp: Dimension get() = toFloat().sp
inline val Int.px: Dimension get() = toFloat().px
inline val Double.dp: Dimension get() = toFloat().dp
inline val Double.sp: Dimension get() = toFloat().sp
inline val Double.px: Dimension get() = toFloat().px
inline val Float.dp: Dimension get() = Dimension.DP(this)
inline val Float.sp: Dimension get() = Dimension.SP(this)
inline val Float.px: Dimension get() = Dimension.PX(this)

/**
 * Converts degree to radian.
 */
internal fun Float.toRadian() = (this / 360) * 2 * PI.toFloat()

internal fun Float.toDegrees() = (this * 360) / 2 * PI.toFloat()

internal infix fun Float.until(that: Float) = this.rangeTo(that - 1E-10f)

/**
 * See https://stackoverflow.com/a/58815613
 */
internal inline fun <reified T : Enum<T>> TypedArray.getEnum(@StyleableRes index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }

/**
 * Or
 * ```
 * val fontId = getResourceId(font, -1)
 * return if (fontId == -1) defaultFont else ResourcesCompat.getFont(context, fontId)!!
 * ```
 */
internal fun TypedArray.getFont(context: Context, @StyleableRes font: Int, default: Typeface) =
    getResourceId(font, -1).let { if (it >= 0) ResourcesCompat.getFont(context, it)!! else default }
