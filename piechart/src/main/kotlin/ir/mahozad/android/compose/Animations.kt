package ir.mahozad.android.compose

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

enum class ChartState { INITIALIZED, RECOMPOSED }







// Create a Transition and return its animation values.
@Composable fun updateTransitionData(
    chartState: ChartState,
    targetHoleRatio: Float
): TransitionData {
    val mutableState = remember { MutableTransitionState(chartState) }
    mutableState.targetState = ChartState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "parent-animation")
    // val transition = updateTransition(animationState, label = "parent-animation")

    val holeRatio = transition.animateFloat(
        label = "hole-animation",
        transitionSpec = {
            tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
        }) { state ->
        when (state) {
            ChartState.INITIALIZED -> 0f
            ChartState.RECOMPOSED -> targetHoleRatio
        }
    }

    return remember(transition) { TransitionData(holeRatio) }
}

// Holds the animation values.
class TransitionData(
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}









/**
 * This approach does not animate smoothly when the value is changed while
 * the previous value animation was still running.
 *
 * On initialization, transition is set the value INITIALIZED and animation is run
 * so the if statement chooses the if branch and the holeRatio is set to value `0f` (without animation).
 * Next times, when the holeRatio is updated (and the function is recomposed),
 * the target state is assigned the same literal (`RECOMPOSED`) and so the conditional always
 * runs the else block and thus the holeRatio animates to the targetHoleRatio.
 */
@Composable fun updateTransitionData2(
    targetSlices: List<Slice>,
    targetHoleRatio: Float
): TransitionData2 {
    val mutableState = remember { MutableTransitionState(ChartState.INITIALIZED) }
    mutableState.targetState = ChartState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "main-animation")

    /**
     *
     * Next, you can think of
     * mutableStateListOf as an observable list where updates to this variable will redraw all
     * the composable functions that access it. We don't need to explicitly subscribe at all. Any
     * composable that reads the value of deletedPersonList will be recomposed any time the value
     * changes. This ensures that only the composables that depend on this will be redrawn while the
     * rest remain unchanged. This ensures efficiency and is a performance optimization. It
     * is inspired from existing frameworks like React.
     *
     */




    val colors = listOf(
        animateColorAsState(targetValue = targetSlices.getOrNull(0)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(1)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(2)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(3)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(4)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(5)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(6)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(7)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(8)?.color ?: Color.Transparent, animationSpec = tween(500)),
        animateColorAsState(targetValue = targetSlices.getOrNull(9)?.color ?: Color.Transparent, animationSpec = tween(500))
    )

    val fractions = listOf(
        animateFloatAsState(targetValue = targetSlices.getOrNull(0)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(1)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(2)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(3)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(4)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(5)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(6)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(7)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(8)?.fraction ?: 0f, animationSpec = tween(500)),
        animateFloatAsState(targetValue = targetSlices.getOrNull(9)?.fraction ?: 0f, animationSpec = tween(500))
    )

    val internalSlices = remember {
        fractions
            .zip(colors)
            .map { InternalSlice(it.first, it.second) }
            .toMutableStateList()
    }


    // var previousSlices by remember { mutableStateOf(emptyList<SliceCompose>()) }
    //
    // // Reinitialize animation value when targetSlices changes
    // val slicesAnimation = remember(targetSlices) { Animatable(0f) }
    // val slicesAnimationProgress by slicesAnimation.asState()
    // LaunchedEffect(targetSlices) {
    //     slicesAnimation.animateTo(1f)
    // }
    //
    // val newSlices = remember { mutableStateListOf<SliceCompose>() }
    // for ((i, previousSlice) in previousSlices.withIndex()) {
    //     val color by animateColorAsState(targetValue = targetSlices[i].color)
    //     val fraction by animateFloatAsState(targetValue = targetSlices[i].fraction)
    //     newSlices.add(SliceCompose(fraction, color))
    // }
    // // targetSlices.value = targetSlices.map { it.copy(fraction = it.fraction * slicesAnimationProgress) }
    //
    // previousSlices = targetSlices



    // var isFirstComposition by remember { mutableStateOf(true) }
    // val slices = remember { mutableStateOf(targetSlices) }
    // if (isFirstComposition) {
    //     isFirstComposition = false
    //     // Reinitialize animation value when targetSlices changes
    //     val slicesAnimation = remember(targetSlices) { Animatable(0f) }
    //     val slicesAnimationProgress by slicesAnimation.asState()
    //     LaunchedEffect(targetSlices) {
    //         slicesAnimation.animateTo(1f)
    //     }
    //     slices.value = targetSlices.map { it.copy(fraction = it.fraction * slicesAnimationProgress) }
    // } else {
    //     slices.value = targetSlices
    // }


    val holeRatio = transition.animateFloat(
        label = "hole-animation",
        transitionSpec = {
            tween(durationMillis = 1500, delayMillis = 0, easing = FastOutSlowInEasing)
        }) { if (it == ChartState.INITIALIZED) 0f else targetHoleRatio }

    return remember(transition) { TransitionData2(internalSlices, holeRatio) }
}

class TransitionData2(
    val slices: List<InternalSlice>,
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}

data class InternalSlice(val fraction: State<Float>, val color: State<Color>)









