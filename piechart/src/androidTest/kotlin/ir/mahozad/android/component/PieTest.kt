package ir.mahozad.android.component

import android.graphics.Typeface
import androidx.test.platform.app.InstrumentationRegistry
import ir.mahozad.android.Coordinates
import ir.mahozad.android.PieChart
import org.assertj.core.api.Assertions
import org.assertj.core.util.FloatComparator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

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
        val pie = Pie(context, width, height, null, null, 0, emptyList(), PieChart.LabelType.INSIDE, 0f, 0f, 0, Typeface.DEFAULT, 0f, 0f, PieChart.IconPlacement.START, null, 0f, false, PieChart.DrawDirection.CLOCKWISE, 0f, 0f, PieChart.GradientType.SWEEP, 0f, null, 0f, PieChart.GapPosition.MIDDLE)
        val center = pie.calculatePieCenter(top, start)
        Assertions.assertThat(center)
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
}
