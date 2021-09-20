package chart.test

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import ir.mahozad.android.PieChart

class ShowcaseLegacyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        legacyViewAsComposable()
        /* OR */
        // legacyViewFromXML()
    }

    private fun legacyViewFromXML() {
        setContentView(R.layout.showcase_legacy_view_activity)
        val chart = findViewById<PieChart>(R.id.pieChart)
        chart.setOnClickListener {
            chart.slices = listOf(
                PieChart.Slice(0.5f, Color.BLACK),
                PieChart.Slice(0.5f, Color.RED)
            )
        }
    }

    private fun legacyViewAsComposable() {
        val initialSlices = listOf(
            PieChart.Slice(0.30f, Color.rgb(120, 181, 0), Color.rgb(149, 224, 0), legend = "Legend A"),
            PieChart.Slice(0.20f, Color.rgb(204, 168, 0), Color.rgb(249, 228, 0), legend = "Legend B"),
            PieChart.Slice(0.20f, Color.rgb(0, 162, 216), Color.rgb(31, 199, 255), legend = "Legend C"),
            PieChart.Slice(0.17f, Color.rgb(255, 4, 4), Color.rgb(255, 72, 86), legend = "Legend D"),
            PieChart.Slice(0.13f, Color.rgb(160, 165, 170), Color.rgb(175, 180, 185), legend = "Legend E")
        )
        setContent {
            var slices by remember { mutableStateOf(initialSlices) }
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.title_legacy),
                    color = colorResource(id = R.color.title_legacy_color),
                    fontSize = dimensionResource(id = R.dimen.title_legacy_size).value.sp
                )
            }
            PieChartView(slices) {
                slices = generateRandomNumbers().mapIndexed { i, fraction ->
                    PieChart.Slice(fraction, generateRandomColor(), legend = "Legend ${'A' + i}")
                }
            }
        }
    }

    @Composable private fun PieChartView(sliceItems: List<PieChart.Slice>, onClick: () -> Unit) {
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
