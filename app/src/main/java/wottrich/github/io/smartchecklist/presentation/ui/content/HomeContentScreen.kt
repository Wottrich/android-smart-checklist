package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wottrich.github.io.smartchecklist.presentation.state.HomeState
import wottrich.github.io.smartchecklist.presentation.state.HomeUiActions
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeViewModel

@Composable
fun HomeContentScreen(
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel,
    checklistState: HomeState,
    showDeleteDialog: DeleteAlertDialogState,
    onAddNewChecklist: () -> Unit,
    onHideDeleteDialog: () -> Unit
) {
    Screen(
        paddingValues = paddingValues,
        checklistState = checklistState,
        homeViewModel = homeViewModel,
        onAddNewChecklist = onAddNewChecklist
    )
    DeleteDialog(
        showDeleteDialog,
        homeViewModel,
        onHideDialog = { onHideDeleteDialog() }
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
            onUpdateItemClicked = {
                homeViewModel.sendAction(HomeUiActions.Action.OnShowTaskChangeStatusSnackbar(it))
            },
            onNewChecklistClicked = onAddNewChecklist,
            onEditTagClicked = {
                homeViewModel.sendAction(HomeUiActions.Action.OnEditChecklistTags)
            },
            onError = { homeViewModel.sendAction(HomeUiActions.Action.OnSnackbarError(it)) },
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
            homeViewModel.sendAction(HomeUiActions.Action.DeleteChecklistAction)
            onHideDialog()
        },
        onDismiss = {
            onHideDialog()
        }
    )
}