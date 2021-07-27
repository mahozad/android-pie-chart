package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import ir.mahozad.android.calculateLabelBounds
import ir.mahozad.android.updatePaintForLabel

internal class Text(
    private val string: String,
    override val margins: Margins? = null,
    override val paddings: Paddings? = null,
    @Dimension size: Float,
    @ColorInt color: Int,
    font: Typeface,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1f
) : Box {

    private val paint = updatePaintForLabel(Paint(), size, color, font, alpha)
    private val dimensions = calculateLabelBounds(string, paint)
    private val bounds = RectF(0f, 0f, 0f, 0f)
    private var descent = paint.descent()
    override val width = dimensions.width()
    override val height = dimensions.height()

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        // TODO: Take into account the drawDirection parameter
        val size = calculateLabelBounds(string, paint)
        val width = size.width()
        val height = size.height()
        bounds.set(start, top, start + width, top + height)
    }

    override fun draw(canvas: Canvas) {
        // The x denotes the horizontal *center* of the text because it is center aligned
        val x = bounds.centerX()
        val y = bounds.bottom - descent
        canvas.drawText(string, x, y, paint)
    }
}
