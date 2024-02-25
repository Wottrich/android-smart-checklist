package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.components.completable.CompletableComponent
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem

@Composable
fun TaskLazyColumnComponent(
    taskList: List<BaseTaskListItem>,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showDeleteItem: Boolean = true
) {
    LazyColumn(
        content = {
            items(taskList) {
                when (it) {
                    is BaseTaskListItem.SectionItem -> TaskSection(sectionItem = it)
                    is BaseTaskListItem.TaskItem -> TaskItem(
                        task = it.task,
                        showDeleteItem = showDeleteItem,
                        onCheckChange = onCheckChange,
                        onDeleteTask = onDeleteTask
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTen))
            }
        }
    )
}

@Composable
private fun TaskSection(
    sectionItem: BaseTaskListItem.SectionItem
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.BaseFour.SizeThree)
    ) {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        Divider()
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        StyledText(textStyle = MaterialTheme.typography.h6) {
            Text(stringResource(id = sectionItem.sectionName))
        }
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
    }
}

@Composable
private fun TaskItem(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        CompletableComponent(
            name = task.name,
            isCompleted = task.isCompleted,
            onCheckChange = { onCheckChange(task) },
            leftIconContent = {
                TaskDeleteComponent(
                    name = task.name,
                    showDeleteItem = showDeleteItem,
                    onDeleteItem = { onDeleteTask(task) }
                )
            }
        )
    }
}