package ir.mahozad.android.component

import android.graphics.Canvas
import ir.mahozad.android.Coordinates
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.FloatComparator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

/**
 * The *@TestInstance* annotation is used an an alternative to
 * making the argument provider method for *@MethodSource* static.
 */
@TestInstance(PER_CLASS)
class ArrangerTest {

    private class MockBox(
        override val width: Float,
        override val height: Float,
        override val margins: Margins? = null,
        override val paddings: Paddings? = null
    ) : Box {
        override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) { /* Not needed */ }
        override fun draw(canvas: Canvas) { /* Not needed */ }
    }

    @Test fun horizontalContainerWithStartAlignmentAndNoChildAndClipWrappingAndNoPaddingsAndNoBorders() {
        val startCoordinates = Coordinates(120f, 235f)
        val availableWidth = 400f
        val availableHeight = 350f
        val children = emptyList<Box>()
        val layoutDirection = LayoutDirection.HORIZONTAL
        val drawDirection = DrawDirection.LTR
        val alignment = Alignment.START
        val wrapping = Wrapping.Clip
        val paddings = null
        val border = null

        val childrenCoordinates = calculateStartPositions(children, layoutDirection, drawDirection, alignment, startCoordinates, wrapping, paddings, border, availableWidth, availableHeight)

        assertThat(childrenCoordinates).isEmpty()
    }

    @DisplayName("Arrange children with")
    @ParameterizedTest(name = "Layout Direction: {1}, Draw Direction: {2}, Alignment: {3}, Wrapping: {4}, Paddings: {5}, Border: {6}, Children: {0}")
    @MethodSource("argumentProvider")
    internal fun calculateChildrenStartPositionsWithTheGivenArguments(
        children: List<Box>,
        layoutDirection: LayoutDirection,
        drawDirection: DrawDirection,
        alignment: Alignment,
        wrapping: Wrapping,
        paddings: Paddings?,
        border: Border?,
        expectedCoordinates: List<Coordinates>
    ) {
        val startCoordinates = Coordinates(120f, 235f)
        val availableWidth = 400f
        val availableHeight = 350f

        val childrenCoordinates = calculateStartPositions(children, layoutDirection, drawDirection, alignment, startCoordinates, wrapping, paddings, border, availableWidth, availableHeight)

        for ((i, coordinates) in childrenCoordinates.withIndex()) {
            assertThat(coordinates)
                .usingRecursiveComparison()
                .withComparatorForFields(FloatComparator(0.01f), Coordinates::x.name, Coordinates::y.name)
                .isEqualTo(expectedCoordinates[i])
        }
    }

    @Suppress("unused")
    private fun argumentProvider() = listOf(
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, null, null, listOf(Coordinates(120f, 235f))),
        /* ONE small child with ARBITRARY margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, null, null, listOf(Coordinates(138f, 256f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, Paddings(10f, 12f, 15f, 7f), null, listOf(Coordinates(135f, 245f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, null, Border(16f), listOf(Coordinates(136f, 251f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with ARBITRARY margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(154f, 272f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.CENTER, Wrapping.Clip, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.END, Wrapping.Clip, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* TWO small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, null, null, listOf(Coordinates(120f, 235f), Coordinates(154f, 235f))),
        /* TWO small child with NO margin, HORIZONTAL direction, LTR, CENTER alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.CENTER, Wrapping.Clip, null, null, listOf(Coordinates(120f, 237f), Coordinates(154f, 235f))),
        /* TWO small child with NO margin, HORIZONTAL direction, LTR, END alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.END, Wrapping.Clip, null, null, listOf(Coordinates(120f, 239f), Coordinates(154f, 235f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(0f, 0f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(0f, 0f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.Clip, null, null, listOf(Coordinates(130f, 235f), Coordinates(176f, 235f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, RTL, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(0f, 0f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(0f, 0f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.RTL, Alignment.START, Wrapping.Clip, Paddings(11f, 15f, 10f,  12f,), Border(16f), listOf(Coordinates(94f, 262f), Coordinates(48f, 262f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, RTL, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(0f, 0f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(0f, 0f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.RTL, Alignment.CENTER, Wrapping.Clip, Paddings(11f, 15f, 10f,  12f,), Border(16f), listOf(Coordinates(94f, 264f), Coordinates(48f, 262f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, RTL, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(0f, 0f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(0f, 0f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.RTL, Alignment.END, Wrapping.Clip, Paddings(11f, 15f, 10f,  12f,), Border(16f), listOf(Coordinates(94f, 266f), Coordinates(48f, 262f))),
    )
}
