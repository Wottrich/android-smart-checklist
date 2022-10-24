package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.impl.presentation.ui.TaskLazyColumnComponent
import wottrich.github.io.quicklychecklist.impl.R
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiEffect.InvalidChecklist
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistViewModel

@Composable
fun QuicklyChecklistScreen(
    quicklyChecklistJson: String,
    onBackPressed: () -> Unit,
    onInvalidChecklist: () -> Unit
) {
    ApplicationTheme {
        Screen(
            quicklyChecklistJson = quicklyChecklistJson,
            onBackPressed = onBackPressed,
            onInvalidChecklist = onInvalidChecklist
        )
    }
}

@Composable
private fun Screen(
    quicklyChecklistJson: String,
    onBackPressed: () -> Unit,
    onInvalidChecklist: () -> Unit,
    viewModel: QuicklyChecklistViewModel = getViewModel {
        parametersOf(quicklyChecklistJson)
    },
) {
    ScreenEffect(viewModel, onInvalidChecklist)
    ScreenContent(viewModel, onBackPressed)
}

@Composable
private fun ScreenEffect(
    viewModel: QuicklyChecklistViewModel,
    onInvalidChecklist: () -> Unit
) {
    val effects = viewModel.effects
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                InvalidChecklist -> onInvalidChecklist()
            }
        }
    }
}

@Composable
private fun ScreenContent(
    viewModel: QuicklyChecklistViewModel,
    onBackPressed: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    Content(showBottomSheet) {
        Scaffold(
            topBar = { TopBarContent(onBackPressed) },
            bottomBar = {
                BottomBarContent {
                    showBottomSheet = false
                }
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                ScaffoldContent(viewModel.tasks, viewModel::onCheckChange)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(showBottomSheet: Boolean, content: @Composable () -> Unit) {
    if (showBottomSheet) {
        BottomSheetScaffold(
            sheetContent = {
                BottomSheetContent()
            }
        ) {
            content()
        }
    } else {
        content()
    }
}

@Composable
private fun ColumnScope.BottomSheetContent() {
    Text("alo")
    Text("alo2")
}

@Composable
private fun TopBarContent(onBackPressed: () -> Unit) {
    IconButton(onClick = onBackPressed) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(id = R.string.arrow_back_content_description),
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
    taskList: List<NewTask>,
    onCheckChange: (NewTask) -> Unit
) {
    TaskLazyColumnComponent(
        taskList = taskList,
        onCheckChange = onCheckChange,
        onDeleteTask = {},
        showDeleteItem = false
    )
}