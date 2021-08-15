package ir.mahozad.android

import android.content.Context
import androidx.core.content.ContextCompat
import ir.mahozad.android.unit.Dimension
import ir.mahozad.android.unit.Dimension.PX
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
    private val mainProperty: KMutableProperty0<T>,
    initialResId: Int = 0
) {
    protected var resId = initialResId
    abstract fun resolveResourceValue(context: Context): T
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        mainProperty.set(resolveResourceValue(chart.context))
    }
}

class IntegerResource(mainProperty: KMutableProperty0<Int>) : PropertyResource<Int>(mainProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getInteger(resId)
}

class FractionResource(mainProperty: KMutableProperty0<Float>) : PropertyResource<Float>(mainProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getFraction(resId, 1, 1)
}

class BooleanResource(mainProperty: KMutableProperty0<Boolean>) : PropertyResource<Boolean>(mainProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getBoolean(resId)
}

class ColorResource(mainProperty: KMutableProperty0<Int>) : PropertyResource<Int>(mainProperty) {
    override fun resolveResourceValue(context: Context) = ContextCompat.getColor(context, resId)
}

class DimensionResource(mainProperty: KMutableProperty0<Dimension>) : PropertyResource<Dimension>(mainProperty) {
    override fun resolveResourceValue(context: Context) = PX(context.resources.getDimension(resId))
}
