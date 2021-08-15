package ir.mahozad.android.component

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.minus
import androidx.core.graphics.withClip
import ir.mahozad.android.*
import ir.mahozad.android.labels.LabelProperties
import ir.mahozad.android.labels.SliceProperties
import ir.mahozad.android.labels.createLabelsMaker

internal class Pie(
    val context: Context,
    override val width: Float,
    override val height: Float,
    override val margins: Margins?,
    override val paddings: Paddings?,
    private var startAngle: Int,
    private val slices: List<PieChart.Slice>,
    val outsideLabelsMargin: Float,
    val labelType: PieChart.LabelType,
    var labelsSize: Float,
    val labelsColor: Int,
    val labelsFont: Typeface,
    val labelIconsHeight: Float,
    val labelIconsMargin: Float,
    val labelIconsPlacement: PieChart.IconPlacement,
    val labelIconsTint: Int?,
    val labelsOffset: Float,
    val shouldCenterPie: Boolean,
    val pieDrawDirection: PieChart.DrawDirection,
    private var overlayRatio: Float,
    private var overlayAlpha: Float,
    val gradientType: PieChart.GradientType,
    private var holeRatio: Float,
    val slicesPointer: PieChart.SlicePointer?,
    var gap:Float,
    val gapPosition: PieChart.GapPosition
) : Box {

    private val pie = Path()
    private val clip = Path()
    private var gaps = Path()
    private var hole = Path()
    private val overlay = Path()
    private val mainPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    var radius = 0f
    var center = Coordinates(0f, 0f)
    private val pieEnclosingRect = RectF()
    private val labels = createLabelsMaker(context, labelType, shouldCenterPie)
    private var top = 0f
    private var start = 0f
    private var drawDirection = DrawDirection.LTR

    override fun layOut(top: Float, start: Float, drawDirection: DrawDirection) {
        this.top = top
        this.start = start
        this.drawDirection = drawDirection
        // TODO: Delete the following four lines of code and use
        //  totalDrawableRect = RectF(start, top, start + width, top + height)
        //  as a parameter of labels::layout and its following line.
        //  Make sure to modify [calculatePieNewBoundsForOutsideLabel] function so that
        //  it can accept non-square bounds as well and use it efficiently to lay the labels.
        radius = calculateRadius(width, height)
        center = calculatePieCenter(top, start, width, height)
        val (pieTop1, pieLeft1, pieRight1, pieBottom1) = calculateBounds(center, radius)
        pieEnclosingRect.set(RectF(pieLeft1, pieTop1, pieRight1, pieBottom1))


        val startAngles = slices.runningFold(startAngle.toFloat()) { angle, slice -> calculateEndAngle(angle, slice.fraction, PieChart.DrawDirection.CLOCKWISE)}
        val slicesProperties = slices.mapIndexed {i, slice -> SliceProperties(slice.fraction, startAngles[i], PieChart.DrawDirection.CLOCKWISE) }
        val labelsProperties = slices.map { LabelProperties(it.label, it.labelOffset ?: labelsOffset, it.outsideLabelMargin ?: outsideLabelsMargin, it.labelSize ?: labelsSize, it.labelColor ?: labelsColor, it.labelFont ?: labelsFont, it.labelIcon, it.labelIconTint ?: labelIconsTint, it.labelIconHeight ?: labelIconsHeight, it.labelIconMargin?: labelIconsMargin, it.labelIconPlacement ?: labelIconsPlacement) }
        labels?.layOut(Bounds(pieEnclosingRect), slicesProperties, labelsProperties)
        val remainingBounds = labels?.getRemainingBounds() ?: Bounds(pieEnclosingRect)
        center = calculatePieCenter(remainingBounds.top, remainingBounds.left, remainingBounds.width, remainingBounds.height)
        radius = calculateRadius(remainingBounds.width, remainingBounds.height)
        val (pieTop, pieLeft, pieRight, pieBottom) = calculateBounds(center, radius)
        pieEnclosingRect.set(RectF(pieLeft, pieTop, pieRight, pieBottom))

        makeOverlay()

        gaps = makeGaps()
        hole = makeHole()
        makeClip(gaps, hole)
    }

    private fun makeOverlay() {
        val overlayRadius = overlayRatio * radius
        overlay.set(Path().apply { addCircle(center.x, center.y, overlayRadius, Path.Direction.CW) })
    }

    private fun makeClip(gaps: Path, hole: Path) {
        val rect = Path().apply { addRect(RectF(start, top, start + width, top + height), Path.Direction.CW) }
        // Could also have set the fillType to EVEN_ODD and just add the other paths to the clip
        // Or could abandon using clip path and do the operations on the pie itself
        // Clipping should be applied before drawing other things
        clip.set(rect - hole - gaps)
    }

    private fun makeHole(): Path {
        val holeRadius = holeRatio * radius
        return Path().apply { addCircle(center.x, center.y, holeRadius, Path.Direction.CW) }
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
            mainPaint.alpha = 255

            val slicePath = makeSlice(center, pieEnclosingRect, currentAngle, slice.fraction, pieDrawDirection, slice.pointer ?: slicesPointer)
            canvas.withClip(clip) {
                canvas.drawPath(slicePath, mainPaint)
            }

            currentAngle = calculateEndAngle(currentAngle, slice.fraction, pieDrawDirection)
        }

        labels?.draw(canvas)

        canvas.withClip(clip) {
            mainPaint.shader = null
            mainPaint.color = ContextCompat.getColor(context, android.R.color.black) // or better Color.BLACK
            mainPaint.alpha = (overlayAlpha * 255).toInt()
            canvas.drawPath(overlay, mainPaint)
        }
    }

    /**
     * The visibility is internal to make it testable. Testability is more important for us.
     */
    internal fun calculatePieCenter(
        pieTop: Float,
        pieStart: Float,
        pieWidth: Float,
        pieHeight: Float
    ): Coordinates {
        val centerX = pieStart + pieWidth / 2f
        val centerY = pieTop + pieHeight / 2f
        return Coordinates(centerX, centerY)
    }

    fun setStartAngle(newStartAngle: Int) {
        startAngle = newStartAngle
        layOut(top, start, drawDirection)
    }

    fun setHoleRatio(newHoleRatio: Float) {
        holeRatio = newHoleRatio
        hole = makeHole()
        makeClip(gaps, hole)
    }

    fun setOverlayRatio(newOverlayRatio: Float) {
        overlayRatio = newOverlayRatio
        makeOverlay()
    }

    fun setOverlayAlpha(newOverlayAlpha: Float) {
        overlayAlpha = newOverlayAlpha
    }
}
