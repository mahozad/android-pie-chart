package chart.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.mahozad.android.PieChart

class ShowcaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chart = findViewById<PieChart>(R.id.pieChart)
        chart.setOnClickListener {
            chart.overlayRatio = 0.8f
        }
    }
}
