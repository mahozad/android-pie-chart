package chart.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ir.mahozad.android.compose.PieChartCompose
import ir.mahozad.android.compose.SliceCompose
import kotlin.random.Random

class ShowcaseComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialSlices = listOf(
            SliceCompose(0.30f, Color(120, 181, 0)),
            SliceCompose(0.20f, Color(204, 168, 0)),
            SliceCompose(0.20f, Color(0, 162, 216)),
            SliceCompose(0.17f, Color(255, 4, 4)),
            SliceCompose(0.13f, Color(160, 165, 170))
        )

        val random = Random(0 /* or System.currentTimeMillis */)

        setContent {
            var slices by remember { mutableStateOf(initialSlices) }
            var holeRatio by remember { mutableStateOf(0.4f) }
            MaterialTheme {
                PieChartCompose(
                    slices,
                    Modifier
                        .fillMaxSize()
                        .clickable {
                            slices = generateRandomNumbers().map { fraction ->
                                SliceCompose(fraction, generateRandomColorCompose())
                            }
                            holeRatio = 0.2f /* or random.nextFloat() */
                        },
                    holeRatio = holeRatio,
                )
            }
        }
    }
}
