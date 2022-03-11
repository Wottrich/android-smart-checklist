package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogState.HIDE
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel

@Composable
fun HomeTaskListScreen(
    it: PaddingValues,
    checklistState: HomeState,
    homeViewModel: HomeViewModel,
    onAddNewChecklist: () -> Unit,
    showDeleteDialog: DeleteAlertDialogState
) {
    var showDeleteDialog1 = showDeleteDialog
    Screen(it, checklistState, homeViewModel, onAddNewChecklist)
    DeleteDialog(
        showDeleteDialog1,
        homeViewModel,
        onHideDialog = { showDeleteDialog1 = HIDE }
    )
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