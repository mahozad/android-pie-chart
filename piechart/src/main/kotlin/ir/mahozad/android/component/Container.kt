package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
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
    private val paint = Paint()

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
            children.sumOf { it.width.toDouble() }.toFloat()
        } else {
            children.maxOf { it.width }
        }
    }
    override val height by lazy {
        if (layoutDirection == LayoutDirection.HORIZONTAL) {
            children.maxOf { it.height }
        } else {
            children.sumOf { it.height.toDouble() }.toFloat()
        }
    }

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        bounds.set(start, top, start + width, top + height) // Used to draw the background

        val maxWidth = children.maxOf { it.width }
        val maxHeight = children.maxOf { it.height }
        var childTop = top
        var childStart = start
        for (child in children) {
            if (childrenAlignment == Alignment.START && layoutDirection == LayoutDirection.HORIZONTAL) {
                // all child tops should be parent top
            } else if (childrenAlignment == Alignment.START && layoutDirection == LayoutDirection.VERTICAL && drawDirection == DrawDirection.RTL) {
                // all child rights should be parent right
            } else if (childrenAlignment == Alignment.START && layoutDirection == LayoutDirection.VERTICAL && drawDirection == DrawDirection.LTR) {
                // all child lefts should be parent left
            } else if (childrenAlignment == Alignment.END && layoutDirection == LayoutDirection.HORIZONTAL) {
                // all child bottoms should be parent bottom
            } else if (childrenAlignment == Alignment.END && layoutDirection == LayoutDirection.VERTICAL && drawDirection == DrawDirection.RTL) {
                // all child lefts should be parent left
            } else if (childrenAlignment == Alignment.END && layoutDirection == LayoutDirection.VERTICAL && drawDirection == DrawDirection.LTR) {
                // all child rights should be parent right
            } else if (childrenAlignment == Alignment.CENTER && layoutDirection == LayoutDirection.HORIZONTAL) {
                childTop = top + max(0f, (maxHeight - child.height) / 2f)
                child.layOut(childTop, childStart, drawDirection)
                childStart += child.width /* + margin, padding, etc. */
            } else /* if CENTER && VERTICAL */ {
                childStart = start + max(0f, (maxWidth - child.width) / 2f)
                child.layOut(childTop, childStart, drawDirection)
                childTop += child.height /* + margin, padding, etc. */
            }
        }
    }

    override fun draw(canvas: Canvas) {
        border?.let { border ->
            paint.style = Paint.Style.STROKE
            paint.color = border.color
            paint.alpha = (border.alpha * 255).toInt()
            paint.strokeWidth = border.thickness
            border.dashArray?.let { paint.pathEffect = DashPathEffect(it.toFloatArray(), 0f) }
            // FIXME: Reduce the bounds so much so that the borders are accommodated
            canvas.drawRoundRect(bounds, border.cornerRadius, border.cornerRadius, paint)
        }
        background?.let { background ->
            paint.style = Paint.Style.FILL
            paint.color = background.color
            paint.alpha = (background.alpha * 255).toInt()
            canvas.drawRoundRect(bounds, background.cornerRadius, background.cornerRadius, paint)
        }
        for (child in children) {
            child.draw(canvas)
        }
    }
}
