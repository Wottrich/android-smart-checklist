package wottrich.github.io.impl.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import github.io.wottrich.common.ui.compose.ripple.RippleStatusColor
import wottrich.github.io.baseui.RowComponent
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.datasource.entity.Task

private val TaskItemShape = RoundedCornerShape(Dimens.BaseFour.SizeTwo)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItemComponent(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    CompositionLocalProvider(
        LocalRippleTheme provides RippleStatusColor(!task.isCompleted)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = Dimens.BaseFour.SizeThree)
                .alpha(getItemAlpha(isCompleted = task.isCompleted)),
            shape = TaskItemShape,
            elevation = 1.dp
        ) {
            RowComponent(
                modifier = Modifier
                    .clickable { onCheckChange() }
                    .clip(TaskItemShape),
                leftIconContent = {
                    AnimatedVisibility(visible = showDeleteItem) {
                        DeleteIconWithProgression(taskName = task.name, onDeleteTask)
                    }
                },
                leftContent = {
                    TextOneLine(
                        primary = {
                            Text(
                                text = task.name,
                                textDecoration = getTextDecoration(task.isCompleted)
                            )
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
}

private fun getTextDecoration(isCompleted: Boolean): TextDecoration? {
    return if (isCompleted) TextDecoration.LineThrough else null
}

@Composable
private fun getItemAlpha(isCompleted: Boolean): Float {
    return if (isCompleted) ContentAlpha.medium else ContentAlpha.high
}