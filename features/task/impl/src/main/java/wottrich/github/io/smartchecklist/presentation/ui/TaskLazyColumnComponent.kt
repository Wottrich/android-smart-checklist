package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.components.checkbox.CheckboxItemComponent
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.fonts.robotoFontFamily
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.model.TaskComponentModel
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem

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
                    Column(
                        modifier = Modifier
                            .padding(top = Dimens.BaseFour.SizeTwo)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = Dimens.BaseFour.SizeOne)
                                .clip(RoundedCornerShape(Dimens.BaseFour.SizeFour))
                                .background(MaterialTheme.colors.surface),
                        ) {
                            Text(
                                modifier = Modifier.padding(
                                    vertical = Dimens.BaseFour.SizeTwo,
                                    horizontal = Dimens.BaseFour.SizeFour
                                ),
                                text = embedded.checklistSection.name,
                                fontFamily = robotoFontFamily,
                                fontWeight = FontWeight.Thin,
                                fontSize = Dimens.BaseFour.SizeFive.value.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                items(embedded.tasks) { task ->
                    TaskItem(
                        task = task,
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