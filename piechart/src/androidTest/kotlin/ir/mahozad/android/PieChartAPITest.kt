package ir.mahozad.android

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
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
