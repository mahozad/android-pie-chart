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
import ir.mahozad.android.compose.PieChartCompose
import ir.mahozad.android.compose.SliceCompose
import ir.mahozad.android.compose.defaultSlices
import kotlin.random.Random

class ShowcaseComposeActivity : ComponentActivity() {

    private val random = Random(0 /* or System.currentTimeMillis */)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var slices by remember { mutableStateOf(defaultSlices) }
            var holeRatio by remember { mutableStateOf(0.4f) }
            MaterialTheme {
                PieChartCompose(
                    Modifier
                        .fillMaxSize()
                        .clickable {
                            slices = generateRandomNumbers().map { fraction ->
                                SliceCompose(fraction, generateRandomColorCompose())
                            }
                            holeRatio = 0.2f /* or random.nextFloat() */
                        },
                    slices,
                    holeRatio = holeRatio,
                )
            }
        }
    }
}
