package ir.mahozad.android

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import ir.mahozad.android.PieChart.LegendPosition.*
import ir.mahozad.android.component.*
import ir.mahozad.android.util.getElementCircular
import java.text.NumberFormat

internal class LegendBuilder {

    private lateinit var legendBox: Box

    internal fun createLegendBox(
        context: Context,
        maxAvailableWidth: Float,
        maxAvailableHeight: Float,
        slices: List<PieChart.Slice>,
        legendIconsTintArray: IntArray?,
        legendsTitle: String,
        legendsTitleSize: Float,
        legendsTitleColor: Int,
        legendTitleMargin: Float,
        legendsTitleAlignment: Alignment,
        legendsIcon: PieChart.Icon,
        legendIconsHeight: Float,
        legendIconsAlpha: Float,
        legendsSize: Float,
        legendsColor: Int,
        legendIconsMargin: Float,
        legendsPercentageMargin: Float,
        isLegendsPercentageEnabled: Boolean,
        legendsPercentageSize: Float,
        legendsPercentageColor: Int,
        legendsMargin: Float,
        legendArrangement: PieChart.LegendArrangement,
        legendsAlignment: Alignment,
        legendBoxBackgroundColor: Int,
        legendBoxPadding: Float,
        legendBoxBorder: Float,
        legendBoxBorderColor: Int,
        legendBoxBorderAlpha: Float,
        legendBoxBorderCornerRadius: Float,
        legendBoxBorderType: PieChart.BorderType,
        legendBoxBorderDashArray: List<Float>,
        legendBoxMargin: Float,
        legendBoxPosition: PieChart.LegendPosition,
        legendLinesMargin: Float,
        legendsWrapping: Wrapping,
        isLegendBoxBorderEnabled: Boolean
    ): Box {
        val title = makeTitle(legendsTitle, legendsTitleSize, legendsTitleColor, legendTitleMargin)
        val legends = mutableListOf<Box>()
        for ((i,slice) in slices.withIndex()) {
            val legend = makeLegend(i,slice,maxAvailableWidth,maxAvailableHeight, context, legendIconsTintArray, legendsIcon, legendIconsHeight, legendIconsAlpha, legendsSize, legendsColor, legendArrangement, legendIconsMargin, legendsPercentageMargin, isLegendsPercentageEnabled, legendsPercentageSize, legendsPercentageColor, legendsMargin)
            legends.add(legend)
        }
        val legendDirection = if (legendArrangement == PieChart.LegendArrangement.HORIZONTAL) LayoutDirection.HORIZONTAL else LayoutDirection.VERTICAL
        val legendsContainer = Container(legends, maxAvailableWidth, maxAvailableHeight, childrenAlignment = legendsAlignment, layoutDirection = legendDirection, legendLinesMargin = legendLinesMargin, wrapping = legendsWrapping)
        val legendBoxMargins = createBoxMargins(legendBoxMargin, legendBoxPosition)
        legendBox = Container(
            listOf(title, legendsContainer),
            maxAvailableWidth, maxAvailableHeight,
            childrenAlignment = legendsTitleAlignment,
            layoutDirection = LayoutDirection.VERTICAL,
            background = Background(legendBoxBackgroundColor),
            margins = legendBoxMargins,
            paddings = Paddings(legendBoxPadding),
            border = if (isLegendBoxBorderEnabled) Border(
                thickness = legendBoxBorder,
                color = legendBoxBorderColor,
                alpha = legendBoxBorderAlpha,
                cornerRadius = legendBoxBorderCornerRadius,
                type = legendBoxBorderType,
                dashArray = legendBoxBorderDashArray
            ) else null
        )
        return legendBox
    }

    private fun createBoxMargins(
        legendBoxMargin: Float,
        legendBoxPosition: PieChart.LegendPosition
    ) = when (legendBoxPosition) {
        TOP -> Margins(bottom = legendBoxMargin)
        BOTTOM -> Margins(top = legendBoxMargin)
        START -> Margins(end = legendBoxMargin)
        END -> Margins(start = legendBoxMargin)
        else -> null
    }

