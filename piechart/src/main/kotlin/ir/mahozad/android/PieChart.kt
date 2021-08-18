package ir.mahozad.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Typeface
import android.graphics.Typeface.DEFAULT
import android.util.AttributeSet
import android.view.View
import androidx.annotation.*
import androidx.annotation.Dimension.*
import androidx.annotation.IntRange
import androidx.core.content.withStyledAttributes
import ir.mahozad.android.PieChart.DefaultIcons.CIRCLE
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.PieChart.GapPosition.MIDDLE
import ir.mahozad.android.PieChart.GradientType.RADIAL
import ir.mahozad.android.PieChart.IconPlacement.START
import ir.mahozad.android.PieChart.LabelType.INSIDE
import ir.mahozad.android.PieChart.LegendPosition.*
import ir.mahozad.android.PieChart.SlicePointer
import ir.mahozad.android.component.*
import ir.mahozad.android.component.DrawDirection.LTR
import ir.mahozad.android.component.Icon
import ir.mahozad.android.unit.Dimension.PX
import ir.mahozad.android.util.calculatePieDimensions
import ir.mahozad.android.util.getColorArray
import ir.mahozad.android.util.getIconTint
import ir.mahozad.android.util.parseBorderDashArray
import java.text.NumberFormat

const val ENABLED = true
const val DISABLED = false
@Dimension(unit = DP) const val DEFAULT_PIE_RADIUS = 256
@IntRange(from = -360, to = 360) const val DEFAULT_START_ANGLE = -90
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_HOLE_RATIO = 0.25f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_OVERLAY_RATIO = 0.55f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_OVERLAY_ALPHA = 0.15f
const val DEFAULT_CENTER_LABEL_STATUS = DISABLED
const val DEFAULT_CENTER_BACKGROUND_STATUS = DISABLED
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_BACKGROUND_RATIO = 0.5f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_BACKGROUND_ALPHA = 1f
val DEFAULT_GAP = 8.px
val DEFAULT_LABELS_SIZE = 18.px
const val DEFAULT_LEGEND_STATUS = DISABLED
val DEFAULT_LEGENDS_SIZE = 16.px
val DEFAULT_LEGEND_BOX_MARGIN = 8.px
@Dimension(unit = DP) const val DEFAULT_LEGEND_TITLE_MARGIN = 8f
@Dimension(unit = DP) const val DEFAULT_LEGEND_LINES_MARGIN = 10f
val DEFAULT_LEGEND_BOX_PADDING = 4.px
val DEFAULT_LEGEND_BOX_BORDER = 2.px
val DEFAULT_LEGEND_BOX_BORDER_DASH_ARRAY = listOf(4.dp, 4.dp) /* ON length, OFF length */
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_LEGEND_BOX_BORDER_ALPHA = 0.4f
val DEFAULT_LEGEND_BOX_BORDER_CORNER_RADIUS = 3.px
val DEFAULT_LEGENDS_TITLE_SIZE = 18.px
@Dimension(unit = DP) const val DEFAULT_LEGEND_ICONS_MARGIN = 8f
const val DEFAULT_LEGEND_ICONS_ALPHA = 1f
const val DEFAULT_LEGEND_BOX_BORDER_STATUS = DISABLED
const val DEFAULT_LEGENDS_PERCENTAGE_STATUS = DISABLED
@Dimension(unit = DP) const val DEFAULT_LEGENDS_PERCENTAGE_MARGIN = 8f
val DEFAULT_LEGENDS_PERCENTAGE_SIZE = DEFAULT_LEGENDS_SIZE
val DEFAULT_LEGENDS_MARGIN = 4.px
val DEFAULT_LABEL_ICONS_HEIGHT = DEFAULT_LABELS_SIZE
val DEFAULT_LEGEND_ICONS_HEIGHT = DEFAULT_LEGENDS_SIZE
@Dimension(unit = DP) const val DEFAULT_LABEL_ICONS_MARGIN = 8f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_LABELS_OFFSET = 0.75f
@Dimension(unit = DP) const val DEFAULT_OUTSIDE_LABELS_MARGIN = 28f
const val DEFAULT_CENTER_LABEL = ""
@Dimension(unit = SP) const val DEFAULT_CENTER_LABEL_SIZE = 16f
@Dimension(unit = SP) const val DEFAULT_CENTER_LABEL_ICON_HEIGHT = DEFAULT_CENTER_LABEL_SIZE
@Dimension(unit = DP) const val DEFAULT_CENTER_LABEL_ICON_MARGIN = 8f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_LABEL_ALPHA = 1f
@FloatRange(from = 0.0, to = 1.0) const val DEFAULT_CENTER_LABEL_ICON_ALPHA = 1f
const val DEFAULT_LEGENDS_TITLE = ""
const val DEFAULT_SHOULD_CENTER_PIE = true
@ColorInt const val DEFAULT_LABELS_COLOR = Color.WHITE
@ColorInt const val DEFAULT_LEGENDS_COLOR = Color.WHITE
@ColorInt const val DEFAULT_LEGEND_BOX_BACKGROUND_COLOR = Color.TRANSPARENT
@ColorInt const val DEFAULT_LEGEND_BOX_BORDER_COLOR = Color.BLACK
@ColorInt const val DEFAULT_LEGENDS_TITLE_COLOR = Color.WHITE
@ColorInt const val DEFAULT_LEGENDS_PERCENTAGE_COLOR = Color.WHITE
@ColorInt const val DEFAULT_CENTER_LABEL_COLOR = Color.WHITE
@ColorInt const val DEFAULT_CENTER_BACKGROUND_COLOR = Color.GRAY
// If null, the colors of the icon itself is used
@ColorInt val defaultLabelIconsTint: Int? = null
@ColorInt val defaultCenterLabelIconTint: Int? = null
val defaultGapPosition = MIDDLE
val defaultGradientType = RADIAL
val defaultDrawDirection = CLOCKWISE
val defaultLabelIconsPlacement = START
val defaultLegendPosition = BOTTOM
val defaultLegendArrangement = PieChart.LegendArrangement.HORIZONTAL
val defaultLegendsIcon = CIRCLE
val defaultCenterLabelIcon = PieChart.DefaultIcons.NO_ICON
val defaultLegendsAlignment = Alignment.CENTER
val defaultLegendsTitleAlignment = Alignment.CENTER
val defaultLegendBoxAlignment = Alignment.CENTER
val defaultLegendsWrapping = Wrapping.WRAP
val defaultLabelType = INSIDE
val defaultLegendBoxBorderType = PieChart.BorderType.SOLID
val defaultLabelsFont: Typeface = DEFAULT
val defaultCenterLabelFont: Typeface = DEFAULT
val defaultSlicesPointer: SlicePointer? = null
val defaultLegendIconsTintArray: IntArray? = null
val defaultSlices = listOf(
    /* ContextCompat.getColor(context, android.R.color.holo_green_dark) */
    PieChart.Slice(0.43f, Color.HSVToColor(floatArrayOf(88f, 0.85f, 0.7f))),
    PieChart.Slice(0.21f, Color.HSVToColor(floatArrayOf(51f, 0.82f, 0.8f))),
    PieChart.Slice(0.19f, Color.HSVToColor(floatArrayOf(200f, 0.82f, 0.8f))),
    PieChart.Slice(0.14f, Color.HSVToColor(floatArrayOf(338f, 0.82f, 0.8f))),
    PieChart.Slice(0.03f, Color.HSVToColor(floatArrayOf(27f, 0.82f, 0.8f)))
)

