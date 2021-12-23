package wottrich.github.io.publicandroid.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.RowComponent
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.R

@Composable
fun TaskItemComponent(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    Surface(
        modifier = Modifier.padding(horizontal = Dimens.BaseFour.SizeThree),
        shape = RoundedCornerShape(Dimens.BaseFour.SizeTwo),
        elevation = Dimens.BaseFour.SizeOne
    ) {
        RowComponent(
            modifier = Modifier.clickable { onCheckChange() },
            leftIconContent = {
                AnimatedVisibility(visible = showDeleteItem) {
                    Icon(
                        tint = MaterialTheme.colors.onSurface,
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(
                            id = R.string.task_item_component_delete_item,
                            task.name
                        ),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onDeleteTask() }
                    )
                }
            },
            leftContent = {
                TextOneLine(
                    primary = {
                        Text(text = task.name)
                    }
                )
            },
            rightIconContent = {
                IconCompletableTaskContent(
                    taskName = task.name,
                    isCompletedTask = task.isCompleted
                ) {
                    onCheckChange()
                }
            }
        )
    }
}

@Composable
private fun IconCompletableTaskContent(
    taskName: String,
    isCompletedTask: Boolean,
    onCheckChange: () -> Unit
) {

    val checkIconContentDescription = if (isCompletedTask) {
        R.string.task_item_component_click_to_uncheck_item_description
    } else {
        R.string.task_item_component_click_to_check_item_description
    }

    val checkIcon = if (isCompletedTask) {
        R.drawable.ic_completed
    } else {
        R.drawable.ic_uncompleted
    }

    Icon(
        tint = MaterialTheme.colors.secondary,
        painter = painterResource(id = checkIcon),
        contentDescription = stringResource(checkIconContentDescription, taskName),
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onCheckChange() }
    )
}