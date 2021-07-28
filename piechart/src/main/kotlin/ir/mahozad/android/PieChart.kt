package ir.mahozad.android

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Typeface.DEFAULT
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.*
import androidx.annotation.Dimension.DP
import androidx.annotation.Dimension.PX
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import androidx.core.graphics.minus
import androidx.core.graphics.withClip
import androidx.core.graphics.withRotation
import ir.mahozad.android.PieChart.DefaultIcons.CIRCLE
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.PieChart.GapPosition.MIDDLE
import ir.mahozad.android.PieChart.GradientType.RADIAL
import ir.mahozad.android.PieChart.IconPlacement.START
import ir.mahozad.android.PieChart.LabelType.*
import ir.mahozad.android.PieChart.SlicePointer
import ir.mahozad.android.component.*
import ir.mahozad.android.component.DrawDirection.LTR
import ir.mahozad.android.component.DrawDirection.RTL
import ir.mahozad.android.component.Icon
import java.text.NumberFormat
import kotlin.math.max
import kotlin.math.min

const val ENABLED = true
const val DISABLED = false
const val DEFAULT_SIZE = 256 /* dp */
const val DEFAULT_START_ANGLE = -90
const val DEFAULT_HOLE_RATIO = 0.25f
const val DEFAULT_OVERLAY_RATIO = 0.55f
const val DEFAULT_OVERLAY_ALPHA = 0.05f
const val DEFAULT_CENTER_BACKGROUND_STATUS = DISABLED
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_BACKGROUND_RATIO = 0.5f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_BACKGROUND_ALPHA = 1f
const val DEFAULT_GAP = 8f /* px */
const val DEFAULT_LABELS_SIZE = 18f /* sp */
const val DEFAULT_LEGENDS_SIZE = 16f /* sp */
const val DEFAULT_LEGEND_TITLE_MARGIN = 8f /* dp */
const val DEFAULT_LEGEND_BOX_MARGIN = 8f /* dp */
const val DEFAULT_LEGEND_BOX_PADDING = 4f /* dp */
const val DEFAULT_LEGEND_BOX_BORDER = 2f /* dp */
const val DEFAULT_LEGEND_BOX_BORDER_DASH_ARRAY = "4, 4" /* dp (each) */
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_LEGEND_BOX_BORDER_ALPHA = 1f
const val DEFAULT_LEGEND_BOX_BORDER_CORNER_RADIUS = 3f /* dp */
const val DEFAULT_LEGENDS_TITLE_SIZE = 18f /* sp */
const val DEFAULT_LEGEND_ICONS_MARGIN = 8f /* dp */
const val DEFAULT_LEGEND_ICONS_ALPHA = 1f
const val DEFAULT_LEGENDS_PERCENTAGE_STATUS = DISABLED
const val DEFAULT_LEGENDS_PERCENTAGE_MARGIN = 8f /* dp */
const val DEFAULT_LEGENDS_PERCENTAGE_SIZE = DEFAULT_LEGENDS_SIZE /* sp */
const val DEFAULT_LEGENDS_MARGIN = 4f /* dp */
/* sp so user can easily specify the same value for both label size and icon height to make them the same size */
const val DEFAULT_LABEL_ICONS_HEIGHT = DEFAULT_LABELS_SIZE /* sp */
const val DEFAULT_LEGEND_ICONS_HEIGHT = DEFAULT_LEGENDS_SIZE /* sp */
const val DEFAULT_LABEL_ICONS_MARGIN = 8f /* dp */
const val DEFAULT_LABEL_OFFSET = 0.75f
const val DEFAULT_OUTSIDE_LABELS_MARGIN = 28f /* dp */
const val DEFAULT_CENTER_LABEL = ""
const val DEFAULT_CENTER_LABEL_SIZE = 16f /* sp */
const val DEFAULT_CENTER_LABEL_ICON_HEIGHT = DEFAULT_CENTER_LABEL_SIZE /* sp */
@Dimension(unit = DP) const val DEFAULT_CENTER_LABEL_ICON_MARGIN = 8f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_LABEL_ALPHA = 1f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_LABEL_ICON_ALPHA = 1f
const val DEFAULT_LEGENDS_TITLE = ""
const val DEFAULT_SHOULD_CENTER_PIE = true
@ColorInt const val DEFAULT_LABELS_COLOR = Color.WHITE
@ColorInt const val DEFAULT_LEGENDS_COLOR = Color.WHITE
@ColorInt const val DEFAULT_LEGEND_BOX_BACKGROUND_COLOR = Color.TRANSPARENT
@ColorInt const val DEFAULT_LEGEND_BOX_BORDER_COLOR = Color.TRANSPARENT
@ColorInt const val DEFAULT_LEGENDS_TITLE_COLOR = Color.WHITE
@ColorInt const val DEFAULT_LEGENDS_PERCENTAGE_COLOR = Color.WHITE
@ColorInt const val DEFAULT_CENTER_LABEL_COLOR = Color.WHITE
@ColorInt const val DEFAULT_CENTER_BACKGROUND_COLOR = Color.GRAY
// If null, the colors of the icon itself is used
@ColorInt val defaultLabelIconsTint: Int? = null
@ColorInt val defaultLegendIconsTint: Int? = null
@ColorInt val defaultCenterLabelIconTint: Int? = null
val defaultGapPosition = MIDDLE
val defaultGradientType = RADIAL
val defaultDrawDirection = CLOCKWISE
val defaultLabelIconsPlacement = START
val defaultLegendType = PieChart.LegendType.NONE
val defaultLegendsIcon = CIRCLE
val defaultCenterLabelIcon = PieChart.DefaultIcons.NO_ICON
val defaultLegendsAlignment = Alignment.CENTER
val defaultLegendBoxAlignment = Alignment.CENTER
val defaultLabelType = INSIDE
val defaultLegendBoxBorderType = PieChart.BorderType.SOLID
val defaultLabelsFont: Typeface = DEFAULT
val defaultCenterLabelFont: Typeface = DEFAULT
val defaultSlicesPointer: SlicePointer? = null
val defaultLegendIconsTintArray = intArrayOf(0xff3F51B5.toInt())

/**
 * This is the order that these commonly used view methods are run:
 * 1. Constructor    // choose your desired size
 * 2. onMeasure      // parent will determine if your desired size is acceptable
 * 3. onSizeChanged
 * 4. onLayout
 * 5. onDraw         // draw your view content at the size specified by the parent
 *
 * Any time that you make a change to your view that affects the appearance but not the size,
 * then call invalidate(). This will cause onDraw to be called again (but not all of those other previous methods).
 *
 * Any time that you make a change to your view that would affect the size, then call requestLayout().
 * This will start the process of measuring and drawing all over again from onMeasure.
 * This call is usually accompanied (preceded) by a call to invalidate().
 *
 * See [this helpful post](https://stackoverflow.com/a/42430834) for more information.
 */
class PieChart(context: Context, attrs: AttributeSet) : View(context, attrs) {

