package ir.mahozad.android

import android.content.Context
import androidx.core.content.ContextCompat
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

class IntegerResource(resId: Int, backingProperty: KMutableProperty0<Int>) :
    PropertyResource<Int>(resId, backingProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getInteger(resId)
}

class FractionResource(resId: Int, backingProperty: KMutableProperty0<Float>) :
    PropertyResource<Float>(resId, backingProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getFraction(resId, 1, 1)
}

class BooleanResource(resId: Int, backingProperty: KMutableProperty0<Boolean>) :
    PropertyResource<Boolean>(resId, backingProperty) {
    override fun resolveResourceValue(context: Context) = context.resources.getBoolean(resId)
}

class ColorResource(resId: Int, backingProperty: KMutableProperty0<Int>) :
    PropertyResource<Int>(resId, backingProperty) {
    override fun resolveResourceValue(context: Context) = ContextCompat.getColor(context, resId)
}
