package wottrich.github.io.featurenew.view.screens.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.components.RowComponent
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.ui.Sizes
import wottrich.github.io.components.ui.defaultButtonColors
import wottrich.github.io.components.ui.defaultOutlinedTextFieldColors
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.R

@Composable
fun TaskListScreen(
    checklistId: String,
    viewModel: TaskListComposeViewModel = getViewModel {
        parametersOf(checklistId)
    }
) {

    var textFieldValue by remember { mutableStateOf("") }

    Screen(
        taskList = viewModel.tasks,
        textFieldValue = textFieldValue,
        onTextFieldValueChange = {
            textFieldValue = it
        },
        onAddItem = {
            viewModel.verifyTaskNameToAddItem(textFieldValue)
            textFieldValue = ""
        },
        onCheckChange = {
            viewModel.updateTask(it)
        },
        onDeleteTask = {
            viewModel.deleteTask(it)
        }
    )
}

@Composable
private fun Screen(
    taskList: List<Task>,
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onCheckChange: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxHeight()
            .padding(horizontal = Sizes.x12)
    ) {

        TitleRow(
            modifier = Modifier.padding(vertical = Sizes.x8),
            text = stringResource(id = R.string.task_list_title)
        )

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.task_list_type_task_name_hint))
            },
            colors = defaultOutlinedTextFieldColors()
        )

        Button(
            modifier = Modifier
                .padding(vertical = Sizes.x8)
                .fillMaxWidth(),
            enabled = textFieldValue.isNotEmpty(),
            onClick = onAddItem,
            colors = defaultButtonColors(),
        ) {
            Text(text = stringResource(id = R.string.task_list_add_item))
        }

        LazyColumn(content = {
            items(taskList) { task ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(Sizes.x8))

                    TaskComponent(
                        task = task,
                        onCheckChange = { onCheckChange(task) },
                        onDeleteTask = { onDeleteTask(task) }
                    )
                }
            }
        },
        contentPadding = PaddingValues(vertical = Sizes.x8))
    }
}

@Composable
private fun TaskComponent(
    task: Task,
    onCheckChange: () -> Unit,
    onDeleteTask: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(Sizes.x8),
        elevation = Sizes.x4
    ) {
        RowComponent(
            leftContent = {
                TitleRow(text = task.name)
            },
            rightIconContent = {

                IconCompletableTaskContent(
                    taskName = task.name,
                    isCompletedTask = task.isCompleted
                ) {
                    onCheckChange()
                }

                Icon(
                    tint = MaterialTheme.colors.onSurface,
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = stringResource(id = R.string.task_delete_item, task.name),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onDeleteTask() }
                )
            }
        )
    }
}

@Composable
private fun IconCompletableTaskContent(
    taskName: String,
    isCompletedTask: Boolean,
    onCheckChange: () -> Unit
) {

    val checkIconContentDescription = if (isCompletedTask) {
        R.string.task_click_to_uncheck_item_description
    } else {
        R.string.task_click_to_check_item_description
    }

    val checkIcon = if (isCompletedTask) {
        R.drawable.ic_completed
    } else {
        R.drawable.ic_uncompleted
    }

    Icon(
        tint = MaterialTheme.colors.secondary,
        painter = painterResource(id = checkIcon),
        contentDescription = stringResource(checkIconContentDescription, taskName),
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onCheckChange() }
    )
}