package chart.test

import android.graphics.Color.rgb
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
            Slice(0.30f, rgb(120, 181, 0), rgb(149, 224, 0), legend = "Legend A"),
            Slice(0.20f, rgb(204, 168, 0), rgb(249, 228, 0), legend = "Legend B"),
            Slice(0.20f, rgb(0, 162, 216), rgb(31, 199, 255), legend = "Legend C"),
            Slice(0.17f, rgb(255, 4, 4), rgb(255, 72, 86), legend = "Legend D"),
            Slice(0.13f, rgb(160, 165, 170), rgb(175, 180, 185), legend = "Legend E")
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
