package io.github.mahozad.piechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.core.graphics.minus
import io.github.mahozad.piechart.PieChart.Direction.CLOCKWISE
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

const val DEFAULT_SIZE = 448
const val DEFAULT_START_ANGLE = -90
const val DEFAULT_HOLE_RATIO = 0.25f
const val DEFAULT_OVERLAY_RATIO = 0.55f
const val DEFAULT_OVERLAY_ALPHA = 0.25f
const val DEFAULT_GAP = 8f
val defaultDrawingDirection = CLOCKWISE

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
 * This call is usually combined with a call to invalidate().
 *
 * See [this helpful post](https://stackoverflow.com/a/42430834) for more information.
 */
class PieChart(context: Context, attrs: AttributeSet) : View(context, attrs) {

    data class Slice(
        @FloatRange(from = 0.0, to = 1.0) val fraction: Float,
        @ColorInt val color: Int,
        val label: String = fraction.toString(),
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

    enum class Direction { CLOCKWISE, COUNTER_CLOCKWISE }

    var startAngle = DEFAULT_START_ANGLE
        set(angle) {
            field = angle.coerceIn(-360, 360)
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
    var drawingDirection = defaultDrawingDirection
    val slices = mutableListOf(
        Slice(0.43f, ContextCompat.getColor(context, android.R.color.holo_green_dark)),
        Slice(0.21f, ContextCompat.getColor(context, android.R.color.holo_orange_dark)),
        Slice(0.19f, ContextCompat.getColor(context, android.R.color.holo_blue_dark)),
        Slice(0.15f, ContextCompat.getColor(context, android.R.color.holo_red_light)),
        Slice(0.02f, ContextCompat.getColor(context, android.R.color.holo_purple))
    )
    private val pie = Path()
    private val clip = Path()
    private val overlay = Path()
    private val enclosingRect = RectF()
    private var pieRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).apply {
            try {
                startAngle = getInt(R.styleable.PieChart_startAngle, DEFAULT_START_ANGLE)
                holeRatio = getFloat(R.styleable.PieChart_holeRatio, DEFAULT_HOLE_RATIO)
                overlayRatio = getFloat(R.styleable.PieChart_overlayRatio, DEFAULT_OVERLAY_RATIO)
                overlayAlpha = getFloat(R.styleable.PieChart_overlayAlpha, DEFAULT_OVERLAY_ALPHA)
                gap = getDimension(R.styleable.PieChart_gap, DEFAULT_GAP)
                drawingDirection = Direction.values()[
                        getInt(R.styleable.PieChart_drawingDirection, defaultDrawingDirection.ordinal)
                ]
            } finally {
                // TypedArray objects are a shared resource and must be recycled after use
                recycle()
            }
        }
    }

    /**
     * Attributes are a powerful way of controlling the behavior and appearance of views, but they can only be read when the view is initialized.
     * To provide dynamic behavior, expose a property getter and setter pair for each custom attribute.
     *
     * Notice that setShowText calls invalidate() and requestLayout().
     * These calls are crucial to ensure that the view behaves reliably.
     * You have to invalidate the view after any change to its properties that might change its appearance,
     * so that the system knows that it needs to be redrawn. Likewise, you need to request a new layout if
     * a property changes that might affect the size or shape of the view. Forgetting these method calls can cause hard-to-find bugs.
     *
     * A good rule to follow is to always expose any property that affects the visible appearance or behavior of your custom view.
     */
    // fun setIsTextShown(shouldShowText: Boolean) {
    //     isTextShown = shouldShowText
    //     invalidate()
    //     requestLayout()
    // }

    /**
     * This method is called when your view is first assigned a size, and again if the size of your view changes for any reason.
     * Calculate positions, dimensions, and any other values related to your view's size in onSizeChanged(),
     * instead of recalculating them every time you draw.
     *
     * When your view is assigned a size, the layout manager assumes that the size includes all of the view's padding.
     * You must handle the padding values when you calculate your view's size.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        centerX = width / 2f
        centerY = height / 2f
        // Account for the padding
        val paddingX = (paddingLeft + paddingRight).toFloat()
        val paddingY = (paddingTop + paddingBottom).toFloat()
        val effectiveWidth = width - paddingX
        val effectiveHeight = height - paddingY
        enclosingRect.set(RectF(0f, 0f, effectiveWidth, effectiveHeight))
        pie.reset()
        pieRadius = min(effectiveWidth, effectiveHeight) / 2f
        val holeRadius = holeRatio * pieRadius
        val overlayRadius = overlayRatio * pieRadius
        overlay.set(Path().apply { addCircle(centerX, centerY, overlayRadius, Path.Direction.CW) })
        val circle = Path().apply { addCircle(centerX, centerY, pieRadius, Path.Direction.CW) }
        val hole = Path().apply { addCircle(centerX, centerY, holeRadius, Path.Direction.CW) }
        val gaps = makeGaps()
        // Could also have set the fillType to EVEN_ODD and just add the other paths to the clip
        clip.set(circle - hole - gaps)
    }

    private fun makeGaps(): Path {
        val gaps = Path()
        val gapLength = pieRadius
        var sliceEndAngle = startAngle.toFloat()
        for (slice in slices) {
            sliceEndAngle += slice.fraction * 360

            // Calculate bottom right corner of the gap rectangle
            var angle = (sliceEndAngle + 90).toRadian()
            var x = centerX + gap * cos(angle)
            var y = centerY + gap * sin(angle)
            gaps.moveTo(x, y)

            // Calculate top right corner of the gap rectangle
            x += gapLength * cos(sliceEndAngle.toRadian())
            y += gapLength * sin(sliceEndAngle.toRadian())
            gaps.lineTo(x, y)

            // Calculate top left corner of the gap rectangle
            angle -= PI.toFloat()
            x += gap * cos(angle)
            y += gap * sin(angle)
            gaps.lineTo(x, y)

            // Calculate bottom left corner of the gap rectangle
            x -= gapLength * cos(sliceEndAngle.toRadian())
            y -= gapLength * sin(sliceEndAngle.toRadian())
            gaps.lineTo(x, y)

            // Join to the first point
            gaps.close()
        }
        return gaps
    }

    private fun Float.toRadian() = (this / 180) * PI.toFloat()

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
        super.onDraw(canvas)
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
        // clip should be called before drawing other things
        canvas.clipPath(clip)

        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        var angle = startAngle.toFloat()
        for (slice in slices) {
            paint.color = slice.color
            val sliceSweep = slice.fraction * 360
            pie.reset()
            pie.moveTo(centerX, centerY)
            pie.arcTo(enclosingRect, angle, sliceSweep)
            angle += sliceSweep
            canvas.drawPath(pie, paint)
        }
        paint.color = ContextCompat.getColor(context, android.R.color.black)
        paint.alpha = (overlayAlpha * 255).toInt()
        canvas.drawPath(overlay, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)
        val suggestedWidth = calculateSize(widthMode, specWidth)
        val suggestedHeight = calculateSize(heightMode, specHeight)
        val (width, height) = makeWidthAndHeightEqual(suggestedWidth, suggestedHeight)
        // This MUST be called
        setMeasuredDimension(width, height)
    }

    private fun makeWidthAndHeightEqual(w: Int, h: Int) = Pair(min(w, h), min(w, h))

    private fun calculateSize(mode: Int, specSize: Int) = when (mode) {
        // Must be this size
        MeasureSpec.EXACTLY -> specSize
        // Can't be bigger than...
        MeasureSpec.AT_MOST -> min(specSize, DEFAULT_SIZE)
        // Can be whatever you want
        else -> DEFAULT_SIZE
    }
}
