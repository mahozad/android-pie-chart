package ir.mahozad.android.component

import ir.mahozad.android.component.LayoutDirection.HORIZONTAL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.math.absoluteValue

/**
 * The *@TestInstance* annotation is used an an alternative to
 * making the argument provider method for *@MethodSource* static.
 */
@TestInstance(PER_CLASS)
class ContainerTest {

    @DisplayName("Calculate container dimensions")
    @ParameterizedTest(name = "Test #{index} with Max width avail: {1}, Max height avail: {2}, Layout direction: {3}, Wrapping: {4}, Paddings: {5}, Border: {6}, Children: {0}")
    @MethodSource("argumentProvider")
    internal fun ensureContainerWidthAndHeightIsRight(
        children: List<Box>,
        maxAvailableWidth: Float,
        maxAvailableHeight: Float,
        layoutDirection: LayoutDirection,
        wrapping: Wrapping,
        paddings: Paddings?,
        border: Border?,
        expectedDimensions: List<Float>
    ) {
        val childrenAlignment = Alignment.CENTER
        val drawDirection = DrawDirection.LTR
        val (expectedWidth, expectedHeight) = expectedDimensions

        val container = Container(children, maxAvailableWidth, maxAvailableHeight, layoutDirection, childrenAlignment, wrapping, border = border, paddings = paddings)

        assertThat(container.width)
            .usingComparator { f1, f2 -> if ((f1 - f2).absoluteValue < 0.001) 0 else 1 }
            .isEqualTo(expectedWidth)
        assertThat(container.height)
            .usingComparator { f1, f2 -> if ((f1 - f2).absoluteValue < 0.001) 0 else 1 }
            .isEqualTo(expectedHeight)
    }

