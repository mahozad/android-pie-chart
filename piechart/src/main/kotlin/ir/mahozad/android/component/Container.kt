package ir.mahozad.android.component

import android.graphics.*
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import kotlin.math.min

internal class Container(
    private val parentMaxWidth: Float,
    private val parentMaxHeight: Float,
    override val padding: Padding = Padding(0f, 0f, 0f, 0f),
    override val margin: Margin = Margin(0f, 0f, 0f, 0f),
    private val children: List<Box>,
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
    @Dimension private val borderThickness: Float = 0f,
    @Dimension private val cornerRadius: Float = 0f
) : Box {

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
