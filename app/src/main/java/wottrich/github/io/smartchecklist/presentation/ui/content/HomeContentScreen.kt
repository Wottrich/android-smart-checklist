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
    onAddNewChecklist: () -> Unit,
    onTaskCounterClicked: () -> Unit,
) {
    Screen(
        paddingValues,
        checklistState,
        homeViewModel,
        onAddNewChecklist,
        onTaskCounterClicked
    )
}

@Composable
private fun Screen(
    paddingValues: PaddingValues,
    checklistState: HomeState,
    homeViewModel: HomeViewModel,
    onAddNewChecklist: () -> Unit,
    onTaskCounterClicked: () -> Unit,
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
            onError = { homeViewModel.sendAction(HomeUiActions.Action.OnSnackbarError(it)) },
            onTaskCounterClicked = onTaskCounterClicked
        )
    }
}