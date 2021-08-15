package ir.mahozad.android.component

import android.graphics.Color
import android.graphics.Typeface
import androidx.test.platform.app.InstrumentationRegistry
import ir.mahozad.android.Coordinates
import ir.mahozad.android.PieChart
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.FloatComparator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.system.measureTimeMillis

/**
 * Could not run these test as unit tests because they and the class under test
 * use android features (*View::MeasureSpec*)
 *
 * The *@TestInstance* annotation is used as an alternative to
 * making the argument provider method for *@MethodSource* static.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieTest {

    @ParameterizedTest(name = "Test #{index} with pie top: {0}, start: {1}, width: {2}, height: {3}")
    @DisplayName("Calculate pie center")
    @MethodSource("argumentProvider1")
    internal fun calculatePieCenterWithTheGivenArguments(top: Float, start: Float, width: Float, height: Float, expectedCoordinates: Coordinates) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, emptyList(), 0f, PieChart.LabelType.INSIDE, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0f, 0f, PieChart.GradientType.SWEEP, 0f, null, 0f, PieChart.GapPosition.MIDDLE)
        val center = pie.calculatePieCenter(top, start, width, height)
        assertThat(center)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(expectedCoordinates)
    }

    private fun argumentProvider1() = listOf(
        arguments(0f, 0f, 0f, 0f, Coordinates(0f, 0f)),
        arguments(0f, 0f, 500f, 0f, Coordinates(250f, 0f)),
        arguments(0f, 0f, 0f, 500f, Coordinates(0f, 250f)),
        arguments(0f, 0f, 500f, 500f, Coordinates(250f, 250f)),
        arguments(0f, 0f, 400f, 600f, Coordinates(200f, 300f)),
        arguments(-100f, -200f, 500f, 700f, Coordinates(50f, 250f)),
    )

    @Test fun changePieAngleForInsideLabel() {
        val slices = listOf(
            PieChart.Slice(0.3f, Color.BLACK),
            PieChart.Slice(0.1f, Color.BLACK),
            PieChart.Slice(0.28f, Color.BLACK),
            PieChart.Slice(0.32f, Color.BLACK)
        )
        val labelType = PieChart.LabelType.INSIDE
        val width = 1000f
        val height = 1000f
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, slices, 0f, labelType, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0f, 0f, PieChart.GradientType.SWEEP, 0f, null, 0f, PieChart.GapPosition.MIDDLE)

        val duration = measureTimeMillis {
            pie.setStartAngle(30)
        }

        assertThat(duration).isLessThan(15)
    }

    @Test fun changePieAngleForOutsideLabel() {
        val slices = listOf(
            PieChart.Slice(0.3f, Color.BLACK),
            PieChart.Slice(0.1f, Color.BLACK),
            PieChart.Slice(0.28f, Color.BLACK),
            PieChart.Slice(0.32f, Color.BLACK)
        )
        val labelType = PieChart.LabelType.OUTSIDE
        val width = 1000f
        val height = 1000f
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, slices, 0f, labelType, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0f, 0f, PieChart.GradientType.SWEEP, 0f, null, 0f, PieChart.GapPosition.MIDDLE)

        val duration = measureTimeMillis {
            pie.setStartAngle(30)
        }

        assertThat(duration).isLessThan(15)
    }

    @Test fun changePieAngleForOutsideCircularLabel() {
        val slices = listOf(
            PieChart.Slice(0.3f, Color.BLACK),
            PieChart.Slice(0.1f, Color.BLACK),
            PieChart.Slice(0.28f, Color.BLACK),
            PieChart.Slice(0.32f, Color.BLACK)
        )
        val labelType = PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD
        val width = 1000f
        val height = 1000f
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, slices, 0f, labelType, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0f, 0f, PieChart.GradientType.SWEEP, 0f, null, 0f, PieChart.GapPosition.MIDDLE)

        val duration = measureTimeMillis {
            pie.setStartAngle(30)
        }

        assertThat(duration).isLessThan(15)
    }

    @Test fun changePieHoleRatio() {
        val slices = listOf(
            PieChart.Slice(0.3f, Color.BLACK),
            PieChart.Slice(0.1f, Color.BLACK),
            PieChart.Slice(0.28f, Color.BLACK),
            PieChart.Slice(0.32f, Color.BLACK)
        )
        val labelType = PieChart.LabelType.INSIDE
        val holeRatio = 0.33f
        val width = 1000f
        val height = 1000f
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, slices, 0f, labelType, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0f, 0f, PieChart.GradientType.SWEEP, holeRatio, null, 0f, PieChart.GapPosition.MIDDLE)

        val duration = measureTimeMillis {
            pie.setHoleRatio(0.19f)
        }

        assertThat(duration).isLessThan(15)
    }

    @Test fun changeOverlayRatio() {
        val slices = listOf(
            PieChart.Slice(0.3f, Color.BLACK),
            PieChart.Slice(0.1f, Color.BLACK),
            PieChart.Slice(0.28f, Color.BLACK),
            PieChart.Slice(0.32f, Color.BLACK)
        )
        val labelType = PieChart.LabelType.INSIDE
        val overlayRatio = 0.49f
        val width = 1000f
        val height = 1000f
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, slices, 0f, labelType, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, overlayRatio, 0f, PieChart.GradientType.SWEEP, 0.3f, null, 0f, PieChart.GapPosition.MIDDLE)

        val duration = measureTimeMillis {
            pie.setOverlayRatio(0.567f)
        }

        assertThat(duration).isLessThan(15)
    }

    @Test fun changeOverlayAlpha() {
        val slices = listOf(
            PieChart.Slice(0.3f, Color.BLACK),
            PieChart.Slice(0.1f, Color.BLACK),
            PieChart.Slice(0.28f, Color.BLACK),
            PieChart.Slice(0.32f, Color.BLACK)
        )
        val labelType = PieChart.LabelType.INSIDE
        val overlayAlpha = 0.89f
        val width = 1000f
        val height = 1000f
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pie = Pie(context, width, height, null, null, 0, slices, 0f, labelType, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0.34f, overlayAlpha, PieChart.GradientType.SWEEP, 0.3f, null, 0f, PieChart.GapPosition.MIDDLE)

        val duration = measureTimeMillis {
            pie.setOverlayAlpha(0.567f)
        }

        assertThat(duration).isLessThan(15)
    }
}
