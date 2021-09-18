package ir.mahozad.android.compose

import androidx.compose.animation.core.*
import androidx.compose.runtime.*






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
 * On initialization, transition is set the value INITIALIZED and animation is run
 * so the if statement chooses the if branch and the holeRatio is set to value `0f` (without animation).
 * Next times, when the holeRatio is updated (and the function is recomposed),
 * the target state is assigned the same literal (`RECOMPOSED`) and so the conditional always
 * runs the else block and thus it animates to the targetHoleRatio.
 */
@Composable fun updateTransitionData2(
    targetHoleRatio: Float
): TransitionData2 {
    val mutableState = remember { MutableTransitionState(AnimationState.INITIALIZED) }
    mutableState.targetState = AnimationState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "main-animation")

    val holeRatio = transition.animateFloat(
        label = "hole-animation",
        transitionSpec = {
            tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
        }) { if (it == AnimationState.INITIALIZED) 0f else targetHoleRatio }

    return remember(transition) { TransitionData2(holeRatio) }
}

class TransitionData2(
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}










@Composable fun updateTransitionData3(
    targetHoleRatio: Float
): TransitionData3 {
    val mutableState = remember { MutableTransitionState(AnimationState.INITIALIZED) }
    mutableState.targetState = AnimationState.RECOMPOSED
    val transition = updateTransition(mutableState, label = "main-animation")
    val animation = remember { SimpleAnimation }
    animation.Animation(transition, targetHoleRatio)
    return remember { TransitionData3(animation.holeRatio) }
}

class TransitionData3(
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
