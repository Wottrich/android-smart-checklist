package wottrich.github.io.publicandroid.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.res.stringResource
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.icons.DeleteIcon
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskLazyColumnComponent(
    taskList: List<Task>,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
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
                    item.hashCode()
                },
                itemContent = { _, task ->
                    val state = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissedToStart) {
                                onDeleteTask(task)
                            }
                            it == DismissedToStart
                        }
                    )

                    SwipeToDismiss(
                        state = state,
                        background = {
                            val color = when (state.dismissDirection) {
                                StartToEnd -> Color.Transparent
                                EndToStart -> SmartChecklistTheme.colors.status.negative
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = color)
                                    .padding(Dimens.BaseFour.SizeTwo)
                            ) {
                                if (color != Color.Transparent) {
                                    DeleteIcon(
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )
                                }
                            }
                        },
                        dismissContent = {
                            TaskItem(
                                task = task,
                                showDeleteItem = showDeleteItem,
                                onCheckChange = onCheckChange,
                                onDeleteTask = onDeleteTask
                            )
                        },
                        directions = setOf(EndToStart)
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
fun TaskItem(
    task: Task,
    showDeleteItem: Boolean,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        TaskItemComponent(
            task = task,
            showDeleteItem = showDeleteItem,
            onCheckChange = { onCheckChange(task) },
            onDeleteTask = { onDeleteTask(task) }
        )
    }
}