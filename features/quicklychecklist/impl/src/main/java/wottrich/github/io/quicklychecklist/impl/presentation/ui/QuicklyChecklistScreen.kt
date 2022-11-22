package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.impl.presentation.ui.TaskLazyColumnComponent
import wottrich.github.io.quicklychecklist.impl.R
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiEffect.InvalidChecklist
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiEffect.OnSaveNewChecklist
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiEffect.OnShareChecklistBack
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistUiEffect.ShowSnackbar
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistViewModel

@Composable
fun QuicklyChecklistScreen(
    quicklyChecklistJson: String,
    onShareBackClick: (String) -> Unit,
    onBackPressed: () -> Unit,
    onInvalidChecklist: () -> Unit
) {
    ApplicationTheme {
        Screen(
            quicklyChecklistJson = quicklyChecklistJson,
            onShareBackClick = onShareBackClick,
            onBackPressed = onBackPressed,
            onInvalidChecklist = onInvalidChecklist
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Screen(
    quicklyChecklistJson: String,
    onShareBackClick: (String) -> Unit,
    onBackPressed: () -> Unit,
    onInvalidChecklist: () -> Unit,
    viewModel: QuicklyChecklistViewModel = getViewModel {
        parametersOf(quicklyChecklistJson)
    },
) {
    val scaffoldState = rememberScaffoldState()
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden) { modalBottomSheetValue ->
            if (modalBottomSheetValue == ModalBottomSheetValue.Hidden) {
                viewModel.setSaveChecklistBottomSheetFalse()
            }
            true
        }
    val coroutineScope = rememberCoroutineScope()
    var quicklyChecklist: MutableState<QuicklyChecklist?> = remember {
        mutableStateOf(null)
    }
    ScreenEffect(
        scaffoldState = scaffoldState,
        viewModel = viewModel,
        onInvalidChecklist = onInvalidChecklist,
        onShareBackClick = onShareBackClick,
        onSaveNewChecklist = {
            quicklyChecklist.value = it
            coroutineScope.launch {
                bottomSheetState.show()
            }
        }
    )
    ScreenContent(
        scaffoldState = scaffoldState,
        bottomSheetState = bottomSheetState,
        coroutineScope = coroutineScope,
        quicklyChecklist = quicklyChecklist.value,
        viewModel = viewModel,
        onBackPressed = onBackPressed
    )
}

@Composable
private fun ScreenEffect(
    scaffoldState: ScaffoldState,
    viewModel: QuicklyChecklistViewModel,
    onInvalidChecklist: () -> Unit,
    onShareBackClick: (String) -> Unit,
    onSaveNewChecklist: (QuicklyChecklist) -> Unit,
) {
    val effects = viewModel.effects
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                InvalidChecklist -> onInvalidChecklist()
                is OnShareChecklistBack -> onShareBackClick(effect.quicklyChecklistJson)
                is OnSaveNewChecklist -> onSaveNewChecklist(effect.quicklyChecklist)
                is ShowSnackbar ->
                    scaffoldState.snackbarHostState.showSnackbar(
                        context.getString(effect.message)
                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ScreenContent(
    scaffoldState: ScaffoldState,
    bottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    quicklyChecklist: QuicklyChecklist?,
    viewModel: QuicklyChecklistViewModel,
    onBackPressed: () -> Unit,
) {
    val isSaveChecklistBottomSheet by viewModel.isSaveChecklistBottomSheet
    fun hideBottomSheet() {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }
    ModalBottomSheetContents(
        bottomSheetState = bottomSheetState,
        bottomSheetContent = {
            if (isSaveChecklistBottomSheet) {
                QuicklyChecklistAddNewChecklistBottomSheetContent(
                    quicklyChecklist = quicklyChecklist,
                    onConfirmButtonClicked = {
                        hideBottomSheet()
                        viewModel.setSaveChecklistBottomSheetFalse(true)
                    }
                )
            } else {
                QuicklyChecklistConfirmBottomSheetContent(
                    hasExistentChecklist = false,
                    onShareBackClick = {
                        hideBottomSheet()
                        viewModel.onShareChecklistBackClick()
                    },
                    onSaveChecklist = {
                        viewModel.onSaveNewChecklist()
                    },
                    onReplaceExistentChecklist = {
                        hideBottomSheet()
                    }
                )
            }
        },
        content = {
            QuicklyChecklistScaffold(
                scaffoldState = scaffoldState,
                onBackPressed = onBackPressed,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                viewModel = viewModel
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun QuicklyChecklistScaffold(
    scaffoldState: ScaffoldState,
    onBackPressed: () -> Unit,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    viewModel: QuicklyChecklistViewModel
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBarContent(onBackPressed) },
        bottomBar = {
            BottomBarContent {
                coroutineScope.launch {
                    bottomSheetState.show()
                }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ModalBottomSheetContents(
    bottomSheetState: ModalBottomSheetState,
    bottomSheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = bottomSheetContent,
        content = content
    )
}