    @Suppress("unused")
    private fun argumentProvider() = listOf(
        /* ZERO child HORIZONTAL, CLIP, NO padding and NO border */
        arguments(emptyList<Box>(), 100f, 84f, HORIZONTAL, Wrapping.CLIP, null, null, listOf(0f, 0f)),
        /* ZERO child HORIZONTAL, CLIP, ARBITRARY padding and ARBITRARY border */
        arguments(emptyList<Box>(), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(20f, 8f, 10f, 15f), Border(14f), listOf(53f, 56f)),
        /* ZERO child HORIZONTAL, CLIP, ARBITRARY padding and ARBITRARY border in total LARGER than available width */
        arguments(emptyList<Box>(), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(20f, 8f, 10f, 15f), Border(47f), listOf(100f, 84f)),
        /* ONE child HORIZONTAL, CLIP, which is SMALLER than available width; NO padding and NO border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, null, null, listOf(34f, 29f)),
        /* ONE child HORIZONTAL, CLIP, with ARBITRARY margin which is SMALLER than available width; NO padding and NO border */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(9f))), 100f, 84f, HORIZONTAL, Wrapping.CLIP, null, null, listOf(52f, 47f)),
        /* ONE child HORIZONTAL, CLIP, with ARBITRARY margin which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(9f))), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(7f, 13f, 10f, 8f), Border(14f), listOf(81f, 79f)),
        /* ONE child HORIZONTAL, CLIP, which is SMALLER than available width; ARBITRARY padding and NO border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(20f, 8f, 10f, 15f), null, listOf(59f, 57f)),
        /* ONE child HORIZONTAL, CLIP, which is SMALLER than available width; NO padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, null, Border(14f), listOf(62f, 57f)),
        /* ONE child HORIZONTAL, CLIP, which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(20f, 8f, 10f, 15f), Border(14f), listOf(87f, 84f)),
        /* ONE child HORIZONTAL, CLIP, which is SMALLER than available width; ARBITRARY padding and ARBITRARY border in total LARGER than available width */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(20f, 8f, 10f, 15f), Border(34f), listOf(100f, 84f)),
        /* ONE child HORIZONTAL, CLIP, which is LARGER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(134f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(20f, 8f, 10f, 15f), Border(14f), listOf(100f, 84f)),
        /* TWO child HORIZONTAL, CLIP, which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 25f), MockBox(9f, 30f)), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(96f, 76f)),
        /* TWO child HORIZONTAL, CLIP, with ARBITRARY margin which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 25f, margins = Margins(top = 11f, bottom = 1f, start = 1f, end = 1f)), MockBox(9f, 25f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(98f, 72f)),
        /* TWO child HORIZONTAL, CLIP, with ARBITRARY margin which is LARGER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 25f, margins = Margins(top = 11f, bottom = 1f, start = 1f, end = 1f)), MockBox(12f, 24f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(100f, 72f)),
        /* THREE child HORIZONTAL, CLIP, with ARBITRARY margin which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(4f, 25f, margins = Margins(1f)), MockBox(2f, 24f, margins = Margins(top = 9f, bottom = 10f, start = 9f, end = 9f)), MockBox(21f, 24f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(98f, 72f)),
        /* THREE child HORIZONTAL, CLIP, with ARBITRARY margin which is LARGER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(8f, 25f, margins = Margins(1f)), MockBox(2f, 24f, margins = Margins(top = 9f, bottom = 10f, start = 9f, end = 9f)), MockBox(21f, 24f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.CLIP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(100f, 72f)),
        ////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        /* ZERO child HORIZONTAL, WRAP, NO padding and NO border */
        arguments(emptyList<Box>(), 100f, 84f, HORIZONTAL, Wrapping.WRAP, null, null, listOf(0f, 0f)),
        /* ZERO child HORIZONTAL, WRAP, ARBITRARY padding and ARBITRARY border */
        arguments(emptyList<Box>(), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(20f, 8f, 10f, 15f), Border(14f), listOf(53f, 56f)),
        /* ZERO child HORIZONTAL, WRAP, ARBITRARY padding and ARBITRARY border in total LARGER than available width */
        arguments(emptyList<Box>(), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(20f, 8f, 10f, 15f), Border(47f), listOf(100f, 84f)),
        /* ONE child HORIZONTAL, WRAP, which is SMALLER than available width; NO padding and NO border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, null, null, listOf(34f, 29f)),
        /* ONE child HORIZONTAL, WRAP, with ARBITRARY margin which is SMALLER than available width; NO padding and NO border */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(9f))), 100f, 84f, HORIZONTAL, Wrapping.WRAP, null, null, listOf(52f, 47f)),
        /* ONE child HORIZONTAL, WRAP, with ARBITRARY margin which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 29f, margins = Margins(9f))), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(7f, 13f, 10f, 8f), Border(14f), listOf(81f, 79f)),
        /* ONE child HORIZONTAL, WRAP, which is SMALLER than available width; ARBITRARY padding and NO border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(20f, 8f, 10f, 15f), null, listOf(59f, 57f)),
        /* ONE child HORIZONTAL, WRAP, which is SMALLER than available width; NO padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, null, Border(14f), listOf(62f, 57f)),
        /* ONE child HORIZONTAL, WRAP, which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(20f, 8f, 10f, 15f), Border(14f), listOf(87f, 84f)),
        /* ONE child HORIZONTAL, WRAP, which is SMALLER than available width; ARBITRARY padding and ARBITRARY border in total LARGER than available width */
        arguments(listOf(MockBox(34f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(20f, 8f, 10f, 15f), Border(34f), listOf(100f, 84f)),
        /* ONE child HORIZONTAL, WRAP, which is LARGER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(134f, 29f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(20f, 8f, 10f, 15f), Border(14f), listOf(100f, 84f)),
        /* TWO child HORIZONTAL, WRAP, which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 25f), MockBox(9f, 30f)), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(96f, 76f)),
        /* TWO child HORIZONTAL, WRAP, with ARBITRARY margin which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        arguments(listOf(MockBox(34f, 25f, margins = Margins(top = 11f, bottom = 1f, start = 1f, end = 1f)), MockBox(9f, 25f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.WRAP, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(98f, 72f)),
        // /* TWO child HORIZONTAL, WRAP, with ARBITRARY margin which is LARGER than available width; ARBITRARY padding and ARBITRARY border */
        // arguments(listOf(MockBox(34f, 25f, margins = Margins(top = 11f, bottom = 1f, start = 1f, end = 1f)), MockBox(12f, 24f, margins = Margins(2f))), 100f, 100f, HORIZONTAL, Wrapping.Wrap, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(87f, 96f)),
        // /* THREE child HORIZONTAL, WRAP, with ARBITRARY margin which is SMALLER than available width; ARBITRARY padding and ARBITRARY border */
        // arguments(listOf(MockBox(4f, 25f, margins = Margins(1f)), MockBox(2f, 24f, margins = Margins(top = 9f, bottom = 10f, start = 9f, end = 9f)), MockBox(21f, 24f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.Wrap, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(98f, 72f)),
        // /* THREE child HORIZONTAL, WRAP, with ARBITRARY margin which is LARGER than available width; ARBITRARY padding and ARBITRARY border */
        // arguments(listOf(MockBox(8f, 25f, margins = Margins(1f)), MockBox(2f, 24f, margins = Margins(top = 9f, bottom = 10f, start = 9f, end = 9f)), MockBox(21f, 24f, margins = Margins(2f))), 100f, 84f, HORIZONTAL, Wrapping.Wrap, Paddings(10f, 8f, 10f, 15f), Border(14f), listOf(100f, 72f)),
    )
}
