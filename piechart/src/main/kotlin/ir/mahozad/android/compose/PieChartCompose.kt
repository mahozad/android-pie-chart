package ir.mahozad.android.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview

data class SliceCompose(val fraction: Float, val color: Color)

@Composable
fun PieChartCompose(
    pieChartData: List<SliceCompose>,
    modifier: Modifier = Modifier,
    animation: AnimationSpec<Float> = TweenSpec(durationMillis = 500),
    // sliceDrawer: SliceDrawer = SimpleSliceDrawer()
) {
    val transitionProgress = remember(pieChartData) { Animatable(initialValue = 0f) }

    // When slices value changes we want to re-animated the chart.
    LaunchedEffect(pieChartData) {
        transitionProgress.animateTo(1f, animationSpec = animation)
    }

    Pie(
        slices = pieChartData,
        modifier = modifier.fillMaxSize(),
        progress = transitionProgress.value,
        holeRatio = 0.34f,
        overlayRatio = 0.6f
        // sliceDrawer = sliceDrawer
    )
}

@Composable
private fun Pie(
    slices: List<SliceCompose>,
    modifier: Modifier,
    progress: Float,
    holeRatio: Float,
    overlayRatio: Float
    // sliceDrawer: SliceDrawer
) {
    Canvas(modifier = modifier) {
        val pieRadius = calculatePieRadius(size)
        val hole = makeHole(pieRadius, holeRatio)
        clipPath(hole, ClipOp.Difference) {
            drawArc(Color.Red, 0f, 110f, true)
            drawOverlay(pieRadius, overlayRatio, Color(0, 0, 0, 100))
        }

        // OR
        // drawIntoCanvas {
        //     var startArc = 0f
        //
        //     slices.forEach { slice ->
        //         val angle = 100
        //
        //         it.drawCircle(center, radius, AndroidPaint())
        //         // OR drawArc()
        //         // OR
        //         // val radius = calculatePieRadius(size)
        //         // drawContext.canvas.drawCircle(center, radius, AndroidPaint())
        //
        //         startArc += angle
        //     }
        // }
    }
}

private fun DrawScope.makeHole(pieRadius: Float, holeRatio: Float): Path {
    val holeRadius = holeRatio * pieRadius
    return Path().apply { addOval(Rect(center, holeRadius)) }
}

private fun DrawScope.drawOverlay(pieRadius: Float, overlayRatio: Float, color: Color) {
    val overlayRadius = overlayRatio * pieRadius
    drawCircle(color, overlayRadius)
}

@Preview
@Composable fun PieChartPreview() {
    PieChartCompose(pieChartData = listOf(SliceCompose(1f, Color.Black)), Modifier.aspectRatio(1f))
}