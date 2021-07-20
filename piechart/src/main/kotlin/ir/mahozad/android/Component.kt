package ir.mahozad.android

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import kotlin.math.min

/**
 * We arrange the components using a box model and implement it with the
 * [*composite* pattern](https://en.wikipedia.org/wiki/Composite_pattern).
 *
 * The margins are treated in [collapsing](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Box_Model/Mastering_margin_collapsing) mode.
 */
internal interface Component {
    val width: Float
    val height: Float
    val margin: Margin
    val padding: Padding
    fun layOut(top: Float, left: Float)
    fun draw(canvas: Canvas)
}

internal enum class Alignment { START, CENTER, END }
internal enum class LayoutDirection { HORIZONTAL, VERTICAL }
internal data class Padding(val top: Float, val bottom: Float, val start: Float, val end: Float)
internal data class Margin(val top: Float, val bottom: Float, val start: Float, val end: Float)
internal sealed class Clipping {
    object Scrollable : Clipping()
    object NextLine : Clipping()
    object Clipped : Clipping()
}

internal class Text(
    private val string: String,
    override val padding: Padding = Padding(0f, 0f, 0f, 0f),
    override val margin: Margin = Margin(0f, 0f, 0f, 0f),
    @Dimension size: Float,
    @ColorInt color: Int,
    font: Typeface
) : Component {

    private val paint = updatePaintForLabel(Paint(), size, color, font)
    private val dimensions = calculateLabelBounds(string, paint)
    private val bounds = RectF(0f, 0f, 0f, 0f)
    private var descent = paint.descent()
    override val width = dimensions.width()
    override val height = dimensions.height()

    override fun layOut(top: Float, left: Float) {
        val size = calculateLabelBounds(string, paint)
        val width = size.width()
        val height = size.height()
        bounds.set(left, top, left + width, top + height)
    }

    override fun draw(canvas: Canvas) {
        // The x denotes the horizontal *center* of the text because it is center aligned
        val x = bounds.centerX()
        val y = bounds.bottom - descent
        canvas.drawText(string, x, y, paint)
    }
}

internal class Icon(
    private val drawable: Drawable,
    override val height: Float,
    override val padding: Padding = Padding(0f, 0f, 0f, 0f),
    override val margin: Margin = Margin(0f, 0f, 0f, 0f),
    @ColorInt private val tint: Int? = null
) : Component {

    override val width = calculateLabelIconWidth(drawable, height)

    override fun layOut(top: Float, left: Float) {
        val right = left + width
        val bottom = top + height
        drawable.bounds = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        tint?.let { drawable.setTint(it) }
    }

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }
}

internal class Box(
    private val parentMaxWidth: Float,
    private val parentMaxHeight: Float,
    override val padding: Padding = Padding(0f, 0f, 0f, 0f),
    override val margin: Margin = Margin(0f, 0f, 0f, 0f),
    private val children: List<Component>,
    private val layoutDirection: LayoutDirection,
    private val childrenAlignment: Alignment,
    private val clipping: Clipping = Clipping.NextLine,
    private val hasBorder: Boolean = false,
    private val hasBackground: Boolean = false,
    private val borderDashIntervals: FloatArray? = null,
    @ColorInt private val borderColor: Int = Color.BLACK,
    @ColorInt private val backgroundColor: Int = Color.TRANSPARENT,
    /**
     * Note that these two are convenience properties because the alpha can be specified in the color itself as well.
     */
    @FloatRange(from = 0.0, to = 1.0) private val backgroundOpacity: Float = 1f,
    @FloatRange(from = 0.0, to = 1.0) private val borderOpacity: Float = 1f,
    @Dimension private val cornerRadius: Float = 0f,
    @Dimension private val borderThickness: Float = 0f
) : Component {

    private val bounds = RectF(0f, 0f, 0f, 0f)
    private val paint = Paint()

    override val width by lazy {
        if (layoutDirection == LayoutDirection.HORIZONTAL) {
            val totalChildrenWidth = children.sumOf { it.width.toDouble() }.toFloat()
            min(parentMaxWidth, totalChildrenWidth)
        } else {
            val childrenMaxWidth = children.maxOf { it.width }
            min(parentMaxWidth, childrenMaxWidth)
        }
    }
    override val height by lazy {
        if (layoutDirection == LayoutDirection.HORIZONTAL) {
            val childrenMaxHeight = children.maxOf { it.height }
            min(parentMaxHeight, childrenMaxHeight)
        } else {
            val totalChildrenHeight = children.sumOf { it.height.toDouble() }.toFloat()
            min(parentMaxHeight, totalChildrenHeight)
        }
    }

    override fun layOut(top: Float, left: Float) {
        var childTop = top
        var childLeft = left
        for (child in children) {
            child.layOut(childTop, childLeft)
            if (layoutDirection == LayoutDirection.HORIZONTAL) {
                childLeft += child.width /* + margin */
            } else {
                childTop += child.height
            }
        }
    }

    override fun draw(canvas: Canvas) {
        if (hasBorder) {
            paint.style = Paint.Style.STROKE
            paint.color = borderColor
            paint.alpha = (borderOpacity * 255).toInt()
            paint.strokeWidth = borderThickness
            paint.pathEffect = DashPathEffect(borderDashIntervals, 0f)
            canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint)
        }
        if (hasBackground) {
            paint.style = Paint.Style.FILL
            paint.color = backgroundColor
            paint.alpha = (backgroundOpacity * 255).toInt()
            canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint)
        }
        for (child in children) {
            child.draw(canvas)
        }
    }
}
