package ir.mahozad.android.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview

data class Slice2(val fraction: Float, val color: Color)

@Composable
fun PieChartCompose(
    pieChartData: List<Slice2>,
    modifier: Modifier = Modifier,
    animation: AnimationSpec<Float> = TweenSpec(durationMillis = 500),
    // sliceDrawer: SliceDrawer = SimpleSliceDrawer()
) {
    val transitionProgress = remember(pieChartData) { Animatable(initialValue = 0f) }

    // When slices value changes we want to re-animated the chart.
    LaunchedEffect(pieChartData) {
        transitionProgress.animateTo(1f, animationSpec = animation)
    }

    DrawChart(
        pieChartData = pieChartData,
        modifier = modifier.fillMaxSize(),
        progress = transitionProgress.value,
        // sliceDrawer = sliceDrawer
    )
}

@Composable
private fun DrawChart(
    pieChartData: List<Slice2>,
    modifier: Modifier,
    progress: Float,
    // sliceDrawer: SliceDrawer
) {
    val slices = pieChartData

    Canvas(modifier = modifier) {
        drawIntoCanvas {
            var startArc = 0f

            slices.forEach { slice ->
                val angle = 100

                // drawArc()
                drawContext.canvas.drawCircle(center, 200f, AndroidPaint())

                startArc += angle
            }
        }
    }
}

@Preview
@Composable fun PieChartPreview() {
    PieChartCompose(pieChartData = listOf(Slice2(1f, Color.Black)))
}
