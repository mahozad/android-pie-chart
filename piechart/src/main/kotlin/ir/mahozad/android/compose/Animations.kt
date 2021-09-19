package ir.mahozad.android.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class AnimationState { INITIALIZED, RECOMPOSED }







// Create a Transition and return its animation values.
@Composable fun updateTransitionData(
    animationState: AnimationState,
    targetHoleRatio: Float
): TransitionData {
    val mutableState = remember { MutableTransitionState(animationState) }
    mutableState.targetState = AnimationState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "parent-animation")
    // val transition = updateTransition(animationState, label = "parent-animation")

    val holeRatio = transition.animateFloat(
        label = "hole-animation",
        transitionSpec = {
            tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
        }) { state ->
        when (state) {
            AnimationState.INITIALIZED -> 0f
            AnimationState.RECOMPOSED -> targetHoleRatio
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
    targetSlices: List<SliceCompose>,
    targetHoleRatio: Float
): TransitionData2 {
    val mutableState = remember { MutableTransitionState(AnimationState.INITIALIZED) }
    mutableState.targetState = AnimationState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "main-animation")


    val color0 = animateColorAsState(targetValue = targetSlices.getOrNull(0)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color1 = animateColorAsState(targetValue = targetSlices.getOrNull(1)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color2 = animateColorAsState(targetValue = targetSlices.getOrNull(2)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color3 = animateColorAsState(targetValue = targetSlices.getOrNull(3)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color4 = animateColorAsState(targetValue = targetSlices.getOrNull(4)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color5 = animateColorAsState(targetValue = targetSlices.getOrNull(5)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color6 = animateColorAsState(targetValue = targetSlices.getOrNull(6)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color7 = animateColorAsState(targetValue = targetSlices.getOrNull(7)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color8 = animateColorAsState(targetValue = targetSlices.getOrNull(8)?.color ?: Color.Transparent, animationSpec = tween(500))
    val color9 = animateColorAsState(targetValue = targetSlices.getOrNull(9)?.color ?: Color.Transparent, animationSpec = tween(500))

    val fraction0 = animateFloatAsState(targetValue = targetSlices.getOrNull(0)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction1 = animateFloatAsState(targetValue = targetSlices.getOrNull(1)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction2 = animateFloatAsState(targetValue = targetSlices.getOrNull(2)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction3 = animateFloatAsState(targetValue = targetSlices.getOrNull(3)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction4 = animateFloatAsState(targetValue = targetSlices.getOrNull(4)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction5 = animateFloatAsState(targetValue = targetSlices.getOrNull(5)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction6 = animateFloatAsState(targetValue = targetSlices.getOrNull(6)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction7 = animateFloatAsState(targetValue = targetSlices.getOrNull(7)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction8 = animateFloatAsState(targetValue = targetSlices.getOrNull(8)?.fraction ?: 0f, animationSpec = tween(500))
    val fraction9 = animateFloatAsState(targetValue = targetSlices.getOrNull(9)?.fraction ?: 0f, animationSpec = tween(500))

    val slices by remember { mutableStateOf(
        listOf(
            InternalSlice(fraction0, color0),
            InternalSlice(fraction1, color1),
            InternalSlice(fraction2, color2),
            InternalSlice(fraction3, color3),
            InternalSlice(fraction4, color4),
            InternalSlice(fraction5, color5),
            InternalSlice(fraction6, color6),
            InternalSlice(fraction7, color7),
            InternalSlice(fraction8, color8),
            InternalSlice(fraction9, color9),
        )
    )  }

    // val slices = listOf(
    //     remember { mutableStateOf(InternalSlice(fractionStates[0], colorStates[0])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[1], colorStates[1])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[2], colorStates[2])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[3], colorStates[3])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[4], colorStates[4])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[5], colorStates[5])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[6], colorStates[6])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[7], colorStates[7])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[8], colorStates[8])) },
    //     remember { mutableStateOf(InternalSlice(fractionStates[9], colorStates[9])) },
    // )
    // val slices = remember {
    //     for (index in targetSlices.indices) {
    //
    //     }
    //     if (targetSlices.size < slices.size)
    // }
    //
    // val slices = remember { mutableStateListOf<SliceCompose>() }
    // if (targetSlices.size < slices.size) {
    //     for (i in (targetSlices.size..slices.size)) {
    //         slices.removeAt(i)
    //     }
    // } else if (targetSlices.size > slices.size) {
    //     for (i in (slices.size..targetSlices.size)) {
    //         slices.add(sliceStates[i].value)
    //     }
    // }
    // for ((i,targetSlice) in targetSlices.withIndex()) {
    //     slices[i] = animateColorAsState(targetValue = targetSlice.color)
    // }


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
        }) { if (it == AnimationState.INITIALIZED) 0f else targetHoleRatio }

    return remember(transition) { TransitionData2(slices, holeRatio) }
}

class TransitionData2(
    slices: List<InternalSlice>,
    holeRatio: State<Float>
) {
    val slices = slices
    val holeRatio by holeRatio
}

data class InternalSlice(val fraction: State<Float>, val color: State<Color>)









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
    val mutableState = remember { MutableTransitionState(AnimationState.INITIALIZED) }
    mutableState.targetState = AnimationState.RECOMPOSED
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
    @Composable fun Animation(transition: Transition<AnimationState>, targetHoleRatio: Float)
}

object SimpleAnimation : ChartAnimation {
    override val holeRatio = mutableStateOf(0.3f)
    @Composable override fun Animation(
        transition: Transition<AnimationState>,
        targetHoleRatio: Float
    ) {
        holeRatio.value = transition.animateFloat(
            label = "hole-animation",
            transitionSpec = {
                tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
            }) { if (it == AnimationState.INITIALIZED) 0f else targetHoleRatio }
            .value
    }
}
