package io.github.mahozad.piechart

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.minus
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

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

    var isTextShown: Boolean private set
    var holeRadiusRatio = 0.25f
        set(ratio) {
            field = ratio.coerceIn(0f, 1f)
        }
    var overlayRatio = 0.55f
        set(ratio) {
            field = ratio.coerceIn(0f, 1f)
        }
    var startAngle = -90
        set(angle) {
            field = angle.coerceIn(-360, 360)
        }
    var gap = 8f
    private val textPosition: Int
    private val slices = mutableListOf(
        Slice(0.43f, ContextCompat.getColor(context, android.R.color.holo_green_dark)),
        Slice(0.21f, ContextCompat.getColor(context, android.R.color.holo_orange_dark)),
        Slice(0.19f, ContextCompat.getColor(context, android.R.color.holo_blue_dark)),
        Slice(0.15f, ContextCompat.getColor(context, android.R.color.holo_red_light)),
        Slice(0.02f, ContextCompat.getColor(context, android.R.color.holo_purple))
    )
    private val clip = Path()
    private val enclosingRect = RectF()
    private val pie = Path()
    private val overlay = Path()
    private var pieRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).apply {
            try {
                isTextShown = getBoolean(R.styleable.PieChart_showText, false)
                textPosition = getInteger(R.styleable.PieChart_labelPosition, 0)
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
    fun setIsTextShown(shouldShowText: Boolean) {
        isTextShown = shouldShowText
        invalidate()
        requestLayout()
    }

    // private val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
    //     color = textColor
    //     if (textHeight == 0f) {
    //         textHeight = textSize
    //     } else {
    //         textSize = textHeight
    //     }
    // }
    //
    // private val piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    //     style = Paint.Style.FILL
    //     textSize = textHeight
    // }
    //
    // private val shadowPaint = Paint(0).apply {
    //     color = 0x101010
    //     maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
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
        val holeRadius = holeRadiusRatio * pieRadius
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
        var sliceEndAngle = -90f
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
     * The clip path (and maybe some other features) do not on emulators with
     * hardware acceleration enabled.
     * Try to call `setLayerType(LAYER_TYPE_SOFTWARE, null)` to use software rendering.
     * See the following posts:
     *  https://stackoverflow.com/q/8895677
     *  https://stackoverflow.com/a/23517980
     *  https://stackoverflow.com/q/13672802
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
            pie.reset()
            paint.color = slice.color
            val sliceSweep = slice.fraction * 360
            pie.moveTo(centerX, centerY)
            pie.arcTo(enclosingRect, angle, sliceSweep)
            angle += sliceSweep
            canvas.drawPath(pie, paint)
        }
        paint.color = ContextCompat.getColor(context, android.R.color.black)
        paint.alpha = 64
        canvas.drawPath(overlay, paint)
    }

    data class Slice(val fraction: Float, @ColorInt val color: Int)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /**
         * With regard to widthMeasureSpec and heightMeasureSpec
         * calculate the required width and height of the component
         */
        val myWidth = 456
        val myHeight = 456
        setMeasuredDimension(myWidth, myHeight)
    }
}
