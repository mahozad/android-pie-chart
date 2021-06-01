package ir.mahozad.android

import android.graphics.*
import android.graphics.Paint.Align.CENTER
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import ir.mahozad.android.PieChart.*
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.PieChart.DrawDirection.COUNTER_CLOCKWISE
import ir.mahozad.android.PieChart.GapPosition.PRECEDING_SLICE
import ir.mahozad.android.PieChart.GapPosition.SUCCEEDING_SLICE
import ir.mahozad.android.PieChart.IconPlacement.*
import java.util.*
import kotlin.math.*

internal data class Size(val width: Float, val height: Float)

internal data class Coordinates(val x: Float, val y: Float)

internal data class Boundaries(val top: Float, val left: Float, val right: Float, val bottom: Float)

private val boundsF = RectF()
private val bounds = Rect()
private val paint = Paint()
private val path = Path()

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
internal fun updatePaintForLabel(paint: Paint, size: Float, @ColorInt color: Int, font: Typeface): Paint {
    paint.color = color
    paint.shader = null
    paint.typeface = font
    paint.textSize = size
    paint.textAlign = CENTER
    return paint
}

// For help on text dimensions see https://stackoverflow.com/a/42091739
internal fun calculateLabelCoordinates(
    angle: Float,
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
    val xShift = if (iconWidth == 0f) 0f else (iconWidth + iconMargin) / 2
    val x = origin.x + cos(angle.toRadian()) * radius * labelOffset + xShift * shiftDirection
    val y = (origin.y + sin((angle.toRadian())) * radius * labelOffset) + (textHeight / 2)
    return Coordinates(x, y)
}

internal fun calculateLabelIconWidth(icon: Drawable?, desiredHeight: Float): Float {
    if (icon == null) return 0f
    val aspectRatio = icon.intrinsicWidth.toFloat() / icon.intrinsicHeight
    return desiredHeight * aspectRatio
}

/**
 * TODO: Needs unit tests
 */
internal fun calculateIconBounds(icon: Drawable, iconHeight: Float): RectF {
    val iconWidth = calculateLabelIconWidth(icon, iconHeight)
    boundsF.set(0f, 0f, iconWidth, iconHeight)
    return boundsF
}

/**
 * Margin can be negative too. When icon size is zero, margin will have no effect.
 */
