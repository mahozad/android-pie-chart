package ir.mahozad.android

import android.graphics.*
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ir.mahozad.android.PieChart.DrawDirection.CLOCKWISE
import ir.mahozad.android.PieChart.DrawDirection.COUNTER_CLOCKWISE
import ir.mahozad.android.PieChart.IconPlacement
import ir.mahozad.android.PieChart.IconPlacement.*
import org.assertj.core.api.Assertions.assertThat
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

    // region calculateCenter

    @Test fun withNotPaddingCenterShouldBeInCenter() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withLeftPaddingCenterShouldBeShiftedToRight() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(325f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withRightPaddingCenterShouldBeShiftedToLeft() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(175f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withSameLeftAndRightPaddingCenterShouldBeInCenter() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withLargerLeftPaddingAndSmallerRightPaddingCenterShouldBeShiftedToRight() {
        val width = 500
        val height = 500
        val paddingLeft = 250
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(300f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withSmallerLeftPaddingAndLargerRightPaddingCenterShouldBeShiftedToLeft() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 250
        val paddingTop = 0
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(200f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withTopPaddingCenterShouldBeShiftedToBottom() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 150
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(325f)
    }

    @Test fun withBottomPaddingCenterShouldBeShiftedToTop() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 150

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(175f)
    }

    @Test fun withSameTopAndBottomPaddingCenterShouldBeInCenter() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 150
        val paddingBottom = 150

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(250f)
    }

    @Test fun withLargerTopPaddingAndSmallerBottomPaddingCenterShouldBeShiftedToBottom() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 250
        val paddingBottom = 150

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(300f)
    }

    @Test fun withSmallerTopPaddingAndLargerBottomPaddingCenterShouldBeShiftedToTop() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 150
        val paddingBottom = 250

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(200f)
    }

    @Test fun withLeftAndTopPaddingCenterShouldBeShiftedToRightAndBottom() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 250
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(325f)
        assertThat(centerY).isEqualTo(375f)
    }

    @Test fun withLeftAndBottomPaddingCenterShouldBeShiftedToRightAndTop() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 250

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(325f)
        assertThat(centerY).isEqualTo(125f)
    }

    @Test fun withRightAndTopPaddingCenterShouldBeShiftedToLeftAndBottom() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 150
        val paddingTop = 250
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(175f)
        assertThat(centerY).isEqualTo(375f)
    }

    @Test fun withRightAndBottomPaddingCenterShouldBeShiftedToLeftAndTop() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 250

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(175f)
        assertThat(centerY).isEqualTo(125f)
    }

    @Test fun withSameLeftAndRightPaddingAndTopPaddingCenterShouldBeShiftedToBottom() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 150
        val paddingTop = 250
        val paddingBottom = 0

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(375f)
    }

    @Test fun withSameLeftAndRightPaddingAndSameTopAndBottomPaddingCenterShouldBeInCenter() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 150
        val paddingTop = 250
        val paddingBottom = 250

        val (centerX, centerY) = calculateCenter(
            width,
            height,
            paddingLeft,
            paddingRight,
            paddingTop,
            paddingBottom
        )

        assertThat(centerX).isEqualTo(250f)
        assertThat(centerY).isEqualTo(250f)
    }

    // endregion

    // region calculateRadius

    @Test fun withNoPaddingRadiusShouldBeHalfTheWidthAndHeight() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(250f)
    }

    @Test fun withLeftPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(175f)
    }

    @Test fun withRightPaddingRadiusShouldBeHalfTheWidthMinusRightPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(175f)
    }

    @Test fun withLeftAndRightPaddingRadiusShouldBeHalfTheWidthMinusThePaddings() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(100f)
    }

    @Test fun withLargerLeftPaddingAndSmallerRightPaddingRadiusShouldBeHalfTheWidthMinusThePaddings() {
        val width = 500
        val height = 500
        val paddingLeft = 250
        val paddingRight = 150
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(50f)
    }

    @Test fun withSmallerLeftPaddingAndLargerRightPaddingRadiusShouldBeHalfTheWidthMinusThePaddings() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 250
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(50f)
    }

    @Test fun withSameLeftPaddingAndTopPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 150
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(175f)
    }

    @Test fun withLargerLeftPaddingAndSmallerTopPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 250
        val paddingRight = 0
        val paddingTop = 150
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(125f)
    }

    @Test fun withSmallerLeftPaddingAndLargerTopPaddingRadiusShouldBeHalfTheHeightMinusTopPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 250
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(125f)
    }

    @Test fun withLargerHorizontalPaddingAndSmallerVerticalPaddingRadiusShouldBeHalfTheWidthMinusHorizontalPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 150
        val paddingRight = 200
        val paddingTop = 250
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(75f)
    }

    @Test fun withSmallerHorizontalPaddingAndLargerVerticalPaddingRadiusShouldBeHalfTheHeightMinusVerticalPadding() {
        val width = 500
        val height = 500
        val paddingLeft = 250
        val paddingRight = 0
        val paddingTop = 150
        val paddingBottom = 200

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(75f)
    }

    @Test fun withSmallerWidthAndLargerHeightAndNoPaddingRadiusShouldBeHalfTheWidth() {
        val width = 500
        val height = 620
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(250f)
    }

    @Test fun withSmallerWidthAndLargerHeightAndLeftPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
        val width = 500
        val height = 620
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(175f)
    }

    @Test fun withSmallerWidthAndLargerHeightAndSmallerLeftPaddingAndTinyLargerTopPaddingRadiusShouldBeHalfTheWidthMinusLeftPadding() {
        val width = 500
        val height = 620
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 10
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(175f)
    }

    @Test fun withSmallerWidthAndLargerHeightAndSmallerLeftPaddingAndHugeLargerTopPaddingRadiusShouldBeHalfTheHeightMinusTopPadding() {
        val width = 500
        val height = 620
        val paddingLeft = 150
        val paddingRight = 0
        val paddingTop = 300
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        assertThat(radius).isEqualTo(160f)
    }

    // endregion

    // region calculateBoundaries

    @Test fun withSmallRadiusBoundaryShouldBeCalculatedWithNoException() {
        val origin = Coordinates(500f, 500f)
        val radius = 200f

        val (top, left, right, bottom) = calculateBoundaries(origin, radius)

        assertThat(top).isEqualTo(300f)
        assertThat(left).isEqualTo(300f)
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
                .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
                .isEqualTo(expectedCoordinates[i])
        }
    }

    @Suppress("unused")
    private fun argumentProvider(): List<Arguments> {
        val angles = arrayOf(0f, 30f, 120f, 210f, -30f)
        val expectedCoordinatesByPosition = mapOf(
            PieChart.GapPosition.MIDDLE to listOf(
                listOf(Coordinates(500f, 510f), Coordinates(650f, 510f), Coordinates(650f, 490f), Coordinates(500f, 490f)),
                listOf(Coordinates(495f, 508f), Coordinates(624f, 583f), Coordinates(634f, 566f), Coordinates(505f, 491f)),
                listOf(Coordinates(491f, 495f), Coordinates(416f, 624f), Coordinates(433f, 634f), Coordinates(508f, 505f)),
                listOf(Coordinates(505f, 491f), Coordinates(375f, 416f), Coordinates(365f, 433f), Coordinates(495f, 508f)),
                listOf(Coordinates(505f, 508f), Coordinates(634f, 433f), Coordinates(624f, 416f), Coordinates(495f, 491f))
            ),
            PieChart.GapPosition.PRECEDING_SLICE to listOf(
                listOf(Coordinates(500f, 500f), Coordinates(650f, 500f), Coordinates(650f, 480f), Coordinates(500f, 480f)),
                listOf(Coordinates(500f, 500f), Coordinates(630f, 575f), Coordinates(640f, 558f), Coordinates(510f, 483f)),
                listOf(Coordinates(500f, 500f), Coordinates(425f, 630f), Coordinates(442f, 640f), Coordinates(517f, 510f)),
                listOf(Coordinates(500f, 500f), Coordinates(370f, 425f), Coordinates(360f, 442f), Coordinates(490f, 517f)),
                listOf(Coordinates(500f, 500f), Coordinates(629f, 425f), Coordinates(620f, 408f), Coordinates(490f, 483f))
            ),
            PieChart.GapPosition.SUCCEEDING_SLICE to listOf(
                listOf(Coordinates(500f, 520f), Coordinates(650f, 520f), Coordinates(650f, 500f), Coordinates(500f, 500f)),
                listOf(Coordinates(490f, 517f), Coordinates(620f, 592f), Coordinates(630f, 575f), Coordinates(500f, 500f)),
                listOf(Coordinates(483f, 490f), Coordinates(408f, 620f), Coordinates(425f, 630f), Coordinates(500f, 500f)),
                listOf(Coordinates(510f, 483f), Coordinates(380f, 408f), Coordinates(370f, 425f), Coordinates(500f, 500f)),
                listOf(Coordinates(510f, 517f), Coordinates(640f, 442f), Coordinates(630f, 425f), Coordinates(500f, 500f)),
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

    // region calculateLabelCoordinates

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0AndIconPlacementLEFT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    /**
     * If no icon is specified (i.e. icon width == 0) then ignore the icon margin
     */
    @Test fun labelCoordinates_WithAngle0AndNoIconAndArbitraryIconMarginAndIconPlacementLEFT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 147f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementLEFT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(850f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndArbitraryIconMarginAndIconPlacementLEFT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 147f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(924f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0AndIconPlacementRIGHT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    /**
     * If no icon is specified (i.e. icon width == 0) then ignore the icon margin
     */
    @Test fun labelCoordinates_WithAngle0AndNoIconAndArbitraryIconMarginAndIconPlacementRIGHT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 147f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementRIGHT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(750f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndArbitraryIconMarginAndIconPlacementRIGHT() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 147f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(676f, 522f))
    }

    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val resources = getInstrumentation().targetContext.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0AndIconPlacementSTARTAndLeftToRightLocale() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)
        setLocale(Locale.ENGLISH)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementSTARTAndLeftToRightLocale() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)
        setLocale(Locale.ENGLISH)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(850f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementSTARTAndRightToLeftLocale() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)
        setLocale(Locale.forLanguageTag("fa"))

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(750f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementENDAndLeftToRightLocale() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.END
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)
        setLocale(Locale.ENGLISH)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(750f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementENDAndRightToLeftLocale() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.END
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)
        setLocale(Locale.forLanguageTag("fa"))

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(850f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0_PersianLabel() {
        val angle =0f // In degrees
        val labelOffset = 0.75f
        val label = "۲۱٪"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 519f))
    }

    @Test fun labelCoordinates_WithArbitraryStartAngleAndAngleSweepAndNoIconAndMargin0() {
        val angle = 102.6f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(540f, 540f)
        val radius = 540f
        val labelPaint = updatePaintForLabel(Paint(), 63f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(452f, 958f))
    }

    @Test fun labelCoordinates_WithArbitraryStartAngleAndAngleSweepAndNoIconAndMargin0_PersianLabel() {
        val angle = 102.6f // In degrees
        val labelOffset = 0.75f
        val label = "۲۱٪"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(540f, 540f)
        val radius = 540f
        val labelPaint = updatePaintForLabel(Paint(), 63f, Color.WHITE, Typeface.DEFAULT)

        val coordinates = calculateLabelCoordinates(angle, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(452f, 955f))
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

    @Test fun calculateLabelBounds_EmptyLabel() {
        val label = ""
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)

        val bounds = calculateLabelBounds(label, labelPaint)

        assertThat(bounds.width()).isEqualTo(0)
        assertThat(bounds.height()).isEqualTo(0)
    }

    @Test fun calculateLabelBounds_ArbitraryLabel() {
        val label = "12%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)
        setLocale(Locale.ENGLISH)

        val bounds = calculateLabelBounds(label, labelPaint)

        assertThat(bounds.width()).isEqualTo(105)
        assertThat(bounds.height()).isEqualTo(45)
    }

    // endregion

    // region calculateLabelIconBounds

    @ParameterizedTest(name = "Icon margin: {0}, Icon placement: {1}, Label: {3}, Locale: {4}")
    @MethodSource("argumentProvider2")
    internal fun calculateLabelIconBounds_WithTheGivenIconMarginAndIconPlacementAndLabelAndLocale(
        iconMargin: Float,
        iconPlacement: IconPlacement,
        label: String,
        locale: Locale,
        expectedBounds: Rect
    ) {
        val targetCoordinates = Coordinates(500f, 500f)
        val iconWidth = 100f
        val iconHeight = 100f
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE, Typeface.DEFAULT)
        val labelBounds = calculateLabelBounds(label, labelPaint)
        setLocale(locale)

        val bounds = calculateLabelIconBounds(targetCoordinates, labelBounds, iconWidth, iconHeight, iconMargin, iconPlacement)

        assertThat(bounds).isEqualTo(expectedBounds)
    }

    @Suppress("unused")
    private fun argumentProvider2(): List<Arguments> {
        val iconMargins = arrayOf(0f, 0f, 0f, 147f, 0f, 0f, 147f, 0f, 147f, 0f, 0f, 0f)
        val iconPlacements = arrayOf(LEFT, LEFT, LEFT, LEFT, RIGHT, RIGHT, RIGHT, START, START, END, START, END)
        val labels = arrayOf("", "23%", "23%", "23%", "23%", "23%", "23%", "23%", "23%", "23%", "23%", "23%")
        val fa = Locale.forLanguageTag("fa")
        val en = Locale.ENGLISH
        val locales = arrayOf(en, en, fa, en, en, fa, en, en, en, en, fa, fa)
        val expectedBounds = arrayOf(
            Rect(400, 450, 500, 550),
            Rect(346, 428, 446, 528),
            Rect(346, 428, 446, 528),
            Rect(199, 428, 299, 528),
            Rect(554, 428, 654, 528),
            Rect(554, 428, 654, 528),
            Rect(701, 428, 801, 528),
            Rect(346, 428, 446, 528),
            Rect(199, 428, 299, 528),
            Rect(554, 428, 654, 528),
            Rect(554, 428, 654, 528),
            Rect(346, 428, 446, 528)
        )
        val arguments = mutableListOf<Arguments>()
        for ((i, iconMargin) in iconMargins.withIndex()) {
            arguments+= arguments(iconMargin, iconPlacements[i], labels[i], locales[i], expectedBounds[i])
        }
        return arguments
    }

    // endregion

    // region calculatePieNewBoundsForOutsideLabels

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingClockWiseAndNoMarginAndFalseShouldCenterPie() {
        val slices = listOf(
            PieChart.Slice(0.5f, Color.BLACK, label = "long label"),
            PieChart.Slice(0.105f, Color.BLACK, label = ""),
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.02f, Color.BLACK, label = ""),
            PieChart.Slice(0.1f, Color.BLACK, label = ""),
            PieChart.Slice(0.15f, Color.BLACK, label = "")
        )
        val labelMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection, startAngle, labelsSize, labelsTypeface)

        assertThat(bounds).isEqualTo(RectF(100f, 234.5f, 731f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingClockWiseAndNoMarginAndFalseShouldCenterPieAndLongLabelOnLeftSide() {
        val slices = listOf(
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.25f, Color.BLACK, label = ""),
            PieChart.Slice(0.5f, Color.BLACK, label = "long label"),
        )
        val labelMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection, startAngle, labelsSize, labelsTypeface)

        assertThat(bounds).isEqualTo(RectF(369f, 234.5f, 1000f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingCounterClockWiseAndNoMarginAndShouldCenterPieFalse() {
        val slices = listOf(
            PieChart.Slice(0.5f, Color.BLACK, label = "long label"),
            PieChart.Slice(0.105f, Color.BLACK, label = ""),
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.02f, Color.BLACK, label = ""),
            PieChart.Slice(0.1f, Color.BLACK, label = ""),
            PieChart.Slice(0.15f, Color.BLACK, label = "")
        )
        val labelMargin = 0f
        val drawDirection = PieChart.DrawDirection.COUNTER_CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection, startAngle, labelsSize, labelsTypeface)

        assertThat(bounds).isEqualTo(RectF(369f, 234.5f, 1000f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingCounterClockWiseAndNoMarginAndFalseShouldCenterPieAndLongLabelOnLeftSide() {
        val slices = listOf(
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.25f, Color.BLACK, label = ""),
            PieChart.Slice(0.5f, Color.BLACK, label = "long label"),
        )
        val labelMargin = 0f
        val drawDirection = PieChart.DrawDirection.COUNTER_CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection,startAngle, labelsSize,  labelsTypeface)

        assertThat(bounds).isEqualTo(RectF(100f, 234.5f, 731f, 865.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithDrawingClockWiseAndArbitraryMarginAndFalseShouldCenterPie() {
        val slices = listOf(
            PieChart.Slice(0.5f, Color.BLACK, label = "long label"),
            PieChart.Slice(0.105f, Color.BLACK, label = ""),
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.02f, Color.BLACK, label = ""),
            PieChart.Slice(0.1f, Color.BLACK, label = ""),
            PieChart.Slice(0.15f, Color.BLACK, label = "")
        )
        val labelMargin = 42f
        val drawDirection = CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -90

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection, startAngle, labelsSize, labelsTypeface)

        assertThat(bounds).isEqualTo(RectF(100f, 255.5f, 689f, 844.5f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithOnlyTopLabelAndNoMarginAndFalseShouldCenterPie() {
        val slices = listOf(
            PieChart.Slice(0.5f, Color.BLACK, label = "ABC"),
            PieChart.Slice(0.105f, Color.BLACK, label = ""),
            PieChart.Slice(0.125f, Color.BLACK, label = ""),
            PieChart.Slice(0.02f, Color.BLACK, label = ""),
            PieChart.Slice(0.1f, Color.BLACK, label = ""),
            PieChart.Slice(0.15f, Color.BLACK, label = "")
        )
        val labelMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = -180

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection, startAngle, labelsSize, labelsTypeface)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(RectF(137f, 174f, 963f, 1000f))
    }

    @Test fun calculatePieNewBoundsForOutsideLabels_WithRightLabelHavingAngleGreaterThan270() {
        val slices = listOf(
            PieChart.Slice(0.43f, Color.BLACK, label = ""),
            PieChart.Slice(0.21f, Color.BLACK, label = ""),
            PieChart.Slice(0.19f, Color.BLACK, label = ""),
            PieChart.Slice(0.14f, Color.BLACK, label = ""),
            PieChart.Slice(0.03f, Color.BLACK, label = "3%"),
        )
        val labelMargin = 0f
        val drawDirection = CLOCKWISE
        val currentBounds = RectF(100f, 100f, 1000f, 1000f)
        val shouldCenterPie = false
        val labelsTypeface = Typeface.DEFAULT
        val labelsSize = 63.15f
        val startAngle = 0

        val bounds = calculatePieNewBounds(currentBounds, slices, shouldCenterPie, labelMargin, drawDirection, startAngle, labelsSize, labelsTypeface)

        assertThat(bounds)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo( RectF(100f, 140f, 919f, 960f))
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

    // region calculateEndAngle

    @ParameterizedTest(name = "Angle: {0}, Fraction: {1}, Direction: {2}")
    @MethodSource("argumentProvider5")
    fun calculateSecondAngle_FromTheGivenAngleAndFractionAndDirection(angle: Float, fraction: Float, direction: PieChart.DrawDirection, expectedAngle: Float) {
        val secondAngle = calculateEndAngle(angle, fraction, direction)
        assertThat(secondAngle)
            .usingComparator { f1, f2 -> if ((f1 - f2).absoluteValue < 0.001) 0 else 1 }
            .isEqualTo(expectedAngle)
    }

    @Suppress("unused")
    private fun argumentProvider5(): List<Arguments> {
        val directions = arrayOf(CLOCKWISE, COUNTER_CLOCKWISE)
        val fractions = arrayOf(-0.1f, 0f, 0.1f)
        val angles = arrayOf(-450f, -370f, -360f, -350f, -170f, -20f, 0f, 20f, 170f, 350f, 360f, 370f, 450f)
        val expectedAngles = arrayOf(
            /* -450: */ 234f, 306f, 270f, 270f, 306f, 234f,
            /* -370: */ 314f, 26f, 350f, 350f, 26f, 314f,
            /* -360: */ 324f, 36f, 0f, 0f, 36f, 324f,
            /* -350: */ 334f, 46f, 10f, 10f, 46f, 334f,
            /* -170: */ 154f, 226f, 190f, 190f, 226f, 154f,
            /*  -20: */ 304f, 16f, 340f, 340f, 16f, 304f,
            /*    0: */ 324f, 36f, 0f, 0f, 36f, 324f,
            /*   20: */ 344f, 56f, 20f, 20f, 56f, 344f,
            /*  170: */ 134f, 206f, 170f, 170f, 206f, 134f,
            /*  350: */ 314f, 26f, 350f, 350f, 26f, 314f,
            /*  360: */ 324f, 36f, 0f, 0f, 36f, 324f,
            /*  370: */ 334f, 46f, 10f, 10f, 46f, 334f,
            /*  450: */ 54f, 126f, 90f, 90f, 126f, 54f
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
    internal fun calculateCoordinatesForOutsideLabel_WithTheGivenAngleAndNoMargin(angle: Float, expectedCoordinates: Coordinates) {
        val label = "43%"
        val margin = 0f
        val center = Coordinates(500f , 500f)
        val pieRadius = 430f

        val coordinates = calculateCoordinatesForOutsideLabel(label, angle, center, pieRadius, margin)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(expectedCoordinates)
    }

    @Test fun calculateCoordinatesForOutsideLabel_WithArbitraryAngleAndMargin() {
        val angle = 0f
        val label = "43%"
        val margin = 50f
        val center = Coordinates(500f , 500f)
        val pieRadius = 430f

        val coordinates = calculateCoordinatesForOutsideLabel(label, angle, center, pieRadius, margin)

        assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(1038f, 522f))
    }

    @Suppress("unused")
    private fun argumentProvider4(): List<Arguments> {
        val angles = arrayOf(
            -15f, 0f, 0.1f, 50f, 89.9f, 90f, 90.1f, 140f, 179.9f, 180f,
            180.1f, 230f, 269.9f, 270f, 270.1f, 320f, 359.9f, 360f, 400f
        )
        val expectedCoordinates = arrayOf(
            Coordinates(978f, 394f),
            Coordinates(988f, 522f),
            Coordinates(988f, 522f),
            Coordinates(818f, 901f),
            Coordinates(500f, 988f),
            Coordinates(500f, 988f),
            Coordinates(500f, 988f),
            Coordinates(118f, 842f),
            Coordinates(12f, 522f),
            Coordinates(12f, 522f),
            Coordinates(12f, 520f),
            Coordinates(182f, 142f),
            Coordinates(500f, 55f),
            Coordinates(500f, 55f),
            Coordinates(500f, 55f),
            Coordinates(882f, 201f),
            Coordinates(988f, 520f),
            Coordinates(988f, 522f),
            Coordinates(882f, 842f)
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

    // endregion

    // region calculateLabelAndIconCombinedBounds

    @ParameterizedTest
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
            .withComparatorForFields(FloatComparator(1f), RectF::left.name, RectF::top.name, RectF::right.name, RectF::bottom.name)
            .isEqualTo(expectedBounds)
    }

    @Suppress("unused")
    private fun argumentProvider7(): List<Arguments> {
        val iconPlacements = arrayOf(LEFT, RIGHT, TOP, BOTTOM)
        val iconHeights = arrayOf(0f, 30f, 100f)
        val iconMargins = arrayOf(-10f, 0f, 20f)
        val arguments = mutableListOf<Arguments>()
        val expectedBounds = arrayOf(
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 125f, 45f),
            RectF(0f, 0f, 135f, 45f),
            RectF(0f, 0f, 155f, 45f),
            RectF(0f, 0f, 195f, 100f),
            RectF(0f, 0f, 205f, 100f),
            RectF(0f, 0f, 225f, 100f),

            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 125f, 45f),
            RectF(0f, 0f, 135f, 45f),
            RectF(0f, 0f, 155f, 45f),
            RectF(0f, 0f, 195f, 100f),
            RectF(0f, 0f, 205f, 100f),
            RectF(0f, 0f, 225f, 100f),

            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 65f),
            RectF(0f, 0f, 105f, 75f),
            RectF(0f, 0f, 105f, 95f),
            RectF(0f, 0f, 105f, 135f),
            RectF(0f, 0f, 105f, 145f),
            RectF(0f, 0f, 105f, 165f),

            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 45f),
            RectF(0f, 0f, 105f, 65f),
            RectF(0f, 0f, 105f, 75f),
            RectF(0f, 0f, 105f, 95f),
            RectF(0f, 0f, 105f, 135f),
            RectF(0f, 0f, 105f, 145f),
            RectF(0f, 0f, 105f, 165f),
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
        arguments.add(arguments(120f, -30, TOP, RectF(0f, 0f, 120f, 135f)))
        return arguments
    }

    // endregion
}
