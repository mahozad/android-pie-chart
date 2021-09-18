package ir.mahozad.android.compose

import androidx.compose.ui.geometry.Size
import ir.mahozad.android.PieChart
import ir.mahozad.android.calculateEndAngle
import ir.mahozad.android.normalizeAngle

internal fun calculatePieRadius(size: Size): Float {
    return size.minDimension / 2f
}

internal fun calculateStartAngles(startAngle: Int, fractions: List<Float>): List<Float> {
    val firstAngle = normalizeAngle(startAngle.toFloat())
    return fractions.scan(firstAngle) { angle, fraction ->
        calculateEndAngle(angle, fraction, PieChart.DrawDirection.CLOCKWISE)
    }.dropLast(1)
}
