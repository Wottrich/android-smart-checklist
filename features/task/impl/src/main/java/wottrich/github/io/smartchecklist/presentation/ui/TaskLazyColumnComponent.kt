package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.components.checkbox.CheckboxItemComponent
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistSectionWithTask
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.model.TaskComponentModel
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
import wottrich.github.io.smartchecklist.presentation.ui.section.SectionComponent
import wottrich.github.io.smartchecklist.presentation.ui.section.SectionTaskComponentSurface

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskLazyColumnComponent(
    taskList: TaskComponentModel,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showDeleteItem: Boolean = true
) {
    LazyColumn(
        content = {
            items(taskList.tasks) { item ->
                when (item) {
                    is BaseTaskListItem.SectionItem -> TaskSection(sectionItem = item)
                    is BaseTaskListItem.TaskItem ->
                        TaskItem(
                            task = item.task,
                            showDeleteItem = showDeleteItem,
                            onCheckChange = onCheckChange,
                            onDeleteTask = onDeleteTask
                        )
                }
            }
            taskList.sectionChecklists.forEach { embedded ->
                stickyHeader {
                    SectionComponent(embedded.checklistSection.name, embedded.tasks.isEmpty())
                }
                itemsIndexed(embedded.tasks) { index, task ->
                    SectionTaskComponentSurface(isLastTask = index == embedded.tasks.size - 1) {
                        TaskItem(
                            task = task,
                            showDeleteItem = showDeleteItem,
                            onCheckChange = onCheckChange,
                            onDeleteTask = onDeleteTask
                        )
                    }
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
        CheckboxItemComponent(
            label = task.name,
            isCompleted = task.isCompleted,
            onCheckChange = { onCheckChange(task) },
            rightIconContent = {
                TaskDeleteComponent(
                    name = task.name,
                    showDeleteItem = showDeleteItem,
                    onDeleteItem = { onDeleteTask(task) }
                )
            }
        )
    }
}

@Preview
@Composable
private fun TaskLazyColumnComponentPreview() {
    ApplicationTheme {
        Scaffold {
            Box(modifier = Modifier.padding(it)) {
                TaskLazyColumnComponent(
                    taskList = TaskComponentModel(
                        checklist = Checklist(
                            name = "Test Checklist"
                        ),
                        tasks = listOf(
                            BaseTaskListItem.TaskItem(Task(parentUuid = "123", name = "Task 1"))
                        ),
                        sectionChecklists = listOf(
                            ChecklistSectionWithTask(
                                Checklist(name = "Section Checklist"),
                                listOf(
                                    Task(parentUuid = "1234", name = "Section Task"),
                                    Task(parentUuid = "1234", name = "Section Task"),
                                )
                            )
                        )
                    ),
                    {},
                    {},
                    false
                )
            }
        }
    }
}