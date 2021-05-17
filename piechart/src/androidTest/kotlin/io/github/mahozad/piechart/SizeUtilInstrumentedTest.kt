package io.github.mahozad.piechart

import android.view.View
import android.view.View.MeasureSpec.makeMeasureSpec
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Could not run these test as unit tests because they and the class under test
 * use android features (*View::MeasureSpec*)
 */
class SizeUtilInstrumentedTest {

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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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

        val (centerX, centerY) = calculateCenterCoordinate(
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
}
