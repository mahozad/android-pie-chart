package ir.mahozad.android

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import ir.mahozad.android.PieChart.GapPosition
import ir.mahozad.android.PieChart.GapPosition.PRECEDING_SLICE
import ir.mahozad.android.PieChart.GapPosition.SUCCEEDING_SLICE
import ir.mahozad.android.PieChart.IconPlacement
import ir.mahozad.android.PieChart.IconPlacement.*
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal data class Coordinates(val x: Float, val y: Float)

internal data class Boundaries(val top: Float, val left: Float, val right: Float, val bottom: Float)

private val bounds = Rect()
private val paint = Paint()

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

internal fun calculateBoundaries(origin: Coordinates, radius: Float): Boundaries {
    val top = origin.y - radius
    val left = origin.x - radius
    val right = origin.x + radius
    val bottom = origin.y + radius
    return Boundaries(top, left, right, bottom)
}

private fun Float.toRadian() = (this / 360) * 2 * PI.toFloat()

internal fun calculateGapCoordinates(
    origin: Coordinates,
    angle: Float,
    gapWidth: Float,
    gapLength: Float,
    placement: GapPosition
): Array<Coordinates> {

    fun makeCorner(angleShift: Int, oldCorner: Coordinates, distance: Float): Coordinates {
        val newAngle = (angle + angleShift).toRadian()
        val newX = oldCorner.x + distance * cos(newAngle)
        val newY = oldCorner.y + distance * sin(newAngle)
        return Coordinates(newX, newY)
    }

    val initialDistance = when (placement) {
        PRECEDING_SLICE  -> 0f
        SUCCEEDING_SLICE -> gapWidth
        else             -> gapWidth / 2
    }

    val c1 = makeCorner(90, origin, initialDistance)
    val c2 = makeCorner(0, c1, gapLength)
    val c3 = makeCorner(-90, c2, gapWidth)
    val c4 = makeCorner(-180, c3, gapLength)
    return arrayOf(c1, c2, c3, c4)
}

// TODO: Move to PaintUtil or another appropriate file
internal fun updatePaintForLabel(paint: Paint, labelSize: Float, @ColorInt labelColor: Int): Paint {
    paint.color = labelColor
    paint.shader = null
    paint.textSize = labelSize
    paint.textAlign = Paint.Align.CENTER
    return paint
}

// For help on text dimensions see https://stackoverflow.com/a/42091739
internal fun calculateLabelCoordinates(
    startAngle: Float,
    sliceSweep: Float,
    labelOffset: Float,
    iconWidth: Float,
    iconMargin: Float,
    iconPlacement: IconPlacement,
    label: String,
    labelPaint: Paint,
    origin: Coordinates,
    radius: Float
): Coordinates {
    val shiftDirection = getDirection(iconPlacement)
    labelPaint.getTextBounds(label, 0, label.length, bounds)
    val textHeight = bounds.height()
    val endAngle = (startAngle + sliceSweep) % 360
    val middleAngle = ((startAngle + endAngle) / 2 % 360).toRadian()
    val xShift = if (iconWidth == 0f) 0f else (iconWidth + iconMargin) / 2
    val x = origin.x + cos(middleAngle) * radius * labelOffset + xShift * shiftDirection
    val y = (origin.y + sin((middleAngle)) * radius * labelOffset) + (textHeight / 2)
    return Coordinates(x, y)
}

internal fun calculateLabelIconWidth(icon: Drawable?, desiredHeight: Float): Float {
    if (icon == null) return 0f
    val aspectRatio = icon.intrinsicWidth.toFloat() / icon.intrinsicHeight
    return desiredHeight * aspectRatio
}

internal fun calculateLabelBounds(label: String, labelPaint: Paint): Rect {
    labelPaint.getTextBounds(label, 0, label.length, bounds)
    return bounds
}

internal fun calculateLabelIconBounds(
    targetCoordinates: Coordinates,
    labelBounds: Rect,
    iconWidth: Float,
    iconHeight: Float,
    iconMargin: Float,
    iconPlacement: IconPlacement
): Rect {
    val direction = getDirection(iconPlacement)
    val labelWidth = labelBounds.width()
    val labelHeight = labelBounds.height()
    val start = (targetCoordinates.x - (labelWidth / 2 + iconMargin) * direction).toInt()
    val end = (start - iconWidth * direction).toInt()
    val iconLeft = if (start < end) start else end
    val iconRight = if (start > end) start else end
    val excess = iconHeight - labelHeight
    val iconTop = (targetCoordinates.y + (labelBounds.top - excess / 2f)).toInt()
    val iconBottom = (iconTop + iconHeight).toInt()
    bounds.set(iconLeft, iconTop, iconRight, iconBottom)
    return bounds
}

/**
 * Returns *1* for left and *-1* for right.
 */
private fun getDirection(iconPlacement: IconPlacement): Int {
    val locale = Locale.getDefault()
    val isLeftToRight = TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_LTR
    val isRightToLeft = !isLeftToRight
    return when {
        iconPlacement == LEFT -> 1
        iconPlacement == START && isLeftToRight -> 1
        iconPlacement == END && isRightToLeft -> 1
        else -> -1
    }
}
