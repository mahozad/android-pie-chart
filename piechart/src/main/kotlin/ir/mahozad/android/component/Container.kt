package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.RectF
import androidx.core.graphics.withClip
import ir.mahozad.android.Coordinates
import ir.mahozad.android.PieChart
import ir.mahozad.android.component.LayoutDirection.HORIZONTAL
import ir.mahozad.android.component.LayoutDirection.VERTICAL
import ir.mahozad.android.component.Wrapping.WRAP
import kotlin.math.max
import kotlin.math.min

internal class Container(
    children: List<Box>,
    private val maxAvailableWidth: Float,
    private val maxAvailableHeight: Float,
    layoutDirection: LayoutDirection,
    private val childrenAlignment: Alignment,
    wrapping: Wrapping = WRAP,
    override val margins: Margins? = null,
    override val paddings: Paddings? = null,
    private val background: Background? = null,
    private val border: Border? = null,
    legendLinesMargin: Float = 0f
) : Box {

    override val width by lazy { min(calculateWidth(), maxAvailableWidth) }
    override val height by lazy { min(calculateHeight(), maxAvailableHeight) }
    private var internalChildren = children
    private var internalLayoutDirection = layoutDirection
    private val bounds = RectF(0f, 0f, 0f, 0f)
    private val borderBounds = RectF(0f, 0f, 0f, 0f)
    private val paint = Paint(ANTI_ALIAS_FLAG)

    init {
        if (wrapping == WRAP) {
            val wrapper = createWrapper(
                layoutDirection,
                maxAvailableWidth,
                maxAvailableHeight,
                childrenAlignment,
                legendLinesMargin,
                paddings,
                border
            )
            if (wrapper.isWrapNeeded(children)) {
                internalChildren = wrapper.wrap(children)
                internalLayoutDirection = wrapper.layoutDirection
            }
        }
    }

    private fun calculateWidth() =
        if (internalLayoutDirection == HORIZONTAL) {
            calculateRowWidth(internalChildren, border, paddings)
        } else {
            internalChildren.maxOfOrNull {
                it.width +
                        (border?.thickness ?: 0f) * 2 +
                        max(paddings?.start ?: 0f, it.margins?.start ?: 0f) +
                        max(paddings?.end ?: 0f, it.margins?.end ?: 0f)
            } ?: ((border?.thickness ?: 0f) * 2 + (paddings?.horizontal ?: 0f))
        }

    private fun calculateHeight() =
        if (internalLayoutDirection == VERTICAL) {
            calculateColumnHeight(internalChildren, border, paddings)
        } else {
            internalChildren.maxOfOrNull {
                it.height +
                        (border?.thickness ?: 0f) * 2 +
                        max(paddings?.top ?: 0f, it.margins?.top ?: 0f) +
                        max(paddings?.bottom ?: 0f, it.margins?.bottom ?: 0f)
            } ?: ((border?.thickness ?: 0f) * 2 + (paddings?.vertical ?: 0f))
        }

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        bounds.set(start, top, start + width, top + height) // Used to draw the background
        val borderOffset = (border?.thickness?: 0f) / 2f
        borderBounds.set(bounds.left + borderOffset, bounds.top + borderOffset, bounds.right - borderOffset, bounds.bottom - borderOffset)
        val positions = arrangeChildren(internalChildren, internalLayoutDirection, drawDirection, childrenAlignment, Coordinates(start, top), paddings, border)
        for ((i, child) in internalChildren.withIndex()) {
            child.layOut(positions[i].y, positions[i].x, drawDirection)
        }
    }

    /**
     * Border is drawn on top of the background. So call [drawBorder] after [drawBackground].
     */
    override fun draw(canvas: Canvas) {
        canvas.withClip(bounds) {
            drawBackground(canvas)
            drawBorder(canvas)
            for (child in internalChildren) {
                child.draw(canvas)
            }
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

    // FIXME: background corner radius is not implemented (its xml and kotlin property)
    private fun drawBackground(canvas: Canvas) {
        background?.let { background ->
            paint.style = Paint.Style.FILL
            paint.color = background.color
            paint.alpha = (background.alpha * 255).toInt() // Setting alpha should be *AFTER* setting the color to override the color alpha
            canvas.drawRoundRect(bounds, background.cornerRadius, background.cornerRadius, paint)
        }
    }
}
