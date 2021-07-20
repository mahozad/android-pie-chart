package ir.mahozad.android.component

import android.graphics.Canvas

/**
 * We arrange the components using a box model and implement it with the
 * [*composite* pattern](https://en.wikipedia.org/wiki/Composite_pattern).
 *
 * The margins are treated in [collapsing](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Box_Model/Mastering_margin_collapsing) mode.
 */
internal interface Box {
    val width: Float
    val height: Float
    val margin: Margin
    val padding: Padding
    fun layOut(top: Float, left: Float)
    fun draw(canvas: Canvas)
}

internal enum class Alignment { START, CENTER, END }

internal enum class LayoutDirection { HORIZONTAL, VERTICAL }

internal data class Padding(val top: Float, val bottom: Float, val start: Float, val end: Float)

internal data class Margin(val top: Float, val bottom: Float, val start: Float, val end: Float)

internal sealed class Clipping {
    object Scrollable : Clipping()
    object NextLine : Clipping()
    object Clipped : Clipping()
}
