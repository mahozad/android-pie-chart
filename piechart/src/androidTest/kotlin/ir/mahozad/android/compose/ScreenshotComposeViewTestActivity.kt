package ir.mahozad.android.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ScreenshotComposeViewTestActivity : AppCompatActivity() {

    private lateinit var chart: PieChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ir.mahozad.android.test.R.layout.screenshot_compose_view_test_layout)
        chart = findViewById(ir.mahozad.android.test.R.id.screenshotComposeViewTestPieChart)
    }

    fun configureChart(config: (chart: PieChartView) -> Unit) = config(chart)
}