/**
 * This is the order that these commonly used view methods are run:
 * 1. Constructor    // choose your desired size
 * 2. onMeasure      // parent will determine if your desired size is acceptable
 * 3. onSizeChanged
 * 4. onLayout
 * 5. onDraw         // draw your view content at the size specified by the parent
 *
 * See [this](https://stackoverflow.com/a/36717800/8583692) and
 * [this](https://stackoverflow.com/q/20670828/8583692) and
 * [this](https://medium.com/@mmlodawski/https-medium-com-mmlodawski-do-not-always-trust-jvmoverloads-5251f1ad2cfe)
 * for more information about the constructors.
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
class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

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
        @FloatRange(from = 0.0, to = 1.0) val labelOffset: Float? = null,
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
        @FloatRange(from = 0.0, to = 1.0) val legendIconAlpha: Float? = 1f,

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
    enum class LegendArrangement { HORIZONTAL, VERTICAL }
    enum class LegendPosition { TOP, BOTTOM, CENTER /* AKA IN_HOLE */, START, END, LEFT, RIGHT }

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

    var slices by Property(defaultSlices) {
        onSizeChanged(width, height, width, height)
    }

    var startAngleResource by IntegerResource(::startAngle)
    /**
     * Can be any integer number. It will be automatically normalized to range 0..360.
     */
    var startAngle by Property(DEFAULT_START_ANGLE, ::normalizeAngle) {
        // FIXME: Using reflection to check for variable initialization state
        if (::pie.isInitialized) {
            pie.setStartAngle(it)
            invalidate()
        }
    }

    var holeRatioResource by FractionResource(::holeRatio)
    var holeRatio by Property(DEFAULT_HOLE_RATIO, { it.coerceIn(0f, 1f) }) {
        if (::pie.isInitialized) {
            pie.setHoleRatio(it)
            invalidate()
        }
    }

    var overlayRatioResource by FractionResource(::overlayRatio)
    var overlayRatio by Property(DEFAULT_OVERLAY_RATIO, { it.coerceIn(0f, 1f) }) {
        if (::pie.isInitialized) {
            pie.setOverlayRatio(it)
            invalidate()
        }
    }

    var overlayAlphaResource by FractionResource(::overlayAlpha)
    var overlayAlpha by Property(DEFAULT_OVERLAY_ALPHA, { it.coerceIn(0f, 1f) }) {
        if (::pie.isInitialized) {
            pie.setOverlayAlpha(it)
            invalidate()
        }
    }

    var gapResource by DimensionResource(::gap)
    /**
     * Examples:
     *   - 4.px
     *   - 13.6.dp
     */
    var gap by Property(DEFAULT_GAP) {
        if (::pie.isInitialized) {
            pie.setGap(it.px)
            invalidate()
        }
    }

    var labelsSizeResource by DimensionResource(::labelsSize)
    /**
     * Examples:
     *   - 4.px
     *   - 13.6.dp
     */
    var labelsSize by Property(DEFAULT_LABELS_SIZE) {
        if (::pie.isInitialized) {
            pie.setLabelsSize(it.px)
            invalidate()
        }
    }

    var isLegendEnabledResource by BooleanResource(::isLegendEnabled)
    var isLegendEnabled by Property(DEFAULT_LEGEND_STATUS) {
        onSizeChanged(width, height, width, height)
    }

    var legendsSizeResource by DimensionResource(::legendsSize)
    var legendsSize by Property(DEFAULT_LEGENDS_SIZE) {
        onSizeChanged(width, height, width, height)
    }

    /**
     * Cannot define a resource version for enum properties (?).
     *
     * We could have accepted a style resource and then extract the
     * attribute value related to this enum property but that was dirty:
     * ```
     * <style name="CustomLegendsTitleAlignmentStyle">
     *   <item name="legendsTitleAlignment">start</item>
     * </style>
     * ```
     * In addition, in the future we can define a property named chartStyle
     * that accepts a style resource id and applies it to all the properties of
     * the chart instead of just a single enum property.
     *
     * See the commented EnumResource class in Properties.kt file.
     */
    var legendsTitleAlignment by Property(defaultLegendsTitleAlignment) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendsAlignment by Property(defaultLegendsAlignment) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendBoxAlignment by Property(defaultLegendBoxAlignment) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendsWrapping by Property(defaultLegendsWrapping) {
        onSizeChanged(width, height, width, height)
    }

    var legendsTitleResource by StringResource(::legendsTitle)
    var legendsTitle by Property(DEFAULT_LEGENDS_TITLE) {
        onSizeChanged(width, height, width, height)
    }

    var legendPosition by Property(defaultLegendPosition) {
        onSizeChanged(width, height, width, height)
    }

    var legendArrangement by Property(defaultLegendArrangement) {
        onSizeChanged(width, height, width, height)
    }

    var legendsMarginResource by DimensionResource(::legendsMargin)
    var legendsMargin by Property(DEFAULT_LEGENDS_MARGIN) {
        onSizeChanged(width, height, width, height)
    }

    var legendsColorResource by ColorResource(::legendsColor)
    var legendsColor by Property(DEFAULT_LEGENDS_COLOR) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendBoxBackgroundColorResource by ColorResource(::legendBoxBackgroundColor)
    var legendBoxBackgroundColor by Property(DEFAULT_LEGEND_BOX_BACKGROUND_COLOR) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    // FIXME: Changing these properties makes the legend box disappear
    var legendBoxMarginResource by DimensionResource(::legendBoxMargin)
    var legendBoxMargin by Property(DEFAULT_LEGEND_BOX_MARGIN) {
        onSizeChanged(width, height, width, height)
    }

    var legendBoxPaddingResource by DimensionResource(::legendBoxPadding)
    var legendBoxPadding by Property(DEFAULT_LEGEND_BOX_PADDING) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendBoxBorderResource by DimensionResource(::legendBoxBorder)
    var legendBoxBorder by Property(DEFAULT_LEGEND_BOX_BORDER) {
        onSizeChanged(width, height, width, height)
    }

    var legendBoxBorderCornerRadiusResource by DimensionResource(::legendBoxBorderCornerRadius)
    var legendBoxBorderCornerRadius by Property(DEFAULT_LEGEND_BOX_BORDER_CORNER_RADIUS) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendBoxBorderColorResource by ColorResource(::legendBoxBorderColor)
    var legendBoxBorderColor by Property(DEFAULT_LEGEND_BOX_BORDER_COLOR) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendBoxBorderAlphaResource by FractionResource(::legendBoxBorderAlpha)
    var legendBoxBorderAlpha by Property(DEFAULT_LEGEND_BOX_BORDER_ALPHA) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendBoxBorderType by Property(defaultLegendBoxBorderType) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    /**
     * A list containing the length of the ON segments and length of the OFF segments.
     * Example:
     * ```
     * listOf(4.dp, 10.px)
     * ```
     * Note that the [legendBoxBorderType] should be [PieChart.BorderType.DASHED].
     */
    var legendBoxBorderDashArray by Property(DEFAULT_LEGEND_BOX_BORDER_DASH_ARRAY) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendIconsAlphaResource by FractionResource(::legendIconsAlpha)
    var legendIconsAlpha by Property(DEFAULT_LEGEND_ICONS_ALPHA) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendsTitleColorResource by ColorResource(::legendsTitleColor)
    var legendsTitleColor by Property(DEFAULT_LEGENDS_TITLE_COLOR) {
        // TODO: No need to recalculate everything; provide a method in legend box for this
        onSizeChanged(width, height, width, height)
    }

    var legendsTitleSizeResource by DimensionResource(::legendsTitleSize)
    var legendsTitleSize by Property(DEFAULT_LEGENDS_TITLE_SIZE) {
        onSizeChanged(width, height, width, height)
    }

    var legendsPercentageSizeResource by DimensionResource(::legendsPercentageSize)
    var legendsPercentageSize by Property(DEFAULT_LEGENDS_PERCENTAGE_SIZE) {
        onSizeChanged(width, height, width, height)
    }

    var isLegendsPercentageEnabledResource by BooleanResource(::isLegendsPercentageEnabled)
    var isLegendsPercentageEnabled by Property(DEFAULT_LEGENDS_PERCENTAGE_STATUS) {
        onSizeChanged(width, height, width, height)
    }

    var isLegendBoxBorderEnabled = DEFAULT_LEGEND_BOX_BORDER_STATUS
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
    var legendLinesMargin = dpToPx(DEFAULT_LEGEND_LINES_MARGIN)
        set(@Dimension(unit = PX) margin) {
            field = margin
            invalidate()
        }

    var legendIconsHeightResource by DimensionResource(::legendIconsHeight)
    var legendIconsHeight by Property(DEFAULT_LEGEND_ICONS_HEIGHT) {
        onSizeChanged(width, height, width, height)
    }

    var legendIconsMargin = dpToPx(DEFAULT_LEGEND_ICONS_MARGIN)
        set(margin /* px */) {
            field = margin
            invalidate()
        }

    /**
     * If this array is not null it is used to tint icons.
     *
     * If user wants to assign the same color to all icons,
     * specify a single color literal or reference.
     * FIXME: Because of an unknown bug, when there is a single color,
     *  icons after the first one do not get tinted with that single color.
     *
     * If the array has fewer colors than there are icons, then the colors are used in circular way.
     */
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

    var isCenterBackgroundEnabled by Property(DEFAULT_CENTER_BACKGROUND_STATUS)
    var isCenterBackgroundEnabledResource by BooleanResource(::isCenterBackgroundEnabled)
    var centerBackgroundColor by Property(DEFAULT_CENTER_BACKGROUND_COLOR)
    var centerBackgroundColorResource by ColorResource(::centerBackgroundColor)
    var centerBackgroundRatio by Property(DEFAULT_CENTER_BACKGROUND_RATIO)
    var centerBackgroundRatioResource by FractionResource(::centerBackgroundRatio)
    var centerBackgroundAlpha by Property(DEFAULT_CENTER_BACKGROUND_ALPHA)
    var centerBackgroundAlphaResource by FractionResource(::centerBackgroundAlpha)
    var labelsOffset by Property(DEFAULT_LABELS_OFFSET)
    var labelsOffsetResource by FractionResource(::labelsOffset)

    var labelIconsHeightResource by DimensionResource(::labelIconsHeight)
    var labelIconsHeight by Property(DEFAULT_LABEL_ICONS_HEIGHT) {
        if (::pie.isInitialized) {
            pie.setLabelIconsHeight(it.px)
            invalidate()
        }
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
    var isCenterLabelEnabled = DEFAULT_CENTER_LABEL_STATUS
        set(shouldEnable) {
            field = shouldEnable
            invalidate()
        }
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
        set(position) {
            field = position
            invalidate()
        }
    var gradientType = defaultGradientType
        set(type) {
            field = type
            invalidate()
        }
    var drawDirection = defaultDrawDirection
        set(direction) {
            field = direction
            invalidate()
        }

    private val paint = Paint(ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private lateinit var pie: Pie
    private lateinit var chartBox: Box
    private lateinit var centerLabelBox: Box

    /**
     * Attributes are a powerful way of controlling the behavior and appearance of views,
     * but they can only be read when the view is initialized. To provide dynamic behavior,
     * expose a property getter and setter pair for each custom attribute.
     *
     * A good rule to follow is to always expose any property that affects the
     * visible appearance or behavior of your custom view.
     */
    init {
        // Could also have used context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).use {...}
        //  TypedArray objects are a shared resource and must be recycled after use (thus the .use {} above)
        // See https://stackoverflow.com/a/68803044/8583692
        context.withStyledAttributes(attrs, R.styleable.PieChart) {
            startAngle = normalizeAngle(getInt(R.styleable.PieChart_startAngle, DEFAULT_START_ANGLE))
            holeRatio = getFloat(R.styleable.PieChart_holeRatio, DEFAULT_HOLE_RATIO)
            overlayRatio = getFloat(R.styleable.PieChart_overlayRatio, DEFAULT_OVERLAY_RATIO)
            overlayAlpha = getFloat(R.styleable.PieChart_overlayAlpha, DEFAULT_OVERLAY_ALPHA)
            gap = PX(getDimension(R.styleable.PieChart_gap, DEFAULT_GAP.px))
            labelsSize = PX(getDimension(R.styleable.PieChart_labelsSize, DEFAULT_LABELS_SIZE.px))
            labelsOffset = getFloat(R.styleable.PieChart_labelsOffset, DEFAULT_LABELS_OFFSET)
            labelsColor = getColor(R.styleable.PieChart_labelsColor, DEFAULT_LABELS_COLOR)
            labelIconsTint = getIconTint(this, R.styleable.PieChart_labelIconsTint)
            labelsFont = getFont(context, R.styleable.PieChart_labelsFont, defaultLabelsFont)
            isCenterLabelEnabled = getInt(R.styleable.PieChart_centerLabelStatus, 0) == 1
            centerLabelFont = getFont(context, R.styleable.PieChart_centerLabelFont, defaultCenterLabelFont)
            centerLabelIconAlpha = getFloat(R.styleable.PieChart_centerLabelIconAlpha, DEFAULT_CENTER_LABEL_ICON_ALPHA)
            centerLabelAlpha = getFloat(R.styleable.PieChart_centerLabelAlpha, DEFAULT_CENTER_LABEL_ALPHA)
            labelIconsHeight = PX(getDimension(R.styleable.PieChart_labelIconsHeight, DEFAULT_LABEL_ICONS_HEIGHT.px))
            centerLabelIconHeight = getDimension(R.styleable.PieChart_centerLabelIconHeight, dpToPx(DEFAULT_CENTER_LABEL_ICON_HEIGHT))
            legendIconsHeight = PX(getDimension(R.styleable.PieChart_legendIconsHeight, DEFAULT_LEGEND_ICONS_HEIGHT.px))
            legendIconsMargin = getDimension(R.styleable.PieChart_legendIconsMargin, dpToPx(DEFAULT_LEGEND_ICONS_MARGIN))
            centerLabelIconMargin = getDimension(R.styleable.PieChart_centerLabelIconMargin, dpToPx(DEFAULT_CENTER_LABEL_ICON_MARGIN))
            labelIconsMargin = getDimension(R.styleable.PieChart_labelIconsMargin, dpToPx(DEFAULT_LABEL_ICONS_MARGIN))
            outsideLabelsMargin = getDimension(R.styleable.PieChart_outsideLabelsMargin, dpToPx(DEFAULT_OUTSIDE_LABELS_MARGIN))
            centerLabel = getString(R.styleable.PieChart_centerLabel) ?: DEFAULT_CENTER_LABEL
            centerLabelSize = getDimension(R.styleable.PieChart_centerLabelSize, spToPx(DEFAULT_CENTER_LABEL_SIZE))
            centerLabelColor = getColor(R.styleable.PieChart_centerLabelColor, DEFAULT_CENTER_LABEL_COLOR)
            isCenterBackgroundEnabled = getInt(R.styleable.PieChart_centerBackgroundStatus, 0) == 1
            centerBackgroundColor = getColor(R.styleable.PieChart_centerBackgroundColor, DEFAULT_CENTER_BACKGROUND_COLOR)
            centerBackgroundRatio = getFloat(R.styleable.PieChart_centerBackgroundRatio, DEFAULT_CENTER_BACKGROUND_RATIO)
            centerBackgroundAlpha = getFloat(R.styleable.PieChart_centerBackgroundAlpha, DEFAULT_CENTER_BACKGROUND_ALPHA)
            isLegendEnabled = getInt(R.styleable.PieChart_legendStatus, 0) == 1
            legendsSize = PX(getDimension(R.styleable.PieChart_legendsSize, DEFAULT_LEGENDS_SIZE.px))
            legendsTitle = getString(R.styleable.PieChart_legendsTitle) ?: DEFAULT_LEGENDS_TITLE
            legendsTitleSize = PX(getDimension(R.styleable.PieChart_legendsTitleSize, DEFAULT_LEGENDS_TITLE_SIZE.px))
            legendsPercentageSize = PX(getDimension(R.styleable.PieChart_legendsPercentageSize, DEFAULT_LEGENDS_PERCENTAGE_SIZE.px))
            legendsPercentageColor = getColor(R.styleable.PieChart_legendsPercentageColor, DEFAULT_LEGENDS_PERCENTAGE_COLOR)
            centerLabelIconTint = getIconTint(this, R.styleable.PieChart_centerLabelIconTint)
            legendIconsTintArray = getColorArray(this, R.styleable.PieChart_legendIconsTint)
            legendsMargin = PX(getDimension(R.styleable.PieChart_legendsMargin, DEFAULT_LEGENDS_MARGIN.px))
            legendsColor = getColor(R.styleable.PieChart_legendsColor, DEFAULT_LEGENDS_COLOR)
            legendBoxBackgroundColor = getColor(R.styleable.PieChart_legendBoxBackgroundColor, DEFAULT_LEGEND_BOX_BACKGROUND_COLOR)
            legendBoxMargin = PX(getDimension(R.styleable.PieChart_legendBoxMargin, DEFAULT_LEGEND_BOX_MARGIN.px))
            legendTitleMargin = getDimension(R.styleable.PieChart_legendTitleMargin, dpToPx(DEFAULT_LEGEND_TITLE_MARGIN))
            legendLinesMargin = getDimension(R.styleable.PieChart_legendLinesMargin, dpToPx(DEFAULT_LEGEND_LINES_MARGIN))
            legendBoxPadding = PX(getDimension(R.styleable.PieChart_legendBoxPadding, DEFAULT_LEGEND_BOX_PADDING.px))
            legendsPercentageMargin = getDimension(R.styleable.PieChart_legendsPercentageMargin, dpToPx(DEFAULT_LEGENDS_PERCENTAGE_MARGIN))
            legendBoxBorder = PX(getDimension(R.styleable.PieChart_legendBoxBorder, DEFAULT_LEGEND_BOX_BORDER.px))
            legendBoxBorderCornerRadius = PX(getDimension(R.styleable.PieChart_legendBoxBorderCornerRadius, DEFAULT_LEGEND_BOX_BORDER_CORNER_RADIUS.px))
            legendBoxBorderAlpha = getFloat(R.styleable.PieChart_legendBoxBorderAlpha, DEFAULT_LEGEND_BOX_BORDER_ALPHA)
            legendBoxBorderColor = getColor(R.styleable.PieChart_legendBoxBorderColor, DEFAULT_LEGEND_BOX_BORDER_COLOR)
            legendBoxBorderDashArray = parseBorderDashArray(getString(R.styleable.PieChart_legendBoxBorderDashArray)) ?: DEFAULT_LEGEND_BOX_BORDER_DASH_ARRAY
            legendIconsAlpha = getFloat(R.styleable.PieChart_legendIconsAlpha, DEFAULT_LEGEND_ICONS_ALPHA)
            legendsTitleColor = getColor(R.styleable.PieChart_legendsTitleColor, DEFAULT_LEGENDS_TITLE_COLOR)
            shouldCenterPie = getBoolean(R.styleable.PieChart_shouldCenterPie, DEFAULT_SHOULD_CENTER_PIE)
            val slicesPointerLength = getDimension(R.styleable.PieChart_slicesPointerLength, -1f)
            val slicesPointerWidth = getDimension(R.styleable.PieChart_slicesPointerWidth, -1f)
            slicesPointer = if (slicesPointerLength <= 0 || slicesPointerWidth <= 0) defaultSlicesPointer else SlicePointer(slicesPointerLength, slicesPointerWidth, 0)
            isLegendsPercentageEnabled = getInt(R.styleable.PieChart_legendsPercentageStatus, 0) == 1
            isLegendBoxBorderEnabled = getInt(R.styleable.PieChart_legendBoxBorderStatus, 0) == 1
            legendsTitleAlignment = getEnum(R.styleable.PieChart_legendsTitleAlignment, defaultLegendsTitleAlignment)
            legendsAlignment = getEnum(R.styleable.PieChart_legendsAlignment, defaultLegendsAlignment)
            legendBoxAlignment = getEnum(R.styleable.PieChart_legendBoxAlignment, defaultLegendBoxAlignment)
            legendsWrapping = getEnum(R.styleable.PieChart_legendsWrapping, defaultLegendsWrapping)
            legendBoxBorderType = getEnum(R.styleable.PieChart_legendBoxBorderType, defaultLegendBoxBorderType)
            labelIconsPlacement = getEnum(R.styleable.PieChart_labelIconsPlacement, defaultLabelIconsPlacement)
            legendPosition = getEnum(R.styleable.PieChart_legendPosition, defaultLegendPosition)
            legendArrangement = getEnum(R.styleable.PieChart_legendArrangement, defaultLegendArrangement)
            labelType = getEnum(R.styleable.PieChart_labelType, defaultLabelType)
            legendsIcon = getEnum(R.styleable.PieChart_legendsIcon, defaultLegendsIcon)
            centerLabelIcon = getEnum(R.styleable.PieChart_centerLabelIcon, defaultCenterLabelIcon)
            gapPosition = getEnum(R.styleable.PieChart_gapPosition, defaultGapPosition)
            gradientType = getEnum(R.styleable.PieChart_gradientType, defaultGradientType)
            drawDirection = getEnum(R.styleable.PieChart_drawDirection, defaultDrawDirection)
        }
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

        val maxAvailableWidthForLegendBox = when (legendPosition) {
            TOP, BOTTOM -> (width - paddingStart - paddingEnd).toFloat()
            LegendPosition.START, END -> (width - paddingStart - paddingEnd) / 2f // Arbitrary
            else -> (width - paddingStart - paddingEnd).toFloat()
        }
        val maxAvailableHeightForLegendBox = when (legendPosition) {
            TOP, BOTTOM -> (height - paddingTop - paddingBottom) / 2f // Arbitrary
            LegendPosition.START, END ->  (height - paddingTop - paddingBottom).toFloat()
            else -> (height - paddingTop - paddingBottom).toFloat()
        }

        val legendBox = LegendBuilder().createLegendBox(context, maxAvailableWidthForLegendBox, maxAvailableHeightForLegendBox, slices,legendIconsTintArray, legendsTitle, legendsTitleSize.px, legendsTitleColor, legendTitleMargin, legendsTitleAlignment, legendsIcon, legendIconsHeight.px, legendIconsAlpha, legendsSize.px, legendsColor, legendIconsMargin, legendsPercentageMargin, isLegendsPercentageEnabled, legendsPercentageSize.px, legendsPercentageColor, legendsMargin.px, legendArrangement, legendsAlignment, legendBoxBackgroundColor, legendBoxPadding.px, legendBoxBorder.px, legendBoxBorderColor, legendBoxBorderAlpha, legendBoxBorderCornerRadius.px, legendBoxBorderType, legendBoxBorderDashArray.map { it.px }, legendBoxMargin.px, legendPosition, legendLinesMargin, legendsWrapping, isLegendBoxBorderEnabled)
        val (pieWidth, pieHeight) = calculatePieDimensions(width, height, Paddings(paddingTop, paddingBottom, paddingStart, paddingEnd), isLegendEnabled, legendBoxMargin.px, legendPosition, legendBox.width, legendBox.height)
        pie = Pie(context, pieWidth, pieHeight, null, null, startAngle, slices, outsideLabelsMargin, labelType, labelsSize.px, labelsColor, labelsFont, labelIconsHeight.px, labelIconsMargin, labelIconsPlacement, labelIconsTint, labelsOffset, shouldCenterPie, drawDirection, overlayRatio, overlayAlpha, gradientType, holeRatio, slicesPointer, gap.px, gapPosition)
        val chartDirection = determineChartDirection(legendPosition)
        val chartComponents = makeChartComponentList(pie, isLegendEnabled, legendBox, legendPosition)
        chartBox = Container(chartComponents, width.toFloat(), height.toFloat(), chartDirection, legendBoxAlignment, paddings = Paddings(paddingTop, paddingBottom, paddingStart, paddingEnd))
        chartBox.layOut(0f, 0f, LTR)

        val centerLabelIcon = Icon(resources.getDrawable(centerLabelIcon.resId, null), centerLabelIconHeight, tint = centerLabelIconTint, alpha = centerLabelIconAlpha, margins = Margins(end = centerLabelIconMargin))
        val centerLabelText = Text(centerLabel, size = centerLabelSize, color = centerLabelColor, font = centerLabelFont, alpha = centerLabelAlpha)
        centerLabelBox = Container(listOf(centerLabelIcon, centerLabelText), width.toFloat()-paddingStart-paddingEnd, height.toFloat()-paddingTop - paddingBottom, childrenAlignment = Alignment.CENTER, layoutDirection = LayoutDirection.HORIZONTAL)
        centerLabelBox.layOut(pie.center.y - centerLabelBox.height / 2f, pie.center.x - centerLabelBox.width / 2f, LTR)
    }

    private fun makeChartComponentList(
        pie: Pie,
        isLegendEnabled: Boolean,
        legendBox: Box,
        legendPosition: LegendPosition
    ): List<Box> {
        return when {
            !isLegendEnabled -> listOf(pie)
            legendPosition == TOP -> listOf(legendBox, pie)
            legendPosition == BOTTOM -> listOf(pie, legendBox)
            legendPosition == LegendPosition.START -> listOf(legendBox, pie)
            legendPosition == LegendPosition.END -> listOf(pie, legendBox)
            else -> /* if == CENTER */ listOf(pie, legendBox)
        }
    }

    private fun determineChartDirection(legendPosition: LegendPosition) = when (legendPosition) {
        TOP, BOTTOM -> LayoutDirection.VERTICAL
        CENTER -> LayoutDirection.LAYERED
        else -> LayoutDirection.HORIZONTAL
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
            paint.color = centerBackgroundColor
            paint.alpha = (centerBackgroundAlpha * 255).toInt()
            val backgroundRadius = centerBackgroundRatio * pie.radius
            canvas.drawCircle(pie.center.x, pie.center.y, backgroundRadius, paint)
        }
        chartBox.draw(canvas)
        centerLabelBox.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val (width, height) = calculateWidthAndHeight(widthMeasureSpec, heightMeasureSpec)
        // This MUST be called
        setMeasuredDimension(width, height)
    }
}
