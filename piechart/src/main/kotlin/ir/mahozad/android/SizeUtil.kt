package ir.mahozad.android

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align.CENTER
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Dimension.PX
import androidx.annotation.FloatRange
import ir.mahozad.android.PieChart.*
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.PieChart.DrawDirection.COUNTER_CLOCKWISE
import ir.mahozad.android.PieChart.GapPosition.PRECEDING_SLICE
import ir.mahozad.android.PieChart.GapPosition.SUCCEEDING_SLICE
import ir.mahozad.android.PieChart.IconPlacement.*
import ir.mahozad.android.labels.LabelProperties
import ir.mahozad.android.labels.SliceProperties
import java.util.*
import kotlin.math.*

internal data class Size(val width: Float, val height: Float)

internal data class Coordinates(val x: Float, val y: Float)

internal data class Defaults(
    val outsideLabelsMargin: Float,
    val labelsSize: Float,
    val labelsColor: Int,
    val labelsFont: Typeface,
    val labelIconsHeight: Float,
    val labelIconsMargin: Float,
    val labelIconsPlacement: IconPlacement
)

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

internal fun calculateRadius(
    width: Float,
    height: Float
): Float {
    return min(width, height) / 2f
}

internal fun calculateBounds(origin: Coordinates, radius: Float): Bounds {
    val top = origin.y - radius
    val left = origin.x - radius
    val right = origin.x + radius
    val bottom = origin.y + radius
    return Bounds(top, left, right, bottom)
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
internal fun updatePaintForLabel(
    paint: Paint,
    @Dimension(unit = PX) size: Float,
    @ColorInt color: Int,
    font: Typeface,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f
): Paint {
    paint.color = color
    paint.shader = null
    paint.typeface = font
    paint.textSize = size
    paint.textAlign = CENTER
    paint.alpha = (alpha * 255).toInt()
    return paint
}

internal fun calculateLabelIconWidth(icon: Drawable?, desiredHeight: Float): Float {
    if (icon == null) return 0f
    val aspectRatio = icon.intrinsicWidth.toFloat() / icon.intrinsicHeight
    return desiredHeight * aspectRatio
}

internal fun calculateIconBounds(icon: Drawable?, iconHeight: Float): RectF {
    val iconWidth = calculateLabelIconWidth(icon, iconHeight)
    boundsF.set(0f, 0f, iconWidth, if (iconWidth == 0f) 0f else iconHeight)
    return boundsF
}

/**
 * Margin can be negative too. When label size or icon size or both are zero,
 * the margin will be ignored (i.e. it will be treated as zero).
 */
internal fun calculateLabelAndIconCombinedBounds(
    labelBounds: RectF,
    iconBounds: RectF,
    iconMargin: Float,
    iconPlacement: IconPlacement
): RectF {
    val width: Float
    val height: Float
    val adjustedMargin = modulateMargin(iconMargin, labelBounds, iconBounds)
    if (iconPlacement == TOP || iconPlacement == BOTTOM) {
        width = max(labelBounds.width(), iconBounds.width())
        height = labelBounds.height() + adjustedMargin + iconBounds.height()
    } else {
        width = labelBounds.width() + adjustedMargin + iconBounds.width()
        height = max(labelBounds.height(), iconBounds.height())
    }
    return RectF(0f, 0f, width, height)
}

private fun modulateMargin(margin: Float, labelBounds: RectF, iconBounds: RectF): Float {
    val isAnyOneZero =
        labelBounds.width() * labelBounds.height() * iconBounds.width() * iconBounds.height() == 0f
    return if (isAnyOneZero) 0f else margin
}

/**
 * For help on text dimensions see [this post](https://stackoverflow.com/a/42091739)
 */
internal fun calculateLabelBounds(label: String, labelPaint: Paint): RectF {
    // labelPaint.getTextBounds(label, 0, label.length, bounds)
    // return RectF(bounds)

    val (textWidth, textHeight) = calculateTextSize(label, labelPaint)
    return RectF(0f, 0f, textWidth, textHeight) /* FIXME: Object creation */
}

private fun resolveAbsolutePosition(iconPlacement: IconPlacement): IconPlacement {
    val locale = Locale.getDefault()
    val isLeftToRight = TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_LTR
    val isRightToLeft = !isLeftToRight
    return when {
        iconPlacement == TOP -> TOP
        iconPlacement == LEFT -> LEFT
        iconPlacement == BOTTOM -> BOTTOM
        iconPlacement == START && isLeftToRight -> LEFT
        iconPlacement == END && isRightToLeft -> LEFT
        else -> RIGHT
    }
}

// TODO: Write unit tests for isOutward == true
/**
 * DrawDirection is unrelated to where we place the icon.
 *
 * The START and END placements are used to place the icon based on the
 * layout direction, so do not complicate things by including draw direction in
 * the calculation for position of the icon.
 */
internal fun makePathForOutsideCircularLabel(
    middleAngle: Float,
    pieCenter: Coordinates,
    pieRadius: Float,
    label: String,
    labelPaint: Paint,
    iconBounds: RectF,
    iconMargin: Float,
    iconPlacement: IconPlacement,
    outsideLabelMargin: Float,
    isOutward: Boolean
): Path {
    val labelBounds = calculateLabelBounds(label, labelPaint)
    val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
    val combinedWidth = labelAndIconCombinedBounds.width()
    val adjustedMargin = modulateMargin(iconMargin, labelBounds, iconBounds)
    val absolutePosition = resolveAbsolutePosition(iconPlacement)
    val labelHeight = labelBounds.height()
    val iconHeight = iconBounds.height()
    val labelOffset = if (isOutward) labelHeight - labelPaint.descent() else labelPaint.descent()
    val offset = when (absolutePosition) {
        TOP -> labelOffset
        BOTTOM -> iconHeight + adjustedMargin + labelOffset
        else -> (max(iconHeight, labelHeight) / 2f) - (labelHeight / 2f) + labelOffset
    }
    val pathRadius = pieRadius + outsideLabelMargin + offset
    val bounds = RectF(pieCenter.x - pathRadius, pieCenter.y - pathRadius, pieCenter.x + pathRadius, pieCenter.y + pathRadius)

    // NOTE: Everything above this line is correct and verified

    val labelSweepFraction = labelBounds.width() / (2 * PI.toFloat() * pathRadius)
    // NOTE for LEFT and RIGHT: Because the icon is rotated, its bottom may overlap with the label
    //  especially when the label and icon are very large.
    //  To fix this, to calculate the start angle of the label, instead of using circumference
    //  of the reference circle with *radius = to mid height of label and icon* as the denominator,
    //  we use the circumference of a smaller circle with *radius = to bottom of the label* as
    //  the denominator so the label and the icon won't overlap (and neither will have redundant margin).
    //  So, this means that the label will be displaced a little to left (especially large label
    //  which makes the difference between circumference of the reference circle and smaller circle more noticeable).
    //  Could we somehow displace both the icon and the label in half the total displacement of the label?
    val startAngle = if (absolutePosition == LEFT) {
        val radiusToCenterOfCombined = pieRadius + outsideLabelMargin + max(iconHeight, labelHeight) / 2f
        val labelSweepFraction = labelBounds.width() / (2 * PI.toFloat() * radiusToCenterOfCombined)
        val sweepFractionFromMiddle = (combinedWidth / 2f) / (2 * PI.toFloat() * radiusToCenterOfCombined)
        val endAngle = calculateEndAngle(middleAngle, sweepFractionFromMiddle, CLOCKWISE)
        calculateEndAngle(endAngle, labelSweepFraction, COUNTER_CLOCKWISE)
    } else if (absolutePosition == RIGHT) {
        val sweepFractionFromMiddle = (combinedWidth / 2f) / (2 * PI.toFloat() * (pathRadius - labelPaint.descent()))
        calculateEndAngle(middleAngle, sweepFractionFromMiddle, COUNTER_CLOCKWISE)
    } else {
        calculateEndAngle(middleAngle, labelSweepFraction / 2f, COUNTER_CLOCKWISE)
    }
    path.reset()
    if (isOutward) {
        val startAngle = calculateEndAngle(startAngle, labelSweepFraction, CLOCKWISE)
        path.addArc(bounds, startAngle, -labelSweepFraction * 360)
    } else {
        path.addArc(bounds, startAngle, labelSweepFraction * 360)
    }
    return path
}

internal fun calculateIconAbsoluteBoundsForOutsideCircularLabel(
    middleAngle: Float,
    pieCenter: Coordinates,
    pieRadius: Float,
    label: String,
    labelPaint: Paint,
    iconBounds: RectF,
    iconMargin: Float,
    iconPlacement: IconPlacement,
    outsideLabelMargin: Float
): RectF {
    val labelBounds = calculateLabelBounds(label, labelPaint)
    val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
    val combinedWidth = labelAndIconCombinedBounds.width()
    val adjustedMargin = modulateMargin(iconMargin, labelBounds, iconBounds)
    val absolutePosition = resolveAbsolutePosition(iconPlacement)
    val iconHeight = iconBounds.height()
    val iconWidth = iconBounds.width()
    val labelHeight = labelBounds.height()
    val offset = when (absolutePosition) {
        TOP -> labelBounds.height() + adjustedMargin + iconHeight / 2f
        BOTTOM -> iconHeight / 2f
        else -> max(iconHeight, labelHeight) / 2f
    }
    val iconRadius = pieRadius + outsideLabelMargin + offset
    val angle = calculateIconAngleForOutsideCircularLabel(iconPlacement, combinedWidth, iconBounds, iconRadius, middleAngle)
    val iconBoundsCenter = Coordinates(pieCenter.x + (iconRadius * cos(angle.toRadian())), pieCenter.y + (iconRadius * sin(angle.toRadian())))
    return RectF(iconBoundsCenter.x - iconWidth / 2f, iconBoundsCenter.y - iconHeight /2f, iconBoundsCenter.x + iconWidth / 2f, iconBoundsCenter.y + iconHeight /2f)
}

// TODO: Write unit tests for isOutward == true
internal fun calculateIconRotationAngleForOutsideCircularLabel(
    middleAngle: Float,
    pieRadius: Float,
    outsideLabelMargin: Float,
    label: String,
    labelPaint: Paint,
    iconBounds: RectF,
    iconMargin: Float,
    iconPlacement: IconPlacement,
    isOutward: Boolean
): Float {
    val labelBounds = calculateLabelBounds(label, labelPaint)
    val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
    val combinedWidth = labelAndIconCombinedBounds.width()
    val absolutePosition = resolveAbsolutePosition(iconPlacement)
    val iconHeight = iconBounds.height()
    val labelHeight = labelBounds.height()
    val offset = when (absolutePosition) {
        TOP -> labelHeight + iconMargin + iconHeight / 2f
        BOTTOM -> iconHeight / 2f
        else -> max(iconHeight, labelHeight) / 2f
    }
    val iconRadius = pieRadius + outsideLabelMargin + offset
    val angle = calculateIconAngleForOutsideCircularLabel(iconPlacement, combinedWidth, iconBounds, iconRadius, middleAngle)
    val correction = if (isOutward) { 270f } else { 90f }
    return angle + correction
}

private fun calculateIconAngleForOutsideCircularLabel(
    iconPlacement: IconPlacement,
    combinedWidth: Float,
    iconBounds: RectF,
    radius: Float,
    middleAngle: Float
): Float {
    val absolutePosition = resolveAbsolutePosition(iconPlacement)
    val fraction = ((combinedWidth / 2f) - (iconBounds.width() / 2f)) / (2 * PI.toFloat() * radius)
    return when (absolutePosition) {
        LEFT -> calculateEndAngle(middleAngle, fraction, COUNTER_CLOCKWISE)
        RIGHT -> calculateEndAngle(middleAngle, fraction, CLOCKWISE)
        else -> middleAngle
    }
}

/**
 * Assumes that the width and height of the currentBounds are equal.
 *
 * The label margin is calculated by adding it to the pie radius.
 *
 * The calculation for resizing the pie is a little more complicated than what I initially thought.
 * To resize the pie and to avoid outside labels from being cropped, we can either
 *   - take the easy and safe way and calculate the offset like this (right bound in this example):
 *     ```kotlin
 *     newRight = currentBounds.right - if(labelIsInRightSideOfPie) labelCombinedWidth else 0
 *     ```
 *     This may waste space that could have been used for a bigger pie.
 *   - or we can calculate an approximate offset which is dirty, inaccurate,
 *     and still prone to be incorrect or to create redundant offset.
 */
internal fun calculatePieNewBoundsForOutsideLabel(
    context: Context,
    currentBounds: Bounds,
    labelsProperties: List<LabelProperties>,
    slicesProperties: List<SliceProperties>,
    shouldCenterPie: Boolean
): Bounds {
    var maxTopExcess = 0f
    var maxLeftExcess = 0f
    var maxRightExcess = 0f
    var maxBottomExcess = 0f
    val currentCenter = Coordinates((currentBounds.right + currentBounds.left) / 2f, (currentBounds.bottom + currentBounds.top) / 2f)
    val currentRadius = currentBounds.width / 2f

    for ((i, label) in labelsProperties.withIndex()) {
        val middleAngle = calculateMiddleAngle(slicesProperties[i].startAngle, slicesProperties[i].fraction, slicesProperties[i].drawDirection)
        updatePaintForLabel(paint, label.size, label.color, label.font)
        var labelIcon : Drawable? = null
        label.icon?.let { labelIcon = context.resources.getDrawable(it, null) }
        val outsideLabelMargin = label.marginFromPie
        val iconPlacement = label.iconPlacement
        val iconMargin = label.iconMargin
        val iconHeight = label.iconHeight
        val iconBounds = calculateIconBounds(labelIcon, iconHeight)
        val labelBounds = calculateLabelBounds(label.text, paint)
        val combinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
        val absoluteCombinedBounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(combinedBounds, middleAngle, currentCenter, currentRadius, outsideLabelMargin)

        val correctionFactor = 0.6f
        val horizontalCorrection = (sin(middleAngle.toRadian()) * absoluteCombinedBounds.width() * correctionFactor).absoluteValue
        val verticalCorrection = (cos(middleAngle.toRadian()) * absoluteCombinedBounds.height() * correctionFactor).absoluteValue

        var leftExcess = currentBounds.left - (absoluteCombinedBounds.left - horizontalCorrection)
        if (combinedBounds.width() == 0f) leftExcess = 0f
        maxLeftExcess = max(maxLeftExcess, leftExcess)
        var topExcess = currentBounds.top - (absoluteCombinedBounds.top - verticalCorrection)
        if (combinedBounds.height() == 0f) topExcess = 0f
        maxTopExcess = max(maxTopExcess, topExcess)
        var rightExcess = (absoluteCombinedBounds.right + horizontalCorrection) - currentBounds.right
        if (combinedBounds.width() == 0f) rightExcess = 0f
        maxRightExcess = max(maxRightExcess, rightExcess)
        var bottomExcess = (absoluteCombinedBounds.bottom + verticalCorrection) - currentBounds.bottom
        if (combinedBounds.height() == 0f) bottomExcess = 0f
        maxBottomExcess = max(maxBottomExcess, bottomExcess)
    }

    if (shouldCenterPie) {
        val maxHorizontalExcess = max(maxLeftExcess, maxRightExcess)
        val maxVerticalExcess = max(maxTopExcess, maxBottomExcess)
        maxTopExcess = maxVerticalExcess
        maxLeftExcess = maxHorizontalExcess
        maxRightExcess = maxHorizontalExcess
        maxBottomExcess = maxVerticalExcess
    }

    // EITHER THIS

    val width = currentBounds.width - (maxLeftExcess + maxRightExcess)
    val height = currentBounds.height - (maxTopExcess + maxBottomExcess)
    if (height > width) {
        val excess = height - width
        maxTopExcess += excess / 2f
        maxBottomExcess += excess / 2f
    } else if (width > height) {
        val excess = width - height
        maxLeftExcess += excess / 2f
        maxRightExcess += excess / 2f
    }
    return Bounds(currentBounds.left + maxLeftExcess, currentBounds.top + maxTopExcess, currentBounds.right - maxRightExcess, currentBounds.bottom - maxBottomExcess)

    // OR THIS (creates a little different layout)

    // val maxExcess = maxOf(maxLeftExcess, maxTopExcess, maxRightExcess, maxBottomExcess)
    // return RectF(currentBounds.left + maxExcess, currentBounds.top + maxExcess, currentBounds.right - maxExcess, currentBounds.bottom - maxExcess)
}

/**
* Assumes that the width and height of the currentBounds are equal.
*/
internal fun calculatePieNewBoundsForOutsideCircularLabel(
    context: Context,
    currentBounds: Bounds,
    labelsProperties: List<LabelProperties>,
    shouldCenterPie: Boolean
): Bounds {
    var maxLabelAndIconHeight = 0f
    for (label in labelsProperties) {
        updatePaintForLabel(paint, label.size, label.color, label.font)
        var labelIcon : Drawable? = null
        label.icon?.let { labelIcon = context.resources.getDrawable(it, null) }
        val outsideLabelMargin = label.marginFromPie
        val iconPlacement = label.iconPlacement
        val iconMargin = label.iconMargin
        val iconHeight = label.iconHeight
        val iconBounds = calculateIconBounds(labelIcon, iconHeight)
        val labelBounds = calculateLabelBounds(label.text, paint)
        val absolutePosition = resolveAbsolutePosition(iconPlacement)
        val adjustedIconMargin = modulateMargin(iconMargin, labelBounds, iconBounds)
        val sliceMax = if (absolutePosition == LEFT || absolutePosition == RIGHT) {
            outsideLabelMargin + max(labelBounds.height(), iconBounds.height())
        } else {
            outsideLabelMargin + labelBounds.height() + adjustedIconMargin + iconBounds.height()
        }
        maxLabelAndIconHeight = max(maxLabelAndIconHeight, sliceMax)
    }
    if (shouldCenterPie) {
        return Bounds(currentBounds.left + maxLabelAndIconHeight, currentBounds.top + maxLabelAndIconHeight, currentBounds.right - maxLabelAndIconHeight, currentBounds.bottom - maxLabelAndIconHeight)
    } else {
        /* FIXME: Same as above branch; implement it */
        return Bounds(currentBounds.left + maxLabelAndIconHeight, currentBounds.top + maxLabelAndIconHeight, currentBounds.right - maxLabelAndIconHeight, currentBounds.bottom - maxLabelAndIconHeight)
    }
}

private fun calculateTextSize(text: String, paint: Paint): Size {
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

internal fun calculateAbsoluteBoundsForInsideLabelAndIcon(
    labelAndIconCombinedBounds: RectF,
    angle: Float,
    origin: Coordinates,
    pieRadius: Float,
    labelOffset: Float
): RectF {
    val x = origin.x + pieRadius * labelOffset * cos(angle.toRadian())
    val y = origin.y + pieRadius * labelOffset * sin(angle.toRadian())
    val top = y - labelAndIconCombinedBounds.height() / 2f
    val left = x - labelAndIconCombinedBounds.width() / 2f
    val right = x + labelAndIconCombinedBounds.width() / 2f
    val bottom = y + labelAndIconCombinedBounds.height() / 2f
    return RectF(left, top, right, bottom)
}

/**
 * For formulae and calculation details refer to [this post](https://math.stackexchange.com/q/4152307).
 *
 * NOTE: If w² + h² > x² (x == diameter?) then this formula will not work.
 */
internal fun calculateAbsoluteBoundsForOutsideLabelAndIcon(
    labelAndIconCombinedBounds: RectF,
    angle: Float,
    origin: Coordinates,
    pieRadius: Float,
    labelMargin: Float
): RectF {
    val normalizedAngle = normalizeAngle(angle)
    var θ = normalizedAngle.toRadian()
    var w = labelAndIconCombinedBounds.width()
    var h = labelAndIconCombinedBounds.height()
    val r = pieRadius

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

    val (x, y) = calculateCoordinatesOnCircumference(angle, origin, pieRadius + labelMargin)
    return RectF(x + a - labelAndIconCombinedBounds.width() / 2, y + b - labelAndIconCombinedBounds.height() / 2, x + a + labelAndIconCombinedBounds.width() / 2, y + b + labelAndIconCombinedBounds.height() / 2)
}

/**
 * When drawing the text on Canvas, the y is used for text descent and not
 * its bottom or vertical center. So, take that into account in calculations
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
internal fun calculateLabelCoordinates(
    labelAndIconCombinedBounds: RectF,
    labelBounds: RectF,
    labelPaint: Paint,
    iconPlacement: IconPlacement,
): Coordinates {
    val absolutePosition = resolveAbsolutePosition(iconPlacement)
    val x: Float
    val y: Float
    if (absolutePosition == TOP) {
        x = labelAndIconCombinedBounds.centerX()
        y = labelAndIconCombinedBounds.bottom - labelPaint.descent()
    } else if (absolutePosition == BOTTOM) {
        x = labelAndIconCombinedBounds.centerX()
        y = labelAndIconCombinedBounds.top - labelPaint.ascent()
    } else if (absolutePosition == LEFT) {
        x = labelAndIconCombinedBounds.right - labelBounds.width() / 2f
        y = labelAndIconCombinedBounds.centerY() + labelBounds.height() / 2f - labelPaint.descent()
    } else {
        x = labelAndIconCombinedBounds.left + labelBounds.width() / 2f
        y = labelAndIconCombinedBounds.centerY() + labelBounds.height() / 2f - labelPaint.descent()
    }
    return Coordinates(x, y)
}

internal fun calculateLabelIconAbsoluteBounds(
    labelAndIconCombinedBounds: RectF,
    iconBounds: RectF,
    iconPlacement: IconPlacement,
): RectF {
    val absolutePosition = resolveAbsolutePosition(iconPlacement)
    val top: Float
    val left: Float
    val right: Float
    val bottom: Float
    if (absolutePosition == TOP) {
        top = labelAndIconCombinedBounds.top
        left = labelAndIconCombinedBounds.centerX() - iconBounds.width() / 2f
        right = left + iconBounds.width()
        bottom = top + iconBounds.height()
    }
    else if (absolutePosition == BOTTOM) {
        bottom = labelAndIconCombinedBounds.bottom
        left = labelAndIconCombinedBounds.centerX() - iconBounds.width() / 2f
        right = left + iconBounds.width()
        top = bottom - iconBounds.height()
    }
    else if (absolutePosition == LEFT) {
        top = labelAndIconCombinedBounds.centerY() - iconBounds.height() / 2f
        left = labelAndIconCombinedBounds.left
        right = left + iconBounds.width()
        bottom = top + iconBounds.height()
    }
    else {
        top = labelAndIconCombinedBounds.centerY() - iconBounds.height() / 2f
        right = labelAndIconCombinedBounds.right
        left = right - iconBounds.width()
        bottom = top + iconBounds.height()
    }
    return RectF(left, top, right, bottom)
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
