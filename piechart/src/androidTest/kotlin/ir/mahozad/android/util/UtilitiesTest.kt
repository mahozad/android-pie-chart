package ir.mahozad.android.util

import androidx.test.platform.app.InstrumentationRegistry
import ir.mahozad.android.dp
import ir.mahozad.android.px
import ir.mahozad.android.sp
import ir.mahozad.android.unit.Dimension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

/**
 * The *@TestInstance* annotation is used an an alternative to
 * making the argument provider method for *@MethodSource* static.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UtilitiesTest {

    @DisplayName("Parse border dash array")
    @ParameterizedTest(name = "#{index} with String: {0}")
    @MethodSource("argumentProvider")
    fun parseBorderDashArrayFromTheGivenString(string: String?, expectedDashArray: List<Dimension>?) {
        val dashArray = parseBorderDashArray(string)
        assertThat(dashArray).isEqualTo(expectedDashArray)
    }

    private fun argumentProvider() = listOf(
        arguments(null, null),
        arguments("", null),
        arguments(" ", null),
        arguments("1dp", listOf(1.dp)),
        arguments(".1dp", listOf(0.1.dp)),
        arguments("0.1dp", listOf(0.1.dp)),
        arguments("0.1fdp", listOf(0.1.dp)),
        arguments("0.1fdp .2dp", listOf(0.1.dp, 0.2.dp)),
        arguments("0.1fdp, .2dp", listOf(0.1.dp, 0.2.dp)),
        arguments("0.1fdp; .2dp", listOf(0.1.dp, 0.2.dp)),
        arguments("0.1fdp ; .2dp", listOf(0.1.dp, 0.2.dp)),
        arguments("0.1fdp  ,.2dp", listOf(0.1.dp, 0.2.dp)),
        arguments("0.1fpx  ,.2dp", listOf(0.1.px, 0.2.dp)),
        arguments("0.1fsp ; .2px", listOf(0.1.sp, 0.2.px)),
    )

    @DisplayName("Get array element in circular mode")
    @ParameterizedTest(name = "Test #{index} with Index: {1}, Array: {0}")
    @MethodSource("argumentProvider2")
    fun <T> getArrayElementCircular(array: Array<T>?, index: Int, expectedElement: T?) {
        val element = array.getElementCircular(index)
        assertThat(element).isEqualTo(expectedElement)
    }

    private fun argumentProvider2() = listOf(
        arguments(null, 0, null),
        arguments(null, 1, null),
        arguments(emptyArray<Int>(), 0, null),
        arguments(emptyArray<Int>(), 1, null),
        arguments(arrayOf<Int?>(null), 0, null),
        arguments(arrayOf("a"), 0, "a"),
        arguments(arrayOf("a"), 1, "a"),
        arguments(arrayOf("a"), 2, "a"),
        arguments(arrayOf("a", "b"), 0, "a"),
        arguments(arrayOf("a", "b"), 1, "b"),
        arguments(arrayOf("a", "b"), 2, "a"),
        arguments(arrayOf("a", "b"), 3, "b"),
    )

    @DisplayName("Get color array")
    @ParameterizedTest(name = "Test #{index} with attr: {0}")
    @MethodSource("argumentProvider3")
    fun getColorArray(themeId: Int, expectedColorArray: IntArray?) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.setTheme(themeId)
        val testTheme = context.theme
        val typedArray = testTheme.obtainStyledAttributes(ir.mahozad.android.test.R.styleable.TestStyleable)

        val array = getColorArray(typedArray, ir.mahozad.android.test.R.styleable.TestStyleable_testColorArrayAttr)

        assertThat(array).isEqualTo(expectedColorArray)
    }

    private fun argumentProvider3() = listOf(
        arguments(ir.mahozad.android.test.R.style.TestStyleNotDefined, null),
        arguments(ir.mahozad.android.test.R.style.TestStyleEmptyAttribute, null),
        arguments(ir.mahozad.android.test.R.style.TestStyleAtNullAttribute, null),
        arguments(ir.mahozad.android.test.R.style.TestStyleLiteralValue, intArrayOf(-256)),
        arguments(ir.mahozad.android.test.R.style.TestStyleSingleReference, intArrayOf(-65281)),
        arguments(ir.mahozad.android.test.R.style.TestStyleArrayReference, intArrayOf(-16732632, 1593880136, -5363457, -15628033))
    )
}