internal fun calculateLabelAndIconCombinedBounds(
    labelBounds: Rect,
    iconBounds: RectF,
    iconMargin: Float,
    iconPlacement: IconPlacement
): RectF {
    val width: Float
    val height: Float
    val adjustedMargin =
        if (iconBounds.width() == 0f || iconBounds.height() == 0f) 0f else iconMargin
    if (iconPlacement == TOP || iconPlacement == BOTTOM) {
        width = max(labelBounds.width().toFloat(), iconBounds.width())
        height = labelBounds.height() + adjustedMargin + iconBounds.height()
    } else {
        width = labelBounds.width() + adjustedMargin + iconBounds.width()
        height = max(labelBounds.height().toFloat(), iconBounds.height())
    }
    return RectF(0f, 0f, width, height)
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

/**
 * Assumes that the width and height of the currentBounds are equal.
 *
 * The label margin is calculated in angular mode (added to the radius).
 */
internal fun calculatePieNewBounds(
    currentBounds: RectF,
    slices: List<Slice>,
    shouldOppositeMarginsBeSymmetric: Boolean,
    labelMargin: Float,
    drawDirection: DrawDirection,
    startAngle: Int,
    labelsSize: Float,
    labelsFont: Typeface
): RectF {
    var maxTopExcess = 0f
    var maxLeftExcess = 0f
    var maxRightExcess = 0f
    var maxBottomExcess = 0f
    val currentCenter = Coordinates((currentBounds.right + currentBounds.left) / 2f, (currentBounds.bottom + currentBounds.top) / 2f)
    val currentRadius = currentBounds.width() / 2f

    var currentAngle = normalizeAngle(startAngle.toFloat())
    for (slice in slices) {
        updatePaintForLabel(paint, slice.labelSize ?: labelsSize, Color.WHITE, slice.labelFont ?: labelsFont)
        val middleAngle = calculateMiddleAngle(currentAngle, slice.fraction, drawDirection)
        val (textWidth, textHeight) = calculateTextSize(slice.label)
        val (x, y) = calculateCoordinatesForOutsideLabel(slice.label, middleAngle, currentCenter, currentRadius, labelMargin)

            var topExcess = currentBounds.top - (y + paint.ascent())
            if (textHeight == 0f) topExcess = 0f
            maxTopExcess = max(maxTopExcess, topExcess)
            var leftExcess = currentBounds.left - (x - textWidth / 2f)
            if (textWidth == 0f) leftExcess = 0f
            maxLeftExcess = max(maxLeftExcess, leftExcess)
            var rightExcess = (x + textWidth / 2f) - currentBounds.right
            if (textWidth == 0f) rightExcess = 0f
            maxRightExcess = max(maxRightExcess, rightExcess)
            var bottomExcess = (y + paint.descent()) - currentBounds.bottom
            if (textHeight == 0f) bottomExcess = 0f
            maxBottomExcess = max(maxBottomExcess, bottomExcess)

        currentAngle = calculateEndAngle(currentAngle, slice.fraction, drawDirection)
    }

    if (shouldOppositeMarginsBeSymmetric) {
        val maxHorizontalExcess = max(maxLeftExcess, maxRightExcess)
        val maxVerticalExcess = max(maxTopExcess, maxBottomExcess)
        maxTopExcess = maxVerticalExcess
        maxLeftExcess = maxHorizontalExcess
        maxRightExcess = maxHorizontalExcess
        maxBottomExcess = maxVerticalExcess
    }

    val width = (currentBounds.right - maxRightExcess) - (currentBounds.left + maxLeftExcess)
    val height = (currentBounds.bottom - maxBottomExcess) - (currentBounds.top + maxTopExcess)
    if (height > width) {
        val excess = height - width
        maxTopExcess += excess / 2f
        maxBottomExcess += excess / 2f
    } else if (width > height) {
        val excess = width - height
        maxLeftExcess += excess / 2f
        maxRightExcess += excess / 2f
    }

    return RectF(currentBounds.left + maxLeftExcess, currentBounds.top + maxTopExcess, currentBounds.right - maxRightExcess, currentBounds.bottom - maxBottomExcess)
}

private fun calculateTextSize(text: String): Size {
    val textWidth = paint.measureText(text)
    val textHeight = paint.descent() - paint.ascent()
    val isAnyOneZero = (textWidth * textHeight) == 0f
    return if (isAnyOneZero) Size(0f, 0f) else Size(textWidth, textHeight)
}

internal fun calculateMiddleAngle(
    startAngle: Float,
    fraction: Float,
    direction: DrawDirection
): Float {
    return calculateEndAngle(startAngle, fraction / 2, direction)
}

internal fun calculateEndAngle(
    startAngle: Float,
    fraction: Float,
    direction: DrawDirection
): Float {
    val normalizedStartAngle = normalizeAngle(startAngle)
    val sweep = calculateSweep(fraction, direction)
    return normalizeAngle(normalizedStartAngle + sweep)
}

private fun calculateSweep(fraction: Float, direction: DrawDirection): Float {
    val sweep  = fraction * 360
    return if (direction == CLOCKWISE) sweep else -sweep
}

/**
 * Returns the distance between the two angles.
 *
 * The input angles should be [normalized][normalizeAngle] in range [0º..360º).
 *
 * To get the absolute distance between the angles, well, get the absolute value of the result:
 * ```kotlin
 * calculateAngleDistance(...).absoluteValue
 * ```
 * or
 * ```kotlin
 * abs(calculateAngleDistance(...))
 * ```
 */
internal fun calculateAnglesDistance(
    startAngle: Float,
    endAngle: Float,
    direction: DrawDirection
): Float {
    val difference = endAngle - startAngle
    return when {
        direction == CLOCKWISE && difference < 0 -> difference + 360
        direction == COUNTER_CLOCKWISE && difference >= 0 -> difference - 360
        else -> difference
    }
}

private fun calculateCoordinatesOnCircumference(angle:Float, center: Coordinates, radius: Float) : Coordinates {
    val x = center.x + radius * cos(angle.toRadian())
    val y = center.y + radius * sin(angle.toRadian())
    return Coordinates(x, y)
}

/**
 * Converts the angle to its positive equivalent and
 * will reduce the magnitude to less than a full circle;
 * in other words, the returned angle will be in range [0º..360º).
 *
 * See [this web page](https://rosettacode.org/wiki/Angles_(geometric),_normalization_and_conversion).
 */
internal fun normalizeAngle(angle: Float): Float {
    val normalized = angle % 360
    return if (normalized < 0) normalized + 360 else normalized
}

internal fun normalizeAngle(angle: Int) = normalizeAngle(angle.toFloat()).toInt()

/**
 * For formulae and calculation details refer to [this post](https://math.stackexchange.com/q/4152307).
 *
 * Note that `paint.getTextBounds` returns the exact height of the **specified text**
 *  whereas `paint.descent - paint.ascent` returns the total height of the current font.
 *  We work with the latter.
 *
 * See [this post](https://stackoverflow.com/q/11120392) and [this post](https://stackoverflow.com/q/4909367)
 * and [this post](https://stackoverflow.com/q/3654321)
 *
 * For difference between `Paint::getTextBounds` and `Paint::measureText` see [this post](https://stackoverflow.com/a/7579469) and [this post](https://stackoverflow.com/q/3257293)
 */
internal fun calculateCoordinatesForOutsideLabel(
    label: String,
    angle: Float,
    center: Coordinates,
    pieRadius: Float,
    labelMargin: Float
): Coordinates {
    val normalizedAngle = normalizeAngle(angle)
    var θ = normalizedAngle.toRadian()
    var (w, h) = calculateTextSize(label)
    val r = pieRadius

    // When drawing the text on Canvas, the y is used for text descent and not
    // its bottom or vertical center. So, make the y the center of the whole box
    val verticalShift = h / 2f - paint.descent()

    // First, convert the theta to the first quadrant (i.e. in range 0..90)
    // because our formula works on the first quadrant
    when (normalizedAngle) {
        in 90f..180f -> θ = PI.toFloat() - θ
        in 180f..270f -> θ = θ - PI.toFloat()
        in 270f..360f -> θ = (2 * PI.toFloat() - θ) % (2 * PI.toFloat())
    }

    when (θ) {
        in 0f until asin(h / 2 / r) -> h = 2 * sin(θ)
        in asin(h / 2 / r)..acos(w / 2 / r) -> { /* Do not modify anything */ }
        else -> w = 2 * cos(θ)
    }
    var a = k(θ, w / r, h / r) * r * cos(θ)
    var b = k(θ, w / r, h / r) * r * sin(θ)

    // Convert the shifts back to the angle quadrant
    when (normalizedAngle) {
        in 90f..180f -> a = -a
        in 270f..360f -> b = -b
        in 180f..270f -> {
            a = -a
            b = -b
        }
    }

    // Fix the bug when angle=90
    if (angle == 90f) b = h / 2

    val (x, y) = calculateCoordinatesOnCircumference(angle, center, pieRadius + labelMargin)
    return Coordinates(x + a, y + verticalShift + b)
}

private fun k(θ: Float, w: Float, h: Float): Float {
    return (1 / 2f) * (
            -2 + h * sin(θ) + cos(θ) *
                    (w + sqrt(4 - h.pow(2) + 2 * h * w * tan(θ) - (w.pow(2) - 4) * tan(θ).pow(2)))
            )
}

internal fun makeSlice(
    center: Coordinates,
    pieEnclosingRect: RectF,
    sliceStartAngle: Float,
    sliceFraction: Float,
    drawDirection: DrawDirection,
    pointer: SlicePointer?
): Path {
    val sliceSweep = calculateSweep(sliceFraction, drawDirection)
    path.reset()
    path.moveTo(center.x, center.y)
    if (pointer == null) {
        path.arcTo(pieEnclosingRect, sliceStartAngle, sliceSweep)
    } else {
        val radiusReduction = pointer.length
        val newEnclosingRect = RectF(
            pieEnclosingRect.left + radiusReduction,
            pieEnclosingRect.top + radiusReduction,
            pieEnclosingRect.right - radiusReduction,
            pieEnclosingRect.bottom - radiusReduction
        )
        val sliceMiddleAngle = calculateMiddleAngle(sliceStartAngle, sliceFraction, drawDirection)
        val newRadius = newEnclosingRect.width() / 2f
        val pointerFraction = pointer.width / (2 * PI * newRadius).toFloat()
        val stop1Angle = calculateMiddleAngle(sliceMiddleAngle, -pointerFraction, drawDirection)
        val stop2Angle = calculateMiddleAngle(sliceMiddleAngle, pointerFraction, drawDirection)
        val stopsSweepAngle = calculateAnglesDistance(sliceStartAngle, stop1Angle, drawDirection)
        val stop2Coordinates = calculateCoordinatesOnCircumference(stop2Angle, center, newRadius)
        val (tipX, tipY) = calculateCoordinatesOnCircumference(sliceMiddleAngle, center, newRadius + pointer.length)
        path.arcTo(newEnclosingRect, sliceStartAngle, stopsSweepAngle)
        path.lineTo(tipX, tipY)
        path.lineTo(stop2Coordinates.x, stop2Coordinates.y)
        path.arcTo(newEnclosingRect, stop2Angle, stopsSweepAngle)
    }
    path.close()
    return path
}
