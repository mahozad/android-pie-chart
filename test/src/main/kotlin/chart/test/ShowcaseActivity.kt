package chart.test

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
            val slices = rememberSaveable { mutableStateOf(sliceItems) }
            // MaterialTheme {
            PieChartView(slices) {
                slices.value = listOf(
                    PieChart.Slice(0.63f, Color.BLACK),
                    PieChart.Slice(0.47f, Color.RED)
                )
            }
            // }
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

    @Composable
    fun PieChartView(slicesState: MutableState<List<PieChart.Slice>>, onClick: () -> Unit) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            factory = { context ->
                PieChart(context).apply {
                    slices = slicesState.value
                    labelType = PieChart.LabelType.INSIDE
                    legendsTitle = "Legends"
                    isLegendEnabled = true
                    isLegendsPercentageEnabled = true
                    legendPosition = PieChart.LegendPosition.START
                }
            },
            update = { chart ->
                // View's been inflated or state read in this block has been updated
                // Add logic here if necessary
                chart.slices = slicesState.value
            }
        )
    }
}
