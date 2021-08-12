package chart.test

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ir.mahozad.android.PieChart

class ShowcaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chart = findViewById<PieChart>(R.id.pieChart)
        chart.setOnClickListener { animateStartAngle(chart) }
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
