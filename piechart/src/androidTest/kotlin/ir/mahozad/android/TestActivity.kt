package ir.mahozad.android

import android.app.Activity
import android.os.Bundle

/**
 * NOTE: Could not extend from *AppCompatActivity* because including *androidx:appcompat* library,
 *       that contains that class, resulted in conflicting versions of androidx:lifecycle dependency
 *       that could not be resolved (by, for example, forcing a specific version of it).
 */
class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ir.mahozad.android.test.R.layout.test_layout)

        val chart = findViewById<PieChart>(ir.mahozad.android.test.R.id.testPieChart)
        chart.setOnClickListener {  }
    }
}
