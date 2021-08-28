package ir.mahozad.android.component

import ir.mahozad.android.component.LayoutDirection.HORIZONTAL
import ir.mahozad.android.component.LayoutDirection.VERTICAL
import ir.mahozad.android.component.Wrapper.HorizontalWrapper
import ir.mahozad.android.component.Wrapper.VerticalWrapper
import kotlin.math.max

/**
 * Factory method.
 */
internal fun createWrapper(
    layoutDirection: LayoutDirection,
    availableWidth: Float,
    availableHeight: Float,
    alignment: Alignment,
    linesMargin: Float,
    paddings: Paddings?,
    border: Border?
) = when (layoutDirection) {
    VERTICAL -> VerticalWrapper(availableWidth, availableHeight, alignment, border, paddings, linesMargin)
    else -> HorizontalWrapper(availableWidth, availableHeight, alignment, border, paddings, linesMargin)
}

internal sealed class Wrapper {

    abstract val layoutDirection : LayoutDirection
    abstract val lineDirection: LayoutDirection
    protected val wrapping = Wrapping.CLIP
    protected abstract val linesMargin: Float
    protected abstract val availableSize: Float
    protected abstract val availableWidth: Float
    protected abstract val availableHeight: Float
    protected abstract val childrenAlignment: Alignment

    abstract fun calculateLineSize(line: List<Box>) : Float
    abstract fun calculateMargins(linesMargin: Float, nextChild: Box?): Margins?
    fun isWrapNeeded(children: List<Box>) = (calculateLineSize(children) - availableSize) > 0.01

    /**
     * Template method.
     */
    fun wrap(children: List<Box>): List<Box> {
        val lines = mutableListOf<Box>()
        val line = mutableListOf<Box>()
        for ((i, child) in children.withIndex()) {
            line.add(child)
            val nextChild = children.getOrNull(i + 1)
            val lineWithNext = if (nextChild != null) line + nextChild else line
            if (isWrapNeeded(lineWithNext) || nextChild == null) {
                val margins = calculateMargins(linesMargin, nextChild)
                val container = Container(
                    line.toList() /* Clone */,
                    availableWidth,
                    availableHeight,
                    lineDirection,
                    childrenAlignment,
                    wrapping,
                    margins
                )
                lines.add(container)
                line.clear()
            }
        }
        return lines
    }

    class HorizontalWrapper(
        override val availableWidth: Float,
        override val availableHeight: Float,
        override val childrenAlignment: Alignment,
        private val border: Border?,
        private val paddings: Paddings?,
        override val linesMargin: Float,
    ) : Wrapper() {
        override val availableSize = availableWidth
        override val layoutDirection = VERTICAL
        override val lineDirection = HORIZONTAL
        override fun calculateLineSize(line: List<Box>) = calculateRowWidth(line, border, paddings)
        override fun calculateMargins(linesMargin: Float, nextChild: Box?) = nextChild?.let { Margins(bottom = linesMargin) }
    }

    class VerticalWrapper(
        override val availableWidth: Float,
        override val availableHeight: Float,
        override val childrenAlignment: Alignment,
        private val border: Border?,
        private val paddings: Paddings?,
        override val linesMargin: Float
    ) : Wrapper() {
        override val availableSize = availableHeight
        override val layoutDirection = HORIZONTAL
        override val lineDirection = VERTICAL
        override fun calculateLineSize(line: List<Box>) = calculateColumnHeight(line, border, paddings)
        override fun calculateMargins(linesMargin: Float, nextChild: Box?) = nextChild?.let { Margins(end = linesMargin) }
    }
}

internal fun calculateRowWidth(row: List<Box>, border: Border?, paddings: Paddings?) =
    row.sumOf { it.width.toDouble() }.toFloat() +
            (border?.thickness ?: 0f) * 2 +
            // Sum of all the collapsing margins between each pair of children
            row.zipWithNext { a, b -> max(a.margins?.end ?: 0f, b.margins?.start ?: 0f) }.sum() +
            max(paddings?.start ?: 0f, row.firstOrNull()?.margins?.start ?: 0f) +
            max(paddings?.end ?: 0f, row.lastOrNull()?.margins?.end ?: 0f)

internal fun calculateColumnHeight(column: List<Box>, border: Border?, paddings: Paddings?) =
    column.sumOf { it.height.toDouble() }.toFloat() +
            (border?.thickness ?: 0f) * 2 +
            // Sum of all the collapsing margins between each pair of children
            column.zipWithNext { a, b -> max(a.margins?.bottom ?: 0f, b.margins?.top ?: 0f) }.sum() +
            max(paddings?.top ?: 0f, column.firstOrNull()?.margins?.top ?: 0f) +
            max(paddings?.bottom ?: 0f, column.lastOrNull()?.margins?.bottom ?: 0f)
