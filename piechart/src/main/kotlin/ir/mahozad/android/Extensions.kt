package ir.mahozad.android

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
