package chart.test

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ir.mahozad.android.PieChart

/**
 * To use this activity,  change the launcher activity to this in the AndroidManifest.
 */
class ShowCaseLegacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chart = findViewById<PieChart>(R.id.pieChart)
        chart.setOnClickListener {
            chart.slices = listOf(
                PieChart.Slice(0.5f, Color.BLACK),
                PieChart.Slice(0.5f, Color.RED)
            )
        }
    }

    private fun animateStartAngle(chart: PieChart) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                chart.startAngle = chart.startAngle + 1
                handler.postDelayed(this, 15)
            }
        }
        handler.postDelayed(runnable, 15)
    }
}
