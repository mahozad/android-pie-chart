package ir.mahozad.android.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

private val elements = mutableListOf<Animatable<Float, AnimationVector1D>>()

@Composable
fun useAnimatableList(x: Any): Unit {
    var list by remember { mutableStateOf(listOf(0.5f, 0.3f, 0.2f)) }
    AnimatedList(list)
    Button(onClick = {
        val count = (1..8).random()
        val values = (1..count).map { (1..10).random() }
        list = values.map { it.toFloat() / values.sum() }
    }) {
        Text("Generate")
    }
}

@Composable
fun AnimatedList(list: List<Float>) {
    val scope = rememberCoroutineScope()
    val colors = remember { (1..20).map { getRandomColor() } }
    // OR val colors = remember(elements.size) { elements.indices.map { getRandomColor() } }

    remember(list) {
        println("new list: $list")
        println("elements: ${elements.map { it.value }}")
        // OR
        // for (i in 0 until max(list.size, elements.size)) {
        //     val targetValue = list.getOrNull(i) ?: 0.0001f
        //     val element = elements.getOrNull(i) ?: Animatable(0.000f).also(elements::add)
        //     scope.launch { element.animateTo(targetValue) }
        // }
        repeat(list.size - elements.size) {
            println("Adding animatable to elements")
            elements.add(Animatable(0.0001f))
        }
        elements.forEachIndexed { i, element ->
            val targetValue = list.getOrNull(i) ?: 0.0001f
            println("Animating element $i from ${element.value} to $targetValue")
            scope.launch { element.animateTo(targetValue) }
        }
    }

    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        for ((i, element) in elements.withIndex()) {
            Box(
                modifier = Modifier
                    .background(colors[i])
                    .fillMaxHeight()
                    .width((element.value * 250).dp)
                // OR
                // .fillMaxHeight()
                // .weight(element.value)
                // OR
                // .fillMaxSize()
                // .weight(element.value, fill = false)
            )
        }
    }
}

private fun getRandomColor(): Color {
    fun generateComponent() = Random.nextInt(255)
    return Color(generateComponent(), generateComponent(), generateComponent())
}
