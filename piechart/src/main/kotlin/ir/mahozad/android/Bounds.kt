package ir.mahozad.android

import android.graphics.RectF

data class Bounds(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    constructor(rectF: RectF) : this(rectF.left, rectF.top, rectF.right, rectF.bottom)
    val width = right - left
    val height = bottom - top
}
