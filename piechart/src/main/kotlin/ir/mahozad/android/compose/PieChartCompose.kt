package ir.mahozad.android.compose

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import ir.mahozad.android.DEFAULT_HOLE_RATIO
import ir.mahozad.android.DEFAULT_OVERLAY_RATIO

data class SliceCompose(val fraction: Float, val color: Color)

/**
 * By default, the ani-alias is *ENABLED*. See [this post](https://stackoverflow.com/a/64689612).
 */

@Composable
fun PieChartCompose(
    pieChartData: List<SliceCompose>,
    modifier: Modifier = Modifier,
    holeRatio: Float = DEFAULT_HOLE_RATIO,
    overlayRatio: Float = DEFAULT_OVERLAY_RATIO,
    animation: AnimationSpec<Float> = TweenSpec(durationMillis = 500),
    // sliceDrawer: SliceDrawer = SimpleSliceDrawer()
) {
    /*val transitionProgress = remember(pieChartData) { Animatable(initialValue = 0f) }

    // When slices value changes we want to re-animated the chart.
    LaunchedEffect(pieChartData) {
        transitionProgress.animateTo(1f, animationSpec = animation)
    }*/

    Pie(
        slices = pieChartData,
        modifier = modifier.aspectRatio(1f),
        1f,/*progress = transitionProgress.value,*/
        holeRatio,
        overlayRatio
        // sliceDrawer = sliceDrawer
    )
}

@Composable
private fun Pie(
    slices: List<SliceCompose>,
    modifier: Modifier,
    progress: Float,
    holeRatio: Float,
    overlayRatio: Float,
    // sliceDrawer: SliceDrawer
) {
    val startAngle = -90
    val fractions = slices.map(SliceCompose::fraction)
    val startAngles = calculateStartAngles(startAngle, fractions)

    Canvas(modifier = modifier) {
        val pieRadius = calculatePieRadius(size)
        val hole = makeHole(pieRadius, holeRatio)
        clipPath(hole, ClipOp.Difference) {
            for ((i, angle) in startAngles.withIndex()) {
                val sweepAngle = slices[i].fraction * 360
                drawArc(slices[i].color, angle, sweepAngle, true)
            }
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
    PieChartCompose(
        pieChartData = listOf(
            SliceCompose(0.3f, Color(120, 181, 0)),
            SliceCompose(0.2f, Color(0, 162, 216)),
            SliceCompose(0.2f, Color(204, 168, 0)),
            SliceCompose(0.17f, Color(255, 4, 4)),
            SliceCompose(0.13f, Color(160, 165, 170))
        ),
        Modifier.aspectRatio(1f), /* OR .fillMaxSize() */
    )
}
