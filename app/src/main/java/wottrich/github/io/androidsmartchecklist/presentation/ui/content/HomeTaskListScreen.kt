package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import wottrich.github.io.androidsmartchecklist.presentation.ui.pressProgressionInteractionState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel

@Composable
fun HomeTaskListScreen(
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel,
    checklistState: HomeState,
    showDeleteDialog: DeleteAlertDialogState,
    onAddNewChecklist: () -> Unit,
    onHideDeleteDialog: () -> Unit,
) {
    Screen(paddingValues, checklistState, homeViewModel, onAddNewChecklist)
    DeleteDialog(
        showDeleteDialog,
        homeViewModel,
        onHideDialog = { onHideDeleteDialog() }
    )
}

@OptIn(ExperimentalFoundationApi::class)
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
        Column {
            val interaction = remember { MutableInteractionSource() }
            val interactions = interaction.interactions
            val progress by pressProgressionInteractionState(
                interactions = interactions,
                initialTimeInMillis = 0,
                timePressingToFinishInMillis = 1000,
                onFinishTimePressing = {
                    println("Delete item")
                }
            )
            CircularProgressIndicator(progress = progress)
            Button(
                interactionSource = interaction,
                onClick = { /*TODO*/ }
            ) {
              Text("Button")
            }
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