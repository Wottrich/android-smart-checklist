package github.io.wottrich.common.ui.compose.utils

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.PressInteraction.Press
import androidx.compose.foundation.interaction.PressInteraction.Release
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

private const val DEFAULT_DELAY_BETWEEN_UPDATES_IN_MILLIS = 50L
private const val DEFAULT_INCREASE_VALUE_IN_TIME = 50L
private const val DEFAULT_DECREASE_VALUE_IN_TIME = 50L

@Composable
fun pressProgressionInteractionState(
    interactions: Flow<Interaction>,
    initialTimeInMillis: Long,
    timePressingToFinishInMillis: Long,
    onFinishTimePressing: () -> Unit,
    key: String? = null
): State<Float> {
    val progressState = remember(key) { mutableStateOf(0f) }
    var currentTime by remember(key) {
        mutableStateOf(initialTimeInMillis)
    }
    var isDeleting by remember(key) {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = currentTime, key2 = isDeleting) {
        delay(DEFAULT_DELAY_BETWEEN_UPDATES_IN_MILLIS)
        if (isDeleting) {
            if (currentTime <= timePressingToFinishInMillis) {
                currentTime += DEFAULT_INCREASE_VALUE_IN_TIME
            }
        } else {
            if (currentTime > initialTimeInMillis) {
                currentTime -= DEFAULT_DECREASE_VALUE_IN_TIME
            }
        }
        progressState.value = currentTime / timePressingToFinishInMillis.toFloat()
        if (currentTime >= timePressingToFinishInMillis) {
            onFinishTimePressing()
        }
    }
    LaunchedEffect(
        key1 = interactions,
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
    return progressState
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