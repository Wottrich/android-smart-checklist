package wottrich.github.io.featurenew.view.screens.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
        TaskListHeader(textFieldValue, onTextFieldValueChange, onAddItem)
        TaskListBody(
            taskList = taskList,
            onCheckChange = onCheckChange,
            onDeleteTask = onDeleteTask
        )
    }
}