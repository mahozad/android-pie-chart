package ir.mahozad.android

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import kotlin.math.min

/**
 * Using the *composite* pattern.
 */
internal interface Component {
    val width: Float
    val height: Float
    fun layOut(top: Float, left: Float)
    fun draw(canvas: Canvas)
}

internal class Text(
    private val string: String,
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

/**
 * Either a row or a column.
 */
internal data class Line(val components: List<Component>, val width: Float, val height: Float)

enum class Alignment { START, CENTER, END }
enum class LayoutDirection { HORIZONTAL, VERTICAL }
data class Padding(
    val top: Float,
    val end: Float,
    val start: Float,
    val bottom: Float,
)

internal class Root(
    private val startCoordinates: Coordinates? = null,
    private val parentMaxWidth: Float,
    private val parentMaxHeight: Float,
    private val padding: Padding = Padding(0f, 0f, 0f, 0f),
    private val children: List<Component>,
    private val layoutDirection: LayoutDirection,
    private val childrenAlignment: Alignment,
    private val maxRows: Int = 1,
    private val maxColumns: Int = 1,
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
    private val rows: List<Line> by lazy {
        val rows = mutableListOf<Line>()
        val rowWidth = padding.start + padding.end
        for (child in children) {
            val row = listOf(child)
        }
        emptyList()
    }

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
        // if (layoutDirection == LayoutDirection.HORIZONTAL) {
        //     for (row in rows) {
        //         for (component in row.components) {
        //             val startCoordinates = 0f /* TODO */
        //             component.layOut(startCoordinates, row.height)
        //         }
        //     }
        // }
        var newTop = top
        var newLeft = left
        for (child in children) {
            child.layOut(newTop, newLeft)
            if (layoutDirection == LayoutDirection.HORIZONTAL) {
                newLeft += child.width /* + margin */
            } else {
                newTop += child.height
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

internal class Container(
    private val padding: Padding = Padding(0f, 0f, 0f, 0f),
    private val parentMaxWidth: Float,
    private val parentMaxHeight: Float,
    private val children: List<Component>,
    private val layoutDirection: LayoutDirection,
    private val childrenAlignment: Alignment,
    private val maxRows: Int = 1,
    private val maxColumns: Int = 1,
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
    private val dashedBorder = Path()

    private val rows: List<Line> by lazy {
        val rows = mutableListOf<Line>()
        val rowWidth = padding.start + padding.end
        for (child in children) {
            val row = listOf(child)
        }
        emptyList()
    }

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
        var newTop = top
        var newLeft = left
        for (child in children) {
            child.layOut(newTop, newLeft)
            if (layoutDirection == LayoutDirection.HORIZONTAL) {
                newLeft += child.width /* + margin */
            } else {
                newTop += child.height
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
