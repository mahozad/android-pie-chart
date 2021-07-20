package ir.mahozad.android.component

import ir.mahozad.android.Coordinates
import kotlin.math.max

/**
 * Arranges children of a [Box]. In other words, returns the start coordinates of the children.
 *
 * Start coordinate is top-left corner for LTR and top-right corner for RTL.
 *
 * Treats **sibling margins** and **parent padding and child margin** in
 * [*collapsing*](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Box_Model/Mastering_margin_collapsing) manner.
 */
internal fun calculateStartPositions(
    children: List<Box>,
    layoutDirection: LayoutDirection,
    drawDirection: DrawDirection,
    childrenAlignment: Alignment,
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
    var start = startCoordinates.x
    var top = startCoordinates.y
    val result = mutableListOf<Coordinates>()
    val factor = if (drawDirection == DrawDirection.LTR) 1 else -1

    val maxSiblingHeight = children.maxOf { it.height }

    border?.let {
        start += it.thickness * factor
        top += it.thickness
    }

    // if HORIZONTAL
    var lastHorizontalMargin = factor * (paddings?.start ?: 0f)
    top += max(children[0].margins?.top ?: 0f, paddings?.top ?: 0f)
    var verticalOffset = 0f
    for (child in children) {
        start += max(lastHorizontalMargin, child.margins?.start ?: 0f) * factor
        if (childrenAlignment == Alignment.CENTER) {
            verticalOffset = max(0f, (maxSiblingHeight - child.height) / 2f)
        } else if (childrenAlignment == Alignment.END) {
            verticalOffset = max(0f, maxSiblingHeight - child.height)
        }
        val coordinates = Coordinates(start, top + verticalOffset)
        result.add(coordinates)
        start += child.width * factor
        lastHorizontalMargin = child.margins?.end ?: 0f
    }
    return result
}
