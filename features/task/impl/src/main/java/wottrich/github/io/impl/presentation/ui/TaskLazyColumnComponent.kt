package wottrich.github.io.impl.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskLazyColumnComponent(
    taskList: List<NewTask>,
    onCheckChange: (NewTask) -> Unit,
    onDeleteTask: (NewTask) -> Unit,
    showDeleteItem: Boolean = true
) {
    val list = taskList.asReversed()
    LazyColumn(
        content = {
            item {
                Column {
                    TextOneLine(
                        modifier = Modifier.padding(all = BaseFour.SizeThree),
                        primary = {
                            Text(
                                text = stringResource(id = R.string.taks_your_tasks_label_header)
                            )
                        }
                    )
                }
            }
            itemsIndexed(
                items = list,
                key = { _, item ->
                    item.uuid
                },
                itemContent = { _, task ->
                    TaskItem(
                        task = task,
                        showDeleteItem = showDeleteItem,
                        onCheckChange = onCheckChange,
                        onDeleteTask = onDeleteTask
                    )
                }
            )
            item {
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTen))
            }
        }
    )
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