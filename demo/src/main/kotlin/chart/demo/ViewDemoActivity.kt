package chart.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import ir.mahozad.android.compose.PieChartView
import ir.mahozad.android.compose.SliceCompose

class ViewDemoActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_for_view_activity)
        val chart = findViewById<PieChartView>(R.id.pieChart)
        chart.setOnClickListener {
            chart.slices = generateRandomNumbers().map { fraction ->
                SliceCompose(fraction, generateRandomColorCompose())
            }
            chart.holeRatio = 0.2f
        }
    }
}
