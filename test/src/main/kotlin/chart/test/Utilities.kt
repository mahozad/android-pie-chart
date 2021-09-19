package chart.test

import androidx.compose.ui.graphics.Color

/**
 * Generate a list of random numbers that sum up to the base.
 * Both the number of values and the values themselves are random.
 *
 * Diversity is how much the resulting numbers can be different.
 * A diversity of 10 means that the largest possible random number
 * could be 10 times larger than the smallest possible random number.
 *
 * See [this post](https://stackoverflow.com/q/2640053).
 */
internal fun generateRandomNumbers(
    base: Int = 1,
    maxSize: Int = 8,
    diversity: Int = 30
): List<Float> {
    val size = (1..maxSize).random()
    val numbers = (1..size).map { (1..diversity).random() }
    return numbers.map { it.toFloat() / numbers.sum() * base }
}

internal fun generateRandomColor(): Int {
    fun component() = (0..255).random()
    return android.graphics.Color.rgb(component(), component(), component())
}

internal fun generateRandomColorCompose(): Color {
    fun component() = (0..255).random()
    return Color(component(), component(), component())
}
