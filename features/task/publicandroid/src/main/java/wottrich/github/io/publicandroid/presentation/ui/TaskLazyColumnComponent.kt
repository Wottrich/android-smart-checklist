package wottrich.github.io.publicandroid.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.database.entity.Task

@Composable
fun TaskLazyColumnComponent(
    taskList: List<Task>,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    showDeleteItem: Boolean = true
) {
    LazyColumn(
        content = {
            items(taskList) { task ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))

                    TaskItemComponent(
                        task = task,
                        showDeleteItem = showDeleteItem,
                        onCheckChange = { onCheckChange(task) },
                        onDeleteTask = { onDeleteTask(task) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
            }
        }
    )
}