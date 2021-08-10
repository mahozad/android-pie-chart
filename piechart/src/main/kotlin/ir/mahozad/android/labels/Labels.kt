package ir.mahozad.android.labels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import ir.mahozad.android.Bounds
import ir.mahozad.android.Coordinates
import ir.mahozad.android.PieChart

internal interface Labels {
    fun layOut(
        availableBounds: Bounds,
        slicesProperties: List<SliceProperties>,
        labelsProperties: List<LabelProperties>
    )

    /**
     * Following command and query principle, [layOut] is command and this is query.
     */
    fun getRemainingBounds(): Bounds
    fun draw(canvas: Canvas)
}

internal fun createLabelsMaker(context: Context, labelType: PieChart.LabelType, shouldCenterPie: Boolean) = when (labelType) {
    PieChart.LabelType.NONE -> null
    PieChart.LabelType.INSIDE -> InsideLabels(context)
    PieChart.LabelType.OUTSIDE -> OutsideLabels(context, shouldCenterPie)
    PieChart.LabelType.OUTSIDE_CIRCULAR_INWARD -> OutsideCircularLabels(context, isOutward = false, shouldCenterPie)
    PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD -> OutsideCircularLabels(context, isOutward = true, shouldCenterPie)
    else -> null
}

data class LabelProperties(val text: String, val offsetFromCenter: Float, val marginFromPie: Float, val size: Float, val color: Int, val font: Typeface, val icon: Int?, val iconTint: Int?, val iconHeight: Float, val iconMargin: Float, val iconPlacement: PieChart.IconPlacement)

internal data class SliceProperties(
    val fraction: Float,
    val startAngle: Float,
    val drawDirection: PieChart.DrawDirection,
    /* Each slice can have different center origin or radius (because it is scaled etc.) */
    val center: Coordinates? = null,
    val radius: Float? = null
)
