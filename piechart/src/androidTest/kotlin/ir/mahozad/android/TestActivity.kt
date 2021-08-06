package ir.mahozad.android

import android.app.Activity
import android.os.Bundle

/**
 * NOTE: Extending from AppCompatActivity resulted in conflicting library dependencies.
 */
class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ir.mahozad.android.test.R.layout.test_layout)

        val chart = findViewById<PieChart>(ir.mahozad.android.test.R.id.testPieChart)
        chart.setOnClickListener {  }
    }
}
