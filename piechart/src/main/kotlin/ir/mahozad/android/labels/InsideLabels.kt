package ir.mahozad.android.labels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import ir.mahozad.android.*
import kotlin.math.min

internal class InsideLabels(private val context: Context) : Labels {

    private lateinit var availableBounds: Bounds
    private lateinit var slicesProperties: List<SliceProperties>
    private lateinit var labelsProperties: List<LabelProperties>
    private val paint = Paint(ANTI_ALIAS_FLAG)

    override fun layOut(
        availableBounds: Bounds,
        slicesProperties: List<SliceProperties>,
        labelsProperties: List<LabelProperties>
    ) {
        this.availableBounds = availableBounds
        this.slicesProperties = slicesProperties
        this.labelsProperties = labelsProperties
    }

    override fun getRemainingBounds(): Bounds {
        return availableBounds
    }

    override fun draw(canvas: Canvas, animationFraction: Float) {
        val pieRadius = min(availableBounds.width, availableBounds.height) / 2f
        val pieCenter = Coordinates(availableBounds.width / 2 + availableBounds.left, availableBounds.height / 2 + availableBounds.top)
        for ((i, label) in labelsProperties.withIndex()) {
            updatePaintForLabel(paint, label.size, label.color, label.font, animationFraction)
            val middleAngle = calculateMiddleAngle(slicesProperties[i].startAngle, slicesProperties[i].fraction, slicesProperties[i].drawDirection)
            val radius = slicesProperties[i].radius ?: pieRadius
            val center = slicesProperties[i].center ?: pieCenter
            var labelIcon : Drawable? = null
            label.icon?.let { iconId ->
                labelIcon = context.resources.getDrawable(iconId, null)
                label.iconTint?.let { tint -> labelIcon?.setTint(tint) }
                labelIcon?.alpha = (animationFraction * 255).toInt()
            }
            val iconPlacement = label.iconPlacement
            val labelOffset = label.offsetFromCenter
            val iconMargin = label.iconMargin
            val iconHeight = label.iconHeight
            val iconBounds = calculateIconBounds(labelIcon, iconHeight)
            val labelBounds = calculateLabelBounds(label.text, paint)
            val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
            val absoluteCombinedBounds = calculateAbsoluteBoundsForInsideLabelAndIcon(labelAndIconCombinedBounds, middleAngle, center, radius, labelOffset)
            val iconAbsoluteBounds = calculateLabelIconAbsoluteBounds(absoluteCombinedBounds, iconBounds, iconPlacement)
            val labelCoordinates = calculateLabelCoordinates(absoluteCombinedBounds, labelBounds, paint, iconPlacement)
            canvas.drawText(label.text, labelCoordinates.x, labelCoordinates.y, paint)
            labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
            labelIcon?.draw(canvas)
        }
    }
}
