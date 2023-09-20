package wottrich.github.io.smartchecklist.presentation.sort.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.previewparams.BooleanPreviewParameter
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState
import wottrich.github.io.smartchecklist.task.R

private val taskSortItemShape = RoundedCornerShape(Dimens.BaseFour.SizeOne)

@Composable
internal fun TaskSortItem(
    state: TaskSortItemState,
    onClick: (TaskSortItemState) -> Unit
) {
    TaskSortItemBox(
        isSelected = state.isSelected,
        onClick = { onClick(state) }
    ) {
        TaskSortMolecule(state = state)
    }
}

@Composable
private fun TaskSortItemBox(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = getBackgroundColorBySelectedState(isSelected),
        label = "TaskSortItemBackgroundColorAnimation"
    )
    val borderColor by animateColorAsState(
        targetValue = getBorderColorBySelectedState(isSelected),
        label = "TaskSortItemBorderColorAnimation"
    )
    Box(
        modifier = Modifier
            .height(Dimens.BaseFour.SizeEight)
            .clip(taskSortItemShape)
            .border(
                BorderStroke(1.dp, borderColor),
                taskSortItemShape
            )
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        content()
    }
}

@Composable
private fun TaskSortMolecule(state: TaskSortItemState) {
    val alpha = getAlphaBySelectedState(state.isSelected)
    val contentColor by animateColorAsState(
        targetValue = getContentColorBySelectedState(state.isSelected),
        label = "TaskSortItemContentColorAnimation"
    )
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = Dimens.BaseFour.SizeTwo),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskSortItemLabel(
            label = stringResource(id = state.labelRes),
            alpha = alpha,
            contentColor = contentColor
        )
        TaskSortSelectedIcon(state.isSelected)
    }
}

@Composable
private fun TaskSortItemLabel(
    label: String,
    alpha: Float,
    contentColor: Color
) {
    StyledText(
        textStyle = MaterialTheme.typography.button,
        alpha = alpha,
        contentColor = contentColor
    ) {
        Text(text = label)
    }
}

@Composable
private fun RowScope.TaskSortSelectedIcon(isSelected: Boolean) {
    AnimatedVisibility(visible = isSelected) {
        Row {
            Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeOne))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SmartChecklistTheme.colors.onSecondary
            )
        }
    }
}

@Composable
private fun getAlphaBySelectedState(isSelected: Boolean) =
    if (isSelected) ContentAlpha.high else ContentAlpha.medium

@Composable
private fun getBackgroundColorBySelectedState(isSelected: Boolean) =
    if (isSelected) SmartChecklistTheme.colors.secondary.copy(alpha = ContentAlpha.medium)
    else SmartChecklistTheme.colors.surface.copy(alpha = ContentAlpha.medium)

@Composable
fun getBorderColorBySelectedState(isSelected: Boolean) =
    if (isSelected) SmartChecklistTheme.colors.onSecondary.copy(alpha = ContentAlpha.high)
    else SmartChecklistTheme.colors.onSecondary.copy(alpha = ContentAlpha.medium)

@Composable
fun getContentColorBySelectedState(isSelected: Boolean) =
    if (isSelected) SmartChecklistTheme.colors.onSecondary
    else SmartChecklistTheme.colors.onPrimary

@Preview(showBackground = true)
@Composable
fun TaskSortItemPreview(
    @PreviewParameter(BooleanPreviewParameter::class) darkTheme: Boolean
) {
    ApplicationTheme(darkTheme) {
        Scaffold {
            Row(
                Modifier
                    .padding(it)
                    .padding(all = 10.dp)
            ) {
                TaskSortItem(
                    state = TaskSortItemState(
                        SortItemType.UNCOMPLETED_TASKS,
                        R.string.task_sort_uncompleted_task_item,
                        false
                    )
                ) { }
                Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeTwo))
                TaskSortItem(
                    state = TaskSortItemState(
                        SortItemType.COMPLETED_TASKS,
                        R.string.task_sort_completed_task_item,
                        true
                    )
                ) { }
            }
        }
    }
}