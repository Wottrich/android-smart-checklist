package wottrich.github.io.smartchecklist.quicklychecklist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.datasource.data.model.Task
// TODO Fix it!
//import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem
//import wottrich.github.io.smartchecklist.presentation.ui.TaskLazyColumnComponent
import wottrich.github.io.smartchecklist.quicklychecklist.R
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.QuicklyChecklistUiEffect.InvalidChecklist
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.QuicklyChecklistUiEffect.OnConfirmQuicklyChecklist
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.states.QuicklyChecklistUiEffect.SnackbarError
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.viewmodels.QuicklyChecklistViewModel
import wottrich.github.io.smartchecklist.baseui.R as BaseUiR

@Composable
fun QuicklyChecklistScreen(
    quicklyChecklistJson: String,
    onBackPressed: () -> Unit,
    onInvalidChecklist: () -> Unit,
    onConfirmBottomSheetEdit: (String) -> Unit
) {
    ApplicationTheme {
        Screen(
            quicklyChecklistJson = quicklyChecklistJson,
            onBackPressed = onBackPressed,
            onInvalidChecklist = onInvalidChecklist,
            onConfirmBottomSheetEdit = onConfirmBottomSheetEdit
        )
    }
}

@Composable
private fun Screen(
    quicklyChecklistJson: String,
    onBackPressed: () -> Unit,
    onInvalidChecklist: () -> Unit,
    onConfirmBottomSheetEdit: (String) -> Unit,
    viewModel: QuicklyChecklistViewModel = getViewModel {
        parametersOf(quicklyChecklistJson)
    },
) {
    val scaffoldState = rememberScaffoldState()
    ScreenEffect(
        scaffoldState = scaffoldState,
        viewModel = viewModel,
        onInvalidChecklist = onInvalidChecklist,
        onConfirmBottomSheetEdit = onConfirmBottomSheetEdit,
    )
    ScreenContent(
        scaffoldState = scaffoldState,
        viewModel = viewModel,
        onBackPressed = onBackPressed
    )
}

@Composable
private fun ScreenEffect(
    scaffoldState: ScaffoldState,
    viewModel: QuicklyChecklistViewModel,
    onInvalidChecklist: () -> Unit,
    onConfirmBottomSheetEdit: (String) -> Unit
) {
    val effects = viewModel.effects
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                InvalidChecklist -> onInvalidChecklist()
                is OnConfirmQuicklyChecklist -> onConfirmBottomSheetEdit(effect.quicklyChecklistJson)
                is SnackbarError ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        context.getString(effect.messageError)
                    )
            }
        }
    }
}

@Composable
private fun ScreenContent(
    scaffoldState: ScaffoldState,
    viewModel: QuicklyChecklistViewModel,
    onBackPressed: () -> Unit,
) {
    QuicklyChecklistScaffold(
        scaffoldState = scaffoldState,
        onBackPressed = onBackPressed,
        viewModel = viewModel
    )
}

@Composable
private fun QuicklyChecklistScaffold(
    scaffoldState: ScaffoldState,
    onBackPressed: () -> Unit,
    viewModel: QuicklyChecklistViewModel
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBarContent(onBackPressed) },
        bottomBar = {
            BottomBarContent {
                viewModel.onConfirmBottomSheetEdit()
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            ScaffoldContent(viewModel.tasks, viewModel::onCheckChange)
        }
    }
}

@Composable
private fun TopBarContent(onBackPressed: () -> Unit) {
    IconButton(onClick = onBackPressed) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(id = BaseUiR.string.arrow_back_content_description),
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
private fun BottomBarContent(onFinishEdit: () -> Unit) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.quickly_checklist_editing_checklist))
        },
        actions = {
            IconButton(
                onClick = onFinishEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(
                        id = R.string.quickly_checklist_finish_editing_content_description
                    )
                )
            }
        }
    )
}

@Composable
private fun ScaffoldContent(
    taskList: List<Task>,
    onCheckChange: (Task) -> Unit
) {
//    TaskLazyColumnComponent(
//        taskList = taskList.map { BaseTaskListItem.TaskItem(it) },
//        onCheckChange = onCheckChange,
//        onDeleteTask = {},
//        showDeleteItem = false
//    )
}