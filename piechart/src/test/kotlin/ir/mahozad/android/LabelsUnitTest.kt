package ir.mahozad.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.labels.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension

/**
 * The *@TestInstance* annotation is used as an alternative to
 * making the argument provider method for *@MethodSource* static.
 */
@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension::class)
class LabelsUnitTest {

    @Mock lateinit var context: Context
    @Mock lateinit var canvas: Canvas
    @Mock lateinit var font: Typeface

    @BeforeEach fun setUp() {
        // import org.mockito.Mockito.`when` as whenever
        // whenever(canvas.width).then { 123 }
        // verify(canvas).drawText(any(String::class.java), any(Float::class.java), any(Float::class.java), any(Paint::class.java))
    }

    @DisplayName("Create the labels maker")
    @ParameterizedTest(name = "#{index} from label type: {0}")
    @MethodSource("argumentProvider2")
    fun <T> factoryShouldCreateTheRightLabelsFromTheGivenLabelType(labelType: PieChart.LabelType, expectedType: Class<T>?) {
        val labels = createLabelsMaker(context, labelType, false)
        labels?.let { assertThat(it::class.java).isEqualTo(expectedType) }
            ?: assertThat(expectedType).isNull()
    }

    private fun argumentProvider2() = listOf(
        arguments(PieChart.LabelType.NONE, null),
        arguments(PieChart.LabelType.INSIDE, InsideLabels::class.java),
        arguments(PieChart.LabelType.OUTSIDE, OutsideLabels::class.java),
        arguments(PieChart.LabelType.OUTSIDE_CIRCULAR_INWARD, OutsideCircularLabels::class.java),
        arguments(PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD, OutsideCircularLabels::class.java)
    )

    @Test fun drawInsideLabel() {
        val availableBounds = Bounds(0f, 0f, 900f, 1300f)
        val slicesProperties = listOf(SliceProperties(1f, 0f, CLOCKWISE))
        val labelsProperties = listOf<LabelProperties>()
        val labels = InsideLabels(context)

        labels.layOut(availableBounds, slicesProperties, labelsProperties)
        labels.draw(canvas)

        verifyNoInteractions(canvas)
        assertThat(canvas.saveCount).isEqualTo(0)
    }

    @DisplayName("Lay out inside label; remaining bounds should be correct")
    @ParameterizedTest(name = "#{index} with Available bounds: {0}, Pie center: {1}, Slices properties: {2}, Labels properties: {3}")
    @MethodSource("argumentProvider")
    internal fun layOutInsideLabel(
        availableBounds: Bounds,
        slicesProperties: List<SliceProperties>,
        labelsProperties: List<LabelProperties>,
        expectedRemainingBounds: Bounds
    ) {
        val labels = InsideLabels(context)
        labels.layOut(availableBounds, slicesProperties, labelsProperties)

        val bounds = labels.getRemainingBounds()

        assertThat(bounds).isEqualToBounds(expectedRemainingBounds)
    }

    private fun argumentProvider() = listOf(
        arguments(Bounds(0f, 0f, 1000f, 1000f), listOf<SliceProperties>(), listOf<LabelProperties>(), Bounds(0f, 0f, 1000f, 1000f)),
        arguments(Bounds(0f, 0f, 1000f, 1000f), listOf(SliceProperties(1f, 0f, CLOCKWISE)), listOf<LabelProperties>(), Bounds(0f, 0f, 1000f, 1000f)),
        arguments(Bounds(0f, 0f, 900f, 1300f), listOf(SliceProperties(1f, 0f, CLOCKWISE)), listOf<LabelProperties>(), Bounds(0f, 0f, 900f, 1300f)),
        arguments(Bounds(0f, 0f, 900f, 1300f), listOf(SliceProperties(1f, 0f, CLOCKWISE)), listOf(LabelProperties("14%",0.65f, 10f, 23f, 0, font, null, null, 14f, 8f, PieChart.IconPlacement.START)), Bounds(0f, 0f, 900f, 1300f)),
        arguments(Bounds(0f, 0f, 900f, 1300f), listOf(SliceProperties(1f, 0f, CLOCKWISE)), listOf(LabelProperties("14%",0.65f, 10f, 23f, 0, font, R.drawable.ic_circle, null, 14f, 8f, PieChart.IconPlacement.START)), Bounds(0f, 0f, 900f, 1300f))
    )

    @DisplayName("Lay out outside label; remaining bounds should be correct")
    @ParameterizedTest(name = "#{index} with Available bounds: {0}, Pie center: {1}, Slices properties: {2}, Labels properties: {3}")
    @MethodSource("argumentProvider3")
    internal fun layOutOutsideLabel(
        availableBounds: Bounds,
        slicesProperties: List<SliceProperties>,
        labelsProperties: List<LabelProperties>,
        expectedRemainingBounds: Bounds
    ) {
        val shouldCenterPie = false
        val labels = OutsideLabels(context, shouldCenterPie)
        labels.layOut(availableBounds, slicesProperties, labelsProperties)

        val bounds = labels.getRemainingBounds()

        assertThat(bounds).isEqualToBounds(expectedRemainingBounds)
    }

    private fun argumentProvider3() = listOf(
        arguments(Bounds(0f, 0f, 1000f, 1000f), listOf<SliceProperties>(), listOf<LabelProperties>(), Bounds(0f, 0f, 1000f, 1000f)),
        arguments(Bounds(0f, 0f, 1000f, 1000f), listOf(SliceProperties(1f, 0f, CLOCKWISE)), listOf<LabelProperties>(), Bounds(0f, 0f, 1000f, 1000f)),
        arguments(Bounds(0f, 0f, 900f, 1300f), listOf(SliceProperties(1f, 0f, CLOCKWISE)), listOf<LabelProperties>(), Bounds(0f, 200f, 900f, 1100f)),
    )
}
