package chart.test

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ir.mahozad.android.PieChart

class ShowcaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliceItems = listOf(
            PieChart.Slice(0.4f, Color.MAGENTA, legend = "Legend A"),
            PieChart.Slice(0.2f, Color.YELLOW, legend = "Legend B"),
            PieChart.Slice(0.1f, Color.GREEN, legend = "Legend C"),
            PieChart.Slice(0.3f, Color.BLUE, legend = "Legend D")
        )

        setContent {
            var slices by rememberSaveable { mutableStateOf(sliceItems) }
            // MaterialTheme {
            PieChartView(slices) {
                slices = generateRandomNumbers().map { PieChart.Slice(it, generateRandomColor()) }
            }
            // }
        }
    }

    /**
     * Generate a list of random numbers that sum up to the base.
     * Both the number of values and the values themselves are random.
     *
     * See [this post](https://stackoverflow.com/q/2640053).
     */
    private fun generateRandomNumbers(base: Int = 1): List<Float> {
        val sizeRange = (1..8)
        val numberRange = (1..50)
        val selectedSize = sizeRange.random()
        val randomNumbers = (1..selectedSize).map { numberRange.random() }
        return randomNumbers.map { it.toFloat() / randomNumbers.sum() * base}
    }

    private fun generateRandomColor(): Int {
        fun component() = (0..255).random()
        return Color.rgb(component(), component(), component())
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

    @Composable
    fun PieChartView(sliceItems: List<PieChart.Slice>, onClick: () -> Unit) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            factory = { context ->
                PieChart(context).apply {
                    slices = sliceItems
                    labelType = PieChart.LabelType.INSIDE
                    legendsTitle = "Legends"
                    isLegendEnabled = true
                    isLegendsPercentageEnabled = true
                    legendPosition = PieChart.LegendPosition.BOTTOM
                }
            },
            update = { chart ->
                // View's been inflated or state read in this block has been updated
                // Add logic here if necessary
                chart.slices = sliceItems
            }
        )
    }
}
