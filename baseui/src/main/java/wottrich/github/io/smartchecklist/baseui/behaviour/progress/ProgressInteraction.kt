package wottrich.github.io.smartchecklist.baseui.behaviour.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.PressInteraction.Press
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow

@Composable
fun pressProgressionInteractionState(
    interactions: Flow<Interaction>,
    timePressingToFinishInMillis: Long,
    key: String? = null
): State<Float> {
    var isDeleting by remember(key) {
        mutableStateOf(false)
    }
    val transition = updateTransition(
        isDeleting,
        label = "pressProgressionInteractionState$key"
    )
    val result = transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = timePressingToFinishInMillis.toInt(),
                easing = LinearEasing
            )
        },
        label = "pressProgressionInteractionStateResult$key"
    ) {
        if (it) 1f else 0f
    }
    LaunchedEffect(
        key1 = interactions,
        key2 = key,
        block = {
            interactions.collect {
                onPressInteraction(
                    pressInteraction = it,
                    onPress = { isDeleting = true },
                    onRelease = { isDeleting = false }
                )
            }
        }
    )
    return result
}

private fun onPressInteraction(
    pressInteraction: Interaction,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    if (pressInteraction is PressInteraction) {
        when (pressInteraction) {
            is Press -> onPress()
            else -> onRelease()
        }
    }
}