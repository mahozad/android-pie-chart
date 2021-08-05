package ir.mahozad.android

import android.view.LayoutInflater
import androidx.test.platform.app.InstrumentationRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PieChartTest {

    private lateinit var pieChart: PieChart

    @BeforeEach fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

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
}
