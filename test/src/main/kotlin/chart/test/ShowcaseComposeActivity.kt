package chart.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import ir.mahozad.android.DEFAULT_HOLE_RATIO
import ir.mahozad.android.compose.PieChartCompose
import ir.mahozad.android.compose.SliceCompose
import ir.mahozad.android.compose.defaultSlices
import kotlin.random.Random

class ShowcaseComposeActivity : ComponentActivity() {

    private val random = Random(0 /* OR System.currentTimeMillis */)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var slices by remember { mutableStateOf(defaultSlices) }
            var holeRatio by remember { mutableStateOf(DEFAULT_HOLE_RATIO) }
            Box(Modifier.fillMaxWidth(), Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.title_compose),
                    color = colorResource(id = R.color.title_compose_color),
                    fontSize = dimensionResource(id = R.dimen.title_compose_size).value.sp
                )
            }
            PieChartCompose(
                Modifier
                    .fillMaxSize()
                    .clickable {
                        slices = generateRandomNumbers().map { fraction ->
                            SliceCompose(fraction, generateRandomColorCompose())
                        }
                        holeRatio = 0.2f /* OR random.nextFloat() */
                    },
                slices,
                holeRatio,
            )
        }
    }
}
