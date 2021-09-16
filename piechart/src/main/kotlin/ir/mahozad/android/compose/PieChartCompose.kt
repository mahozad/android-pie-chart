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

    Pie(
        slices = pieChartData,
        modifier = modifier.fillMaxSize(),
        progress = transitionProgress.value,
        holeRatio = 0.34f
        // sliceDrawer = sliceDrawer
    )
}

@Composable
private fun Pie(
    slices: List<Slice2>,
    modifier: Modifier,
    progress: Float,
    holeRatio: Float,
    // sliceDrawer: SliceDrawer
) {
    Canvas(modifier = modifier) {
        val pieRadius = calculatePieRadius(size)
        val hole = makeHole(pieRadius, holeRatio)
        clipPath(hole, ClipOp.Difference) {
            drawArc(Color.Red, 0f, 110f, true)
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

@Preview
@Composable fun PieChartPreview() {
    PieChartCompose(pieChartData = listOf(Slice2(1f, Color.Black)), Modifier.aspectRatio(1f))
}
