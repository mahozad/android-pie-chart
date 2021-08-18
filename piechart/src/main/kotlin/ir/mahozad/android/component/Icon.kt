package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import ir.mahozad.android.calculateLabelIconWidth

internal class Icon(
    private val drawable: Drawable?,
    override val height: Float,
    override val margins: Margins? = null,
    override val paddings: Paddings? = null,
    @ColorInt private val tint: Int? = null,
    @FloatRange(from = 0.0, to = 1.0) private val alpha: Float = 1f
) : Box {

    override val width = calculateLabelIconWidth(drawable, height)
    private val drawableAlpha = (alpha * 255).toInt()

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        // TODO: Take into account the drawDirection parameter
        val right = start + width
        val bottom = top + if (width == 0f) 0f else height
        drawable?.bounds = Rect(start.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        tint?.let { drawable?.setTint(it) }
    }

    override fun draw(canvas: Canvas) {
        drawable?.alpha = drawableAlpha
        drawable?.draw(canvas)
    }
}