    data class Slice(
        @FloatRange(from = 0.0, to = 1.0) val fraction: Float,
        @ColorInt val color: Int,
        @ColorInt val colorEnd: Int = color,
        val label: String = NumberFormat.getPercentInstance().format(fraction),
        /**
         * This color overrides the generic *labelsColor* if assigned a value other than *null*
         */
        @ColorInt val labelColor: Int? = null,
        @Dimension val labelSize: Float? = null,
        val labelFont: Typeface? = null,

        @DrawableRes val labelIcon: Int? = null,
        @Dimension val labelIconHeight: Float? = null,
        @Dimension val labelIconMargin: Float? = null,
        @ColorInt val labelIconTint: Int? = null,
        val labelIconPlacement: IconPlacement? = null,

        /**
         * Distance of the start of the outside label from the pie
         */
        @Dimension val outsideLabelMargin: Float? = null,
        val pointer: SlicePointer? = null,

        val legend: String = "",
        @ColorInt val legendColor: Int? = null,
        @Dimension val legendSize: Float? = null,
        @DrawableRes val legendIcon: Int? = null,
        @Dimension val legendIconHeight: Float? = null,
        @Dimension val legendIconMargin: Float? = null,
        @ColorInt val legendIconTint: Int? = color,
        @ColorInt val legendPercentageColor: Int? = null,
        @Dimension val legendPercentageSize: Float? = null,
        @Dimension val legendPercentageMargin: Float? = null,
        @FloatRange(from = 0.0, to = 1.0) val legendIconAlpha: Float = 1f,

        /**
         * Can also set the default value to the slice fraction.
         *
         * Scale the slice like this:
         * ```kotlin
         * val scaleMatrix = Matrix()
         * scaleMatrix.setScale(slice.scale, slice.scale, centerX, centerY)
         * piePath.transform(scaleMatrix)
         * ```
         * Or with the canvas which seems to scale the whole drawing:
         * ```kotlin
         * canvas.scale(slice.scale, slice.scale, centerX, centerY)
         * ```
         */
        @FloatRange(from = 0.0, to = 1.0) val scale: Float = 1f
    )

    enum class DrawDirection { CLOCKWISE, COUNTER_CLOCKWISE }
    enum class IconPlacement { START, END, LEFT, RIGHT, TOP, BOTTOM }
    enum class GradientType { RADIAL, SWEEP }
    enum class GapPosition { MIDDLE, PRECEDING_SLICE, SUCCEEDING_SLICE }
    enum class BorderType {SOLID, DASHED}
    /* TODO: Rename inside to inner or internal and outside to outer or external (?) */
    enum class LabelType { NONE, INSIDE, OUTSIDE, INSIDE_CIRCULAR, OUTSIDE_CIRCULAR_INWARD, OUTSIDE_CIRCULAR_OUTWARD, OUTSIDE_WITH_LINES_ON_SIDES }
    data class SlicePointer(val length: Float, val width: Float, val color: Int)

    interface Icon { val resId: Int }
    class CustomIcon(@DrawableRes override val resId: Int) : Icon
    enum class DefaultIcons(@DrawableRes override val resId: Int) : Icon {
        SQUARE(R.drawable.ic_square),
        SQUARE_HOLLOW(R.drawable.ic_square_hollow),
        CIRCLE(R.drawable.ic_circle),
        CIRCLE_HALLOW(R.drawable.ic_circle_hollow),
        RECTANGLE(R.drawable.ic_rectangle),
        RECTANGLE_HALLOW(R.drawable.ic_rectangle_hollow),
        RECTANGLE_TALL(R.drawable.ic_rectangle_tall),
        RECTANGLE_TALL_HALLOW(R.drawable.ic_rectangle_tall_hollow),
        TRIANGLE(R.drawable.ic_triangle),
        TRIANGLE_HALLOW(R.drawable.ic_triangle_hollow),
        TRIANGLE_INWARD(R.drawable.ic_triangle_inward),
        TRIANGLE_INWARD_HALLOW(R.drawable.ic_triangle_inward_hollow),
        TRIANGLE_OUTWARD(R.drawable.ic_triangle_outward),
        TRIANGLE_OUTWARD_HALLOW(R.drawable.ic_triangle_outward_hollow),
        TRIANGLE_DOWNWARD(R.drawable.ic_triangle_downward),
        TRIANGLE_DOWNWARD_HALLOW(R.drawable.ic_triangle_downward_hollow),
        ARC1(R.drawable.ic_arc1),
        ARC2(R.drawable.ic_arc2),
        ARC3(R.drawable.ic_arc3),
        SLICE1(R.drawable.ic_slice1),
        SLICE2(R.drawable.ic_slice2),
        SLICE3(R.drawable.ic_slice3),
        NO_ICON(R.drawable.ic_empty)
    }

    enum class LegendType {
        NONE,
        BOTTOM_HORIZONTAL,
        TOP_HORIZONTAL,
        START_VERTICAL,
        IN_HOLE_VERTICAL,
        END_VERTICAL,
        BOTTOM_VERTICAL,
        TOP_VERTICAL,
        START_HORIZONTAL,
        END_HORIZONTAL
    }

    private lateinit var legendsRect : RectF

    var startAngle = DEFAULT_START_ANGLE
        set(angle) {
            field = normalizeAngle(angle)
            invalidate()
        }
    var holeRatio = DEFAULT_HOLE_RATIO
        set(ratio) {
            field = ratio.coerceIn(0f, 1f)
            invalidate()
        }
    var overlayRatio = DEFAULT_OVERLAY_RATIO
        set(ratio) {
            field = ratio.coerceIn(0f, 1f)
            invalidate()
        }
    var overlayAlpha = DEFAULT_OVERLAY_ALPHA
        set(alpha) {
            field = alpha.coerceIn(0f, 1f)
            invalidate()
        }
    var gap = DEFAULT_GAP
        set(width) {
            field = width
            invalidate()
        }
    var labelsSize = spToPx(DEFAULT_LABELS_SIZE)
        set(size /* px */) {
            field = size
            invalidate()
        }
    var legendsSize = spToPx(DEFAULT_LEGENDS_SIZE)
        set(size /* px */) {
            field = size
            invalidate()
        }
    var legendsAlignment = defaultLegendsAlignment
        set(alignment) {
            field = alignment
            invalidate()
        }
    var legendBoxAlignment  = defaultLegendBoxAlignment
        set(alignment) {
            field = alignment
            invalidate()
        }
    var legendsTitle = DEFAULT_LEGENDS_TITLE
        set(title) {
            field = title
            invalidate()
        }
    var legendType = defaultLegendType
        set(type) {
            field = type
            invalidate()
            requestLayout()
        }
    var legendsMargin = dpToPx(DEFAULT_LEGENDS_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
            requestLayout()
        }
    var legendsColor = DEFAULT_LEGENDS_COLOR
        set(color) {
            field = color
            invalidate()
        }
    var legendBoxBackgroundColor = DEFAULT_LEGEND_BOX_BACKGROUND_COLOR
        set(color) {
            field = color
            invalidate()
        }
    var legendBoxMargin = dpToPx(DEFAULT_LEGEND_BOX_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }
    var legendBoxPadding = dpToPx(DEFAULT_LEGEND_BOX_PADDING)
        set(padding /* px */) {
            field = padding
            invalidate()
        }
    var legendBoxBorder = dpToPx(DEFAULT_LEGEND_BOX_BORDER)
        set(border /* px */) {
            field = border
            invalidate()
        }
    var legendBoxBorderCornerRadius = dpToPx(DEFAULT_LEGEND_BOX_BORDER_CORNER_RADIUS)
        set(radius /* px */) {
            field = radius
            invalidate()
        }
    var legendBoxBorderColor = DEFAULT_LEGEND_BOX_BORDER_COLOR
        set(color) {
            field = color
            invalidate()
        }
    var legendBoxBorderAlpha = DEFAULT_LEGEND_BOX_BORDER_ALPHA
        set(alpha) {
            field = alpha
            invalidate()
        }
    var legendBoxBorderType = defaultLegendBoxBorderType
        set(type) {
            field = type
            invalidate()
        }
    var legendBoxBorderDashArray = parseBorderDashArray(DEFAULT_LEGEND_BOX_BORDER_DASH_ARRAY)
        set(@Dimension(unit = PX) array) {
            field = array
            invalidate()
        }
    var legendIconsAlpha = DEFAULT_LEGEND_ICONS_ALPHA
        set(alpha) {
            field = alpha
            invalidate()
        }
    var legendsTitleColor = DEFAULT_LEGENDS_TITLE_COLOR
        set(color) {
            field = color
            invalidate()
        }
    var legendsTitleSize = spToPx(DEFAULT_LEGENDS_TITLE_SIZE)
        set(size /* px */) {
            field = size
            invalidate()
        }
    var legendsPercentageSize = spToPx(DEFAULT_LEGENDS_PERCENTAGE_SIZE)
        set(size /* px */) {
            field = size
            invalidate()
        }
    var isLegendsPercentageEnabled = DEFAULT_LEGENDS_PERCENTAGE_STATUS
        set(shouldEnable) {
            field = shouldEnable
            invalidate()
            requestLayout()
        }
    var legendsPercentageColor = DEFAULT_LEGENDS_PERCENTAGE_COLOR
        set(color) {
            field = color
            invalidate()
        }
    var legendsPercentageMargin = dpToPx(DEFAULT_LEGENDS_PERCENTAGE_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }
    var legendTitleMargin = dpToPx(DEFAULT_LEGEND_TITLE_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }
    var legendIconsHeight = spToPx(DEFAULT_LEGEND_ICONS_HEIGHT)
        set(height /* px */) {
            field = height
            invalidate()
        }
    var legendIconsMargin = dpToPx(DEFAULT_LEGEND_ICONS_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }

