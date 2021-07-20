package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import ir.mahozad.android.calculateLabelBounds
import ir.mahozad.android.updatePaintForLabel

internal class Text(
    private val string: String,
    override val padding: Padding = Padding(0f, 0f, 0f, 0f),
    override val margin: Margin = Margin(0f, 0f, 0f, 0f),
    @Dimension size: Float,
    @ColorInt color: Int,
    font: Typeface
) : Box {

    private val paint = updatePaintForLabel(Paint(), size, color, font)
    private val dimensions = calculateLabelBounds(string, paint)
    private val bounds = RectF(0f, 0f, 0f, 0f)
    private var descent = paint.descent()
    override val width = dimensions.width()
    override val height = dimensions.height()

    override fun layOut(top: Float, left: Float) {
        val size = calculateLabelBounds(string, paint)
        val width = size.width()
        val height = size.height()
        bounds.set(left, top, left + width, top + height)
    }

    override fun draw(canvas: Canvas) {
        // The x denotes the horizontal *center* of the text because it is center aligned
        val x = bounds.centerX()
        val y = bounds.bottom - descent
        canvas.drawText(string, x, y, paint)
    }
}
