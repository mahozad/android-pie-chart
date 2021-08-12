package ir.mahozad.android

import androidx.annotation.*
import androidx.core.content.ContextCompat
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

// https://youtu.be/6P20npkvcb8?t=489


class Integer(
    private var value: Int,
    private val valueProcessor: ((Int) -> Int)? = null,
    private val valueChangeHandler: ((Int) -> Unit)? = null
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = value
    operator fun setValue(chart: PieChart, property: KProperty<*>, newValue: Int) {
        value = valueProcessor?.invoke(newValue) ?: newValue
        valueChangeHandler?.invoke(newValue) ?: chart.invalidate()
    }
}

class IntegerResource(
    @IntegerRes
    private var resId: Int,
    private val backingProperty: KMutableProperty0<Int>
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        backingProperty.set(chart.resources.getInteger(resId))
    }
}

class Fraction(
    @FloatRange(from = 0.0, to = 1.0)
    private var value: Float,
    private val onChange: (() -> Unit)? = null
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = value
    operator fun setValue(chart: PieChart, property: KProperty<*>, newValue: Float) {
        value = newValue.coerceIn(0f, 1f)
        onChange?.invoke() ?: chart.invalidate()
    }
}

class FractionResource(
    @FractionRes
    private var resId: Int,
    private val backingProperty: KMutableProperty0<Float>
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        backingProperty.set(chart.resources.getFraction(resId, 1, 1))
    }
}

class Status(
    private var value: Boolean,
    private val onChange: (() -> Unit)? = null
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = value
    operator fun setValue(chart: PieChart, property: KProperty<*>, newValue: Boolean) {
        value = newValue
        onChange?.invoke() ?: chart.invalidate()
    }
}

class StatusResource(
    @BoolRes
    private var resId: Int,
    private val backingProperty: KMutableProperty0<Boolean>
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        backingProperty.set(chart.resources.getBoolean(resId))
    }
}

class Color(
    @ColorInt
    private var value: Int,
    private val onChange: (() -> Unit)? = null
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = value
    operator fun setValue(chart: PieChart, property: KProperty<*>, newValue: Int) {
        value = newValue
        onChange?.invoke() ?: chart.invalidate()
    }
}

class ColorResource(
    @ColorRes
    private var resId: Int,
    private val backingProperty: KMutableProperty0<Int>
) {
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        backingProperty.set(ContextCompat.getColor(chart.context, resId))
    }
}