    /**
     * Order:
     *   1. use slice.legendIconTint
     *   2. if slice.legendIconTint == null, use legendIconTintArray
     *   3. if legendIconTintArray == null, use legendIconsTint
     */
    var legendIconsTint = defaultLegendIconsTint
        set(color) {
            field = color
            invalidate()
        }
    var legendIconsTintArray = defaultLegendIconsTintArray
        set(array) {
            field = array
            invalidate()
        }
    var labelsFont = defaultLabelsFont
        set(font) {
            field = font
            invalidate()
        }
    var centerLabelFont = defaultCenterLabelFont
        set(font) {
            field = font
            invalidate()
        }
    var centerLabelIconHeight = DEFAULT_CENTER_LABEL_ICON_HEIGHT
        set(@Dimension(unit = PX) height) {
            field = height
            invalidate()
        }
    var centerLabelIconMargin = DEFAULT_CENTER_LABEL_ICON_MARGIN
        set(@Dimension(unit = PX) margin) {
            field = margin
            invalidate()
        }
    var centerLabelIconTint = defaultCenterLabelIconTint
        set(@ColorInt color) {
            field = color
            invalidate()
        }
    var centerLabelAlpha = DEFAULT_CENTER_LABEL_ALPHA
        set(@FloatRange(from = 0.0, to = 1.0) alpha) {
            field = alpha.coerceIn(0f, 1f)
            invalidate()
        }
    var centerLabelIconAlpha = DEFAULT_CENTER_LABEL_ICON_ALPHA
        set(@FloatRange(from = 0.0, to = 1.0) alpha) {
            field = alpha.coerceIn(0f, 1f)
            invalidate()
        }
    var isCenterBackgroundEnabled = DEFAULT_CENTER_BACKGROUND_STATUS
        set(shouldEnable) {
            field = shouldEnable
            invalidate()
        }
    var centerBackgroundColor = DEFAULT_CENTER_BACKGROUND_COLOR
        set(@ColorInt color) {
            field = color
            invalidate()
        }
    var centerBackgroundRatio = DEFAULT_CENTER_BACKGROUND_RATIO
        set(@FloatRange(from = 0.0, to = 1.0) ratio) {
            field = ratio
            invalidate()
        }
    var centerBackgroundAlpha = DEFAULT_CENTER_BACKGROUND_ALPHA
        set(@FloatRange(from = 0.0, to = 1.0) alpha) {
            field = alpha
            invalidate()
        }
    var labelOffset = DEFAULT_LABEL_OFFSET
        set(offset) {
            field = offset.coerceIn(0f, 1f)
            invalidate()
        }
    var labelIconsHeight = spToPx(DEFAULT_LABEL_ICONS_HEIGHT)
        set(height /* px */) {
            field = height
            invalidate()
        }
    var labelIconsMargin = dpToPx(DEFAULT_LABEL_ICONS_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }
    var outsideLabelsMargin = dpToPx(DEFAULT_OUTSIDE_LABELS_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }
    var labelType = defaultLabelType
        set(type) {
            field = type
            invalidate()
            requestLayout()
        }
    /**
     * Is overridden by color of the slice if it is assigned a value other than *null*
     */
    var labelsColor = DEFAULT_LABELS_COLOR
        set(color) {
            field = color
            invalidate()
        }
    /**
     * Is overridden by color of the slice if it is assigned a value other than *null*
     */
    var labelIconsTint = defaultLabelIconsTint
        set(color) {
            field = color
            invalidate()
        }
    var slicesPointer = defaultSlicesPointer
        set(pointer) {
            field = pointer
            invalidate()
        }
    var labelIconsPlacement = defaultLabelIconsPlacement
        set(placement) {
            field = placement
            invalidate()
        }
    /**
     * When using outside labels, if this is set to true,
     * the pie will always be centered on its canvas which may sacrifice
     * some space that could have been used to make the pie bigger.
     */
    var shouldCenterPie = DEFAULT_SHOULD_CENTER_PIE
        set(shouldCenter) {
            field = shouldCenter
            invalidate()
        }
    /**
     * Is overridden by icon of the slice if it is assigned a value other than *null*
     */
    var legendsIcon: Icon = defaultLegendsIcon
    var centerLabelIcon : Icon = defaultCenterLabelIcon
        set(icon) {
            field = icon
            invalidate()
        }
    var centerLabel = DEFAULT_CENTER_LABEL
        set(label) {
            field = label
            invalidate()
        }
    var centerLabelSize = DEFAULT_CENTER_LABEL_SIZE
        set(@Dimension(unit = PX) size) {
            field = size
            invalidate()
        }
    var centerLabelColor = DEFAULT_CENTER_LABEL_COLOR
        set(color) {
            field = color
            invalidate()
        }
    var gapPosition = defaultGapPosition
    var gradientType = defaultGradientType
    var drawDirection = defaultDrawDirection
    var slices = listOf(
        // Slice(fraction = 0.125f, label = "qlyO([", color = ContextCompat.getColor(context, android.R.color.holo_green_dark)),
        // Slice(fraction = 0.25f, label = "qlyO([", color = ContextCompat.getColor(context, android.R.color.holo_orange_dark)),
        // Slice(fraction = 0.125f, label = "qlyO([", color = ContextCompat.getColor(context, android.R.color.holo_purple)),
        // Slice(fraction = 0.5f, label = "qlyO([", color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)),

        Slice(0.43f, ContextCompat.getColor(context, android.R.color.holo_green_dark), labelIcon = R.drawable.ic_square /*pointer = SlicePointer(50f,100f,0)*/),
        Slice(0.21f, ContextCompat.getColor(context, android.R.color.holo_orange_dark), legend = "Dairy"),
        Slice(0.19f, ContextCompat.getColor(context, android.R.color.holo_blue_dark)),
        Slice(0.14f, ContextCompat.getColor(context, android.R.color.holo_red_light)),
        Slice(0.03f, ContextCompat.getColor(context, android.R.color.holo_purple))
    )
    private val pie = Path()
    private val clip = Path()
    private lateinit var gaps: Path
    private val overlay = Path()
    private val mainPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val pieEnclosingRect = RectF()
    private val totalDrawableRect = RectF()
    private var pieRadius = 0f
    private var center = Coordinates(0f, 0f)
    private lateinit var centerLabelBox: Box
    private lateinit var legendsBox: Box

