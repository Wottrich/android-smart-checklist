package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogState.HIDE
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogState.SHOW
import wottrich.github.io.androidsmartchecklist.presentation.ui.drawer.HomeDrawerStatefulContent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiEffects
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun HomeScreen(
    onAddNewChecklist: () -> Unit,
    onCopyChecklist: (String) -> Unit,
    homeViewModel: HomeViewModel = getViewModel()
) {
    val checklistState by homeViewModel.homeStateFlow.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val drawerState = scaffoldState.drawerState
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(HIDE) }

    val defaultColor = SnackbarDefaults.backgroundColor
    var snackbarColor by remember { mutableStateOf(defaultColor) }

    Effects(
        coroutineScope = coroutineScope,
        snackbarHostState = scaffoldState.snackbarHostState,
        effects = homeViewModel.uiEffects
    ) {
        snackbarColor = it
    }

    HomeScaffold(
        scaffoldState = scaffoldState,
        drawerState = drawerState,
        coroutineScope = coroutineScope,
        drawerContent = {
            DrawerContent(
                coroutineScope,
                drawerState,
                onAddNewChecklist
            )
        },
        onTitleContent = {
            TopBarTitleContent(checklistState)
        },
        actionContent = {
            TopBarActionContent(
                checklistState,
                onCopyChecklist,
                homeViewModel,
                onShowDeleteDialog = { showDeleteDialog = SHOW }
            )
        },
        snackbarHost = { snackbarHostState ->
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    backgroundColor = snackbarColor
                )
            }
        }
    ) {
        Screen(it, checklistState, homeViewModel, onAddNewChecklist)
        DeleteDialog(
            showDeleteDialog,
            homeViewModel,
            onHideDialog = { showDeleteDialog = HIDE }
        )
    }
}

@Composable
private fun Effects(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    effects: Flow<HomeUiEffects>,
    updateSnackbarColor: (Color) -> Unit
) {
    val uncompletedTaskMessage = stringResource(id = R.string.snackbar_uncompleted_task_message)
    val completeTaskMessage = stringResource(id = R.string.snackbar_completed_task_message)
    val deleteChecklistMessage = stringResource(id = string.snackbar_checklist_deleted)
    val positiveColor = SmartChecklistTheme.colors.status.positive
    val negativeColor = SmartChecklistTheme.colors.status.negative
    LaunchedEffect(key1 = effects) {
        fun showSnackbar(message: String, color: Color, shouldRemoveLastSnackbar: Boolean = false) {
            if (shouldRemoveLastSnackbar) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            updateSnackbarColor(color)
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }

        effects.collect {
            when (it) {
                is HomeUiEffects.SnackbarTaskCompleted -> {
                    val message = "${it.taskName} $completeTaskMessage"
                    showSnackbar(message, positiveColor, true)
                }
                is HomeUiEffects.SnackbarTaskUncompleted -> {
                    val message = "${it.taskName} $uncompletedTaskMessage"
                    showSnackbar(message, negativeColor, true)
                }
                is HomeUiEffects.SnackbarChecklistDelete -> {
                    showSnackbar(deleteChecklistMessage, positiveColor, true)
                }

            }
        }
    }
}

@Composable
private fun DrawerContent(
    rememberCoroutineScope: CoroutineScope,
    drawerState: DrawerState,
    onAddNewChecklist: () -> Unit
) {
    HomeDrawerStatefulContent(
        onCloseDrawer = {
            rememberCoroutineScope.launch {
                drawerState.close()
            }
        },
        onAddNewChecklist = {
            onAddNewChecklist()
            rememberCoroutineScope.launch {
                drawerState.close()
            }
        }
    )
}

@Composable
private fun RowScope.TopBarActionContent(
    checklistState: HomeState,
    onCopyChecklist: (String) -> Unit,
    homeViewModel: HomeViewModel,
    onShowDeleteDialog: () -> Unit
) {
    if (checklistState.shouldShowActionContent()) {
        HomeTopBarActionsContent(
            isEditMode = checklistState.isEditUiState,
            onShowDeleteConfirmDialog = onShowDeleteDialog,
            onCopyChecklist = {
                onCopyChecklist(checklistState.checklistWithTasks.toString())
            },
            onChangeState = homeViewModel::onChangeEditModeClicked
        )
    }
}

@Composable
private fun Screen(
    paddingValues: PaddingValues,
    checklistState: HomeState,
    homeViewModel: HomeViewModel,
    onAddNewChecklist: () -> Unit
) {
    Box(
        modifier = Modifier.padding(paddingValues)
    ) {
        HomeContentComponent(
            checklistState = checklistState,
            tasks = homeViewModel.tasks,
            onAddItemClicked = homeViewModel::onAddItemButtonClicked,
            onUpdateItemClicked = homeViewModel::onUpdateItemClicked,
            onDeleteItemClicked = homeViewModel::onDeleteItemClicked,
            onNewChecklistClicked = onAddNewChecklist
        )
    }
}

@Composable
private fun DeleteDialog(
    showDeleteDialog: DeleteAlertDialogState,
    homeViewModel: HomeViewModel,
    onHideDialog: () -> Unit
) {
    DeleteAlertDialogContent(
        deleteAlertDialogState = showDeleteDialog,
        onConfirmDeleteChecklist = {
            homeViewModel.onDeleteChecklist()
            onHideDialog()
        },
        onDismiss = {
            onHideDialog()
        }
    )
}

@Composable
private fun TopBarTitleContent(checklistState: HomeState) {
    when {
        checklistState.homeUiState == HomeUiState.Loading -> Unit
        checklistState.checklistWithTasks == null -> {
            Text(text = stringResource(id = R.string.label_home_fragment))
        }
        else -> {
            val checklist =
                checkNotNull(checklistState.checklistWithTasks.checklist)
            Text(text = checklist.name)
        }
    }
}

@Preview
@Composable
fun Preview() {}