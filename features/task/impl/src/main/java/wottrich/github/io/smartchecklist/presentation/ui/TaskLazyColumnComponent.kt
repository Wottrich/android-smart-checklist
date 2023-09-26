package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.task.R

@Composable
fun TaskLazyColumnComponent(
    taskList: List<BaseTaskListItem>,
    onCheckChange: (NewTask) -> Unit,
    onDeleteTask: (NewTask) -> Unit,
    showDeleteItem: Boolean = true
) {
    LazyColumn(
        content = {
            item {
                Column(modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree)) {
                    StyledText(textStyle = MaterialTheme.typography.h5) {
                        Text(text = stringResource(id = R.string.taks_your_tasks_label_header))
                    }
                }
            }
            items(
                count = taskList.size,
                key = { taskList[it].getTaskItemOrNull()?.task?.uuid ?: "Section" },
                contentType = { taskList[it] },
                itemContent = {
                    when (val item = taskList[it]) {
                        is BaseTaskListItem.SectionItem -> TaskSection(sectionItem = item)
                        is BaseTaskListItem.TaskItem -> TaskItem(
                            task = item.task,
                            showDeleteItem = showDeleteItem,
                            onCheckChange = onCheckChange,
                            onDeleteTask = onDeleteTask
                        )
                    }
                }
            )
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
    task: NewTask,
    showDeleteItem: Boolean,
    onCheckChange: (NewTask) -> Unit,
    onDeleteTask: (NewTask) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        TaskComponent(
            task = task,
            showDeleteItem = showDeleteItem,
            onCheckChange = { onCheckChange(task) },
            onDeleteTask = { onDeleteTask(task) }
        )
    }
}