    private fun makeLegend(
        legendIndex: Int,
        slice: PieChart.Slice,
        maxAvailableWidth:Float,
        maxAvailableHeight:Float,
        context: Context,
        legendIconsTintArray: IntArray?,
        legendsIcon: PieChart.Icon,
        legendIconsHeight: Float,
        legendIconsAlpha: Float,
        legendsSize: Float,
        legendsColor: Int,
        legendArrangement: PieChart.LegendArrangement,
        legendIconsMargin: Float,
        legendsPercentageMargin: Float,
        isLegendsPercentageEnabled: Boolean,
        legendsPercentageSize: Float,
        legendsPercentageColor: Int,
        legendsMargin: Float
    ): Container {
        var legendDrawable: Drawable? = null
        slice.legendIcon?.let { iconId ->
            legendDrawable = context.resources.getDrawable(iconId, null)
            slice.labelIconTint?.let { tint -> legendDrawable?.setTint(tint) }
        }
        val legendIcon = makeLegendIcon(legendDrawable, context, legendsIcon, slice, legendIndex, legendIconsTintArray, legendIconsHeight, legendIconsAlpha)
        val legendText = makeLegendText(slice, legendsSize, legendsColor, legendIconsMargin, legendsPercentageMargin)
        val legendComponents = mutableListOf<Box>()
        legendComponents.add(legendIcon)
        legendComponents.add(legendText)
        if (isLegendsPercentageEnabled) {
            val legendPercentage = makeLegendPercentage(slice, legendsPercentageSize, legendsPercentageColor)
            legendComponents.add(legendPercentage)
        }
        val margins = if (legendArrangement == PieChart.LegendArrangement.HORIZONTAL) {
            Margins(start = legendsMargin, end = legendsMargin)
        } else {
            Margins(top = legendsMargin, bottom = legendsMargin)
        }
        /* FIXME: The first legend should not have start margin and the last legend should not have end margin (user can achieve first start margin and last end margin with parent padding) */
        return Container(legendComponents, maxAvailableWidth, maxAvailableHeight, childrenAlignment = Alignment.CENTER, layoutDirection = LayoutDirection.HORIZONTAL, margins = margins)
    }

    private fun makeLegendText(
        slice: PieChart.Slice,
        legendsSize: Float,
        legendsColor: Int,
        legendIconsMargin: Float,
        legendsPercentageMargin: Float
    ) = Text(
        slice.legend,
        size = slice.legendSize ?: legendsSize,
        color = slice.legendColor ?: legendsColor,
        margins = Margins(
            start = slice.legendIconMargin ?: legendIconsMargin,
            end = slice.legendPercentageMargin ?: legendsPercentageMargin
        ),
        font = Typeface.DEFAULT
    )

    private fun makeLegendPercentage(
        slice: PieChart.Slice,
        legendsPercentageSize: Float,
        legendsPercentageColor: Int
    ) = Text(
        NumberFormat.getPercentInstance().format(slice.fraction),
        size = slice.legendPercentageSize ?: legendsPercentageSize,
        color = slice.legendPercentageColor ?: legendsPercentageColor,
        font = Typeface.DEFAULT
    )

    private fun makeLegendIcon(
        legendDrawable: Drawable?,
        context: Context,
        legendsIcon: PieChart.Icon,
        slice: PieChart.Slice,
        iconIndex: Int,
        legendIconsTintArray: IntArray?,
        legendIconsHeight: Float,
        legendIconsAlpha: Float
    ) = Icon(
        legendDrawable ?: context.resources.getDrawable(legendsIcon.resId, null),
        slice.legendIconHeight ?: legendIconsHeight,
        tint = legendIconsTintArray.getElementCircular(iconIndex) ?: slice.legendIconTint,
        alpha = slice.legendIconAlpha ?: legendIconsAlpha
    )

    private fun makeTitle(
        legendsTitle: String,
        legendsTitleSize: Float,
        legendsTitleColor: Int,
        legendTitleMargin: Float
    ) = Text(
        legendsTitle,
        size = legendsTitleSize,
        color = legendsTitleColor,
        font = Typeface.DEFAULT,
        margins = Margins(bottom = legendTitleMargin)
    )
}
