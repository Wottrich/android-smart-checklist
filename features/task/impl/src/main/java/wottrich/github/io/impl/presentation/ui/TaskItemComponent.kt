package wottrich.github.io.impl.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import github.io.wottrich.common.ui.compose.ripple.RippleStatusColor
import github.io.wottrich.common.ui.compose.utils.pressProgressionInteractionState
import wottrich.github.io.baseui.RowComponent
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.icons.DeleteIcon
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.impl.R.string

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
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.BaseFour.SizeThree)
        ) {
            val boxConstraints = this
            val interaction = remember { MutableInteractionSource() }
            val interactions = interaction.interactions
            val progress by pressProgressionInteractionState(
                interactions = interactions,
                initialTimeInMillis = 0,
                timePressingToFinishInMillis = 500,
                onFinishTimePressing = { /*onDeleteTask()*/ }
            )
            CoreTaskComponent(task, interaction, showDeleteItem, onCheckChange, onDeleteTask)
            Canvas(
                modifier = Modifier.height(10.dp),
                onDraw = {

                    val topLeft = Offset(maxWidth.value * progress, 1f)
                    println(progress)
                    println(maxWidth.value * progress)
                    drawRoundRect(
                        color = Color.Red,
                        topLeft = topLeft,
                        size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }
            )
        }
    }
}

@Composable
private fun CoreTaskComponent(
    task: Task,
    interactionSource: MutableInteractionSource,
    showDeleteItem: Boolean,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    Surface(
        modifier = Modifier
            .alpha(getItemAlpha(isCompleted = task.isCompleted)),
        shape = TaskItemShape,
        elevation = 1.dp
    ) {
        RowComponent(
            modifier = Modifier
                .clickable { onCheckChange() }
                .clip(TaskItemShape),
            leftIconContent = {
//                AnimatedVisibility(visible = showDeleteItem) {
//                    DeleteIconWithProgression(taskName = task.name, onDeleteTask)
//                }
                DeleteIcon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current
                        ) { },
                    contentDescription = stringResource(
                        id = string.task_item_component_delete_item,
                        task.name
                    )
                )
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

private fun getTextDecoration(isCompleted: Boolean): TextDecoration? {
    return if (isCompleted) TextDecoration.LineThrough else null
}

@Composable
private fun getItemAlpha(isCompleted: Boolean): Float {
    return if (isCompleted) ContentAlpha.medium else ContentAlpha.high
}