    /**
     * Attributes are a powerful way of controlling the behavior and appearance of views,
     * but they can only be read when the view is initialized. To provide dynamic behavior,
     * expose a property getter and setter pair for each custom attribute.
     *
     * A good rule to follow is to always expose any property that affects the
     * visible appearance or behavior of your custom view.
     */
    init {
        // TypedArray objects are a shared resource and must be recycled after use (thus the ::use function)
        context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).use {
            startAngle = normalizeAngle(it.getInt(R.styleable.PieChart_startAngle, DEFAULT_START_ANGLE))
            holeRatio = it.getFloat(R.styleable.PieChart_holeRatio, DEFAULT_HOLE_RATIO)
            overlayRatio = it.getFloat(R.styleable.PieChart_overlayRatio, DEFAULT_OVERLAY_RATIO)
            overlayAlpha = it.getFloat(R.styleable.PieChart_overlayAlpha, DEFAULT_OVERLAY_ALPHA)
            gap = it.getDimension(R.styleable.PieChart_gap, DEFAULT_GAP)
            labelsSize = it.getDimension(R.styleable.PieChart_labelsSize, spToPx(DEFAULT_LABELS_SIZE))
            labelOffset = it.getFloat(R.styleable.PieChart_labelOffset, DEFAULT_LABEL_OFFSET)
            labelsColor = it.getColor(R.styleable.PieChart_labelsColor, DEFAULT_LABELS_COLOR)
            labelIconsTint = getIconTint(it, R.styleable.PieChart_labelIconsTint)
            labelsFont = getFont(it, R.styleable.PieChart_labelsFont, defaultLabelsFont)
            centerLabelFont = getFont(it, R.styleable.PieChart_centerLabelFont, defaultCenterLabelFont)
            centerLabelIconAlpha = it.getFloat(R.styleable.PieChart_centerLabelIconAlpha, DEFAULT_CENTER_LABEL_ICON_ALPHA)
            centerLabelAlpha = it.getFloat(R.styleable.PieChart_centerLabelAlpha, DEFAULT_CENTER_LABEL_ALPHA)
            labelIconsHeight = it.getDimension(R.styleable.PieChart_labelIconsHeight, spToPx(DEFAULT_LABEL_ICONS_HEIGHT))
            centerLabelIconHeight = it.getDimension(R.styleable.PieChart_centerLabelIconHeight, dpToPx(DEFAULT_CENTER_LABEL_ICON_HEIGHT))
            legendIconsHeight = it.getDimension(R.styleable.PieChart_legendIconsHeight, spToPx(DEFAULT_LEGEND_ICONS_HEIGHT))
            legendIconsMargin = it.getDimension(R.styleable.PieChart_legendIconsMargin, dpToPx(DEFAULT_LEGEND_ICONS_MARGIN))
            centerLabelIconMargin = it.getDimension(R.styleable.PieChart_centerLabelIconMargin, dpToPx(DEFAULT_CENTER_LABEL_ICON_MARGIN))
            labelIconsMargin = it.getDimension(R.styleable.PieChart_labelIconsMargin, dpToPx(DEFAULT_LABEL_ICONS_MARGIN))
            outsideLabelsMargin = it.getDimension(R.styleable.PieChart_outsideLabelsMargin, dpToPx(DEFAULT_OUTSIDE_LABELS_MARGIN))
            centerLabel = it.getString(R.styleable.PieChart_centerLabel) ?: DEFAULT_CENTER_LABEL
            legendsSize = it.getDimension(R.styleable.PieChart_legendsSize, spToPx(DEFAULT_LEGENDS_SIZE))
            centerLabelSize = it.getDimension(R.styleable.PieChart_centerLabelSize, spToPx(DEFAULT_CENTER_LABEL_SIZE))
            centerLabelColor = it.getColor(R.styleable.PieChart_centerLabelColor, DEFAULT_CENTER_LABEL_COLOR)
            isCenterBackgroundEnabled = it.getInt(R.styleable.PieChart_centerBackground, 0) == 1
            centerBackgroundColor = it.getColor(R.styleable.PieChart_centerBackgroundColor, DEFAULT_CENTER_BACKGROUND_COLOR)
            centerBackgroundRatio = it.getFloat(R.styleable.PieChart_centerBackgroundRatio, DEFAULT_CENTER_BACKGROUND_RATIO)
            centerBackgroundAlpha = it.getFloat(R.styleable.PieChart_centerBackgroundAlpha, DEFAULT_CENTER_BACKGROUND_ALPHA)
            legendsTitle = it.getString(R.styleable.PieChart_legendsTitle) ?: DEFAULT_LEGENDS_TITLE
            legendsTitleSize = it.getDimension(R.styleable.PieChart_legendsTitleSize, spToPx(DEFAULT_LEGENDS_TITLE_SIZE))
            legendsPercentageSize = it.getDimension(R.styleable.PieChart_legendsPercentageSize, spToPx(DEFAULT_LEGENDS_PERCENTAGE_SIZE))
            legendsPercentageColor = it.getColor(R.styleable.PieChart_legendsPercentageColor, DEFAULT_LEGENDS_PERCENTAGE_COLOR)
            legendIconsTint = getIconTint(it, R.styleable.PieChart_legendIconsTint)
            centerLabelIconTint = getIconTint(it, R.styleable.PieChart_centerLabelIconTint)
            legendIconsTintArray = getColorArray(it, R.styleable.PieChart_legendIconsTintArray)
            legendsMargin = it.getDimension(R.styleable.PieChart_legendsMargin, dpToPx(DEFAULT_LEGENDS_MARGIN))
            legendsColor = it.getColor(R.styleable.PieChart_legendsColor, DEFAULT_LEGENDS_COLOR)
            legendBoxBackgroundColor = it.getColor(R.styleable.PieChart_legendBoxBackgroundColor, DEFAULT_LEGEND_BOX_BACKGROUND_COLOR)
            legendBoxMargin = it.getDimension(R.styleable.PieChart_legendBoxMargin, dpToPx(DEFAULT_LEGEND_BOX_MARGIN))
            legendTitleMargin = it.getDimension(R.styleable.PieChart_legendTitleMargin, dpToPx(DEFAULT_LEGEND_TITLE_MARGIN))
            legendBoxPadding = it.getDimension(R.styleable.PieChart_legendBoxPadding, dpToPx(DEFAULT_LEGEND_BOX_PADDING))
            legendsPercentageMargin = it.getDimension(R.styleable.PieChart_legendsPercentageMargin, dpToPx(DEFAULT_LEGENDS_PERCENTAGE_MARGIN))
            legendBoxBorder = it.getDimension(R.styleable.PieChart_legendBoxBorder, dpToPx(DEFAULT_LEGEND_BOX_BORDER))
            legendBoxBorderCornerRadius = it.getDimension(R.styleable.PieChart_legendBoxBorderCornerRadius, dpToPx(DEFAULT_LEGEND_BOX_BORDER_CORNER_RADIUS))
            legendBoxBorderAlpha = it.getFloat(R.styleable.PieChart_legendBoxBorderAlpha, DEFAULT_LEGEND_BOX_BORDER_ALPHA)
            legendBoxBorderColor = it.getColor(R.styleable.PieChart_legendBoxBorderColor, DEFAULT_LEGEND_BOX_BORDER_COLOR)
            legendBoxBorderDashArray = parseBorderDashArray(it.getString(R.styleable.PieChart_legendBoxBorderDashArray) ?: DEFAULT_LEGEND_BOX_BORDER_DASH_ARRAY)
            legendIconsAlpha = it.getFloat(R.styleable.PieChart_legendIconsAlpha, DEFAULT_LEGEND_ICONS_ALPHA)
            legendsTitleColor = it.getColor(R.styleable.PieChart_legendsTitleColor, DEFAULT_LEGENDS_TITLE_COLOR)
            shouldCenterPie = it.getBoolean(R.styleable.PieChart_shouldCenterPie, DEFAULT_SHOULD_CENTER_PIE)
            val slicesPointerLength = it.getDimension(R.styleable.PieChart_slicesPointerLength, -1f)
            val slicesPointerWidth = it.getDimension(R.styleable.PieChart_slicesPointerWidth, -1f)
            slicesPointer = if (slicesPointerLength <= 0 || slicesPointerWidth <= 0) defaultSlicesPointer else SlicePointer(slicesPointerLength, slicesPointerWidth, 0)
            isLegendsPercentageEnabled = it.getInt(R.styleable.PieChart_legendsPercentage, 0) == 1
            legendsAlignment = Alignment.values()[
                    it.getInt(R.styleable.PieChart_legendsAlignment, defaultLegendsAlignment.ordinal)
            ]
            legendBoxAlignment = Alignment.values()[
                    it.getInt(R.styleable.PieChart_legendBoxAlignment, defaultLegendBoxAlignment.ordinal)
            ]
            legendBoxBorderType = BorderType.values()[
                    it.getInt(R.styleable.PieChart_legendBoxBorderType, defaultLegendBoxBorderType.ordinal)
            ]
            labelIconsPlacement = IconPlacement.values()[
                    it.getInt(R.styleable.PieChart_labelIconsPlacement, defaultLabelIconsPlacement.ordinal)
            ]
            legendType = LegendType.values()[
                    it.getInt(R.styleable.PieChart_legendType, defaultLegendType.ordinal)
            ]
            labelType = LabelType.values()[
                    it.getInt(R.styleable.PieChart_labelType, defaultLabelType.ordinal)
            ]
            legendsIcon = DefaultIcons.values()[
                    it.getInt(R.styleable.PieChart_legendsIcon, defaultLegendsIcon.ordinal)
            ]
            centerLabelIcon = DefaultIcons.values()[
                    it.getInt(R.styleable.PieChart_centerLabelIcon, defaultCenterLabelIcon.ordinal)
            ]
            gapPosition = GapPosition.values()[
                    it.getInt(R.styleable.PieChart_gapPosition, defaultGapPosition.ordinal)
            ]
            gradientType = GradientType.values()[
                    it.getInt(R.styleable.PieChart_gradientType, defaultGradientType.ordinal)
            ]
            drawDirection = DrawDirection.values()[
                    it.getInt(R.styleable.PieChart_drawDirection, defaultDrawDirection.ordinal)
            ]
        }
    }

    private fun getFont(typedArray: TypedArray, @StyleableRes font: Int, defaultFont: Typeface): Typeface {
        val fontId = typedArray.getResourceId(font, -1)
        return if (fontId == -1) defaultFont else ResourcesCompat.getFont(context, fontId)!!
    }

    private fun getIconTint(typedArray: TypedArray, @StyleableRes attrName: Int): Int? {
        // Do not use -1 as no color; -1 is white: https://stackoverflow.com/a/30430194
        val tint = typedArray.getColor(attrName, /* if user specified no value or @null */ Int.MAX_VALUE)
        return if (tint == Int.MAX_VALUE) null else tint
    }

    @ColorInt
    private fun getColorArray(typedArray: TypedArray, @StyleableRes attrName: Int): IntArray {
        val arrayId = typedArray.getResourceId(attrName, -1)
        return if (arrayId == -1) intArrayOf() else resources.getIntArray(arrayId)
    }

    /**
     * This method is called when your view is first assigned a size, and again
     * if the size of your view changes for any reason.
     *
     * Calculate positions, dimensions, and any other values related to your
     * view's size in onSizeChanged(), instead of recalculating them every time you draw.
     *
     * When your view is assigned a size, the layout manager assumes that
     * the size includes all of the view's padding.
     * You must handle the padding values when you calculate your view's size.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)


        val legendsTitle = Text(legendsTitle, size = legendsTitleSize, color = legendsTitleColor, font = DEFAULT, margins = Margins(bottom = legendTitleMargin))
        val legends = mutableListOf<Box>()
        for (slice in slices) {
            var legendDrawable: Drawable? = null
            slice.legendIcon?.let { iconId ->
                legendDrawable = resources.getDrawable(iconId, null)
                slice.labelIconTint?.let { tint -> legendDrawable?.setTint(tint) }
            }
            val legendIcon = Icon(legendDrawable?:resources.getDrawable(legendsIcon.resId, null), slice.legendIconHeight?: legendIconsHeight, tint= slice.legendIconTint?:legendIconsTint, alpha = slice.legendIconAlpha ?: legendIconsAlpha)
            val legendText = Text(slice.legend, size = slice.legendSize ?: legendsSize, color = slice.legendColor?: legendsColor, margins = Margins(start = slice.legendIconMargin?:legendIconsMargin, end = slice.legendPercentageMargin ?: legendsPercentageMargin), font = DEFAULT)
            val legendComponents = mutableListOf<Box>()
            legendComponents.add(legendIcon)
            legendComponents.add(legendText)
            if (isLegendsPercentageEnabled) {
                val legendPercentage = Text(NumberFormat.getPercentInstance().format(slice.fraction), size = slice.legendPercentageSize?: legendsPercentageSize, color = slice.legendPercentageColor?: legendsPercentageColor, font = DEFAULT)
                legendComponents.add(legendPercentage)
            }
            /* FIXME: The first legend should not have start margin and the last legend should not have end margin (user can achieve first start margin and last end margin with parent padding) */
            val legend = Container(legendComponents, childrenAlignment = Alignment.CENTER, layoutDirection = LayoutDirection.HORIZONTAL, margins = Margins(start = legendsMargin, end = legendsMargin))
            legends.add(legend)
        }


        if (legendType == LegendType.BOTTOM_HORIZONTAL || legendType == LegendType.TOP_HORIZONTAL) {
            val legendsContainer = Container(children = legends, childrenAlignment = legendsAlignment, layoutDirection = LayoutDirection.HORIZONTAL)
            legendsBox = Container(children = listOf(legendsTitle, legendsContainer), childrenAlignment = Alignment.CENTER, layoutDirection = LayoutDirection.VERTICAL, background = Background(legendBoxBackgroundColor), paddings = Paddings(legendBoxPadding), border = Border(legendBoxBorder, color = legendBoxBorderColor, alpha = legendBoxBorderAlpha, cornerRadius = legendBoxBorderCornerRadius, type = legendBoxBorderType, dashArray = legendBoxBorderDashArray))
        } else if (legendType == LegendType.START_VERTICAL || legendType == LegendType.IN_HOLE_VERTICAL || legendType == LegendType.END_VERTICAL) {
            val legendsContainer = Container(children = legends, childrenAlignment = legendsAlignment, layoutDirection = LayoutDirection.VERTICAL)
            legendsBox = Container(children = listOf(legendsTitle, legendsContainer), childrenAlignment = Alignment.CENTER, layoutDirection = LayoutDirection.VERTICAL, background = Background(legendBoxBackgroundColor), paddings = Paddings(legendBoxPadding), border = Border(legendBoxBorder, color = legendBoxBorderColor, alpha = legendBoxBorderAlpha, cornerRadius = legendBoxBorderCornerRadius, type = legendBoxBorderType, dashArray = legendBoxBorderDashArray))
        }
        val direction = if (layoutDirection == LAYOUT_DIRECTION_LTR) LTR else RTL
        var newPaddings = Paddings(0f)
        var legendsRectLeft = 0f
        var legendsRectTop = 0f
        var legendsRectHeight = 0f
        var legendsRectWidth = 0f
        if (legendType == LegendType.BOTTOM_HORIZONTAL) {
            val maxAvailableWidth = (width - paddingLeft - paddingRight).toFloat()
            val maxAvailableHeight = (height - paddingTop - paddingBottom) / 2f // Arbitrary
            legendsRectHeight = min(maxAvailableHeight, legendsBox.height)
            legendsRectWidth = min(maxAvailableWidth, legendsBox.width)
            legendsRectLeft = when (legendBoxAlignment) {
                Alignment.START -> 0f
                Alignment.CENTER -> max(0f, (maxAvailableWidth - legendsRectWidth) / 2f)
                Alignment.END -> width-paddingEnd-legendsRectWidth
            }
            legendsRectTop = height - paddingBottom - legendsRectHeight
            newPaddings = Paddings(paddingTop.toFloat(), paddingBottom+legendsRectHeight+legendBoxMargin, paddingStart.toFloat(), paddingEnd.toFloat())
        } else if (legendType == LegendType.TOP_HORIZONTAL) {
            val maxAvailableWidth = (width - paddingLeft - paddingRight).toFloat()
            val maxAvailableHeight = (height - paddingTop - paddingBottom) / 2f // Arbitrary
            legendsRectHeight = min(maxAvailableHeight, legendsBox.height)
            legendsRectWidth = min(maxAvailableWidth, legendsBox.width)
            legendsRectLeft = when (legendBoxAlignment) {
                Alignment.START -> 0f
                Alignment.CENTER -> max(0f, (maxAvailableWidth - legendsRectWidth) / 2f)
                Alignment.END -> width-paddingEnd-legendsRectWidth
            }
            legendsRectTop = paddingTop.toFloat()
            newPaddings = Paddings(paddingTop.toFloat()+legendsRectHeight+legendBoxMargin, paddingBottom.toFloat(), paddingStart.toFloat(), paddingEnd.toFloat())
        }else if (legendType == LegendType.START_VERTICAL) {
            val maxAvailableWidth = (width - paddingLeft - paddingRight) / 2f
            val maxAvailableHeight = (height - paddingTop - paddingBottom).toFloat()
            legendsRectHeight = min(maxAvailableHeight, legendsBox.height)
            legendsRectWidth = min(maxAvailableWidth, legendsBox.width)
            legendsRectLeft = paddingLeft.toFloat()
            legendsRectTop = when (legendBoxAlignment) {
                Alignment.START -> 0f
                Alignment.CENTER -> max(0f, (maxAvailableHeight - legendsRectHeight) / 2f)
                Alignment.END -> height-paddingBottom-legendsRectHeight
            }
            newPaddings = Paddings(paddingTop.toFloat(), paddingBottom.toFloat(), paddingStart+ legendsRectWidth+legendBoxMargin, paddingEnd.toFloat())
        }else if (legendType == LegendType.IN_HOLE_VERTICAL) {
            val maxAvailableWidth = (width - paddingLeft - paddingRight) / 3f
            val maxAvailableHeight = (height - paddingTop - paddingBottom).toFloat()
            legendsRectHeight = min(maxAvailableHeight, legendsBox.height)
            legendsRectWidth = min(maxAvailableWidth, legendsBox.width)
            legendsRectLeft = (width / 2f) - (legendsRectWidth / 2f)
            legendsRectTop = max(0f, (maxAvailableHeight - legendsRectHeight) / 2f)
            newPaddings = Paddings(paddingTop.toFloat(), paddingBottom.toFloat(), paddingStart.toFloat(), paddingEnd.toFloat())
        }else if (legendType == LegendType.END_VERTICAL) {
            val maxAvailableWidth = (width - paddingLeft - paddingRight) / 2f
            val maxAvailableHeight = (height - paddingTop - paddingBottom).toFloat()
            legendsRectHeight = min(maxAvailableHeight, legendsBox.height)
            legendsRectWidth = min(maxAvailableWidth, legendsBox.width)
            legendsRectLeft = width - paddingRight - legendsRectWidth
            legendsRectTop = when (legendBoxAlignment) {
                Alignment.START -> 0f
                Alignment.CENTER -> max(0f, (maxAvailableHeight - legendsRectHeight) / 2f)
                Alignment.END -> height-paddingBottom-legendsRectHeight
            }
            newPaddings = Paddings(paddingTop.toFloat(), paddingBottom.toFloat(), paddingStart.toFloat(), paddingEnd+ legendsRectWidth+legendBoxMargin)
        } else {
            legendsBox = EmptyBox()
        }
        legendsRect = RectF(legendsRectLeft, legendsRectTop, legendsRectLeft + legendsRectWidth, legendsRectTop + legendsRectHeight)
        legendsBox.layOut(legendsRectTop, legendsRectLeft, direction)




        // FIXME: modify the following methods in this way:
        //  val drawableArea = calculateDrawableArea(paddings)
        //  val legendsBoxHeight = max(legendsBoxHeight, view.height/2)
        //  drawableArea = drawableArea - legendsBox.height // if legends box on top or bottom
        //  val pieRadius = calculateRadius(drawableArea, ...)
        //  val pieCenter = calculateRadius(drawableArea, ...)

        totalDrawableRect.set(0f+paddingLeft, 0f + paddingTop, width - paddingRight.toFloat(), height - paddingBottom.toFloat())
        pieRadius = calculateRadius(width, height, newPaddings.start.toInt(), newPaddings.end.toInt(), newPaddings.top.toInt(), newPaddings.bottom.toInt())
        center = calculateCenter(width, height, newPaddings.start.toInt(), newPaddings.end.toInt(), newPaddings.top.toInt(), newPaddings.bottom.toInt())
        val (top, left, right, bottom) = calculateBoundaries(center, pieRadius)
        pieEnclosingRect.set(RectF(left, top, right, bottom))


        if (labelType == OUTSIDE) {
            val defaults = Defaults(outsideLabelsMargin, labelsSize, labelsColor, labelsFont, labelIconsHeight, labelIconsMargin, labelIconsPlacement)
            pieEnclosingRect.set(calculatePieNewBoundsForOutsideLabel(context, pieEnclosingRect, slices, drawDirection, startAngle, defaults, shouldCenterPie))
            center = Coordinates((pieEnclosingRect.left + pieEnclosingRect.right) / 2f, (pieEnclosingRect.top + pieEnclosingRect.bottom) / 2f)
            pieRadius = pieEnclosingRect.width() / 2f
        } else if (labelType == OUTSIDE_CIRCULAR_INWARD || labelType == OUTSIDE_CIRCULAR_OUTWARD) {
            val defaults = Defaults(outsideLabelsMargin, labelsSize, labelsColor, labelsFont, labelIconsHeight, labelIconsMargin, labelIconsPlacement)
            pieEnclosingRect.set(calculatePieNewBoundsForOutsideCircularLabel(context, pieEnclosingRect, slices, defaults, shouldCenterPie))
            center = Coordinates((pieEnclosingRect.left + pieEnclosingRect.right) / 2f, (pieEnclosingRect.top + pieEnclosingRect.bottom) / 2f)
            pieRadius = pieEnclosingRect.width() / 2f
        }

        pie.reset()
        val overlayRadius = overlayRatio * pieRadius
        overlay.set(Path().apply { addCircle(center.x, center.y, overlayRadius, Path.Direction.CW) })


        val centerLabelIcon = Icon(resources.getDrawable(centerLabelIcon.resId, null), centerLabelIconHeight, tint = centerLabelIconTint, alpha = centerLabelIconAlpha, margins = Margins(end = centerLabelIconMargin))
        val centerLabelText = Text(centerLabel, size = centerLabelSize, color = centerLabelColor, font = centerLabelFont, alpha = centerLabelAlpha)
        centerLabelBox = Container(listOf(centerLabelIcon, centerLabelText), childrenAlignment = Alignment.CENTER, layoutDirection = LayoutDirection.HORIZONTAL)
        centerLabelBox.layOut(center.y - centerLabelBox.height / 2f, center.x - centerLabelBox.width / 2f, LTR)
    }

    private fun parseBorderDashArray(string: String) = string
        .replace(Regex("""[,;]"""), " ")
        .replace(Regex("""\s+"""), " ")
        .split(" ")
        .map { it.toFloat() }

    private fun makeGaps(): Path {
        val gaps = Path()
        var angle = startAngle.toFloat()
        for (slice in slices) {
            angle = calculateEndAngle(angle, slice.fraction, drawDirection)
            val (c1, c2, c3, c4) = calculateGapCoordinates(center, angle, gap, pieRadius, gapPosition)
            gaps.moveTo(c1.x, c1.y)
            gaps.lineTo(c2.x, c2.y)
            gaps.lineTo(c3.x, c3.y)
            gaps.lineTo(c4.x, c4.y)
            gaps.close()
        }
        return gaps
    }

    /**
     * The clip path (and maybe some other features) do not work on
     * emulators with hardware acceleration enabled.
     *
     * Try to disable hardware acceleration of device
     * or disable hardware acceleration for activity or whole application
     * or call `setLayerType(LAYER_TYPE_SOFTWARE, null)` here to use software rendering.
     * See the following posts:
     * [1](https://stackoverflow.com/q/16889815),
     * [2](https://stackoverflow.com/q/16432565),
     * [3](https://stackoverflow.com/q/8895677),
     * [4](https://stackoverflow.com/a/23517980),
     * [5](https://stackoverflow.com/q/13672802)
     *
     * Another solution would be to not use clip path and instead use the
     * operations on the paths themselves
     * (differencing the gaps and the hole from the pie and the overlay path).
     */
    override fun onDraw(canvas: Canvas) {
        /**
         * The android.graphics framework divides drawing into two areas:
         * - What to draw, handled by Canvas
         * - How to draw, handled by Paint.
         * Simply put, Canvas defines shapes that you can draw on the screen,
         * while Paint defines the color, style, font, and so forth of each shape you draw.
         *
         * So, before you can call any drawing methods, it's necessary to create a Paint object.
         *
         * Creating objects ahead of time is an important optimization. Views are redrawn very frequently,
         * and many drawing objects require expensive initialization. Creating drawing objects within your
         * onDraw() method significantly reduces performance and can make your UI appear sluggish.
         */

        if (isCenterBackgroundEnabled) {
            mainPaint.color = centerBackgroundColor
            mainPaint.alpha = (centerBackgroundAlpha * 255).toInt()
            canvas.drawCircle(center.x, center.y, centerBackgroundRatio * pieRadius, mainPaint)
        }

        var currentAngle = startAngle.toFloat()
        for (slice in slices) {

            val gradient = if (gradientType == RADIAL) {
                RadialGradient(center.x, center.y, pieRadius, slice.color, slice.colorEnd, Shader.TileMode.MIRROR)
            } else {
                val colors = slices.map {  listOf(it.color, it.colorEnd) }.flatten().toIntArray()
                val positions = slices.map { it.fraction }
                    .scan(listOf(0f)) { acc, value -> listOf(acc.first() + value, acc.first() + value) }
                    .flatten()
                    .dropLast(1)
                    .toFloatArray()
                val sweepGradient = SweepGradient(center.x, center.y, colors, positions)
                // Adjust the start angle
                val sweepGradientDefaultStartAngle = 0f
                val rotate = startAngle - sweepGradientDefaultStartAngle
                val gradientMatrix = Matrix()
                gradientMatrix.preRotate(rotate, center.x, center.y)
                sweepGradient.setLocalMatrix(gradientMatrix)
                sweepGradient
            }

            mainPaint.shader = gradient



            // FIXME: Move clip object creation out of the for loop
            val rect = Path().apply { addRect(totalDrawableRect, Path.Direction.CW) }
            val holeRadius = holeRatio * pieRadius
            val hole = Path().apply { addCircle(center.x, center.y, holeRadius, Path.Direction.CW) }
            gaps = makeGaps()
            // Could also have set the fillType to EVEN_ODD and just add the other paths to the clip
            // Or could abandon using clip path and do the operations on the pie itself
            // Clipping should be applied before drawing other things
            clip.set(rect - hole - gaps)
            val slicePath = makeSlice(center, pieEnclosingRect, currentAngle, slice.fraction, drawDirection, slice.pointer ?: slicesPointer)
            canvas.withClip(clip) {
                canvas.drawPath(slicePath, mainPaint)
                mainPaint.shader = null
                mainPaint.color = ContextCompat.getColor(context, android.R.color.black) // or better Color.BLACK
                mainPaint.alpha = (overlayAlpha * 255).toInt()
                canvas.drawPath(overlay, mainPaint)
            }

            updatePaintForLabel(mainPaint, slice.labelSize ?: labelsSize, slice.labelColor ?: labelsColor, slice.labelFont ?: labelsFont)

            val middleAngle = calculateMiddleAngle(currentAngle, slice.fraction, drawDirection)

            if (labelType == NONE) {
                // Do nothing
            } else if (labelType == OUTSIDE) {
                var labelIcon : Drawable? = null
                slice.labelIcon?.let { iconId ->
                    labelIcon = resources.getDrawable(iconId, null)
                    slice.labelIconTint?.let { tint -> labelIcon?.setTint(tint) }
                }
                val outsideLabelMargin = slice.outsideLabelMargin ?: outsideLabelsMargin
                val iconPlacement = slice.labelIconPlacement  ?: labelIconsPlacement
                val iconMargin = slice.labelIconMargin ?: labelIconsMargin
                val iconHeight = slice.labelIconHeight ?: labelIconsHeight
                val labelBounds = calculateLabelBounds(slice.label, mainPaint)
                val iconBounds = calculateIconBounds(labelIcon, iconHeight)
                val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
                val absoluteCombinedBounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(labelAndIconCombinedBounds, middleAngle, center, pieRadius, outsideLabelMargin)
                val iconAbsoluteBounds = calculateBoundsForOutsideLabelIcon(absoluteCombinedBounds, iconBounds, iconPlacement)
                val labelCoordinates = calculateCoordinatesForOutsideLabel(absoluteCombinedBounds, labelBounds, mainPaint, iconPlacement)
                canvas.drawText(slice.label, labelCoordinates.x, labelCoordinates.y, mainPaint)
                labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                labelIcon?.draw(canvas)





                // This block of code is for debugging
                /*val rect = RectF(labelCoordinates.x - labelBounds.width() / 2f, labelCoordinates.y + mainPaint.ascent(), labelCoordinates.x + labelBounds.width() / 2f, labelCoordinates.y + mainPaint.descent())
                mainPaint.style = Paint.Style.STROKE
                mainPaint.color = Color.RED
                canvas.drawRect(rect, mainPaint)
                mainPaint.style = Paint.Style.FILL
                canvas.drawCircle(labelCoordinates.x, labelCoordinates.y, 4f, mainPaint)

                mainPaint.style = Paint.Style.STROKE
                mainPaint.color = Color.BLUE
                canvas.drawRect(absoluteCombinedBounds, mainPaint)
                mainPaint.style = Paint.Style.FILL
                canvas.drawCircle(absoluteCombinedBounds.centerX(), absoluteCombinedBounds.centerY(), 4f, mainPaint)

                mainPaint.style = Paint.Style.STROKE
                mainPaint.color = Color.MAGENTA
                val pieCenterMarker = Path()
                pieCenterMarker.moveTo(center.x, center.y - 20)
                pieCenterMarker.lineTo(center.x, center.y - 200)
                pieCenterMarker.moveTo(center.x, center.y + 20)
                pieCenterMarker.lineTo(center.x, center.y + 200)
                pieCenterMarker.moveTo(center.x - 20, center.y)
                pieCenterMarker.lineTo(center.x - 200, center.y)
                pieCenterMarker.moveTo(center.x + 20, center.y)
                pieCenterMarker.lineTo(center.x + 200, center.y)
                canvas.drawPath(pieCenterMarker, mainPaint)
                mainPaint.style = Paint.Style.FILL*/




            } else if (labelType == OUTSIDE_CIRCULAR_INWARD || labelType == OUTSIDE_CIRCULAR_OUTWARD) {
                val isOutward = labelType == OUTSIDE_CIRCULAR_OUTWARD
                var labelIcon : Drawable? = null
                slice.labelIcon?.let { iconId ->
                    labelIcon = resources.getDrawable(iconId, null)
                    (slice.labelIconTint ?: labelIconsTint)?.let { labelIcon?.setTint(it) }
                }
                val outsideLabelMargin = slice.outsideLabelMargin ?: outsideLabelsMargin
                val iconPlacement = slice.labelIconPlacement ?: labelIconsPlacement
                val iconMargin = slice.labelIconMargin ?: labelIconsMargin
                val iconHeight = slice.labelIconHeight ?: labelIconsHeight
                val iconBounds = calculateIconBounds(labelIcon, iconHeight)
                val pathForLabel = makePathForOutsideCircularLabel(middleAngle, center, pieRadius, slice.label, mainPaint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin, isOutward)
                val iconRotation = calculateIconRotationAngleForOutsideCircularLabel(middleAngle, pieRadius, outsideLabelMargin, slice.label, mainPaint, iconBounds, iconMargin, iconPlacement, isOutward)
                val iconAbsoluteBounds = calculateIconAbsoluteBoundsForOutsideCircularLabel(middleAngle, center, pieRadius, slice.label, mainPaint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin)
                canvas.drawTextOnPath(slice.label, pathForLabel, 0f, 0f, mainPaint)
                canvas.withRotation(iconRotation, iconAbsoluteBounds.centerX(), iconAbsoluteBounds.centerY()) {
                    labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                    labelIcon?.draw(canvas)
                }




                // This block of code is for debugging
                /*mainPaint.style = Paint.Style.STROKE
                val radius = pieRadius + outsideLabelMargin + max(iconBounds.height(), calculateLabelBounds(slice.label, mainPaint).height()) / 2f
                val totalFraction = (iconBounds.width() + iconMargin + calculateLabelBounds(slice.label, mainPaint).width()) / (2 * PI.toFloat() * radius)
                val startAngle = calculateEndAngle(middleAngle, totalFraction / 2f, DrawDirection.COUNTER_CLOCKWISE)
                val sweepAngle = totalFraction * 360f
                val bounds = RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
                canvas.drawArc(bounds, startAngle, sweepAngle, true, mainPaint)
                mainPaint.style = Paint.Style.STROKE
                mainPaint.color = Color.RED
                canvas.drawPath(pathForLabel, mainPaint)*/



            } else {
                var labelIcon : Drawable? = null
                slice.labelIcon?.let { iconId ->
                    labelIcon = resources.getDrawable(iconId, null)
                    slice.labelIconTint?.let { tint -> labelIcon?.setTint(tint) }
                }
                val iconPlacement = slice.labelIconPlacement  ?: labelIconsPlacement
                val iconMargin = slice.labelIconMargin ?: labelIconsMargin
                val iconHeight = slice.labelIconHeight ?: labelIconsHeight
                val labelOffset = /* TODO: add slice.LabelOffset ?:*/ labelOffset
                val labelBounds = calculateLabelBounds(slice.label, mainPaint)
                val iconBounds = calculateIconBounds(labelIcon, iconHeight)
                val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
                val absoluteCombinedBounds = calculateAbsoluteBoundsForInsideLabelAndIcon(labelAndIconCombinedBounds, middleAngle, center, pieRadius, labelOffset)
                val iconAbsoluteBounds = calculateBoundsForOutsideLabelIcon(absoluteCombinedBounds, iconBounds, iconPlacement)
                val labelCoordinates = calculateCoordinatesForOutsideLabel(absoluteCombinedBounds, labelBounds, mainPaint, iconPlacement)
                canvas.drawText(slice.label, labelCoordinates.x, labelCoordinates.y, mainPaint)
                labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                labelIcon?.draw(canvas)
            }

            currentAngle = calculateEndAngle(currentAngle, slice.fraction, drawDirection)
        }

        // The center label gets clipped by the clip path and is not shown
        // mainPaint.color = ContextCompat.getColor(context, android.R.color.black)
        // mainPaint.textSize = labelSize
        // mainPaint.textAlign = Paint.Align.CENTER
        // val bounds = Rect()
        // mainPaint.getTextBounds(centerLabel, 0, centerLabel.length, bounds)
        // val textHeight = bounds.height()
        // canvas.drawText(centerLabel, centerX, centerY + (textHeight / 2), mainPaint)

        canvas.withClip(legendsRect) {
            legendsBox.draw(canvas)
        }
        centerLabelBox.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val (width, height) = calculateWidthAndHeight(widthMeasureSpec, heightMeasureSpec)
        // This MUST be called
        setMeasuredDimension(width, height)
    }
}
