package ir.mahozad.android.labels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import androidx.core.graphics.withRotation
import ir.mahozad.android.*
import kotlin.math.min

internal class OutsideCircularLabels(
    private val context: Context,
    private val isOutward: Boolean,
    var shouldCenterPie: Boolean
) : Labels {

    private lateinit var remainingBounds: Bounds
    private lateinit var slicesProperties: List<SliceProperties>
    private lateinit var labelsProperties: List<LabelProperties>
    private val paint = Paint(ANTI_ALIAS_FLAG)

    override fun layOut(
        availableBounds: Bounds,
        slicesProperties: List<SliceProperties>,
        labelsProperties: List<LabelProperties>
    ) {
        this.slicesProperties = slicesProperties
        this.labelsProperties = labelsProperties
        this.remainingBounds = calculatePieNewBoundsForOutsideCircularLabel(context, availableBounds, labelsProperties, shouldCenterPie)
    }

    override fun getRemainingBounds(): Bounds {
        return remainingBounds
    }

    override fun draw(canvas: Canvas) {
        val pieRadius = min(remainingBounds.width, remainingBounds.height) / 2f
        val pieCenter = Coordinates(remainingBounds.width / 2 + remainingBounds.left, remainingBounds.height / 2 + remainingBounds.top)
        for ((i, label) in labelsProperties.withIndex()) {
            updatePaintForLabel(paint, label.size, label.color, label.font)
            val middleAngle = calculateMiddleAngle(slicesProperties[i].startAngle, slicesProperties[i].fraction, slicesProperties[i].drawDirection)
            val radius = slicesProperties[i].radius ?: pieRadius
            val center = slicesProperties[i].center ?: pieCenter
            var labelIcon : Drawable? = null
            label.icon?.let { iconId ->
                labelIcon = context.resources.getDrawable(iconId, null)
                label.iconTint?.let { labelIcon?.setTint(it) }
            }
            val outsideLabelMargin = label.marginFromPie
            val iconPlacement = label.iconPlacement
            val iconMargin = label.iconMargin
            val iconHeight = label.iconHeight
            val iconBounds = calculateIconBounds(labelIcon, iconHeight)
            val pathForLabel = makePathForOutsideCircularLabel(middleAngle, center, radius, label.text, paint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin, isOutward)
            val iconRotation = calculateIconRotationAngleForOutsideCircularLabel(middleAngle, radius, outsideLabelMargin, label.text, paint, iconBounds, iconMargin, iconPlacement, isOutward)
            val iconAbsoluteBounds = calculateIconAbsoluteBoundsForOutsideCircularLabel(middleAngle, center, radius, label.text, paint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin)
            canvas.drawTextOnPath(label.text, pathForLabel, 0f, 0f, paint)
            canvas.withRotation(iconRotation, iconAbsoluteBounds.centerX(), iconAbsoluteBounds.centerY()) {
                labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                labelIcon?.draw(canvas)
            }
        }


        // This block of code is for debugging
        /*mainPaint.style = Paint.Style.STROKE
        val radius = pieRadius + outsideLabelMargin + max(iconBounds.height(), calculateLabelBounds(slice.label, mainPaint).height()) / 2f
        val totalFraction = (iconBounds.width() + iconMargin + calculateLabelBounds(slice.label, mainPaint).width()) / (2 * PI.toFloat() * radius)
        val startAngle = calculateEndAngle(middleAngle, totalFraction / 2f, DrawDirection.COUNTER_CLOCKWISE)
        val sweepAngle = totalFraction * 360f
        val bounds = RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
        canvas.drawArc(bounds, startAngle, sweepAngle, true, mainPaint)
        mainPaint.style = Paint.Style.STROKE
        mainPaint.color = Color.RED
        canvas.drawPath(pathForLabel, mainPaint)*/
    }
}
