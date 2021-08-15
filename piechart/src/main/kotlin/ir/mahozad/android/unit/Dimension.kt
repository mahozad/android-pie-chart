package ir.mahozad.android.unit

import android.content.res.Resources

/**
 * See [#57](https://github.com/mahozad/android-pie-chart/issues/57).
 */
sealed class Dimension {

    // TODO: Is it safe to use `Resources.getSystem().displayMetrics`
    //  instead of `context.resources.displayMetrics` which requires context?
    //  See https://stackoverflow.com/a/17880012/ and https://stackoverflow.com/a/62433235

    protected abstract val value: Float
    /**
     * The pixel value of the dimension.
     */
    abstract val px: Float
    /**
     * The dp value of the dimension.
     */
    abstract val dp: Float
    /**
     * The sp value of the dimension.
     */
    abstract val sp: Float

    override fun toString() = "${this::class.simpleName}: $value${this::class.simpleName?.lowercase()}"

    class PX(override val value: Float) : Dimension() {
        override val px = value
        override val dp = value / Resources.getSystem().displayMetrics.density
        override val sp = value / Resources.getSystem().displayMetrics.scaledDensity
    }

    class DP(override val value: Float) : Dimension() {
        override val px = value * Resources.getSystem().displayMetrics.density
        override val dp = value
        override val sp = PX(px).sp
    }

    class SP(override val value: Float) : Dimension() {
        override val px = value * Resources.getSystem().displayMetrics.scaledDensity
        override val dp = PX(px).dp
        override val sp = value
    }
}

inline val Int.dp: Dimension get() = Dimension.DP(this.toFloat())
inline val Float.dp: Dimension get() = Dimension.DP(this)
inline val Double.dp: Dimension get() = Dimension.DP(this.toFloat())
inline val Int.sp: Dimension get() = Dimension.SP(this.toFloat())
inline val Float.sp: Dimension get() = Dimension.SP(this)
inline val Double.sp: Dimension get() = Dimension.SP(this.toFloat())
inline val Int.px: Dimension get() = Dimension.PX(this.toFloat())
inline val Float.px: Dimension get() = Dimension.PX(this)
inline val Double.px: Dimension get() = Dimension.PX(this.toFloat())
