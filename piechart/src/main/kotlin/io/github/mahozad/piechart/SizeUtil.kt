package io.github.mahozad.piechart

import android.util.Log
import android.view.View
import io.github.mahozad.piechart.PieChart.GapPosition
import io.github.mahozad.piechart.PieChart.GapPosition.PRECEDING_SLICE
import io.github.mahozad.piechart.PieChart.GapPosition.SUCCEEDING_SLICE
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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

private fun Float.toRadian() = (this / 360) * 2 * PI.toFloat()

internal fun calculateGapCoordinates(
    originX: Float,
    originY: Float,
    angle: Float,
    gapWidth: Float,
    gapLength: Float,
    placement: GapPosition
): Array<Coordinates> {

    fun makeNextCorner(angleShift: Int, oldX: Float, oldY: Float, distance: Float): Coordinates {
        val newAngle = (angle + angleShift).toRadian()
        val newX = oldX + distance * cos(newAngle)
        val newY = oldY + distance * sin(newAngle)
        return Coordinates(newX, newY)
    }

    val c1 = when (placement) {
        PRECEDING_SLICE -> makeNextCorner(90, originX, originY, 0f)
        SUCCEEDING_SLICE -> makeNextCorner(90, originX, originY, gapWidth)
        else -> makeNextCorner(90, originX, originY, gapWidth / 2)
    }
    val c2 = makeNextCorner(0, c1.x, c1.y, gapLength)
    val c3 = makeNextCorner(-90, c2.x, c2.y, gapWidth)
    val c4 = makeNextCorner(-180, c3.x, c3.y, gapLength)
    // In order: bottom-right, top-right, top-left, bottom-left corner
    return arrayOf(c1, c2, c3, c4)
}
