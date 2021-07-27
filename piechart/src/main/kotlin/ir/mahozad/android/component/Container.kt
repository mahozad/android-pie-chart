package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import ir.mahozad.android.Coordinates
import ir.mahozad.android.PieChart
import ir.mahozad.android.component.Wrapping.Wrap
import kotlin.math.max

internal class Container(
    private val children: List<Box>,
    private var layoutDirection: LayoutDirection,
    private val childrenAlignment: Alignment,
    private val wrapping: Wrapping = Wrap,
    override val margins: Margins? = null,
    override val paddings: Paddings? = null,
    private val background: Background? = null,
    private val border: Border? = null
) : Box {

    private val bounds = RectF(0f, 0f, 0f, 0f)
    private val borderBounds = RectF(0f, 0f, 0f, 0f)
    private val paint = Paint(ANTI_ALIAS_FLAG)

    // init {
    //     if (wrapping == Wrap) {
    //         if (layoutDirection == LayoutDirection.HORIZONTAL) {
    //             val childrenWidth = children.sumOf { it.width.toDouble() }.toFloat()
    //             // Convert me to vertical container of two or more rows
    //             if (childrenWidth > maxAvailableWidth) {
    //                 layoutDirection = LayoutDirection.VERTICAL
    //                 // TODO: children = calculate rows
    //             }
    //         } else {
    //             val childrenHeight = children.sumOf { it.height.toDouble() }.toFloat()
    //             // Convert me to horizontal container of two or more columns
    //             if (childrenHeight > maxAvailableHeight) {
    //                 layoutDirection = LayoutDirection.HORIZONTAL
    //                 // TODO: children = calculate rows
    //             }
    //         }
    //     }
    // }

    override val width by lazy {
        if (layoutDirection == LayoutDirection.HORIZONTAL) {
            children.sumOf { it.width.toDouble() }.toFloat() +
                    (border?.thickness ?: 0f) * 2 +
                    // Sum of all the collapsing margins between each pair of children
                    children.zipWithNext { a, b -> max(a.margins?.end ?: 0f, b.margins?.start ?: 0f) }.sum() +
                    max(paddings?.start ?: 0f, children.first().margins?.start ?: 0f) +
                    max(paddings?.end ?: 0f, children.last().margins?.end ?: 0f)
        } else {
            children.maxOf { it.width } +
                    (border?.thickness ?: 0f) * 2 +
                    max(paddings?.start ?: 0f, children.maxOf { it.margins?.start ?: 0f }) +
                    max(paddings?.end ?: 0f, children.maxOf { it.margins?.end ?: 0f })
        }
    }
    override val height by lazy {
        if (layoutDirection == LayoutDirection.HORIZONTAL) {
            children.maxOf { it.height } +
                    (border?.thickness ?: 0f) * 2 +
                    max(paddings?.top ?: 0f, children.maxOf { it.margins?.top ?: 0f }) +
                    max(paddings?.bottom ?: 0f, children.maxOf { it.margins?.bottom ?: 0f })
        } else {
            children.sumOf { it.height.toDouble() }.toFloat() +
                    (border?.thickness ?: 0f) * 2 +
                    // Sum of all the collapsing margins between each pair of children
                    children.zipWithNext { a, b -> max(a.margins?.bottom ?: 0f, b.margins?.top ?: 0f) }.sum() +
                    max(paddings?.top ?: 0f, children.first().margins?.top ?: 0f) +
                    max(paddings?.bottom ?: 0f, children.last().margins?.bottom ?: 0f)
        }
    }

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        bounds.set(start, top, start + width, top + height) // Used to draw the background
        val borderOffset = (border?.thickness?: 0f) / 2f
        borderBounds.set(bounds.left + borderOffset, bounds.top + borderOffset, bounds.right - borderOffset, bounds.bottom - borderOffset)
        val positions = arrangeChildren(children, layoutDirection, drawDirection, childrenAlignment, Coordinates(start, top), wrapping, paddings, border, 10000000f, 10000000f,)
        for ((i, child) in children.withIndex()) {
            child.layOut(positions[i].y, positions[i].x, drawDirection)
        }
    }

    /**
     * Border is drawn on top of the background. So call [drawBorder] after [drawBackground].
     */
    override fun draw(canvas: Canvas) {
        drawBackground(canvas)
        drawBorder(canvas)
        for (child in children) {
            child.draw(canvas)
        }
    }

    private fun drawBorder(canvas: Canvas) {
        border?.let { border ->
            paint.strokeWidth = border.thickness
            paint.style = Paint.Style.STROKE
            paint.color = border.color
            paint.alpha = (border.alpha * 255).toInt() // Setting alpha should be *AFTER* setting the color to override the color alpha
            if (border.type == PieChart.BorderType.DASHED) {
                border.dashArray?.let { paint.pathEffect = DashPathEffect(it.toFloatArray(), 0f) }
            }
            canvas.drawRoundRect(borderBounds, border.cornerRadius, border.cornerRadius, paint)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        background?.let { background ->
            paint.style = Paint.Style.FILL
            paint.color = background.color
            paint.alpha = (background.alpha * 255).toInt() // Setting alpha should be *AFTER* setting the color to override the color alpha
            canvas.drawRoundRect(bounds, background.cornerRadius, background.cornerRadius, paint)
        }
    }
}
