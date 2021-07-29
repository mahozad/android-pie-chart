package ir.mahozad.android.util

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

    @DisplayName("Parse border dash array from")
    @ParameterizedTest(name = "String: {0}")
    @MethodSource("argumentProvider")
    fun parseBorderDashArrayFromTheGivenString(string: String?, expectedDashArray: List<Float>) {
        val dashArray = parseBorderDashArray(string)
        assertThat(dashArray).isEqualTo(expectedDashArray)
    }

    private fun argumentProvider() = listOf(
        arguments(null, emptyList<Float>()),
        arguments("", emptyList<Float>()),
        arguments(" ", emptyList<Float>()),
        arguments(".1", listOf(0.1f)),
        arguments(".1f", listOf(0.1f)),
        arguments("0.1", listOf(0.1f)),
        arguments("0.1f", listOf(0.1f)),
        arguments("0.1f .2", listOf(0.1f, 0.2f)),
        arguments("0.1f, .2", listOf(0.1f, 0.2f)),
        arguments("0.1f; .2", listOf(0.1f, 0.2f)),
        arguments("0.1f ; .2", listOf(0.1f, 0.2f)),
        arguments("0.1f  ,.2", listOf(0.1f, 0.2f))
    )
}
