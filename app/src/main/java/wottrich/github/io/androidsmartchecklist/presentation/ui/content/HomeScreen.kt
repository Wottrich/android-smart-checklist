package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogState.HIDE
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogState.SHOW
import wottrich.github.io.androidsmartchecklist.presentation.ui.drawer.HomeDrawerStatefulContent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel

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
    val rememberCoroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(HIDE) }
    HomeScaffold(
        scaffoldState = scaffoldState,
        drawerState = drawerState,
        coroutineScope = rememberCoroutineScope,
        drawerContent = {
            DrawerContent(
                rememberCoroutineScope,
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
            onAddItemClicked = homeViewModel::onAddItemClicked,
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
            Text(text = stringResource(id = string.label_home_fragment))
        }
        else -> {
            val checklist =
                checkNotNull(checklistState.checklistWithTasks.checklist)
            Text(text = checklist.name)
        }
    }
}