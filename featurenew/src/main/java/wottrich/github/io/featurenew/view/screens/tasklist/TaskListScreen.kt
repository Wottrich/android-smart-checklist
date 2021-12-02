package wottrich.github.io.featurenew.view.screens.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.view.screens.sharedcomponents.TaskListBody
import wottrich.github.io.featurenew.view.screens.sharedcomponents.TaskListHeader

@Composable
fun TaskListScreen(
    checklistId: String,
    viewModel: TaskListViewModel = getViewModel {
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
            viewModel.onAddClicked(textFieldValue)
            textFieldValue = ""
        },
        onCheckChange = {
            viewModel.onUpdateClicked(it)
        },
        onDeleteTask = {
            viewModel.onDeleteClicked(it)
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
            .padding(horizontal = Dimens.BaseFour.SizeThree)
    ) {
        TaskListHeader(textFieldValue, onTextFieldValueChange, onAddItem)
        TaskListBody(
            taskList = taskList,
            onCheckChange = onCheckChange,
            onDeleteTask = onDeleteTask
        )
    }
}