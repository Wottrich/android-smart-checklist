package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiEffects
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiEffects.SnackbarChecklistDelete
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiEffects.SnackbarTaskCompleted
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiEffects.SnackbarTaskUncompleted
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun HomeScreenEffects(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    effects: Flow<HomeUiEffects>,
    updateSnackbarColor: (Color) -> Unit
) {
    val uncompletedTaskMessage = stringResource(id = string.snackbar_uncompleted_task_message)
    val completeTaskMessage = stringResource(id = string.snackbar_completed_task_message)
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
                is SnackbarTaskCompleted -> {
                    val message = "${it.taskName} $completeTaskMessage"
                    showSnackbar(message, positiveColor, true)
                }
                is SnackbarTaskUncompleted -> {
                    val message = "${it.taskName} $uncompletedTaskMessage"
                    showSnackbar(message, negativeColor, true)
                }
                is SnackbarChecklistDelete -> {
                    showSnackbar(deleteChecklistMessage, positiveColor, true)
                }

            }
        }
    }
}