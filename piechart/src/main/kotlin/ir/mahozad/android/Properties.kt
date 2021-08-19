package ir.mahozad.android

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

abstract class PropertyResource<T>(initialResId: Int = 0) {
    protected abstract val mainProperty: KMutableProperty0<T>
    protected var resId = initialResId
    abstract fun resolveResourceValue(context: Context): T
    operator fun getValue(chart: PieChart, property: KProperty<*>) = resId
    operator fun setValue(chart: PieChart, property: KProperty<*>, newResId: Int) {
        resId = newResId
        mainProperty.set(resolveResourceValue(chart.context))
    }
}

class IntegerResource(override val mainProperty: KMutableProperty0<Int>) : PropertyResource<Int>() {
    override fun resolveResourceValue(context: Context) = context.resources.getInteger(resId)
}

class FractionResource(override val mainProperty: KMutableProperty0<Float>) : PropertyResource<Float>() {
    override fun resolveResourceValue(context: Context) = context.resources.getFraction(resId, 1, 1)
}

class BooleanResource(override val mainProperty: KMutableProperty0<Boolean>) : PropertyResource<Boolean>() {
    override fun resolveResourceValue(context: Context) = context.resources.getBoolean(resId)
}

class ColorResource(override val mainProperty: KMutableProperty0<Int>) : PropertyResource<Int>() {
    override fun resolveResourceValue(context: Context) = ContextCompat.getColor(context, resId)
}

class TintResource(override val mainProperty: KMutableProperty0<Int?>) : PropertyResource<Int?>() {
    override fun resolveResourceValue(context: Context): Int? {
        return if (resId == 0) null else ContextCompat.getColor(context, resId)
    }
}

class DimensionResource(override val mainProperty: KMutableProperty0<Dimension>) : PropertyResource<Dimension>() {
    override fun resolveResourceValue(context: Context) = PX(context.resources.getDimension(resId))
}

class StringResource(override val mainProperty: KMutableProperty0<String>) : PropertyResource<String>() {
    override fun resolveResourceValue(context: Context) = context.resources.getString(resId)
}

class FontResource(override val mainProperty: KMutableProperty0<Typeface>) : PropertyResource<Typeface>() {
    override fun resolveResourceValue(context: Context) = ResourcesCompat.getFont(context, resId)!!
}

// class EnumResource<T : Enum<T>>(override val mainProperty: KMutableProperty0<T>) : PropertyResource<T>() {
//     override fun resolveResourceValue(context: Context): T {
//         context.withStyledAttributes(resId, R.styleable.PieChart) {
//             val mainPropertyClass = mainProperty.get()::class
//             if (mainPropertyClass == Alignment::class) {
//                 return getEnum(R.styleable.PieChart_legendsTitleAlignment, defaultLegendsTitleAlignment) as T
//             } /*else if (mainPropertyClass == AnotherEnumClass) {
//                 return getEnum(R.styleable.PieChart_anotherEnumAttribute, anotherEnumDefault) as T
//             }*/
//         }
//         throw Exception("No enum class matched the type of the property")
//     }
// }
