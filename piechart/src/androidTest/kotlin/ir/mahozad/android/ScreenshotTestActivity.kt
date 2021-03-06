package ir.mahozad.android

import android.app.Activity
import android.os.Bundle

/**
 * NOTE: Could not extend from *AppCompatActivity* because including *androidx:appcompat* library,
 *       that contains that class, resulted in conflicting versions of androidx:lifecycle dependency
 *       that could not be resolved (by, for example, forcing a specific version of it).
 */
class ScreenshotTestActivity : Activity() {

    private lateinit var chart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ir.mahozad.android.test.R.layout.screenshot_test_layout)
        chart = findViewById(ir.mahozad.android.test.R.id.screenshotTestPieChart)
    }

    fun configureChart(config: (chart: PieChart) -> Unit) = config(chart)
}