val random = Random(1)




@Composable fun updateTransitionDataNew(
    targetSlices: List<Slice>,
    targetHoleRatio: Float
): TransitionDataNew {
    val slicesMutableState = remember { MutableTransitionState(ChartState.INITIALIZED) }
    slicesMutableState.targetState = ChartState.RECOMPOSED
    val transition = updateTransition(slicesMutableState, label = "main-animation")



    val listOfFractions = remember { mutableStateListOf<MutableState<Float>>() }
    Log.i("aabbcc", "listOfFractions: ${listOfFractions.map { it.value }}")
    // val listOfAnimatedFractions = remember { SnapshotStateList<State<Float>>() }
    for (index in targetSlices.indices) {
        if (index in listOfFractions.indices) {
            listOfFractions[index].value = targetSlices[index].fraction
        } else {
            listOfFractions.add(mutableStateOf(0.0f))
            // listOfAnimatedFractions.add(
            //     transition.animateFloat(label = "fraction-$index-animation", transitionSpec = {
            //         tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
            //     }) { state -> if (state == ChartState.INITIALIZED) {
            //         Log.i("aabbcc", "in animateFloat lambda INITIALIZED; listOfFractions[$index]: ${listOfFractions[index]}")
            //         derivedStateOf { listOfFractions[index].value }.value
            //     } else /*0.2f*/{
            //         Log.i("aabbcc", "in animateFloat lambda RECOMPOSED; listOfFractions[$index]: ${listOfFractions[index]}")
            //         listOfFractions[index].value
            //     }
            //     }
            // )
            listOfFractions[index].value = targetSlices[index].fraction
        }
    }
    for (i in targetSlices.size until listOfFractions.size) {
        listOfFractions[i].value = 0f // OR listOfFractions.removeAt(i)
        // listOfAnimatedFractions.removeAt(i)
    }


    val animatedFraction2 = listOfFractions.mapIndexed { index, fraction ->
        transition.animateFloat(label = "fraction-$index-animation", transitionSpec = {
            tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
        }) { state -> if (state == ChartState.INITIALIZED) {
            Log.i("aabbcc", "in animateFloat lambda INITIALIZED; listOfFractions[$index]: ${listOfFractions[index]}")
            fraction.value
        } else /*0.2f*/{
            Log.i("aabbcc", "in animateFloat lambda RECOMPOSED; listOfFractions[$index]: ${listOfFractions[index]}")
            fraction.value
        }
        }
    }

    val internalSlices = remember(animatedFraction2.size) {
        animatedFraction2
            // .zip(animatedColors)
            .map { InternalSliceNew(it, mutableStateOf(Color(random.nextFloat(), random.nextFloat(), random.nextFloat()))) }
        // .toMutableStateList()
    }

    internalSlices.forEachIndexed { i, slice -> Log.i("aabbcc", "internalSlice[$i].fraction: ${slice.fraction.value}") }

    val holeRatio = transition.animateFloat(label = "hole-animation", transitionSpec = {
        tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
    }) { if (it == ChartState.INITIALIZED) 0f else targetHoleRatio }

    Log.i("aabbcc", "holeRatio: ${holeRatio.value}")

    return remember(animatedFraction2) { TransitionDataNew(internalSlices, holeRatio) }
}

class TransitionDataNew(
    val slices: List<InternalSliceNew>,
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}

data class InternalSliceNew(val fraction: State<Float>, val color: State<Color>)













@Composable fun updateTransitionData3(
    targetHoleRatio: Float
): TransitionData3 {
    /*
    // Reinitialize animation value when targetHoleRatio changes
    val holeRatioAnimation = remember(targetHoleRatio) { Animatable(0f) }
    val holeRatioAnimationProgress by holeRatioAnimation.asState()
    LaunchedEffect (targetHoleRatio) {
        holeRatioAnimation.animateTo(1f)
    }
    */

    val holeRatio = animateFloatAsState(
        targetValue = targetHoleRatio,
        animationSpec = tween(durationMillis = 1500, delayMillis = 0, easing = FastOutSlowInEasing)
    )

    return remember { TransitionData3(holeRatio) }
}

class TransitionData3(
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}








@Composable fun updateTransitionData4(
    targetHoleRatio: Float
): TransitionData4 {
    val mutableState = remember { MutableTransitionState(ChartState.INITIALIZED) }
    mutableState.targetState = ChartState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "main-animation")
    val animation = remember { SimpleAnimation }
    animation.Animation(transition, targetHoleRatio)
    return remember { TransitionData4(animation.holeRatio) }
}

class TransitionData4(
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}

interface ChartAnimation {
    val holeRatio: MutableState<Float>
    @Composable fun Animation(transition: Transition<ChartState>, targetHoleRatio: Float)
}

object SimpleAnimation : ChartAnimation {
    override val holeRatio = mutableStateOf(0.3f)
    @Composable override fun Animation(
        transition: Transition<ChartState>,
        targetHoleRatio: Float
    ) {
        holeRatio.value = transition.animateFloat(
            label = "hole-animation",
            transitionSpec = {
                tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
            }) { if (it == ChartState.INITIALIZED) 0f else targetHoleRatio }
            .value
    }
}
