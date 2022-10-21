package wottrich.github.io.impl.presentation.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.io.wottrich.common.ui.compose.components.CircularProgressIndicatorSizable
import github.io.wottrich.common.ui.compose.utils.pressProgressionInteractionState
import wottrich.github.io.baseui.icons.DeleteIcon
import wottrich.github.io.impl.R.string

@Composable
fun DeleteIconWithProgression(
    taskName: String,
    onDeleteTask: () -> Unit
) {
    Box {
        val interaction = remember { MutableInteractionSource() }
        val interactions = interaction.interactions
        val progress by pressProgressionInteractionState(
            interactions = interactions,
            initialTimeInMillis = 0,
            timePressingToFinishInMillis = 500,
            onFinishTimePressing = { onDeleteTask() }
        )
        DeleteIcon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    interactionSource = interaction,
                    indication = LocalIndication.current
                ) { },
            contentDescription = stringResource(
                id = string.task_item_component_delete_item,
                taskName
            )
        )
        CircularProgressIndicatorSizable(progress = progress, size = 32.dp)
    }
}