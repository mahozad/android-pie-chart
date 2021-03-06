package ir.mahozad.android.component

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

    @Test fun horizontalContainerWithStartAlignmentAndNoChildAndClipWrappingAndNoPaddingsAndNoBorders() {
        val startCoordinates = Coordinates(120f, 235f)
        val children = emptyList<Box>()
        val layoutDirection = LayoutDirection.HORIZONTAL
        val drawDirection = DrawDirection.LTR
        val alignment = Alignment.START
        val paddings = null
        val border = null

        val childrenCoordinates = arrangeChildren(children, layoutDirection, drawDirection, alignment, startCoordinates, paddings, border)

        assertThat(childrenCoordinates).isEmpty()
    }

    @Test fun verticalContainerWithStartAlignmentAndNoChildAndClipWrappingAndNoPaddingsAndNoBorders() {
        val startCoordinates = Coordinates(120f, 235f)
        val children = emptyList<Box>()
        val layoutDirection = LayoutDirection.VERTICAL
        val drawDirection = DrawDirection.LTR
        val alignment = Alignment.START
        val paddings = null
        val border = null

        val childrenCoordinates = arrangeChildren(children, layoutDirection, drawDirection, alignment, startCoordinates, paddings, border)

        assertThat(childrenCoordinates).isEmpty()
    }

    @Test fun layeredContainerWithStartAlignmentAndNoChildAndClipWrappingAndNoPaddingsAndNoBorders() {
        val startCoordinates = Coordinates(120f, 235f)
        val children = emptyList<Box>()
        val layoutDirection = LayoutDirection.LAYERED
        val drawDirection = DrawDirection.LTR
        val alignment = Alignment.START
        val paddings = null
        val border = null

        val childrenCoordinates = arrangeChildren(children, layoutDirection, drawDirection, alignment, startCoordinates, paddings, border)

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

        val childrenCoordinates = arrangeChildren(children, layoutDirection, drawDirection, alignment, startCoordinates, paddings, border,)

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
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 235f))),
        /* ONE small child with ARBITRARY margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(138f, 256f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), null, listOf(Coordinates(135f, 245f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, Border(16f), listOf(Coordinates(136f, 251f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with ARBITRARY margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(154f, 272f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.CENTER, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with NO margin, HORIZONTAL direction, LTR, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.END, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* TWO small child with NO margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 235f), Coordinates(154f, 235f))),
        /* TWO small child with NO margin, HORIZONTAL direction, LTR, CENTER alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.CENTER, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 237f), Coordinates(154f, 235f))),
        /* TWO small child with NO margin, HORIZONTAL direction, LTR, END alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.END, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 239f), Coordinates(154f, 235f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(5f, 9f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4f, 8f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(130f, 240f), Coordinates(176f, 239f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, RTL, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(5f, 9f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4f, 8f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.RTL, Alignment.START, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(94f, 262f), Coordinates(48f, 262f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, RTL, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(5f, 9f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4f, 8f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.RTL, Alignment.CENTER, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(94f, 264f), Coordinates(48f, 262f))),
        /* TWO small child with ARBITRARY margin, HORIZONTAL direction, RTL, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(9f, 5f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4f, 8f, 7f, 13.5f))), LayoutDirection.HORIZONTAL, DrawDirection.RTL, Alignment.END, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(94f, 266f), Coordinates(48f, 262f))),
        /* **************************************************************** */
        /* ONE small child with NO margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 235f))),
        /* ONE small child with ARBITRARY margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(138f, 256f))),
        /* ONE small child with NO margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), null, listOf(Coordinates(135f, 245f))),
        /* ONE small child with NO margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, NO paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, Border(16f), listOf(Coordinates(136f, 251f))),
        /* ONE small child with NO margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with ARBITRARY margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(154f, 272f))),
        /* ONE small child with NO margin, VERTICAL direction, LTR, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.CENTER, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with NO margin, VERTICAL direction, LTR, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.END, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* TWO small child with NO margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 235f), Coordinates(120f, 264f))),
        /* TWO small child with NO margin, VERTICAL direction, LTR, CENTER alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.CENTER, Wrapping.CLIP, null, null, listOf(Coordinates(126f, 235f), Coordinates(120f, 264f))),
        /* TWO small child with NO margin, VERTICAL direction, LTR, END alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.END, Wrapping.CLIP, null, null, listOf(Coordinates(132f, 235f), Coordinates(120f, 264f))),
        /* TWO small child with ARBITRARY margin, VERTICAL direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.VERTICAL, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(130f, 248f), Coordinates(127f, 294f))),
        /* TWO small child with ARBITRARY margin, VERTICAL direction, RTL, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.VERTICAL, DrawDirection.RTL, Alignment.START, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(94f, 264f), Coordinates(94f, 310f))),
        /* TWO small child with ARBITRARY margin, VERTICAL direction, RTL, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.VERTICAL, DrawDirection.RTL, Alignment.CENTER, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(88f, 264f), Coordinates(94f, 310f))),
        /* TWO small child with ARBITRARY margin, VERTICAL direction, RTL, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.VERTICAL, DrawDirection.RTL, Alignment.END, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(82f, 264f), Coordinates(94f, 310f))),
        /* **************************************************************** */
        /* ONE small child with NO margin, LAYERED direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 235f))),
        /* ONE small child with ARBITRARY margin, LAYERED direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(138f, 256f))),
        /* ONE small child with NO margin, LAYERED direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), null, listOf(Coordinates(135f, 245f))),
        /* ONE small child with NO margin, LAYERED direction, LTR, START alignment, CLIP wrapping, NO paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, Border(16f), listOf(Coordinates(136f, 251f))),
        /* ONE small child with NO margin, LAYERED direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with ARBITRARY margin, LAYERED direction, LTR, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(21f, 20f, 18f, 14f))), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(154f, 272f))),
        /* ONE small child with NO margin, LAYERED direction, LTR, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.CENTER, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* ONE small child with NO margin, LAYERED direction, LTR, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.END, Wrapping.CLIP, Paddings(10f, 12f, 15f, 7f), Border(16f), listOf(Coordinates(151f, 261f))),
        /* TWO small child with NO margin, LAYERED direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(120f, 235f), Coordinates(120f, 235f))),
        /* TWO small child with NO margin, LAYERED direction, LTR, CENTER alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.CENTER, Wrapping.CLIP, null, null, listOf(Coordinates(126f, 237f), Coordinates(120f, 235f))),
        /* TWO small child with NO margin, LAYERED direction, LTR, END alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f), MockBox(46f, 33f)), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.END, Wrapping.CLIP, null, null, listOf(Coordinates(132f, 239f), Coordinates(120f, 235f))),
        /* TWO small child with ARBITRARY margin, LAYERED direction, LTR, START alignment, CLIP wrapping, NO paddings, NO Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.LAYERED, DrawDirection.LTR, Alignment.START, Wrapping.CLIP, null, null, listOf(Coordinates(130f, 248f), Coordinates(127f, 239.5f))),
        /* TWO small child with ARBITRARY margin, LAYERED direction, RTL, START alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.LAYERED, DrawDirection.RTL, Alignment.START, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(94f, 264f), Coordinates(94f, 262f))),
        /* TWO small child with ARBITRARY margin, LAYERED direction, RTL, CENTER alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.LAYERED, DrawDirection.RTL, Alignment.CENTER, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(88f, 264f), Coordinates(94f, 262f))),
        /* TWO small child with ARBITRARY margin, LAYERED direction, RTL, END alignment, CLIP wrapping, ARBITRARY paddings, ARBITRARY Borders */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(13f, 17f, 10f, 12f)), MockBox(46f, 33f, margins = Margins(4.5f, 8f, 7f, 13.5f))), LayoutDirection.LAYERED, DrawDirection.RTL, Alignment.END, Wrapping.CLIP, Paddings(11f, 15f, 10f, 12f,), Border(16f), listOf(Coordinates(82f, 266f), Coordinates(94f, 262f))),
    )
}
