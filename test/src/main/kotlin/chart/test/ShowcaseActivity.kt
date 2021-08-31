package chart.test

import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ir.mahozad.android.PieChart
import ir.mahozad.android.PieChart.Slice

class ShowcaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliceItems = listOf(
            Slice(0.4f, Color.MAGENTA, legend = "Legend A"),
            Slice(0.2f, Color.YELLOW, legend = "Legend B"),
            Slice(0.1f, Color.GREEN, legend = "Legend C"),
            Slice(0.3f, Color.BLUE, legend = "Legend D")
        )

        setContent {
            var slices by remember { mutableStateOf(sliceItems) }
            MaterialTheme {
                PieChartView(slices) {
                    slices = generateRandomNumbers().mapIndexed { i, fraction ->
                        Slice(fraction, generateRandomColor(), legend = "Legend ${'A' + i}")
                    }
                }
            }
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

    @Composable
    fun PieChartView(sliceItems: List<Slice>, onClick: () -> Unit) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            factory = { context ->
                PieChart(context).apply {
                    slices = sliceItems
                    labelType = PieChart.LabelType.INSIDE
                    legendsTitle = "Legends"
                    isLegendEnabled = true
                    isAnimationEnabled = true
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
