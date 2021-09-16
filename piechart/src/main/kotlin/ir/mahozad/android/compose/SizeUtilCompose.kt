package ir.mahozad.android.compose

import androidx.compose.ui.geometry.Size

internal fun calculatePieRadius(size: Size): Float {
    return size.minDimension / 2f
}
