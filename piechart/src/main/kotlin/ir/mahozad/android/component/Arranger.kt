package ir.mahozad.android.component

import ir.mahozad.android.Coordinates
import ir.mahozad.android.component.Alignment.*
import ir.mahozad.android.component.DrawDirection.LTR
import ir.mahozad.android.component.LayoutDirection.HORIZONTAL
import ir.mahozad.android.component.LayoutDirection.VERTICAL
import kotlin.math.max

/**
 * Arranges children of a [Box]. In other words, returns the start coordinates of the children.
 *
 * Start coordinate is top-left corner for LTR and top-right corner for RTL.
 *
 * NOTE 1: With [LayoutDirection.HORIZONTAL], if the children **top** margins are different,
 *  we do not align the children tops by, for example, their maximum top margin.
 *  The same is true for children **start** margins when using [LayoutDirection.VERTICAL].
 *  This is intentional, because, why the user specified different start or top margins
 *  for each child? They could have specified the same margin for all the children
 *  if they wanted to align all their start or top side.
 *
 * NOTE 2: We treat **sibling margins** and **parent padding and first child margins** in
 *  [*collapsing*](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Box_Model/Mastering_margin_collapsing) manner.
 */
internal fun arrangeChildren(
    children: List<Box>,
    layoutDirection: LayoutDirection,
    drawDirection: DrawDirection,
    alignment: Alignment,
    /** top-left when LTR, top-right when RTL of where the whole box should start */
    startCoordinates: Coordinates,
    wrapping: Wrapping,
    paddings: Paddings?, // paddings of this box
    border: Border?, // borders of this box
    maxAvailableWidth: Float,
    maxAvailableHeight: Float
): List<Coordinates> {
    if (children.isEmpty()) {
        return emptyList()
    }
    val arranger = when (layoutDirection) {
        HORIZONTAL -> HorizontalArranger(children, alignment, drawDirection, startCoordinates, paddings, border)
        VERTICAL -> VerticalArranger(children, alignment, drawDirection, startCoordinates, paddings, border)
        else -> LayeredArranger(children, alignment, alignment, drawDirection, startCoordinates, paddings, border)
    }
    return arranger.arrange()
}

private abstract class Arranger(
    protected val children: List<Box>,
    protected val alignment: Alignment,
    drawDirection: DrawDirection,
    startCoordinates: Coordinates,
    border: Border?, // borders of this box
) {

    protected var lastSpace = 0f
    protected val factor = if (drawDirection == LTR) 1 else -1
    protected var start = startCoordinates.x + (border?.thickness ?: 0f) * factor
    protected var top = startCoordinates.y + (border?.thickness ?: 0f)
    protected val maxSiblingWidth = children.maxOf { it.width }
    protected val maxSiblingHeight = children.maxOf { it.height }

    /**
     * Template pattern.
     */
    fun arrange(): List<Coordinates> {
        lastSpace = getInitialSpace()
        val result = mutableListOf<Coordinates>()
        for (child in children) {
            val offset = calculateOffset(child)
            val childTop = calculateChildTop(child, offset)
            val childStart = calculateChildStart(child, offset)
            result.add(Coordinates(childStart, childTop))
            incrementAxis(child)
            lastSpace = getFinishMargin(child)
        }
        return result
    }

    private fun calculateOffset(child: Box) = when (alignment) {
        CENTER -> max(0f, getMaxMinusChildSize(child) / 2f)
        START -> 0f
        END -> max(0f, getMaxMinusChildSize(child))
    }

    abstract fun getInitialSpace(): Float

    abstract fun incrementAxis(child: Box)

    abstract fun getFinishMargin(child: Box): Float

    abstract fun calculateChildTop(child: Box, offset: Float): Float

    abstract fun calculateChildStart(child: Box, offset: Float): Float

    abstract fun getMaxMinusChildSize(child: Box): Float
}

private class HorizontalArranger(
    children: List<Box>,
    alignment: Alignment,
    drawDirection: DrawDirection,
    startCoordinates: Coordinates,
    val paddings: Paddings?,
    border: Border?
) : Arranger(children, alignment, drawDirection, startCoordinates, border) {

    override fun getInitialSpace() = (paddings?.start ?: 0f) * factor

    override fun incrementAxis(child: Box) {
        start += child.width * factor
    }

    override fun getFinishMargin(child: Box) = child.margins?.end ?: 0f

    override fun calculateChildTop(child: Box, offset: Float) = top + max(child.margins?.top ?: 0f, paddings?.top ?: 0f) + offset

    override fun calculateChildStart(child: Box, offset: Float): Float {
        start += max(lastSpace, child.margins?.start ?: 0f) * factor
        return start
    }

    override fun getMaxMinusChildSize(child: Box) = maxSiblingHeight - child.height
}

private class VerticalArranger(
    children: List<Box>,
    alignment: Alignment,
    drawDirection: DrawDirection,
    startCoordinates: Coordinates,
    val paddings: Paddings?,
    border: Border?
) : Arranger(children, alignment, drawDirection, startCoordinates, border) {

    override fun getInitialSpace() = paddings?.top ?: 0f

    override fun incrementAxis(child: Box) {
        top += child.height
    }

    override fun getFinishMargin(child: Box) = child.margins?.bottom ?: 0f

    override fun calculateChildTop(child: Box, offset: Float): Float {
        top += max(lastSpace, child.margins?.top ?: 0f)
        return top
    }

    override fun calculateChildStart(child: Box, offset: Float) = start + (max(child.margins?.start ?: 0f, paddings?.start ?: 0f) + offset) * factor

    override fun getMaxMinusChildSize(child: Box) = maxSiblingWidth - child.width
}

private class LayeredArranger(
    children: List<Box>,
    val horizontalAlignment: Alignment,
    val verticalAlignment: Alignment,
    val drawDirection: DrawDirection,
    startCoordinates: Coordinates,
    val paddings: Paddings?,
    border: Border?
) : Arranger(children, verticalAlignment, drawDirection, startCoordinates, border) {

    override fun getInitialSpace() = 0f
    override fun incrementAxis(child: Box) {}
    override fun getFinishMargin(child: Box) = 0f
    override fun getMaxMinusChildSize(child: Box) = 0f

    override fun calculateChildTop(child: Box, offset: Float): Float {
        return when (verticalAlignment) {
            START -> top + max(paddings?.top ?: 0f, child.margins?.top ?: 0f)
            CENTER -> top + (paddings?.top ?: 0f) + (maxSiblingHeight / 2f) - (child.height / 2f)
            else -> top + (paddings?.top ?: 0f) + (maxSiblingHeight) - child.height
        }
    }

    override fun calculateChildStart(child: Box, offset: Float): Float {
        return when (horizontalAlignment) {
            START -> start + max(paddings?.start ?: 0f, child.margins?.start ?: 0f) * factor
            CENTER -> start + ((paddings?.start ?: 0f) + (maxSiblingWidth / 2f) - (child.width / 2f)) * factor
            else -> start + ((paddings?.start ?: 0f) + (maxSiblingWidth) - child.width) * factor
        }
    }
}
