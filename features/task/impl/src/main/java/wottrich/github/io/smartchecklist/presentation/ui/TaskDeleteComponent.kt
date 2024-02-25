package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.icons.DeleteIcon
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.task.R

@Composable
fun TaskDeleteComponent(
    name: String,
    showDeleteItem: Boolean,
    onDeleteItem: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    AnimatedVisibility(visible = showDeleteItem) {
        DeleteIcon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        expanded.value = true
                    }
                ),
            contentDescription = stringResource(
                id = R.string.task_item_component_delete_item,
                name
            )
        )
    }
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        StyledText(textStyle = MaterialTheme.typography.h6) {
            Text(
                modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree),
                text = stringResource(
                    id = R.string.task_item_component_delete_item_confirm_title,
                    name
                )
            )
        }
        DropdownMenuItem(
            onClick = {
                expanded.value = false
                onDeleteItem()
            }
        ) {
            Text(text = stringResource(id = R.string.task_item_component_delete_item_confirm_label))
        }
        DropdownMenuItem(onClick = { expanded.value = false }) {
            Text(text = stringResource(id = R.string.task_item_component_delete_item_cancel_label))
        }
    }
}