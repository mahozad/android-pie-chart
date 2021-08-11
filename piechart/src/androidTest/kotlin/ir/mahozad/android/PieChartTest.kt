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
class PieChartTest {

    private lateinit var pieChart: PieChart
    private lateinit var context: Context
    private lateinit var resources: Resources

    @BeforeEach fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        resources = context.resources

        val inflater = LayoutInflater.from(context)
        val testLayout = inflater.inflate(ir.mahozad.android.test.R.layout.test_layout, null)
        // OR val testLayout = View.inflate(context, ir.mahozad.android.test.R.layout.test_layout, null)

        pieChart = testLayout.findViewById(ir.mahozad.android.test.R.id.testPieChart)
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
        val testLayout = inflater.inflate(ir.mahozad.android.test.R.layout.test_layout, null)
        val pieChart = testLayout.findViewById<PieChart>(ir.mahozad.android.test.R.id.testPieChart)
        assertThat(pieChart.slices).isNotEmpty()
    }

    @Test fun changeChartStartAngleResourceShouldChangeStartAngleAsWell() {
        val resourceId = ir.mahozad.android.test.R.integer.testStartAngle
        val expected = resources.getInteger(resourceId)
        pieChart.startAngleResource = resourceId
        assertThat(pieChart.startAngle).isEqualTo(expected)
    }

    @Test fun changeChartLabelOffsetResourceShouldChangeLabelOffsetAsWell() {
        val resourceId = ir.mahozad.android.test.R.fraction.testLabelsOffset
        val expected = resources.getFraction(resourceId, 1, 1)
        pieChart.labelsOffsetResource = resourceId
        assertThat(pieChart.labelsOffset).isEqualTo(expected)
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
