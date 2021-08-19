package ir.mahozad.android

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import androidx.test.platform.app.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * These tests are used to test the [PieChart] class and checking that changing its properties work.
 * This is a kind of end-to-end testing.
 */
class PieChartAPITest {

    private lateinit var pieChart: PieChart
    private lateinit var context: Context
    private lateinit var resources: Resources

    @BeforeEach fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        resources = context.resources

        val inflater = LayoutInflater.from(context)
        val testLayout = inflater.inflate(ir.mahozad.android.test.R.layout.api_test_layout, null)
        // OR val testLayout = View.inflate(context, ir.mahozad.android.test.R.layout.test_layout, null)

        pieChart = testLayout.findViewById(ir.mahozad.android.test.R.id.apiTestPieChart)
        pieChart.layout(0, 0, 500, 500)

        /*
          pieChart.getLayoutParams().width = 500
          pieChart.getLayoutParams().height = 500
          pieChart.setLayoutParams(pieChart.getLayoutParams())
          pieChart.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
          pieChart.requestLayout()
        */
    }

    @Test fun ensureCanInstantiateTheViewProgrammatically() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val pieChart = PieChart(context)
        assertThat(pieChart.slices).isNotEmpty()
    }

    @Test fun ensureCanInstantiateTheViewFromLayout() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater = LayoutInflater.from(context)
        val testLayout = inflater.inflate(ir.mahozad.android.test.R.layout.api_test_layout, null)
        val pieChart = testLayout.findViewById<PieChart>(ir.mahozad.android.test.R.id.apiTestPieChart)
        assertThat(pieChart.slices).isNotEmpty()
    }

    @Test fun changeChartStartAngleResourceShouldChangeStartAngleAsWell() {
        val resourceId = ir.mahozad.android.test.R.integer.testStartAngle
        val expected = resources.getInteger(resourceId)
        pieChart.startAngleResource = resourceId
        assertThat(pieChart.startAngle).isEqualTo(expected)
    }

    @Test fun changeStartAngleToAValueGreaterThan360() {
        pieChart.startAngle = 409
        assertThat(pieChart.startAngle).isEqualTo(49)
    }

    @Test fun changeStartAngleToAValueLessThanZero() {
        pieChart.startAngle = -13
        assertThat(pieChart.startAngle).isEqualTo(347)
    }

    @Test fun changeHoleRatioResourceShouldChangeHoleRatioAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testHoleRatio
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.holeRatioResource = resourceId
        assertThat(pieChart.holeRatio).isEqualTo(expected)
    }

    @Test fun changeHoleRatioToAValueGreaterThanOne() {
        pieChart.holeRatio = 3.4f
        assertThat(pieChart.holeRatio).isEqualTo(1f)
    }

    @Test fun changeHoleRatioToAValueLessThanZero() {
        pieChart.holeRatio = -3.4f
        assertThat(pieChart.holeRatio).isEqualTo(0f)
    }

    @Test fun changeOverlayRatioResourceShouldChangeOverlayRatioAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testOverlayRatio
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.overlayRatioResource = resourceId
        assertThat(pieChart.overlayRatio).isEqualTo(expected)
    }

    @Test fun changeOverlayRatioToAValueGreaterThanOne() {
        pieChart.overlayRatio = 3.4f
        assertThat(pieChart.overlayRatio).isEqualTo(1f)
    }

    @Test fun changeOverlayRatioToAValueLessThanZero() {
        pieChart.overlayRatio = -3.4f
        assertThat(pieChart.overlayRatio).isEqualTo(0f)
    }

    @Test fun changeOverlayAlphaResourceShouldChangeOverlayAlphaAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testOverlayAlpha
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.overlayAlphaResource = resourceId
        assertThat(pieChart.overlayAlpha).isEqualTo(expected)
    }

    @Test fun changeOverlayAlphaToAValueGreaterThanOne() {
        pieChart.overlayAlpha = 3.4f
        assertThat(pieChart.overlayAlpha).isEqualTo(1f)
    }

    @Test fun changeOverlayAlphaToAValueLessThanZero() {
        pieChart.overlayAlpha = -3.4f
        assertThat(pieChart.overlayAlpha).isEqualTo(0f)
    }

    @Test fun changeGapResourceShouldChangeGapAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testGap
        val expected = resources.getDimension(resourceId)
        pieChart.gapResource = resourceId
        assertThat(pieChart.gap.px).isEqualTo(expected)
    }

    @Test fun changeLabelsSizeResourceShouldChangeLabelsSizeAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLabelsSize
        val expected = resources.getDimension(resourceId)
        pieChart.labelsSizeResource = resourceId
        assertThat(pieChart.labelsSize.px).isEqualTo(expected)
    }

    @Test fun changeLabelIconHeightResourceShouldChangeLabelIconsHeightAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLabelIconsHeight
        val expected = resources.getDimension(resourceId)
        pieChart.labelIconsHeightResource = resourceId
        assertThat(pieChart.labelIconsHeight.px).isEqualTo(expected)
    }

    @Test fun changeChartLabelOffsetResourceShouldChangeLabelOffsetAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testLabelsOffset
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.labelsOffsetResource = resourceId
        assertThat(pieChart.labelsOffset).isEqualTo(expected)
    }

    @Test fun changeChartLegendEnabledResourceShouldChangeLegendEnabledAsWell() {
        val resourceId = ir.mahozad.android.test.R.bool.testIsLegendEnabled
        val expected = resources.getBoolean(resourceId)
        pieChart.isLegendEnabledResource = resourceId
        assertThat(pieChart.isLegendEnabled).isEqualTo(expected)
    }

    @Test fun changeChartLegendsSizeResourceShouldChangeLegendsSizeAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendsSize
        val expected = resources.getDimension(resourceId)
        pieChart.legendsSizeResource = resourceId
        assertThat(pieChart.legendsSize.px).isEqualTo(expected)
    }

    @Test fun changeChartLegendsPercentageSizeResourceShouldChangeLegendsPercentageSizeAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendsPercentageSize
        val expected = resources.getDimension(resourceId)
        pieChart.legendsPercentageSizeResource = resourceId
        assertThat(pieChart.legendsPercentageSize.px).isEqualTo(expected)
    }

    @Test fun changeChartLegendIconsHeightResourceShouldChangeLegendIconsHeightAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendIconsHeight
        val expected = resources.getDimension(resourceId)
        pieChart.legendIconsHeightResource = resourceId
        assertThat(pieChart.legendIconsHeight.px).isEqualTo(expected)
    }

    @Test fun changeLegendsTitleResourceShouldChangeLegendsTitleAsWell() {
        val resourceId = ir.mahozad.android.test.R.string.testLegendsTitle
        val expected = resources.getString(resourceId)
        pieChart.legendsTitleResource = resourceId
        assertThat(pieChart.legendsTitle).isEqualTo(expected)
    }

    @Test fun changeLegendsMarginResourceShouldChangeLegendsMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendsMargin
        val expected = resources.getDimension(resourceId)
        pieChart.legendsMarginResource = resourceId
        assertThat(pieChart.legendsMargin.px).isEqualTo(expected)
    }

    @Test fun changeChartLegendsColorResourceShouldChangeLegendsColorAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testLegendsColor
        val expected = resources.getColor(resourceId, null)
        pieChart.legendsColorResource = resourceId
        assertThat(pieChart.legendsColor).isEqualTo(expected)
    }

    @Test fun changeLegendBoxBackgroundColorResourceShouldChangeLegendBoxBackgroundColorAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testLegendBoxBackgroundColor
        val expected = resources.getColor(resourceId, null)
        pieChart.legendBoxBackgroundColorResource = resourceId
        assertThat(pieChart.legendBoxBackgroundColor).isEqualTo(expected)
    }

    @Test fun changeLegendBoxMarginResourceShouldChangeLegendBoxMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendBoxMargin
        val expected = resources.getDimension(resourceId)
        pieChart.legendBoxMarginResource = resourceId
        assertThat(pieChart.legendBoxMargin.px).isEqualTo(expected)
    }

    @Test fun changeLegendBoxPaddingResourceShouldChangeLegendBoxPaddingAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendBoxPadding
        val expected = resources.getDimension(resourceId)
        pieChart.legendBoxPaddingResource = resourceId
        assertThat(pieChart.legendBoxPadding.px).isEqualTo(expected)
    }

    @Test fun changeLegendBoxBorderResourceShouldChangeLegendBoxBorderAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendBoxBorder
        val expected = resources.getDimension(resourceId)
        pieChart.legendBoxBorderResource = resourceId
        assertThat(pieChart.legendBoxBorder.px).isEqualTo(expected)
    }

    @Test fun changeLegendBoxBorderCornerRadiusResourceShouldChangeLegendBoxBorderCornerRadiusAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendBoxBorderCornerRadius
        val expected = resources.getDimension(resourceId)
        pieChart.legendBoxBorderCornerRadiusResource = resourceId
        assertThat(pieChart.legendBoxBorderCornerRadius.px).isEqualTo(expected)
    }

    @Test fun changeLegendBoxBorderColorResourceShouldChangeLegendBoxBorderColorAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testLegendBoxBorderColor
        val expected = resources.getColor(resourceId, null)
        pieChart.legendBoxBorderColorResource = resourceId
        assertThat(pieChart.legendBoxBorderColor).isEqualTo(expected)
    }

    @Test fun changeLegendBoxBorderAlphaResourceShouldChangeLegendBoxBorderAlphaAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testLegendBoxBorderAlpha
        val expected = resources.getFraction(resourceId,1, 1)
        pieChart.legendBoxBorderAlphaResource = resourceId
        assertThat(pieChart.legendBoxBorderAlpha).isEqualTo(expected)
    }

    @Test fun changeLegendIconsAlphaResourceShouldChangeLegendIconsAlphaAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testLegendIconsAlpha
        val expected = resources.getFraction(resourceId,1, 1)
        pieChart.legendIconsAlphaResource = resourceId
        assertThat(pieChart.legendIconsAlpha).isEqualTo(expected)
    }

    @Test fun changeLegendsTitleColorResourceShouldChangeLegendsTitleColorAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testLegendsTitleColor
        val expected = resources.getColor(resourceId, null)
        pieChart.legendsTitleColorResource = resourceId
        assertThat(pieChart.legendsTitleColor).isEqualTo(expected)
    }

    @Test fun changeLegendsTitleSizeResourceShouldChangeLegendsTitleSizeAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendsTitleSize
        val expected = resources.getDimension(resourceId)
        pieChart.legendsTitleSizeResource = resourceId
        assertThat(pieChart.legendsTitleSize.px).isEqualTo(expected)
    }

    @Test fun changeLegendsPercentageEnabledResourceShouldChangeLegendsPercentageEnabledAsWell() {
        val resourceId = ir.mahozad.android.test.R.bool.testIsLegendsPercentageEnabled
        val expected = resources.getBoolean(resourceId)
        pieChart.isLegendsPercentageEnabledResource = resourceId
        assertThat(pieChart.isLegendsPercentageEnabled).isEqualTo(expected)
    }

    @Test fun changeLegendBoxBorderEnabledResourceShouldChangeLegendBoxBorderEnabledAsWell() {
        val resourceId = ir.mahozad.android.test.R.bool.testIsLegendBoxBorderEnabled
        val expected = resources.getBoolean(resourceId)
        pieChart.isLegendBoxBorderEnabledResource = resourceId
        assertThat(pieChart.isLegendBoxBorderEnabled).isEqualTo(expected)
    }

    @Test fun changeLegendsPercentageColorResourceShouldChangeLegendsPercentageColorAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testLegendPercentageColor
        val expected = resources.getColor(resourceId, null)
        pieChart.legendsPercentageColorResource = resourceId
        assertThat(pieChart.legendsPercentageColor).isEqualTo(expected)
    }

    @Test fun changeLegendsPercentageMarginResourceShouldChangeLegendsPercentageMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendsPercentageMargin
        val expected = resources.getDimension(resourceId)
        pieChart.legendsPercentageMarginResource = resourceId
        assertThat(pieChart.legendsPercentageMargin.px).isEqualTo(expected)
    }

    @Test fun changeLegendTitleMarginResourceShouldChangeLegendTitleMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendTitleMargin
        val expected = resources.getDimension(resourceId)
        pieChart.legendTitleMarginResource = resourceId
        assertThat(pieChart.legendTitleMargin.px).isEqualTo(expected)
    }

    @Test fun changeLegendLinesMarginResourceShouldChangeLegendLinesMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendLinesMargin
        val expected = resources.getDimension(resourceId)
        pieChart.legendLinesMarginResource = resourceId
        assertThat(pieChart.legendLinesMargin.px).isEqualTo(expected)
    }

    @Test fun changeLegendIconsMarginResourceShouldChangeLegendIconsMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testLegendIconsMargin
        val expected = resources.getDimension(resourceId)
        pieChart.legendIconsMarginResource = resourceId
        assertThat(pieChart.legendIconsMargin.px).isEqualTo(expected)
    }

    @Test fun changeLabelsFontResourceShouldChangeLabelsFontAsWell() {
        val resourceId = ir.mahozad.android.test.R.font.lobster_regular
        val expected = ResourcesCompat.getFont(context, resourceId)!!
        pieChart.labelsFontResource = resourceId
        assertThat(pieChart.labelsFont).isEqualTo(expected)
    }

    @Test fun changeCenterLabelFontResourceShouldChangeCenterLabelFontAsWell() {
        val resourceId = ir.mahozad.android.test.R.font.lobster_regular
        val expected = ResourcesCompat.getFont(context, resourceId)!!
        pieChart.centerLabelFontResource = resourceId
        assertThat(pieChart.centerLabelFont).isEqualTo(expected)
    }

    @Test fun changeCenterLabelIconHeightResourceShouldChangeCenterLabelIconHeightAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testCenterLabelIconHeight
        val expected = resources.getDimension(resourceId)
        pieChart.centerLabelIconHeightResource = resourceId
        assertThat(pieChart.centerLabelIconHeight.px).isEqualTo(expected)
    }

    @Test fun changeCenterLabelSizeResourceShouldChangeCenterLabelSizeAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testCenterLabelSize
        val expected = resources.getDimension(resourceId)
        pieChart.centerLabelSizeResource = resourceId
        assertThat(pieChart.centerLabelSize.px).isEqualTo(expected)
    }

    @Test fun changeCenterLabelIconMarginResourceShouldChangeCenterLabelIconMarginAsWell() {
        val resourceId = ir.mahozad.android.test.R.dimen.testCenterLabelIconMargin
        val expected = resources.getDimension(resourceId)
        pieChart.centerLabelIconMarginResource = resourceId
        assertThat(pieChart.centerLabelIconMargin.px).isEqualTo(expected)
    }

    @Test fun changeCenterLabelIconTintResourceShouldChangeCenterLabelIconTintAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testCenterLabelIconTint
        val expected = resources.getColor(resourceId, null)
        pieChart.centerLabelIconTintResource = resourceId
        assertThat(pieChart.centerLabelIconTint).isEqualTo(expected)
    }





    @Test fun changeChartCenterBackgroundStatusResourceShouldChangeCenterBackgroundStatusAsWell() {
        val resourceId1 = ir.mahozad.android.test.R.bool.testCenterBackgroundEnabled
        val resourceId2 = ir.mahozad.android.test.R.bool.testCenterBackgroundDisabled
        val expected1 = resources.getBoolean(resourceId1)
        val expected2 = resources.getBoolean(resourceId2)

        pieChart.isCenterBackgroundEnabledResource = resourceId1
        assertThat(pieChart.isCenterBackgroundEnabled).isEqualTo(expected1)
        pieChart.isCenterBackgroundEnabledResource = resourceId2
        assertThat(pieChart.isCenterBackgroundEnabled).isEqualTo(expected2)
    }

    @Test fun changeCenterBackgroundColorResourceShouldChangeCenterBackgroundColorAsWell() {
        val resourceId = ir.mahozad.android.test.R.color.testCenterBackgroundColor
        val expected = resources.getColor(resourceId, null)
        pieChart.centerBackgroundColorResource = resourceId
        assertThat(pieChart.centerBackgroundColor).isEqualTo(expected)
    }

    @Test fun changeCenterBackgroundRatioResourceShouldChangeCenterBackgroundRatioAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testCenterBackgroundRatio
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.centerBackgroundRatioResource = resourceId
        assertThat(pieChart.centerBackgroundRatio).isEqualTo(expected)
    }

    @Test fun changeCenterBackgroundAlphaResourceShouldChangeCenterBackgroundAlphaAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testCenterBackgroundAlpha
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.centerBackgroundAlphaResource = resourceId
        assertThat(pieChart.centerBackgroundAlpha).isEqualTo(expected)
    }
}
