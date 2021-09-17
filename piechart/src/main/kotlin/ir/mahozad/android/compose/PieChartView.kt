package ir.mahozad.android.compose

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView
import androidx.core.content.withStyledAttributes
import ir.mahozad.android.DEFAULT_HOLE_RATIO
import ir.mahozad.android.R

/**
 * See [this article](https://proandroiddev.com/jetpack-compose-interop-part-2-using-compose-in-traditional-android-views-and-layouts-with-a3c50fc2eaa5).
 */
class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    private val holeRatioState = mutableStateOf(DEFAULT_HOLE_RATIO)
    var holeRatioResource by FractionResource(::holeRatio)
    var holeRatio by Property(holeRatioState, { it.coerceIn(0f, 1f) })

    /**
     * Attributes are a powerful way of controlling the behavior and appearance of views,
     * but they can only be read when the view is initialized. To provide dynamic behavior,
     * expose a property getter and setter pair for each custom attribute.
     *
     * A good rule to follow is to always expose any property that affects the
     * visible appearance or behavior of your custom view.
     */
    init {
        // Could also have used context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).use {...}
        //  TypedArray objects are a shared resource and must be recycled after use (thus the .use {})
        // See https://stackoverflow.com/a/68803044
        context.withStyledAttributes(attrs, R.styleable.PieChart) {
            holeRatio = getFloat(R.styleable.PieChart_holeRatio, DEFAULT_HOLE_RATIO)
        }
    }

    @Composable override fun Content() {
        PieChartCompose(
            pieChartData = emptyList(),
            /*Modifier
                .height(Dp(height.toFloat()))
                .width(Dp(width.toFloat()))
                .padding(),*/
            holeRatio = holeRatioState.value,
            overlayRatio = 0.65f
        )
    }
}
