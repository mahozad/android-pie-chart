package ir.mahozad.android.labels

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import ir.mahozad.android.*
import kotlin.math.min

internal class OutsideLabels(private val context: Context, var shouldCenterPie: Boolean): Labels {

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
        this.remainingBounds = calculatePieNewBoundsForOutsideLabel(context, availableBounds, labelsProperties, slicesProperties, shouldCenterPie)
    }

    override fun getRemainingBounds(): Bounds {
        return remainingBounds
    }

    override fun draw(canvas: Canvas, animationFraction: Float) {
        val pieRadius = min(remainingBounds.width, remainingBounds.height) / 2f
        val pieCenter = Coordinates(remainingBounds.width / 2 + remainingBounds.left, remainingBounds.height / 2 + remainingBounds.top)
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
            val outsideLabelMargin = label.marginFromPie
            val iconPlacement = label.iconPlacement
            val iconMargin = label.iconMargin
            val iconHeight = label.iconHeight
            val iconBounds = calculateIconBounds(labelIcon, iconHeight)
            val labelBounds = calculateLabelBounds(label.text, paint)
            val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
            val absoluteCombinedBounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(labelAndIconCombinedBounds, middleAngle, center, radius, outsideLabelMargin)
            val iconAbsoluteBounds = calculateLabelIconAbsoluteBounds(absoluteCombinedBounds, iconBounds, iconPlacement)
            val labelCoordinates = calculateLabelCoordinates(absoluteCombinedBounds, labelBounds, paint, iconPlacement)
            canvas.drawText(label.text, labelCoordinates.x, labelCoordinates.y, paint)
            labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
            labelIcon?.draw(canvas)
        }



        // This block of code is for debugging
        /*val rect = RectF(labelCoordinates.x - labelBounds.width() / 2f, labelCoordinates.y + mainPaint.ascent(), labelCoordinates.x + labelBounds.width() / 2f, labelCoordinates.y + mainPaint.descent())
        mainPaint.style = Paint.Style.STROKE
        mainPaint.color = Color.RED
        canvas.drawRect(rect, mainPaint)
        mainPaint.style = Paint.Style.FILL
        canvas.drawCircle(labelCoordinates.x, labelCoordinates.y, 4f, mainPaint)

        mainPaint.style = Paint.Style.STROKE
        mainPaint.color = Color.BLUE
        canvas.drawRect(absoluteCombinedBounds, mainPaint)
        mainPaint.style = Paint.Style.FILL
        canvas.drawCircle(absoluteCombinedBounds.centerX(), absoluteCombinedBounds.centerY(), 4f, mainPaint)

        mainPaint.style = Paint.Style.STROKE
        mainPaint.color = Color.MAGENTA
        val pieCenterMarker = Path()
        pieCenterMarker.moveTo(center.x, center.y - 20)
        pieCenterMarker.lineTo(center.x, center.y - 200)
        pieCenterMarker.moveTo(center.x, center.y + 20)
        pieCenterMarker.lineTo(center.x, center.y + 200)
        pieCenterMarker.moveTo(center.x - 20, center.y)
        pieCenterMarker.lineTo(center.x - 200, center.y)
        pieCenterMarker.moveTo(center.x + 20, center.y)
        pieCenterMarker.lineTo(center.x + 200, center.y)
        canvas.drawPath(pieCenterMarker, mainPaint)
        mainPaint.style = Paint.Style.FILL*/
    }
}
