package ir.mahozad.android

import android.graphics.*
import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import ir.mahozad.android.PieChart.IconPlacement
import ir.mahozad.android.PieChart.IconPlacement.*
import org.assertj.core.api.Assertions
import org.assertj.core.util.FloatComparator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*

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

    /**
     * UNSPECIFIED measure spec could indicate, among oter things, wrap_content
     */
    @Test fun twoSameUNSPECIFIEDSpecShouldResultInMaximumGivenAvailableSize() {
        val availableSize = 1000
        val widthSpec = makeMeasureSpec(availableSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(availableSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(availableSize)
        Assertions.assertThat(height).isEqualTo(availableSize)
    }

    @Test fun twoDifferentUNSPECIFIEDSpecShouldResultInTheSmallerAvailableSize() {
        val smallerSize = 1000
        val largerSize = 1250
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoDifferentUNSPECIFIEDSpecShouldResultInTheSmallerAvailableSize_WH_REVERSED() {
        val smallerSize = 1000
        val largerSize = 1250
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneAT_MOSTSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_SPECS_SWAPPED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneEXACTLYSpecShouldResultInTheEXACTLYSize() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneEXACTLYSpecShouldResultInTheEXACTLYSize_SPECS_SWAPPED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneUNSPECIFIEDSpecAndOneEXACTLYSpecShouldResultInTheEXACTLYSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 1000
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoSameEXACTLYSpecShouldResultInTheEXACTLYSize() {
        val size = 500
        val widthSpec = makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(size)
        Assertions.assertThat(height).isEqualTo(size)
    }

    @Test fun twoDifferentEXACTLYSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoDifferentEXACTLYSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneEXACTLYAndOneAT_MOSTSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneEXACTLYAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun oneEXACTLYAndOneAT_MOSTSpecShouldResultInTheirMinimumSize_SPECS_SWAPPED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.EXACTLY)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoSameAT_MOSTSpecShouldResultInTheAT_MOSTSize() {
        val maxSize = 500
        val widthSpec = makeMeasureSpec(maxSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(maxSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(maxSize)
        Assertions.assertThat(height).isEqualTo(maxSize)
    }

    @Test fun twoDifferentAT_MOSTSpecShouldResultInTheirMinimumSize() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    @Test fun twoDifferentAT_MOSTSpecShouldResultInTheirMinimumSize_WH_REVERSED() {
        val smallerSize = 500
        val largerSize = 750
        val widthSpec = makeMeasureSpec(largerSize, View.MeasureSpec.AT_MOST)
        val heightSpec = makeMeasureSpec(smallerSize, View.MeasureSpec.AT_MOST)

        val (width, height) = calculateWidthAndHeight(widthSpec, heightSpec)

        Assertions.assertThat(width).isEqualTo(smallerSize)
        Assertions.assertThat(height).isEqualTo(smallerSize)
    }

    // -------------------------------------------------------------------------

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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(325f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(175f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(300f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(200f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(325f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(175f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(250f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(300f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(200f)
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

        Assertions.assertThat(centerX).isEqualTo(325f)
        Assertions.assertThat(centerY).isEqualTo(375f)
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

        Assertions.assertThat(centerX).isEqualTo(325f)
        Assertions.assertThat(centerY).isEqualTo(125f)
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

        Assertions.assertThat(centerX).isEqualTo(175f)
        Assertions.assertThat(centerY).isEqualTo(375f)
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

        Assertions.assertThat(centerX).isEqualTo(175f)
        Assertions.assertThat(centerY).isEqualTo(125f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(375f)
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

        Assertions.assertThat(centerX).isEqualTo(250f)
        Assertions.assertThat(centerY).isEqualTo(250f)
    }

    // -------------------------------------------------------------------------

    @Test fun withNoPaddingRadiusShouldBeHalfTheWidthAndHeight() {
        val width = 500
        val height = 500
        val paddingLeft = 0
        val paddingRight = 0
        val paddingTop = 0
        val paddingBottom = 0

        val radius =
            calculateRadius(width, height, paddingLeft, paddingRight, paddingTop, paddingBottom)

        Assertions.assertThat(radius).isEqualTo(250f)
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

        Assertions.assertThat(radius).isEqualTo(175f)
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

        Assertions.assertThat(radius).isEqualTo(175f)
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

        Assertions.assertThat(radius).isEqualTo(100f)
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

        Assertions.assertThat(radius).isEqualTo(50f)
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

        Assertions.assertThat(radius).isEqualTo(50f)
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

        Assertions.assertThat(radius).isEqualTo(175f)
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

        Assertions.assertThat(radius).isEqualTo(125f)
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

        Assertions.assertThat(radius).isEqualTo(125f)
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

        Assertions.assertThat(radius).isEqualTo(75f)
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

        Assertions.assertThat(radius).isEqualTo(75f)
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

        Assertions.assertThat(radius).isEqualTo(250f)
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

        Assertions.assertThat(radius).isEqualTo(175f)
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

        Assertions.assertThat(radius).isEqualTo(175f)
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

        Assertions.assertThat(radius).isEqualTo(160f)
    }

    // -------------------------------------------------------------------------

    @Test fun withSmallRadiusBoundaryShouldBeCalculatedWithNoException() {
        val origin = Coordinates(500f, 500f)
        val radius = 200f

        val (top, left, right, bottom) = calculateBoundaries(origin, radius)

        Assertions.assertThat(top).isEqualTo(300f)
        Assertions.assertThat(left).isEqualTo(300f)
        Assertions.assertThat(right).isEqualTo(700f)
        Assertions.assertThat(bottom).isEqualTo(700f)
    }

    // -------------------------------------------------------------------------

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
            Assertions.assertThat(corner)
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

    // -------------------------------------------------------------------------

    /**
     * Ensures that no new object is allocated in view::onDraw method
     */
    @Test fun updatedPaintForLabel_NoNewPaintObjectShouldBeCreated() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        val newPaint = updatePaintForLabel(paint, labelSize, labelColor)

        Assertions.assertThat(newPaint).isSameAs(paint)
    }

    @Test fun updatedPaintForLabel_TextSizeShouldBeUpdated() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor)

        Assertions.assertThat(paint.textSize).isEqualTo(labelSize)
    }

    @Test fun updatedPaintForLabel_AlignmentShouldBeCenter() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor)

        Assertions.assertThat(paint.textAlign).isEqualTo(Paint.Align.CENTER)
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

        updatePaintForLabel(paint, labelSize, labelColor)

        Assertions.assertThat(paint.shader).isEqualTo(null)
    }

    @Test fun updatedPaintForLabel_ColorShouldBeUpdated() {
        val paint = Paint()
        val labelSize = 10f
        val labelColor = Color.CYAN

        updatePaintForLabel(paint, labelSize, labelColor)

        Assertions.assertThat(paint.color).isEqualTo(labelColor)
    }

    // -------------------------------------------------------------------------

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0AndIconPlacementLEFT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    /**
     * If no icon is specified (i.e. icon width == 0) then ignore the icon margin
     */
    @Test fun labelCoordinates_WithAngle0AndNoIconAndArbitraryIconMarginAndIconPlacementLEFT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 147f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementLEFT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(850f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndArbitraryIconMarginAndIconPlacementLEFT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 147f
        val iconPlacement = LEFT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(924f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0AndIconPlacementRIGHT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    /**
     * If no icon is specified (i.e. icon width == 0) then ignore the icon margin
     */
    @Test fun labelCoordinates_WithAngle0AndNoIconAndArbitraryIconMarginAndIconPlacementRIGHT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 147f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementRIGHT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(750f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndArbitraryIconMarginAndIconPlacementRIGHT() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 147f
        val iconPlacement = IconPlacement.RIGHT
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
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
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)
        setLocale(Locale.ENGLISH)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementSTARTAndLeftToRightLocale() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)
        setLocale(Locale.ENGLISH)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(850f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementSTARTAndRightToLeftLocale() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)
        setLocale(Locale.forLanguageTag("fa"))

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(750f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementENDAndLeftToRightLocale() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.END
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)
        setLocale(Locale.ENGLISH)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(750f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndAnIconAndIconMargin0AndIconPlacementENDAndRightToLeftLocale() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 100f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.END
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)
        setLocale(Locale.forLanguageTag("fa"))

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(850f, 522f))
    }

    @Test fun labelCoordinates_WithAngle0AndNoIconAndIconMargin0_PersianLabel() {
        val startAngle = -45f // In degrees
        val sweepAmount = 90f // In degrees
        val labelOffset = 0.75f
        val label = "۲۱٪"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(500f, 500f)
        val radius = 400f
        val labelPaint = updatePaintForLabel(Paint(), 60f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(800f, 519f))
    }

    @Test fun labelCoordinates_WithArbitraryStartAngleAndAngleSweepAndNoIconAndMargin0() {
        val startAngle = 64.8f // In degrees
        val sweepAmount = 75.6f // In degrees
        val labelOffset = 0.75f
        val label = "21%"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(540f, 540f)
        val radius = 540f
        val labelPaint = updatePaintForLabel(Paint(), 63f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(452f, 958f))
    }

    @Test fun labelCoordinates_WithArbitraryStartAngleAndAngleSweepAndNoIconAndMargin0_PersianLabel() {
        val startAngle = 64.8f // In degrees
        val sweepAmount = 75.6f // In degrees
        val labelOffset = 0.75f
        val label = "۲۱٪"
        val iconWidth = 0f
        val iconMargin = 0f
        val iconPlacement = IconPlacement.START
        val center = Coordinates(540f, 540f)
        val radius = 540f
        val labelPaint = updatePaintForLabel(Paint(), 63f, Color.WHITE)

        val coordinates = calculateLabelCoordinates(startAngle, sweepAmount, labelOffset, iconWidth, iconMargin, iconPlacement, label, labelPaint, center, radius)

        Assertions.assertThat(coordinates)
            .usingRecursiveComparison()
            .withComparatorForFields(FloatComparator(1f), Coordinates::x.name, Coordinates::y.name)
            .isEqualTo(Coordinates(452f, 955f))
    }

    // -------------------------------------------------------------------------

    @Test fun calculateLabelIconWidth_ForNullDrawable() {
        val desiredIconHeight = 50f
        val icon  = null

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        Assertions.assertThat(width).isEqualTo(0f)
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

        Assertions.assertThat(width).isEqualTo(50f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith1To2AspectRatio() {
        val desiredIconHeight = 50f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_1to2_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        Assertions.assertThat(width).isEqualTo(25f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith2To1AspectRatio() {
        val desiredIconHeight = 50f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_2to1_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        Assertions.assertThat(width).isEqualTo(100f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith3To4AspectRatio() {
        val desiredIconHeight = 80f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_3to4_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        Assertions.assertThat(width).isEqualTo(60f)
    }

    @Test fun calculateLabelIconWidth_ForDrawableWith4To3AspectRatio() {
        val desiredIconHeight = 60f
        val resources = getInstrumentation().targetContext.resources
        val icon = resources.getDrawable(ir.mahozad.android.test.R.drawable.ic_test_4to3_ratio, null)

        val width = calculateLabelIconWidth(icon, desiredIconHeight)

        Assertions.assertThat(width).isEqualTo(80f)
    }

    // -------------------------------------------------------------------------

    @Test fun calculateLabelBounds_EmptyLabel() {
        val label = ""
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE)

        val bounds = calculateLabelBounds(label, labelPaint)

        Assertions.assertThat(bounds.width()).isEqualTo(0)
        Assertions.assertThat(bounds.height()).isEqualTo(0)
    }

    @Test fun calculateLabelBounds_ArbitraryLabel() {
        val label = "12%"
        val labelPaint = Paint()
        updatePaintForLabel(labelPaint, 60f, Color.WHITE)
        setLocale(Locale.ENGLISH)

        val bounds = calculateLabelBounds(label, labelPaint)

        Assertions.assertThat(bounds.width()).isEqualTo(105)
        Assertions.assertThat(bounds.height()).isEqualTo(45)
    }

    // -------------------------------------------------------------------------

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
        updatePaintForLabel(labelPaint, 60f, Color.WHITE)
        val labelBounds = calculateLabelBounds(label, labelPaint)
        setLocale(locale)

        val bounds = calculateLabelIconBounds(targetCoordinates, labelBounds, iconWidth, iconHeight, iconMargin, iconPlacement)

        Assertions.assertThat(bounds).isEqualTo(expectedBounds)
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
}
