package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import ir.mahozad.android.calculateLabelIconWidth

internal class Icon(
    private val drawable: Drawable,
    override val height: Float,
    override val margins: Margins? = null,
    override val paddings: Paddings? = null,
    @ColorInt private val tint: Int? = null
) : Box {

    override val width = calculateLabelIconWidth(drawable, height)

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        // TODO: Take into account the drawDirection parameter
        val right = start + width
        val bottom = top + height
        drawable.bounds = Rect(start.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        tint?.let { drawable.setTint(it) }
    }

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }
}
