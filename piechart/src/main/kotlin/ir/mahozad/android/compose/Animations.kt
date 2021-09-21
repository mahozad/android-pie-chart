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

    val slices = remember {
        mutableStateListOf<InternalSlice>().apply {
            addAll(fractions.zip(colors).map { InternalSlice(it.first, it.second) })
        }
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
