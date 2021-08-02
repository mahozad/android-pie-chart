package ir.mahozad.android.component

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.minus
import androidx.core.graphics.withClip
import androidx.core.graphics.withRotation
import ir.mahozad.android.*

internal class Pie(
    val context: Context,
    override val width: Float,
    override val height: Float,
    override val margins: Margins?,
    override val paddings: Paddings?,
    var startAngle: Int,
    var slices: List<PieChart.Slice>,
    val labelType: PieChart.LabelType,
    val outsideLabelsMargin: Float,
    var labelsSize: Float,
    val labelsColor: Int,
    val labelsFont: Typeface,
    val labelIconsHeight: Float,
    val labelIconsMargin: Float,
    val labelIconsPlacement: PieChart.IconPlacement,
    val labelIconsTint: Int?,
    val labelOffset: Float,
    val shouldCenterPie: Boolean,
    val pieDrawDirection: PieChart.DrawDirection,
    var overlayRatio: Float,
    var overlayAlpha: Float,
    val gradientType: PieChart.GradientType,
    var holeRatio: Float,
    val slicesPointer: PieChart.SlicePointer?,
    var gap:Float,
    val gapPosition: PieChart.GapPosition
) : Box {

    private val pie = Path()
    private val clip = Path()
    private val overlay = Path()
    private lateinit var gaps: Path
    private val totalDrawableRect = RectF()
    private val mainPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    var radius = 0f
    lateinit var center: Coordinates
    private val pieEnclosingRect = RectF()

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        radius = calculateRadius(width, height)
        center = calculateCenter(top, start, width, height)
        val (pieTop, pieLeft, pieRight, pieBottom) = calculateBoundaries(center, radius)
        pieEnclosingRect.set(RectF(pieLeft, pieTop, pieRight, pieBottom))




        if (labelType == PieChart.LabelType.OUTSIDE) {
            val defaults = Defaults(outsideLabelsMargin, labelsSize, labelsColor, labelsFont, labelIconsHeight, labelIconsMargin, labelIconsPlacement)
            pieEnclosingRect.set(calculatePieNewBoundsForOutsideLabel(context, pieEnclosingRect, slices, pieDrawDirection, startAngle, defaults, shouldCenterPie))
            center = Coordinates((pieEnclosingRect.left + pieEnclosingRect.right) / 2f, (pieEnclosingRect.top + pieEnclosingRect.bottom) / 2f)
            radius = pieEnclosingRect.width() / 2f
        } else if (labelType == PieChart.LabelType.OUTSIDE_CIRCULAR_INWARD || labelType == PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD) {
            val defaults = Defaults(outsideLabelsMargin, labelsSize, labelsColor, labelsFont, labelIconsHeight, labelIconsMargin, labelIconsPlacement)
            pieEnclosingRect.set(calculatePieNewBoundsForOutsideCircularLabel(context, pieEnclosingRect, slices, defaults, shouldCenterPie))
            center = Coordinates((pieEnclosingRect.left + pieEnclosingRect.right) / 2f, (pieEnclosingRect.top + pieEnclosingRect.bottom) / 2f)
            radius = pieEnclosingRect.width() / 2f
        }

        pie.reset()
        val overlayRadius = overlayRatio * radius
        overlay.set(Path().apply { addCircle(center.x, center.y, overlayRadius, Path.Direction.CW) })



        totalDrawableRect.set(start, top, start + width, top + height)
        val rect = Path().apply { addRect(totalDrawableRect, Path.Direction.CW) }
        val holeRadius = holeRatio * radius
        val hole = Path().apply { addCircle(center.x, center.y, holeRadius, Path.Direction.CW) }
        gaps = makeGaps()
        // Could also have set the fillType to EVEN_ODD and just add the other paths to the clip
        // Or could abandon using clip path and do the operations on the pie itself
        // Clipping should be applied before drawing other things
        clip.set(rect - hole - gaps)
    }

    private fun makeGaps(): Path {
        val gaps = Path()
        var angle = startAngle.toFloat()
        for (slice in slices) {
            angle = calculateEndAngle(angle, slice.fraction, pieDrawDirection)
            val (c1, c2, c3, c4) = calculateGapCoordinates(center, angle, gap, radius, gapPosition)
            gaps.moveTo(c1.x, c1.y)
            gaps.lineTo(c2.x, c2.y)
            gaps.lineTo(c3.x, c3.y)
            gaps.lineTo(c4.x, c4.y)
            gaps.close()
        }
        return gaps
    }


    override fun draw(canvas: Canvas) {
        var currentAngle = startAngle.toFloat()
        for (slice in slices) {

            val gradient = if (gradientType == PieChart.GradientType.RADIAL) {
                RadialGradient(center.x, center.y, radius, slice.color, slice.colorEnd, Shader.TileMode.MIRROR)
            } else {
                val colors = slices.map {  listOf(it.color, it.colorEnd) }.flatten().toIntArray()
                val positions = slices.map { it.fraction }
                    .scan(listOf(0f)) { acc, value -> listOf(acc.first() + value, acc.first() + value) }
                    .flatten()
                    .dropLast(1)
                    .toFloatArray()
                val sweepGradient = SweepGradient(center.x, center.y, colors, positions)
                // Adjust the start angle
                val sweepGradientDefaultStartAngle = 0f
                val rotate = startAngle - sweepGradientDefaultStartAngle
                val gradientMatrix = Matrix()
                gradientMatrix.preRotate(rotate, center.x, center.y)
                sweepGradient.setLocalMatrix(gradientMatrix)
                sweepGradient
            }

            mainPaint.shader = gradient

            val slicePath = makeSlice(center, pieEnclosingRect, currentAngle, slice.fraction, pieDrawDirection, slice.pointer ?: slicesPointer)
            canvas.withClip(clip) {
                canvas.drawPath(slicePath, mainPaint)
            }

            updatePaintForLabel(mainPaint, slice.labelSize ?: labelsSize, slice.labelColor ?: labelsColor, slice.labelFont ?: labelsFont)

            val middleAngle = calculateMiddleAngle(currentAngle, slice.fraction, pieDrawDirection)

            if (labelType == PieChart.LabelType.NONE) {
                // Do nothing
            } else if (labelType == PieChart.LabelType.OUTSIDE) {
                var labelIcon : Drawable? = null
                slice.labelIcon?.let { iconId ->
                    labelIcon = context.resources.getDrawable(iconId, null)
                    slice.labelIconTint?.let { tint -> labelIcon?.setTint(tint) }
                }
                val outsideLabelMargin = slice.outsideLabelMargin ?: outsideLabelsMargin
                val iconPlacement = slice.labelIconPlacement  ?: labelIconsPlacement
                val iconMargin = slice.labelIconMargin ?: labelIconsMargin
                val iconHeight = slice.labelIconHeight ?: labelIconsHeight
                val labelBounds = calculateLabelBounds(slice.label, mainPaint)
                val iconBounds = calculateIconBounds(labelIcon, iconHeight)
                val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
                val absoluteCombinedBounds = calculateAbsoluteBoundsForOutsideLabelAndIcon(labelAndIconCombinedBounds, middleAngle, center, radius, outsideLabelMargin)
                val iconAbsoluteBounds = calculateBoundsForOutsideLabelIcon(absoluteCombinedBounds, iconBounds, iconPlacement)
                val labelCoordinates = calculateCoordinatesForOutsideLabel(absoluteCombinedBounds, labelBounds, mainPaint, iconPlacement)
                canvas.drawText(slice.label, labelCoordinates.x, labelCoordinates.y, mainPaint)
                labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                labelIcon?.draw(canvas)





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




            } else if (labelType == PieChart.LabelType.OUTSIDE_CIRCULAR_INWARD || labelType == PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD) {
                val isOutward = labelType == PieChart.LabelType.OUTSIDE_CIRCULAR_OUTWARD
                var labelIcon : Drawable? = null
                slice.labelIcon?.let { iconId ->
                    labelIcon = context.resources.getDrawable(iconId, null)
                    (slice.labelIconTint ?: labelIconsTint)?.let { labelIcon?.setTint(it) }
                }
                val outsideLabelMargin = slice.outsideLabelMargin ?: outsideLabelsMargin
                val iconPlacement = slice.labelIconPlacement ?: labelIconsPlacement
                val iconMargin = slice.labelIconMargin ?: labelIconsMargin
                val iconHeight = slice.labelIconHeight ?: labelIconsHeight
                val iconBounds = calculateIconBounds(labelIcon, iconHeight)
                val pathForLabel = makePathForOutsideCircularLabel(middleAngle, center, radius, slice.label, mainPaint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin, isOutward)
                val iconRotation = calculateIconRotationAngleForOutsideCircularLabel(middleAngle, radius, outsideLabelMargin, slice.label, mainPaint, iconBounds, iconMargin, iconPlacement, isOutward)
                val iconAbsoluteBounds = calculateIconAbsoluteBoundsForOutsideCircularLabel(middleAngle, center, radius, slice.label, mainPaint, iconBounds, iconMargin, iconPlacement, outsideLabelMargin)
                canvas.drawTextOnPath(slice.label, pathForLabel, 0f, 0f, mainPaint)
                canvas.withRotation(iconRotation, iconAbsoluteBounds.centerX(), iconAbsoluteBounds.centerY()) {
                    labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                    labelIcon?.draw(canvas)
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



            } else {
                var labelIcon : Drawable? = null
                slice.labelIcon?.let { iconId ->
                    labelIcon = context.resources.getDrawable(iconId, null)
                    slice.labelIconTint?.let { tint -> labelIcon?.setTint(tint) }
                }
                val iconPlacement = slice.labelIconPlacement  ?: labelIconsPlacement
                val iconMargin = slice.labelIconMargin ?: labelIconsMargin
                val iconHeight = slice.labelIconHeight ?: labelIconsHeight
                val labelOffset = /* TODO: add slice.LabelOffset ?:*/ labelOffset
                val labelBounds = calculateLabelBounds(slice.label, mainPaint)
                val iconBounds = calculateIconBounds(labelIcon, iconHeight)
                val labelAndIconCombinedBounds = calculateLabelAndIconCombinedBounds(labelBounds, iconBounds, iconMargin, iconPlacement)
                val absoluteCombinedBounds = calculateAbsoluteBoundsForInsideLabelAndIcon(labelAndIconCombinedBounds, middleAngle, center, radius, labelOffset)
                val iconAbsoluteBounds = calculateBoundsForOutsideLabelIcon(absoluteCombinedBounds, iconBounds, iconPlacement)
                val labelCoordinates = calculateCoordinatesForOutsideLabel(absoluteCombinedBounds, labelBounds, mainPaint, iconPlacement)
                canvas.drawText(slice.label, labelCoordinates.x, labelCoordinates.y, mainPaint)
                labelIcon?.setBounds(iconAbsoluteBounds.left.toInt(), iconAbsoluteBounds.top.toInt(), iconAbsoluteBounds.right.toInt(), iconAbsoluteBounds.bottom.toInt())
                labelIcon?.draw(canvas)
            }

            currentAngle = calculateEndAngle(currentAngle, slice.fraction, pieDrawDirection)
        }

        canvas.withClip(clip) {
            mainPaint.shader = null
            mainPaint.color = ContextCompat.getColor(context, android.R.color.black) // or better Color.BLACK
            mainPaint.alpha = (overlayAlpha * 255).toInt()
            canvas.drawPath(overlay, mainPaint)
        }

        // The center label gets clipped by the clip path and is not shown
        // mainPaint.color = ContextCompat.getColor(context, android.R.color.black)
        // mainPaint.textSize = labelSize
        // mainPaint.textAlign = Paint.Align.CENTER
        // val bounds = Rect()
        // mainPaint.getTextBounds(centerLabel, 0, centerLabel.length, bounds)
        // val textHeight = bounds.height()
        // canvas.drawText(centerLabel, centerX, centerY + (textHeight / 2), mainPaint)
    }

}
