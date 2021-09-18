package ir.mahozad.android.compose

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

enum class AnimationState { STARTED, FINISHED }

// Holds the animation values.
class TransitionData(
    holeRatio: State<Float>
) {
    val holeRatio by holeRatio
}

// Create a Transition and return its animation values.
@Composable fun updateTransitionData(
    animationState: AnimationState,
    targetHoleRatio: Float
): TransitionData {
    val mutableState = remember { MutableTransitionState(animationState) }
    mutableState.targetState = AnimationState.FINISHED
    val transition = updateTransition(mutableState, label = "parent-animation")
    // val transition = updateTransition(animationState, label = "parent-animation")

    val holeRatio = transition.animateFloat(
        label = "hole-animation",
        transitionSpec = {
            tween(durationMillis = 500, delayMillis = 0, easing = FastOutSlowInEasing)
        }) { state ->
        when (state) {
            AnimationState.STARTED -> 0f
            AnimationState.FINISHED -> targetHoleRatio
        }
    }

    return remember(transition) { TransitionData(holeRatio) }
}
