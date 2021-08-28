package ir.mahozad.android.component

import android.graphics.Canvas

internal class MockBox(
    override val width: Float,
    override val height: Float,
    override val margins: Margins? = null,
    override val paddings: Paddings? = null
) : Box {
    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) { /* Not needed */ }
    override fun draw(canvas: Canvas, animationFraction: Float) { /* Not needed */ }
}
