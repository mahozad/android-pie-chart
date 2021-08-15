package ir.mahozad.android

import android.content.Context
import android.content.res.Resources
import androidx.core.content.ContextCompat
import ir.mahozad.android.Dimension.PX
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

// https://youtu.be/6P20npkvcb8?t=489

class Property<T>(
    private var value: T,
    private val valueProcessor: ((T) -> T)? = null,
    private val valueChangeHandler: ((T) -> Unit)? = null
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = value
    operator fun setValue(chart: PieChart, property: KProperty<*>, newValue: T) {
        value = valueProcessor?.invoke(newValue) ?: newValue
        valueChangeHandler?.invoke(value) ?: chart.invalidate()
    }
}

abstract class PropertyResource<T>(
    protected var resId: Int,
    private val backingProperty: KMutableProperty0<T>
) {
    abstract fun resolveResourceValue(context: Context): T
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        backingProperty.set(resolveResourceValue(chart.context))
    }
}

class IntegerResource(backingProperty: KMutableProperty0<Int>) :
    PropertyResource<Int>(0, backingProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getInteger(resId)
}

class FractionResource(backingProperty: KMutableProperty0<Float>) :
    PropertyResource<Float>(0, backingProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getFraction(resId, 1, 1)
}

class BooleanResource(backingProperty: KMutableProperty0<Boolean>) :
    PropertyResource<Boolean>(0, backingProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getBoolean(resId)
}

class ColorResource(backingProperty: KMutableProperty0<Int>) :
    PropertyResource<Int>(0, backingProperty) {
    override fun resolveResourceValue(context: Context) = ContextCompat.getColor(context, resId)
}

class DimensionResource(backingProperty: KMutableProperty0<Dimension>) :
    PropertyResource<Dimension>(0, backingProperty) {
    override fun resolveResourceValue(context: Context) = PX(context.resources.getDimension(resId))
}

/**
 * See [#57](https://github.com/mahozad/android-pie-chart/issues/57).
 */
sealed class Dimension {

    // TODO: Is it safe to use `Resources.getSystem().displayMetrics`
    //  instead of using `context.resources.displayMetrics`which requires context?
    //  See https://stackoverflow.com/a/17880012/ and https://stackoverflow.com/a/62433235

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

    class PX(val value: Float) : Dimension() {
        override val px = value
        override val dp = value / Resources.getSystem().displayMetrics.density
        override val sp = value / Resources.getSystem().displayMetrics.scaledDensity
    }

    class DP(val value: Float) : Dimension() {
        override val px = value * Resources.getSystem().displayMetrics.density
        override val dp = value
        override val sp = PX(px).sp
    }

    class SP(val value: Float) : Dimension() {
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

inline val Int.px: Dimension get() = PX(this.toFloat())
inline val Float.px: Dimension get() = PX(this)
inline val Double.px: Dimension get() = PX(this.toFloat())
