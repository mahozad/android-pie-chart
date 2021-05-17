package io.github.mahozad.piechart

import android.util.Log
import android.view.View
import kotlin.math.min

internal data class Coordinates(val x: Float, val y: Float)

internal data class Boundaries(val top: Float, val left: Float, val right: Float, val bottom: Float)

/**
 * Extracted the calculation logics to a separate class and function to be testable.
 */
internal fun calculateWidthAndHeight(
    widthMeasureSpec: Int,
    heightMeasureSpec: Int
): Pair<Int, Int> {
    val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
    val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
    val min = min(widthSize, heightSize)
    Log.d("SizeUtil", "width: ${View.MeasureSpec.toString(widthMeasureSpec)}")
    Log.d("SizeUtil", "height: ${View.MeasureSpec.toString(heightMeasureSpec)}")
    Log.d("SizeUtil", "min: $min")
    return Pair(min, min)
}

/**
 * Note that this function works with paddingLeft and PaddingRight
 *  not paddingStart and paddingEnd.
 */
internal fun calculateCenter(
    width: Int,
    height: Int,
    paddingLeft: Int,
    paddingRight: Int,
    paddingTop: Int,
    paddingBottom: Int
): Coordinates {
    val centerX = (width + paddingLeft - paddingRight) / 2f
    val centerY = (height + paddingTop - paddingBottom) / 2f
    return Coordinates(centerX, centerY)
}

internal fun calculateRadius(
    width: Int,
    height: Int,
    paddingLeft: Int,
    paddingRight: Int,
    paddingTop: Int,
    paddingBottom: Int
): Float {
    val availableWidth = width - (paddingLeft + paddingRight)
    val availableHeight = height - (paddingTop + paddingBottom)
    return min(availableWidth, availableHeight) / 2f
}

internal fun calculateBoundaries(centerX: Float, centerY: Float, radius: Float): Boundaries {
    val top = centerY - radius
    val left = centerX - radius
    val right = centerX + radius
    val bottom = centerY + radius
    return Boundaries(top, left, right, bottom)
}
