package ir.mahozad.android.compose

import android.content.Context
import androidx.compose.runtime.MutableState
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

// https://youtu.be/6P20npkvcb8?t=489

internal class Property<T>(
    private val state: MutableState<T>,
    private val valueProcessor: ((T) -> T)? = null,
    private val valueChangeHandler: ((T) -> Unit)? = null
) {
    operator fun getValue(chart: PieChartView, property: KProperty<*>) = state.value
    operator fun setValue(chart: PieChartView, property: KProperty<*>, newValue: T) {
        state.value = valueProcessor?.invoke(newValue) ?: newValue
        valueChangeHandler?.invoke(state.value) /*?: chart.invalidate()*/
    }
}

abstract class PropertyResource<T>(initialResId: Int = 0) {
    protected abstract val mainProperty: KMutableProperty0<T>
    protected var resId = initialResId
    abstract fun resolveResourceValue(context: Context): T
    operator fun getValue(chart: PieChartView, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChartView, property: KProperty<*>, newResId: Int) {
        resId = newResId
        mainProperty.set(resolveResourceValue(chart.context))
    }
}

class FractionResource(override val mainProperty: KMutableProperty0<Float>) : PropertyResource<Float>() {
    override fun resolveResourceValue(context: Context) = context.resources.getFraction(resId, 1, 1)
}
