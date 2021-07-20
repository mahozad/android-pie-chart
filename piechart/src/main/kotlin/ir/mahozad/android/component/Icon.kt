package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import ir.mahozad.android.calculateLabelIconWidth

internal class Icon(
    private val drawable: Drawable,
    override val height: Float,
    override val padding: Padding = Padding(0f, 0f, 0f, 0f),
    override val margin: Margin = Margin(0f, 0f, 0f, 0f),
    @ColorInt private val tint: Int? = null
) : Box {

    override val width = calculateLabelIconWidth(drawable, height)

    override fun layOut(top: Float, left: Float) {
        val right = left + width
        val bottom = top + height
        drawable.bounds = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        tint?.let { drawable.setTint(it) }
    }

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }
}
