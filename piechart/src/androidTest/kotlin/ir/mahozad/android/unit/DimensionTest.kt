package ir.mahozad.android.unit

import de.mannodermaus.junit5.condition.DisabledIfBuildConfigValue
import ir.mahozad.android.ScreenshotTest
import ir.mahozad.android.unit.Dimension.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.FloatComparator
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

/**
 * NOTE: The dimensions and the tests depend on the device and its display density.
 *  So we disable these tests on CI. See [ScreenshotTest] class documentations for more information.
 */
@DisabledIfBuildConfigValue(named = "CI", matches = "true")
@TestInstance(PER_CLASS)
class DimensionTest {

    @DisplayName("Dimensions and their conversions should be correct")
    @ParameterizedTest(name = "#{index} with Dimension: {0}")
    @MethodSource("argumentProvider")
    fun dimensionsAndTheirConversionsShouldBeCorrect(
        dimension: Dimension,
        expectedPxValue: Float,
        expectedDpValue: Float,
        expectedSpValue: Float,
    ) {
        assertThat(dimension.px)
            .usingComparator(FloatComparator(0.01f))
            .isEqualTo(expectedPxValue)
        assertThat(dimension.dp)
            .usingComparator(FloatComparator(0.01f))
            .isEqualTo(expectedDpValue)
        assertThat(dimension.sp)
            .usingComparator(FloatComparator(0.01f))
            .isEqualTo(expectedSpValue)
    }

    /**
     * Could also have used Kotlin [Triple].
     */
    private fun argumentProvider() = listOf(
        arguments(PX(0f), 0f, 0f, 0f),
        arguments(PX(10f), 10f, 3.8f, 3.8f),
        arguments(PX(47f), 47f, 17.86f, 17.86f),
        arguments(DP(0f), 0f, 0f, 0f),
        arguments(DP(10f), 26.31f, 10f, 10f),
        arguments(DP(47f), 123.67f, 47f, 47f),
        arguments(SP(0f), 0f, 0f, 0f),
        arguments(SP(10f), 26.31f, 10f, 10f),
        arguments(SP(47f), 123.67f, 47f, 47f),
    )
}
