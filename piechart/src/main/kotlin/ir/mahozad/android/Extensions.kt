package ir.mahozad.android

import android.content.res.TypedArray
import android.view.View
import kotlin.math.PI

internal fun View.spToPx(sp: Float) = sp * resources.displayMetrics.scaledDensity

internal fun View.pxToSp(px: Float) = px / resources.displayMetrics.scaledDensity

internal fun View.dpToPx(dp: Float) = dp * resources.displayMetrics.density

internal fun View.pxToDp(px: Float) = px / resources.displayMetrics.density

/**
 * Converts degree to radian.
 */
internal fun Float.toRadian() = (this / 360) * 2 * PI.toFloat()

internal fun Float.toDegrees() = (this * 360) / 2 * PI.toFloat()

internal infix fun Float.until(that : Float) = this.rangeTo(that - 1E-10f)

/**
 * See https://stackoverflow.com/a/58815613
 */
internal inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }
