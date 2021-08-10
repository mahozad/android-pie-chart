package ir.mahozad.android

import android.graphics.*
import android.graphics.Color
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.PieChart.DrawDirection.COUNTER_CLOCKWISE
import ir.mahozad.android.PieChart.IconPlacement
import ir.mahozad.android.PieChart.IconPlacement.*
import ir.mahozad.android.labels.LabelProperties
import ir.mahozad.android.labels.SliceProperties
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.withPrecision
import org.assertj.core.util.FloatComparator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import kotlin.math.absoluteValue

/**
 * Could not run these test as unit tests because they and the class under test
 * use android features (*View::MeasureSpec*)
 *
 * The *@TestInstance* annotation is used an an alternative to
 * making the argument provider method for *@MethodSource* static.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SizeUtilInstrumentedTest {

    @AfterEach fun tearDown() {
        // Reset the locale if it was changed
        setLocale(Locale.ENGLISH)
    }

    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val resources = getInstrumentation().targetContext.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    // region calculateHeightAndWidth

    /**
     * UNSPECIFIED measure spec could indicate, among oter things, wrap_content
     */
    @Test fun twoSameUNSPECIFIEDSpecShouldResultInMaximumGivenAvailableSize() {
        val availableSize = 1000
        val widthSpec = makeMeasureSpec(availableSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(availableSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(availableSize)
        assertThat(height).isEqualTo(availableSize)
    }

    @Test fun twoDifferentUNSPECIFIEDSpecShouldResultInTheSmallerAvailableSize() {
        val smallerSize = 1000
        val largerSize = 1250
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoDifferentUNSPECIFIEDSpecShouldResultInTheSmallerAvailableSize_WH_REVERSED() {
        val smallerSize = 1000
        val largerSize = 1250
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneAT_MOSTSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_SPECS_SWAPPED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneEXACTLYSpecShouldResultInTheEXACTLYSize() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneEXACTLYSpecShouldResultInTheEXACTLYSize_SPECS_SWAPPED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneEXACTLYSpecShouldResultInTheEXACTLYSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoSameEXACTLYSpecShouldResultInTheEXACTLYSize() {
        val size = 500
        val widthSpec = makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(size)
        assertThat(height).isEqualTo(size)
    }

    @Test fun twoDifferentEXACTLYSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoDifferentEXACTLYSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneEXACTLYAndOneAT_MOSTSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneEXACTLYAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneEXACTLYAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_SPECS_SWAPPED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoSameAT_MOSTSpecShouldResultInTheAT_MOSTSize() {
        val maxSize = 500
        val widthSpec = makeMeasureSpec(maxSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(maxSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(maxSize)
        assertThat(height).isEqualTo(maxSize)
    }

    @Test fun twoDifferentAT_MOSTSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoDifferentAT_MOSTSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        assertThat(width).isEqualTo(smallerSize)
        assertThat(height).isEqualTo(smallerSize)
    }

    // endregion

    // region calculateRadius

    // @Test fun withNoPaddingRadiusShouldBeHalfTheWidthAndHeight() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 0
    //     val paddingRight = 0
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(250f)
    // }
    //
    // @Test fun withLeftPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 150
    //     val paddingRight = 0
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(175f)
    // }
    //
    // @Test fun withRightPaddingRadiusShouldBeHalfTheWidthMinusRightPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 0
    //     val paddingRight = 150
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(175f)
    // }
    //
    // @Test fun withLeftAndRightPaddingRadiusShouldBeHalfTheWidthMinusThePaddings() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 150
    //     val paddingRight = 150
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(100f)
    // }
    //
    // @Test fun withLargerLeftPaddingAndSmallerRightPaddingRadiusShouldBeHalfTheWidthMinusThePaddings() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 250
    //     val paddingRight = 150
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(50f)
    // }
    //
    // @Test fun withSmallerLeftPaddingAndLargerRightPaddingRadiusShouldBeHalfTheWidthMinusThePaddings() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 150
    //     val paddingRight = 250
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(50f)
    // }
    //
    // @Test fun withSameLeftPaddingAndTopPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 150
    //     val paddingRight = 0
    //     val paddingTop = 150
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(175f)
    // }
    //
    // @Test fun withLargerLeftPaddingAndSmallerTopPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 250
    //     val paddingRight = 0
    //     val paddingTop = 150
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(125f)
    // }
    //
    // @Test fun withSmallerLeftPaddingAndLargerTopPaddingRadiusShouldBeHalfTheHeightMinusTopPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 150
    //     val paddingRight = 0
    //     val paddingTop = 250
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(125f)
    // }
    //
    // @Test fun withLargerHorizontalPaddingAndSmallerVerticalPaddingRadiusShouldBeHalfTheWidthMinusHorizontalPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 150
    //     val paddingRight = 200
    //     val paddingTop = 250
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(75f)
    // }
    //
    // @Test fun withSmallerHorizontalPaddingAndLargerVerticalPaddingRadiusShouldBeHalfTheHeightMinusVerticalPadding() {
    //     val width = 500
    //     val height = 500
    //     val paddingLeft = 250
    //     val paddingRight = 0
    //     val paddingTop = 150
    //     val paddingBottom = 200
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(75f)
    // }
    //
    // @Test fun withSmallerWidthAndLargerHeightAndNoPaddingRadiusShouldBeHalfTheWidth() {
    //     val width = 500
    //     val height = 620
    //     val paddingLeft = 0
    //     val paddingRight = 0
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(250f)
    // }
    //
    // @Test fun withSmallerWidthAndLargerHeightAndLeftPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
    //     val width = 500
    //     val height = 620
    //     val paddingLeft = 150
    //     val paddingRight = 0
    //     val paddingTop = 0
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(175f)
    // }
    //
    // @Test fun withSmallerWidthAndLargerHeightAndSmallerLeftPaddingAndTinyLargerTopPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
    //     val width = 500
    //     val height = 620
    //     val paddingLeft = 150
    //     val paddingRight = 0
    //     val paddingTop = 10
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(175f)
    // }
    //
    // @Test fun withSmallerWidthAndLargerHeightAndSmallerLeftPaddingAndHugeLargerTopPaddingRadiusShouldBeHalfTheHeightMinusTopPadding() {
    //     val width = 500
    //     val height = 620
    //     val paddingLeft = 150
    //     val paddingRight = 0
    //     val paddingTop = 300
    //     val paddingBottom = 0
    //
    //     val radius =
    //         calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)
    //
    //     assertThat(radius).isEqualTo(160f)
    // }

    // endregion

    // region calculateBoundaries

    @Test fun withSmallRadiusBoundaryShouldBeCalculatedWithNoException() {
        val origin = Coordinates(500f, 500f)
        val radius = 200f

        val (left, top, right, bottom) = calculateBounds(origin, radius)

        assertThat(left).isEqualTo(300f)
        assertThat(top).isEqualTo(300f)
        assertThat(right).isEqualTo(700f)
        assertThat(bottom).isEqualTo(700f)
    }

    // endregion

    // region calculateGapCoordinates

    @ParameterizedTest(name = "Angle: {0}, Position: {1}")
    @MethodSource("argumentProvider")
    internal fun calculateGapCoordinatesForOrigin500And500AndTheGivenAngleAndPosition(
        angle: Float,
        position: PieChart.GapPosition,
        expectedCoordinates: List<Coordinates>
    ) {
        val origin = Coordinates(500f, 500f)
        val gapWidth = 20f
        val gapLength = 150f

        val coordinates = calculateGapCoordinates(origin, angle, gapWidth, gapLength, position)

        for ((i, corner) in coordinates.withIndex()) {
            assertThat(corner)
                .usingRecursiveComparison()
                .withComparatorForFields(FloatComparator(0.01f), Coordinates::x.name, Coordinates::y.name)
                .isEqualTo(expectedCoordinates[i])
        }
    }

    @Suppress("unused")
    private fun argumentProvider(): List<Arguments> {
        val angles = arrayOf(0f, 30f, 120f, 210f, -30f)
        val expectedCoordinatesByPosition = mapOf(
            PieChart.GapPosition.MIDDLE to listOf(
                listOf(Coordinates(500f, 510f), Coordinates(650f, 510f), Coordinates(650f, 490f), Coordinates(500f, 490f)),
                listOf(Coordinates(495f, 508.66f), Coordinates(624.90f, 583.66f), Coordinates(634.90f, 566.34f), Coordinates(505f, 491.34f)),
                listOf(Coordinates(491.34f, 495f), Coordinates(416.34f, 624.90f), Coordinates(433.66f, 634.90f), Coordinates(508.66f, 505f)),
                listOf(Coordinates(505f, 491.34f), Coordinates(375.10f, 416.34f), Coordinates(365.10f, 433.66f), Coordinates(495f, 508.66f)),
                listOf(Coordinates(505f, 508.66f), Coordinates(634.90f, 433.66f), Coordinates(624.90f, 416.34f), Coordinates(495f, 491.34f))
            ),
            PieChart.GapPosition.PRECEDING_SLICE to listOf(
                listOf(Coordinates(500f, 500f), Coordinates(650f, 500f), Coordinates(650f, 480f), Coordinates(500f, 480f)),
                listOf(Coordinates(500f, 500f), Coordinates(629.90f, 575f), Coordinates(639.90f, 557.68f), Coordinates(510f, 482.68f)),
                listOf(Coordinates(500f, 500f), Coordinates(425f, 629.90f), Coordinates(442.32f, 639.90f), Coordinates(517.32f, 510f)),
                listOf(Coordinates(500f, 500f), Coordinates(370.10f, 425f), Coordinates(360.10f, 442.32f), Coordinates(490f, 517.32f)),
                listOf(Coordinates(500f, 500f), Coordinates(629.90f, 425f), Coordinates(619.90f, 407.67f), Coordinates(490f, 482.68f))
            ),
            PieChart.GapPosition.SUCCEEDING_SLICE to listOf(
                listOf(Coordinates(500f, 520f), Coordinates(650f, 520f), Coordinates(650f, 500f), Coordinates(500f, 500f)),
                listOf(Coordinates(490f, 517.32f), Coordinates(619.90f, 592.32f), Coordinates(629.90f, 575f), Coordinates(500f, 500f)),
                listOf(Coordinates(482.68f, 490f), Coordinates(407.68f, 619.90f), Coordinates(425f, 629.90f), Coordinates(500f, 500f)),
                listOf(Coordinates(510f, 482.68f), Coordinates(380.10f, 407.68f), Coordinates(370.10f, 425f), Coordinates(500f, 500f)),
                listOf(Coordinates(510f, 517.32f), Coordinates(639.90f, 442.32f), Coordinates(629.90f, 425f), Coordinates(500f, 500f)),
            )
        )
        val arguments = mutableListOf<Arguments>()
        for (position in expectedCoordinatesByPosition) {
            arguments += position.value.mapIndexed { i, list ->
                arguments(angles[i], position.key, list)
            }
        }
        return arguments
    }

    // endregion

    // region updatePaintForLabel

    /**
     * Ensures that no new object is allocated in view::onDraw method
     */
    @Test fun updatedPaintForLabel_NoNewPaintObjectShouldBeCreated() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        val newPaint = updatePaintForLabel(paint, labelSize, labelColor, Typeface.DEFAULT)

        assertThat(newPaint).isSameAs(paint)
    }

    @Test fun updatedPaintForLabel_TextSizeShouldBeUpdated() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor, Typeface.DEFAULT)

        assertThat(paint.textSize).isEqualTo(labelSize)
    }

    @Test fun updatedPaintForLabel_AlignmentShouldBeCenter() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor, Typeface.DEFAULT)

        assertThat(paint.textAlign).isEqualTo(Paint.Align.CENTER)
    }

    /**
     * We don't want gradient for label.
     * If any was set previously, remove it.
     */
    @Test fun updatedPaintForLabel_NoGradientShouldBeSet() {
        val paint = Paint().apply {
            shader = RadialGradient(0f, 0f, 1f, 0, 0, Shader.TileMode.CLAMP)
        }
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor, Typeface.DEFAULT)

        assertThat(paint.shader).isEqualTo(null)
    }

    @Test fun updatedPaintForLabel_ColorShouldBeUpdated() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor, Typeface.DEFAULT)

        assertThat(paint.color).isEqualTo(labelColor)
    }

    // endregion

    // region calculateLabelIconWidth

    @Test fun calculateLabelIconWidth_ForNullDrawable() {
        val desiredIconHeight = 50f
        val icon  = null

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        assertThat(width).isEqualTo(0f)
    }

    /**
     * Refer to [this post](https://stackoverflow.com/q/40715100) for how to
     * access resource in *androidTest* source set.
     */
    @Test fun calculateLabelIconWidth_ForDrawableWith1To1AspectRatio() {
        val desiredIconHeight = 50f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to1_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        assertThat(width).isEqualTo(50f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith1To2AspectRatio() {
        val desiredIconHeight = 50f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to2_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        assertThat(width).isEqualTo(25f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith2To1AspectRatio() {
        val desiredIconHeight = 50f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_2to1_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        assertThat(width).isEqualTo(100f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith3To4AspectRatio() {
        val desiredIconHeight = 80f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_3to4_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        assertThat(width).isEqualTo(60f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith4To3AspectRatio() {
        val desiredIconHeight = 60f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_4to3_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        assertThat(width).isEqualTo(80f)
    }

    // endregion

    // region calculateLabelBounds

    /**
     * Could also have merged multiple assertions like this
     * ```kotlin
     * assertThat(bounds).extracting(RectF::width, RectF::height).contains(x, y)
     * ```
     * or used JUnit 5 *Assertions::assertAll* method.
     */
    @Test fun calculateLabelBounds_EmptyLabel() {
        val label = ""
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)

        val bounds = calculateLabelBounds(label, labelPaint)

        assertThat(bounds.width()).isEqualTo(0f)
        assertThat(bounds.height()).isEqualTo(0f)
    }

    @Test fun calculateLabelBounds_ArbitraryLabel() {
        val label = "12%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)

        val bounds = calculateLabelBounds(label, labelPaint)

        assertThat(bounds.width()).isEqualTo(112f)
        assertThat(bounds.height()).isEqualTo(70.3125f)
    }

    // endregion

    // region calculateAbsoluteBoundsForInsideLabelAndIcon

    @ParameterizedTest(name = "Angle: {0}, Label offset: {1}")
    @MethodSource("argumentProvider2")
    internal fun calculateAbsoluteBoundsForInsideLabelAndIcon_WithTheGivenAngleAndLabelOffset(
        angle: Float,
        labelOffset: Float,
        expectedBounds: RectF
    ) {
        val labelAndIconCombinedBounds = RectF(0f, 0f, 210f, 133f)
        val origin = Coordinates(400f, 400f)
        val pieRadius = 500f

        val bounds = calculateAbsoluteBoundsForInsideLabelAndIcon(labelAndIconCombinedBounds, angle, origin, pieRadius, labelOffset)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(expectedBounds)
    }

    @Suppress("unused")
    private fun argumentProvider2(): List<Arguments> {
        val angles = arrayOf(0f, 10f, 45f, 90f, 91f)
        val offsets = arrayOf(0f, 0.5f, 0.75f, 1f)
        val expectedBounds = arrayOf(
            RectF(295f, 333.5f, 505f, 466.5f),
            RectF(545f, 333.5f, 755f, 466.5f),
            RectF(670f, 333.5f, 880f, 466.5f),
            RectF(795f, 333.5f, 1005f, 466.5f),
            RectF(295f, 333.5f, 505f, 466.5f),
            RectF(541.20f, 376.91f, 751.20f, 509.91f),
            RectF(664.30f, 398.62f, 874.30f, 531.62f),
            RectF(787.40f, 420.32f, 997.40f, 553.32f),
            RectF(295f, 333.5f, 505f, 466.5f),
            RectF(471.78f, 510.28f, 681.78f, 643.28f),
            RectF(560.17f, 598.67f, 770.17f, 731.67f),
            RectF(648.55f, 687.05f, 858.55f, 820.05f),
            RectF(295f, 333.5f, 505f, 466.5f),
            RectF(295f, 583.5f, 505f, 716.5f),
            RectF(295f, 708.5f, 505f, 841.5f),
            RectF(295f, 833.5f, 505f, 966.5f),
            RectF(295f, 333.5f, 505f, 466.5f),
            RectF(290.64f, 583.46f, 500.64f, 716.46f),
            RectF(288.46f, 708.44f, 498.46f, 841.44f),
            RectF(286.27f, 833.42f, 496.27f, 966.42f)
        )
        var i = 0
        val arguments = mutableListOf<Arguments>()
        for (angle in angles) {
            for (offset in offsets) {
                arguments += arguments(angle, offset, expectedBounds[i++])
            }
        }
        return arguments
    }

    // endregion

    // region calculatePieNewBoundsForOutsideLabels

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingClockWiseAndNoMarginAndFalseShouldCenterPie() {
        val fractions = listOf(0.5f, 0.105f, 0.125f, 0.02f, 0.1f, 0.15f)
        val outsideLabelsMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("long label", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, 0f, 0f, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, 0f, 0f, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, 0f, 0f, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, 0f, 0f, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, 0f, 0f, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, 0f, 0f, iconPlacement)
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds).isEqualTo(Bounds(100f, 234.5f, 731f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithOneSliceHavingIcon() {
        val fractions = listOf(0.43f, 0.21f, 0.19f, 0.14f, 0.03f)
        val outsideLabelsMargin = 73.675f
        val drawDirection = CLOCKWISE
        val currentBounds = Bounds(0f, 0f, 1080f, 1080f)
        val shouldCenterPie = true
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 42.1f
        val startAngle = 250f
        val iconsHeight = 42.1f
        val iconsMargin = 73.675f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("43%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, R.drawable.ic_circle, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("21%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("19%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("14%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("3%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(216.87f, 216.87f, 863.13f, 863.13f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabel_WithDrawingClockWiseAndNoMarginAndFalseShouldCenterPieAndLongLabelOnLeftSide() {
        val fractions = listOf(0.125f, 0.125f, 0.25f, 0.5f)
        val outsideLabelsMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90f
        val iconsHeight = 0f
        val iconsMargin = 0f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("long label", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(369f, 234.5f, 1000f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabel_WithDrawingCounterClockWiseAndNoMarginAndShouldCenterPieFalse() {
        val fractions = listOf(0.5f, 0.105f, 0.125f, 0.02f, 0.1f, 0.15f)
        val outsideLabelsMargin = 0f
        val drawDirection = COUNTER_CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90f
        val iconsHeight = 0f
        val iconsMargin = 0f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("long label", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(369f, 234.5f, 1000f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabel_WithDrawingCounterClockWiseAndNoMarginAndFalseShouldCenterPieAndLongLabelOnLeftSide() {
        val fractions = listOf(0.125f, 0.125f, 0.25f, 0.5f)
        val outsideLabelsMargin = 0f
        val drawDirection = COUNTER_CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90f
        val iconsHeight = 0f
        val iconsMargin = 0f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("long label", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds).isEqualTo(Bounds(100f, 234.5f, 731f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingClockWiseAndArbitraryMarginAndFalseShouldCenterPie() {
        val fractions = listOf(0.5f, 0.105f, 0.125f, 0.02f, 0.1f, 0.15f)
        val outsideLabelsMargin = 42f
        val drawDirection = CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90f
        val iconsHeight = 0f
        val iconsMargin = 0f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("long label", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds).isEqualTo(Bounds(100f, 255.5f, 689f, 844.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithOnlyTopLabelAndNoMarginAndFalseShouldCenterPie() {
        val fractions = listOf(0.5f, 0.105f, 0.125f, 0.02f, 0.1f, 0.15f)
        val outsideLabelsMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -180f
        val iconsHeight = 0f
        val iconsMargin = 0f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("ABC", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(137f, 174f, 963f, 1000f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabel_WithRightLabelHavingAngleGreaterThan270() {
        val fractions = listOf(0.43f, 0.21f, 0.19f, 0.14f, 0.03f)
        val outsideLabelsMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = 0f
        val iconsHeight = 0f
        val iconsMargin = 0f
        val context = getInstrumentation().targetContext
        val iconPlacement = LEFT
        val labelsProperties = listOf(
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
            LabelProperties("3%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconsHeight, iconsMargin, iconPlacement),
        )
        val startAngles = fractions.runningFold(startAngle) { angle, fraction -> calculateEndAngle(angle, fraction, drawDirection) }
        val slicesProperties = fractions.mapIndexed { i, fraction -> SliceProperties(fraction, startAngles[i], drawDirection) }

        val bounds = calculatePieNewBoundsForOutsideLabel(context, currentBounds, labelsProperties, slicesProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(100f, 142.74f, 914.52f, 957.26f))
    }

    // endregion

    // region normalizeAngle

    @ParameterizedTest(name = "Angle: {0}")
    @MethodSource("argumentProvider3")
    fun normalizeAngle_WithTheGivenAngle(angle: Float, expectedAngle: Float) {
        val normalizedAngle = normalizeAngle(angle)
        assertThat(normalizedAngle)
            .usingComparator { f1, f2 -> if ((f1 - f2).absoluteValue < 0.001) 0 else 1 }
            .isEqualTo(expectedAngle)
    }

    @Suppress("unused")
    private fun argumentProvider3(): List<Arguments> {
        val angles = arrayOf(
            -450f, -370f, -360f, -359.9f, -310f, -270.1f, -270f, -269.9f, -220f,
            -180.1f, -180f, -179.9f, -130f, -90.1f, -90f, -89.9f, -40f,
            -0.1f, 0f, 0.1f, 50f, 89.9f, 90f, 90.1f, 140f, 179.9f, 180f, 180.1f,
            230f, 269.9f, 270f, 270.1f, 320f, 359.9f, 360f ,360.1f, 370f, 450f
        )
        val expectedAngles = arrayOf(
            270f, 350f, 0f, 0.1f, 50f, 89.9f, 90f, 90.1f, 140f,
            179.9f, 180f, 180.1f, 230f, 269.9f, 270f, 270.1f, 320f,
            359.9f, 0f, 0.1f, 50f, 89.9f, 90f, 90.1f, 140f, 179.9f, 180f, 180.1f,
            230f, 269.9f, 270f, 270.1f, 320f, 359.9f, 0f, 0.1f, 10f, 90f
        )
        return angles.mapIndexed { i, angle -> arguments(angle, expectedAngles[i]) }
    }

    // endregion

    // region calculateMiddleAngle

    /**
     * There are a few ways to assert floating point values:
     * ```kotlin
     * assertThat(1.2f)
     * .usingComparator { f1, f2 -> if ((f1 - f2).absoluteValue < 0.001) 0 else 1 }
     * .isEqualTo(1.23f)
     * ```
     * ```kotlin
     * assertThat(1.2f).isEqualTo(1.23f, withPrecision(2f))
     * ```
     * ```kotlin
     * assertThat(1.2f).isCloseTo(1.23f, Offset.offset(0.1f))
     * ```
     */
    @ParameterizedTest(name = "Angle: {0}, Fraction: {1}, Direction: {2}")
    @MethodSource("argumentProvider8")
    fun calculateMiddleAngle_WithTheGivenAngleAndFractionAndDirection(angle: Float, fraction: Float, direction: PieChart.DrawDirection, expectedAngle: Float) {
        val middleAngle = calculateMiddleAngle(angle, fraction, direction)
        assertThat(middleAngle).isEqualTo(expectedAngle, withPrecision(0.01f))
    }

    @Suppress("unused")
    private fun argumentProvider8(): List<Arguments> {
        val directions = arrayOf(CLOCKWISE, COUNTER_CLOCKWISE)
        val fractions = arrayOf(-0.1f, 0f, 0.1f)
        val angles = arrayOf(-450f, -370f, -360f, -350f, -170f, -20f, 0f, 20f, 170f, 350f, 360f, 370f, 450f)
        val expectedAngles = arrayOf(
            /* for -450: */ 252f, 288f, 270f, 270f, 288f, 252f,
            /* for -370: */ 332f, 8f, 350f, 350f, 8f, 332f,
            /* for -360: */ 342f, 18f, 0f, 0f, 18f, 342f,
            /* for -350: */ 352f, 28f, 10f, 10f, 28f, 352f,
            /* for -170: */ 172f, 208f, 190f, 190f, 208f, 172f,
            /* for  -20: */ 322f, 358f, 340f, 340f, 358f, 322f,
            /* for    0: */ 342f, 18f, 0f, 0f, 18f, 342f,
            /* for   20: */ 2f, 38f, 20f, 20f, 38f, 2f,
            /* for  170: */ 152f, 188f, 170f, 170f, 188f, 152f,
            /* for  350: */ 332f, 8f, 350f, 350f, 8f, 332f,
            /* for  360: */ 342f, 18f, 0f, 0f, 18f, 342f,
            /* for  370: */ 352f, 28f, 10f, 10f, 28f, 352f,
            /* for  450: */ 72f, 108f, 90f, 90f, 108f, 72f
        )
        var i = 0
        val arguments = mutableListOf<Arguments>()
        for (angle in angles) {
            for (fraction in fractions) {
                for (direction in directions) {
                    val tuple = arguments(angle, fraction, direction, expectedAngles[i++])
                    arguments.add(tuple)
                }
            }
        }
        return arguments
    }

    // endregion

    // region calculateEndAngle

    @ParameterizedTest(name = "Angle: {0}, Fraction: {1}, Direction: {2}")
    @MethodSource("argumentProvider5")
    fun calculateEndAngle_WithTheGivenAngleAndFractionAndDirection(angle: Float, fraction: Float, direction: PieChart.DrawDirection, expectedAngle: Float) {
        val endAngle = calculateEndAngle(angle, fraction, direction)
        assertThat(endAngle).isEqualTo(expectedAngle, withPrecision(0.01f))
    }

    @Suppress("unused")
    private fun argumentProvider5(): List<Arguments> {
        val directions = arrayOf(CLOCKWISE, COUNTER_CLOCKWISE)
        val fractions = arrayOf(-0.1f, 0f, 0.1f)
        val angles = arrayOf(-450f, -370f, -360f, -350f, -170f, -20f, 0f, 20f, 170f, 350f, 360f, 370f, 450f)
        val expectedAngles = arrayOf(
            /* for -450: */ 234f, 306f, 270f, 270f, 306f, 234f,
            /* for -370: */ 314f, 26f, 350f, 350f, 26f, 314f,
            /* for -360: */ 324f, 36f, 0f, 0f, 36f, 324f,
            /* for -350: */ 334f, 46f, 10f, 10f, 46f, 334f,
            /* for -170: */ 154f, 226f, 190f, 190f, 226f, 154f,
            /* for  -20: */ 304f, 16f, 340f, 340f, 16f, 304f,
            /* for    0: */ 324f, 36f, 0f, 0f, 36f, 324f,
            /* for   20: */ 344f, 56f, 20f, 20f, 56f, 344f,
            /* for  170: */ 134f, 206f, 170f, 170f, 206f, 134f,
            /* for  350: */ 314f, 26f, 350f, 350f, 26f, 314f,
            /* for  360: */ 324f, 36f, 0f, 0f, 36f, 324f,
            /* for  370: */ 334f, 46f, 10f, 10f, 46f, 334f,
            /* for  450: */ 54f, 126f, 90f, 90f, 126f, 54f
        )
        var i = 0
        val arguments = mutableListOf<Arguments>()
        for (angle in angles) {
            for (fraction in fractions) {
                for (direction in directions) {
                    val tuple = arguments(angle, fraction, direction, expectedAngles[i++])
                    arguments.add(tuple)
                }
            }
        }
        return arguments
    }

    // endregion

    // region calculateAnglesDistance

    @ParameterizedTest(name = "Angle: {0}, Fraction: {1}, Direction: {2}")
    @MethodSource("argumentProvider6")
    fun calculateAnglesDistance_FromTheGivenAngleAndFractionAndDirection(startAngle: Float, endAngle: Float, direction: PieChart.DrawDirection, expectedDistance: Float) {
        val distance = calculateAnglesDistance(startAngle, endAngle, direction)
        assertThat(distance)
            .usingComparator { f1, f2 -> if ((f1 - f2).absoluteValue < 0.001) 0 else 1 }
            .isEqualTo(expectedDistance)
    }

    @Suppress("unused")
    private fun argumentProvider6(): List<Arguments> {
        val directions = arrayOf(CLOCKWISE, COUNTER_CLOCKWISE)
        val startAngles = arrayOf(330f, 20f, 50f, 110f)
        val endAngles = arrayOf(20f, 330f, 110f, 50f)
        val expectedDistances = arrayOf(50f, -310f, 310f, -50f, 60f, -300f, 300f, -60f)
        var i = 0
        val arguments = mutableListOf<Arguments>()
        for ((j, startAngle) in startAngles.withIndex()) {
            for (direction in directions) {
                val tuple = arguments(startAngle, endAngles[j], direction, expectedDistances[i])
                arguments.add(tuple)
                i++
            }
        }
        return arguments
    }

    // endregion

    // region calculateCoordinatesForOutsideLabel

    @ParameterizedTest(name = "Angle: {0}")
    @MethodSource("argumentProvider4")
    internal fun calculateAbsoluteBoundsForOutsideLabelAndIcon_WithTheGivenAngleAndNoMargin(angle: Float, expectedBounds: RectF) {
        val margin = 0f
        val pieRadius = 430f
        val center = Coordinates(500f , 500f)
        val paint = Paint()
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to2_ratio, null)
        val labelBounds = calculateLabelBounds("14%", updatePaintForLabel(paint,60f, 0, Typeface.DEFAULT))
        val iconBounds = calculateIconBounds(icon, 100f)
        val combinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, 0f, LEFT)

        val bounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(combinedBounds, angle, center, pieRadius, margin)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(expectedBounds)
    }

    @Test fun calculateAbsoluteBoundsForOutsideLabelAndIcon_WithArbitraryAngleAndMargin() {
        val angle = 0f
        val label = "43%"
        val margin = 50f
        val center = Coordinates(500f , 500f)
        val pieRadius = 430f
        val paint = Paint()
        val labelBounds = calculateLabelBounds(label, updatePaintForLabel(paint,60f, 0, Typeface.DEFAULT))
        val iconBounds = calculateIconBounds(null, 100f)
        val combinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, 0f, LEFT)

        val bounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(combinedBounds, angle, center, pieRadius, margin)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(RectF(980f, 464.84f, 1092f, 535.16f))
    }

    /**
     * Fixes a bug with tight angles.
     */
    @Test fun calculateAbsoluteBoundsForOutsideLabelAndIcon_WithAngleCloseToZero() {
        val angle = 2.5999756f
        val margin = 50f
        val center = Coordinates(540f , 540f)
        val pieRadius = 540f
        val combinedBounds = RectF(0f, 0f, 79f, 49.33594f)

        val bounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(combinedBounds, angle, center, pieRadius, margin)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(RectF(1129.31f, 543.89f, 1208.31f, 593.22f))
    }

    @Suppress("unused")
    private fun argumentProvider4(): List<Arguments> {
        val angles = arrayOf(
            -15f, 0f, 0.1f, 50f, 89.9f, 90f, 90.1f, 140f, 179.9f, 180f,
            180.1f, 230f, 269.9f, 270f, 270.1f, 320f, 359.9f, 360f, 400f
        )
        val expectedCoordinates = arrayOf(
            RectF(921.58f, 315.33f, 1083.58f, 415.33f),
            RectF(930f, 450f, 1092f, 550f),
            RectF(930f, 450.89f, 1092f, 550.89f),
            RectF(752.82f, 847.83f, 914.82f, 947.83f),
            RectF(419.84f, 930f, 581.84f, 1030f),
            RectF(419f, 930f, 581f, 1030f),
            RectF(418.16f, 930f, 580.16f, 1030f),
            RectF(17.62f, 786.80f, 179.62f, 886.80f),
            RectF(-92f, 450.89f, 70f, 550.90f),
            RectF(-92f, 450f, 70f, 550f),
            RectF(-92f, 449.11f, 70f, 549.11f),
            RectF(85.18f, 52.17f, 247.18f, 152.17f),
            RectF(418.16f, -30f, 580.16f, 70f),
            RectF(419f, -30f, 581f, 70f),
            RectF(419.84f, -30f, 581.84f, 70f),
            RectF(820.38f, 113.20f, 982.38f, 213.20f),
            RectF(930f, 449.11f, 1092f, 549.11f),
            RectF(930f, 450f, 1092f, 550f),
            RectF(820.38f, 786.80f, 982.38f, 886.80f)
        )
        return angles.mapIndexed { i, angle -> arguments(angle, expectedCoordinates[i]) }
    }

    // endregion

    // region makeSlice

    @Test fun makeSlice_WithNoPointer() {
        val center = Coordinates(500f, 500f)
        val pieEnclosingRect = RectF(0f, 0f, 1000f, 1000f)
        val sliceStartAngle = -90f
        val sliceFraction = 0.5f
        val pointer: PieChart.SlicePointer? = null
        val drawDirection = CLOCKWISE

        val slice = makeSlice(center, pieEnclosingRect, sliceStartAngle, sliceFraction, drawDirection, pointer)

        assertThat(PathMeasure(slice, false).length).isEqualTo(2570.638f)
    }

    /**
     * Path::reset should be called in the method to clear previous path object state.
     */
    @Test fun makeSlice_ThePathShouldBeResetBeforeEachCall() {
        val center = Coordinates(500f, 500f)
        val pieEnclosingRect = RectF(0f, 0f, 1000f, 1000f)
        val sliceStartAngle = -90f
        val sliceFraction = 0.5f
        val pointer: PieChart.SlicePointer? = null
        val drawDirection = CLOCKWISE

        makeSlice(center, pieEnclosingRect, sliceStartAngle, sliceFraction + 100f, drawDirection, pointer)
        val slice = makeSlice(center, pieEnclosingRect, sliceStartAngle, sliceFraction, drawDirection, pointer)

        assertThat(PathMeasure(slice, false).length).isEqualTo(2570.638f)
    }

    @Test fun makeSlice_WithArbitraryPointer() {
        val center = Coordinates(500f, 500f)
        val pieEnclosingRect = RectF(0f, 0f, 1000f, 1000f)
        val sliceStartAngle = -90f
        val sliceFraction = 0.5f
        val pointer = PieChart.SlicePointer(50f, 40f, 0)
        val drawDirection = CLOCKWISE

        val slice = makeSlice(center, pieEnclosingRect, sliceStartAngle, sliceFraction, drawDirection, pointer)

        assertThat(PathMeasure(slice, false).length).isEqualTo(2381.9175f)
    }

    // endregion

    // region calculateIconBounds

    @Test fun calculateIconBounds_WithAnArbitraryHeight() {
        val iconHeight = 100f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to2_ratio, null)

        val bounds = calculateIconBounds(icon, iconHeight)

        assertThat(bounds).isEqualTo(RectF(0f, 0f, 50f, 100f))
    }

    @Test fun calculateIconBounds_WithHeightOfZero() {
        val iconHeight = 0f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to2_ratio, null)

        val bounds = calculateIconBounds(icon, iconHeight)

        assertThat(bounds).isEqualTo(RectF(0f, 0f, 0f, 0f))
    }

    @Test fun calculateIconBounds_WithNullDrawable() {
        val iconHeight = 100f

        val bounds = calculateIconBounds(null, iconHeight)

        assertThat(bounds).isEqualTo(RectF(0f, 0f, 0f, 0f))
    }

    // endregion

    // region calculateLabelAndIconCombinedBounds

    @ParameterizedTest(name = "Icon height: {0}, Icon margin: {1}, Icon placement: {2}")
    @MethodSource("argumentProvider7")
    fun calculateLabelAndIconCombinedBounds_WithAnArbitraryLabelAndTheGivenIconWidthAndIconHeightAndIconMargin(iconHeight: Float, iconMargin: Float, iconPlacement: IconPlacement, expectedBounds: RectF) {
        val label = "14%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to1_ratio, null)
        val labelBounds = calculateLabelBounds(label, labelPaint)
        val iconBounds = calculateIconBounds(icon, iconHeight)

        val bounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(expectedBounds)
    }

    @Test fun calculateLabelAndIconCombinedBounds_WithEmptyLabelAndNoIconAndNoMargin() {
        val label = ""
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)
        val icon = null
        val labelBounds = calculateLabelBounds(label, labelPaint)
        val iconBounds = calculateIconBounds(icon, 100f)
        val iconMargin = 0f

        val bounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, LEFT)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(RectF(0f, 0f, 0f, 0f))
    }

    @Test fun calculateLabelAndIconCombinedBounds_WithEmptyLabelAndNoMargin() {
        val label = ""
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to1_ratio, null)
        val labelBounds = calculateLabelBounds(label, labelPaint)
        val iconBounds = calculateIconBounds(icon, 100f)
        val iconMargin = 0f

        val bounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, LEFT)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(RectF(0f, 0f, 100f, 100f))
    }

    @Test fun calculateLabelAndIconCombinedBounds_WithEmptyLabelAndArbitraryMargin() {
        val label = ""
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to1_ratio, null)
        val labelBounds = calculateLabelBounds(label, labelPaint)
        val iconBounds = calculateIconBounds(icon, 100f)
        val iconMargin = 45f

        val bounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, LEFT)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(RectF(0f, 0f, 100f, 100f))
    }

    @Suppress("unused")
    private fun argumentProvider7(): List<Arguments> {
        val iconPlacements = arrayOf(LEFT, RIGHT, TOP, BOTTOM)
        val iconHeights = arrayOf(0f, 30f, 100f)
        val iconMargins = arrayOf(-10f, 0f, 20f)
        val arguments = mutableListOf<Arguments>()
        val expectedBounds = arrayOf(
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 132f, 70.31f),
            RectF(0f, 0f, 142f, 70.31f),
            RectF(0f, 0f, 162f, 70.31f),
            RectF(0f, 0f, 202f, 100f),
            RectF(0f, 0f, 212f, 100f),
            RectF(0f, 0f, 232f, 100f),

            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 132f, 70.31f),
            RectF(0f, 0f, 142f, 70.31f),
            RectF(0f, 0f, 162f, 70.31f),
            RectF(0f, 0f, 202f, 100f),
            RectF(0f, 0f, 212f, 100f),
            RectF(0f, 0f, 232f, 100f),

            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 90.31f),
            RectF(0f, 0f, 112f, 100.31f),
            RectF(0f, 0f, 112f, 120.31f),
            RectF(0f, 0f, 112f, 160.31f),
            RectF(0f, 0f, 112f, 170.31f),
            RectF(0f, 0f, 112f, 190.31f),

            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 70.31f),
            RectF(0f, 0f, 112f, 90.31f),
            RectF(0f, 0f, 112f, 100.31f),
            RectF(0f, 0f, 112f, 120.31f),
            RectF(0f, 0f, 112f, 160.31f),
            RectF(0f, 0f, 112f, 170.31f),
            RectF(0f, 0f, 112f, 190.31f),
        )
        var i = 0
        for (iconPlacement in iconPlacements) {
            for (iconHeight in iconHeights) {
                for (iconMargin in iconMargins) {
                    arguments.add(
                        arguments(
                            iconHeight,
                            iconMargin,
                            iconPlacement,
                            expectedBounds[i++]
                        )
                    )
                }
            }
        }
        arguments.add(arguments(120f, -30, TOP, RectF(0f, 0f, 120f, 160.31f)))
        return arguments
    }

    // endregion

    // region calculatePieNewBoundsForOutsideCircularLabel

    @Test fun calculatePieNewBoundsForOutsideCircularLabel_WithLabelIconOnSideAndSmallerLabelHeight() {
        val outsideLabelsMargin = 30f
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = true
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 60f
        val iconHeight = 100f
        val iconMargin = 55f
        val iconPlacement = LEFT
        val context = getInstrumentation().targetContext
        val labelsProperties = listOf(
            LabelProperties("14%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, R.drawable.ic_rectangle_tall, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement)
        )

        val bounds = calculatePieNewBoundsForOutsideCircularLabel(context, currentBounds, labelsProperties, shouldCenterPie)

        assertThat(bounds).isEqualTo(Bounds(230f, 230f, 870f, 870f))
    }

    @Test fun calculatePieNewBoundsForOutsideCircularLabel_WithMarginsButNoSideIcon() {
        val outsideLabelsMargin = 30f
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = true
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 60f
        val iconHeight = 100f
        val iconMargin = 55f
        val iconPlacement = LEFT
        val context = getInstrumentation().targetContext
        val labelsProperties = listOf(
            LabelProperties("14%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement)
        )

        val bounds = calculatePieNewBoundsForOutsideCircularLabel(context, currentBounds, labelsProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(200.31f, 200.31f, 899.69f, 899.69f))
    }

    @Test fun calculatePieNewBoundsForOutsideCircularLabel_WithMarginsButNoTopOrBottomIcon() {
        val outsideLabelsMargin = 30f
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = true
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 60f
        val iconHeight = 100f
        val iconMargin = 55f
        val iconPlacement = TOP
        val context = getInstrumentation().targetContext
        val labelsProperties = listOf(
            LabelProperties("14%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement)
        )

        val bounds = calculatePieNewBoundsForOutsideCircularLabel(context, currentBounds, labelsProperties, shouldCenterPie)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
            .ignoringFields(Bounds::width.name, Bounds::height.name)
            .isEqualTo(Bounds(200.31f, 200.31f, 899.69f, 899.69f))
    }

    @Test fun calculatePieNewBoundsForOutsideCircularLabel_WithLabelIconOnSideAndLargerLabelHeight() {
        val outsideLabelsMargin = 30f
        val currentBounds = Bounds(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = true
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 160f
        val iconHeight = 100f
        val iconMargin = 55f
        val iconPlacement = LEFT
        val context = getInstrumentation().targetContext
        val labelsProperties = listOf(
            LabelProperties("14%", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, R.drawable.ic_rectangle_tall, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement),
            LabelProperties("", 0f, outsideLabelsMargin, labelsSize, Color.BLACK, labelsTypeface, null, null, iconHeight, iconMargin, iconPlacement)
        )

        val bounds = calculatePieNewBoundsForOutsideCircularLabel(context, currentBounds, labelsProperties, shouldCenterPie)

        assertThat(bounds).isEqualTo(Bounds(317.5f, 317.5f, 782.5f, 782.5f))
    }

    // endregion

    // region calculateIconAngleForOutsideCircularLabel

    @ParameterizedTest(name = "Label Size: {0}, Icon Bounds: {1}, Icon Margin: {2}, Icon Placement: {3}, Is Outward: {4}")
    @MethodSource("argumentProvider9")
    fun calculateIconRotationAngleForOutsideCircularLabel_WithTheGivenIconBoundsAndIconMarginAndIconPlacement(labelSize: Float, iconBounds: RectF, iconMargin: Float, iconPlacement: IconPlacement, isOutward: Boolean, expectedAngle: Float) {
        // Assumptions.assumeThat(Locale.getDefault().language).isEqualTo("en")
        val pieRadius = 520f
        val sliceMiddleAngle = 330f
        val outsideLabelMargin = 36f
        val label = "14%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, labelSize, Color.WHITE, Typeface.DEFAULT)

        val rotationAngle = calculateIconRotationAngleForOutsideCircularLabel(sliceMiddleAngle, pieRadius, outsideLabelMargin, label, labelPaint, iconBounds, iconMargin, iconPlacement, isOutward)

        assertThat(rotationAngle)
            .usingComparator(FloatComparator(0.01f))
            .isEqualTo(expectedAngle)
    }

    @Suppress("unused")
    private fun argumentProvider9() = listOf(
        /* TOP narrower icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, TOP, false, 420f),
        /* TOP wider icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 100f, 10f), 0f, TOP, false, 420f),
        /* TOP icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, TOP, false, 420f),
        /* No TOP icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, TOP, false, 420f),
        /* TOP icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, TOP, false, 420f),
        /* BOTTOM narrower icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, BOTTOM, false, 420f),
        /* BOTTOM wider icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 100f, 10f), 0f, BOTTOM, false, 420f),
        /* BOTTOM icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, BOTTOM, false, 420f),
        /* No BOTTOM icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, BOTTOM, false, 420f),
        /* BOTTOM icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, BOTTOM, false, 420f),
        /* LEFT shorter icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, LEFT, false, 414.57f),
        /* LEFT taller icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 10f, 100f), 0f, LEFT, false, 414.71f),
        /* LEFT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, LEFT, false, 412.44f),
        /* No LEFT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, LEFT, false, 414.57f),
        /* LEFT icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, LEFT, false, 420f),
        /* RIGHT shorter icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, RIGHT, false, 425.43f),
        /* RIGHT taller icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 150f, 100f), 0f, RIGHT, false, 425.29f),
        /* RIGHT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, RIGHT, false, 427.56f),
        /* No RIGHT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, RIGHT, false, 425.43f),
        /* RIGHT icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, RIGHT, false, 420f),
        ////////////// OUTWARD //////////////
        /* TOP icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, TOP, false, 420f),
        /* BOTTOM icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, BOTTOM, false, 420f),
        /* LEFT icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, LEFT, false, 413.60f),
        /* RIGHT icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, RIGHT, false, 426.40f),
    )

    // endregion

    // region calculateIconAbsoluteBoundsForOutsideCircularLabel

    @ParameterizedTest(name = "Label Size: {0}, Icon Bounds: {1}, Icon Margin: {2}, Icon Placement: {3}")
    @MethodSource("argumentProvider10")
    fun calculateIconAbsoluteBoundsForOutsideCircularLabel_WithTheGivenIconBoundsAndIconMarginAndIconPlacement(labelSize: Float, iconBounds: RectF, iconMargin: Float, iconPlacement: IconPlacement, expectedBounds: RectF) {
        // Assumptions.assumeThat(Locale.getDefault().language).isEqualTo("en")
        val pieRadius = 520f
        val pieCenter = Coordinates(410f, 410f)
        val sliceMiddleAngle = 330f
        val outsideLabelMargin = 36f
        val label = "14%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, labelSize, Color.WHITE, Typeface.DEFAULT)

        val bounds = calculateIconAbsoluteBoundsForOutsideCircularLabel(sliceMiddleAngle, pieCenter, pieRadius, label, labelPaint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(expectedBounds)
    }

    @Suppress("unused")
    private fun argumentProvider10() = listOf(
        /* TOP narrower icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, TOP, RectF(954.23f, 89.34f, 959.23f, 99.34f)),
        /* TOP wider icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 100f, 10f), 0f, TOP, RectF(906.73f, 89.34f, 1006.73f, 99.34f)),
        /* TOP icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, TOP, RectF(977.16f, 37.34f, 1047.16f, 87.34f)),
        /* No TOP icon with an arbitrary icon margin (can also accept RectF(0f, 0f, 0f, 0f)) */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, TOP, RectF(952.40f, 96.84f, 952.40f, 96.84f)),
        /* TOP icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, TOP, RectF(878.16f, 94.50f, 948.16f, 144.50f)),
        /* BOTTOM narrower icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, BOTTOM, RectF(893.34f, 124.50f, 898.34f, 134.50f)),
        /* BOTTOM wider icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 100f, 10f), 0f, BOTTOM, RectF(845.84f, 124.50f, 945.84f, 134.50f)),
        /* BOTTOM icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, BOTTOM, RectF(878.16f, 94.50f, 948.16f, 144.50f)),
        /* No BOTTOM icon with an arbitrary icon margin (can also accept RectF(0f, 0f, 0f, 0f)) */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, BOTTOM, RectF(891.51f, 132.00f, 891.51f, 132.00f)),
        /* BOTTOM icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, BOTTOM, RectF(878.16f, 94.50f, 948.16f, 144.50f)),
        /* LEFT shorter icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, LEFT, RectF(889.20f, 62.32f, 894.20f, 72.32f)),
        /* LEFT taller icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 10f, 100f), 0f, LEFT, RectF(899.61f, 9.86f, 909.61f, 109.86f)),
        /* LEFT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, LEFT, RectF(843.62f, 24.64f, 913.62f, 74.64f)),
        /* No LEFT icon with an arbitrary icon margin (can also accept RectF(0f, 0f, 0f, 0f)) */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, LEFT, RectF(891.70f, 67.32f, 891.70f, 67.32f)),
        /* LEFT icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, LEFT, RectF(878.16f, 94.50f, 948.16f, 144.50f)),
        /* RIGHT shorter icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, RIGHT, RectF(945.12f, 159.17f, 950.12f, 169.17f)),
        /* RIGHT taller icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 10f, 100f), 0f, RIGHT, RectF(955.53f, 106.72f, 965.53f, 206.72f)),
        /* RIGHT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, RIGHT, RectF(921.39f, 159.35f, 991.39f, 209.35f)),
        /* No RIGHT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, RIGHT, RectF(947.62f, 164.17f, 947.62f, 164.17f)),
        /* RIGHT icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, RIGHT, RectF(878.16f, 94.50f, 948.16f, 144.50f)),
    )

    // endregion

    // region makePathForOutsideCircularLabel

    @ParameterizedTest(name = "Label Size: {0}, Icon Bounds: {1}, Icon Margin: {2}, Icon Placement: {3}, Is Outward: {4}")
    @MethodSource("argumentProvider11")
    fun makePathForOutsideCircularLabel_WithTheGivenIconBoundsAndIconMarginAndIconPlacement(labelSize: Float, iconBounds: RectF, iconMargin: Float, iconPlacement: IconPlacement, isOutward: Boolean, expectedPathLength: Float, expectedPathBounds: RectF) {
        // Assumptions.assumeThat(Locale.getDefault().language).isEqualTo("en")
        val pieRadius = 520f
        val pieCenter = Coordinates(410f, 410f)
        val sliceMiddleAngle = 330f
        val outsideLabelMargin = 36f
        val label = "14%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, labelSize, Color.WHITE, Typeface.DEFAULT)

        val path = makePathForOutsideCircularLabel(sliceMiddleAngle, pieCenter, pieRadius, label, labelPaint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin, isOutward)

        val pathBounds = RectF()
        path.computeBounds(pathBounds, true)
        assertThat(pathBounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(0.01f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(expectedPathBounds)

        assertThat(PathMeasure(path, false).length)
            .usingComparator(FloatComparator(0.01f))
            .isEqualTo(expectedPathLength)
    }

    @Suppress("unused")
    private fun argumentProvider11() = listOf(
        /* TOP narrower icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, TOP, false, 111.99f, RectF(873.86f, 77.63f, 929.77f, 174.47f)),
        /* TOP wider icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 100f, 10f), 0f, TOP, false, 111.99f, RectF(873.86f, 77.63f, 929.77f, 174.47f)),
        /* TOP icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, TOP, false, 111.99f, RectF(873.86f, 77.63f, 929.77f, 174.47f)),
        /* No TOP icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, TOP, false, 111.99f, RectF(873.86f, 77.63f, 929.77f, 174.47f)),
        /* TOP icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, TOP, false, 0f, RectF(0f, 0f, 0f, 0f)),
        /* BOTTOM narrower icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, BOTTOM, false, 111.99f, RectF(882.56f, 72.60f, 938.48f, 169.45f)),
        /* BOTTOM wider icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 100f, 10f), 0f, BOTTOM, false, 111.99f, RectF(882.56f, 72.60f, 938.48f, 169.45f)),
        /* BOTTOM icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, BOTTOM, false, 111.98f, RectF(955.59f, 30.41f, 1011.53f, 127.29f)),
        /* No BOTTOM icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, BOTTOM, false, 111.99f, RectF(873.86f, 77.63f, 929.77f, 174.47f)),
        /* BOTTOM icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, BOTTOM, false, 0f, RectF(0f, 0f, 0f, 0f)),
        /* LEFT shorter icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, LEFT, false, 111.99f, RectF(876.39f, 81.18f, 931.56f, 178.44f)),
        /* LEFT taller icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 10f, 100f), 0f, LEFT, false, 111.99f,  RectF(890.65f, 75.67f, 945.44f, 173.15f)),
        /* LEFT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, LEFT, false, 111.99f, RectF(904.68f, 125.51f, 950.66f, 227.44f)),
        /* No LEFT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, LEFT, false, 111.99f, RectF(874.99f, 79.21f, 930.57f, 176.24f)),
        /* LEFT icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, LEFT, false, 0f, RectF(0f, 0f, 0f, 0f)),
        /* RIGHT shorter icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 0f, RIGHT, false, 111.99f, RectF(871.50f, 74.35f, 928.09f, 170.79f)),
        /* RIGHT taller icon with icon margin 0 */
        arguments(60f, RectF(0f, 0f, 10f, 100f), 0f, RIGHT, false, 111.99f, RectF(882.94f, 64.84f, 939.93f, 161.06f)),
        /* RIGHT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 70f, 50f), 44f, RIGHT, false, 111.99f, RectF(836.43f, 30.80f, 902.20f, 121.24f)),
        /* No RIGHT icon with an arbitrary icon margin */
        arguments(60f, RectF(0f, 0f, 0f, 0f), 44f, RIGHT, false, 111.99f, RectF(873.00f, 76.43f, 929.16f, 173.13f)),
        /* RIGHT icon with an arbitrary icon margin and label size 0 */
        arguments(0f, RectF(0f, 0f, 70f, 50f), 44f, RIGHT, false, 0f, RectF(0f, 0f, 0f, 0f)),
        ////////////// OUTWARD //////////////
        /* TOP icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, TOP, false, 111.99f, RectF(873.86f, 77.63f, 929.77f, 174.47f)),
        /* BOTTOM icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, BOTTOM, false, 111.99f, RectF(899.96f, 62.55f, 955.88f, 159.41f)),
        /* LEFT icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, LEFT, false, 111.99f, RectF(881.88f, 89.12f, 935.40f, 187.30f)),
        /* RIGHT icon */
        arguments(60f, RectF(0f, 0f, 5f, 10f), 20f, RIGHT, false, 111.99f, RectF(865.39f, 66.11f, 923.71f, 161.51f)),
    )

    // endregion
}
