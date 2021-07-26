package ir.mahozad.android.component

import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import androidx.core.graphics.alpha

/**
 * We arrange the components using a
 * [box model](https://limpet.net/mbrubeck/2014/09/08/toy-layout-engine-5-boxes.html).
 *
 * Implement the model with the
 * [*composite* pattern](https://en.wikipedia.org/wiki/Composite_pattern):
 * treat a collection of objects in the same way as individual objects.
 *
 * Refer here for how [*Flexbox* algorithm](https://www.w3.org/TR/css-flexbox-1/#layout-algorithm) works.
 * [Here](https://github.com/facebook/yoga) is an example library implementing Flexbox.
 *
 * https://www.html5rocks.com/en/tutorials/internals/howbrowserswork/
 * https://medium.com/jspoint/how-the-browser-renders-a-web-page-dom-cssom-and-rendering-df10531c9969
 */
internal interface Box {

    // TODO: Convert this to an abstract class and use Template pattern for layOut and draw methods

    val width: Float
    val height: Float
    val margins: Margins?
    val paddings: Paddings?
    // Start is left or right corner depending on the draw direction
    fun layOut(top: Float, start: Float, drawDirection: DrawDirection)
    fun draw(canvas: Canvas)
}

internal enum class Alignment { START, CENTER, END }

/**
 * Refer [here](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction)
 * for how the flexbox implements its direction.
 */
internal enum class LayoutDirection { VERTICAL, HORIZONTAL }

internal enum class DrawDirection { RTL, LTR }

internal data class Paddings(val top: Float = 0f, val bottom: Float = 0f, val start: Float = 0f, val end: Float = 0f) {
    constructor(padding: Float) : this(padding, padding, padding, padding)
}

internal data class Margins(val top: Float = 0f, val bottom: Float = 0f, val start: Float = 0f, val end: Float = 0f) {
    constructor(margin: Float) : this(margin, margin, margin, margin)
}

internal data class Border(
    @Dimension val thickness: Float,
    @ColorInt val color: Int = Color.BLACK,
    @Dimension val cornerRadius: Float = 0f,
    val dashArray: List<Float>? = null,
    /**
     * NOTE: this is a convenience property because the alpha can be specified in the color itself as well.
     */
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float = color.alpha / 255f
)

internal data class Background(
    @ColorInt val color: Int = Color.TRANSPARENT,
    @Dimension val cornerRadius: Float = 0f,
    /**
     * NOTE: this is a convenience property because the alpha can be specified in the color itself as well.
     */
    @FloatRange(from = 0.0, to = 1.0) val alpha: Float = color.alpha / 255f
)

internal sealed class Wrapping {
    object Scroll : Wrapping()
    object Wrap : Wrapping()
    object Clip : Wrapping()
}
