package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import wottrich.github.io.smartchecklist.R.string
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects.OnShareQuicklyChecklist
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects.SnackbarChecklistDelete
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects.SnackbarError
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects.SnackbarTaskCompleted
import wottrich.github.io.smartchecklist.presentation.state.HomeUiEffects.SnackbarTaskUncompleted
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun HomeScreenEffects(
    snackbarHostState: SnackbarHostState,
    effects: Flow<HomeUiEffects>,
    updateSnackbarColor: (Color) -> Unit,
    onShareQuicklyChecklist: (String) -> Unit,
    onOpenEditChecklistTagsScreen: (String) -> Unit
) {
    val uncompletedTaskMessage = stringResource(id = string.snackbar_uncompleted_task_message)
    val completeTaskMessage = stringResource(id = string.snackbar_completed_task_message)
    val deleteChecklistMessage = stringResource(id = string.snackbar_checklist_deleted)
    val context = LocalContext.current
    val positiveColor = SmartChecklistTheme.colors.status.positive
    val negativeColor = SmartChecklistTheme.colors.status.negative
    LaunchedEffect(key1 = effects) {
        suspend fun showSnackbar(message: String, color: Color, shouldRemoveLastSnackbar: Boolean = false) {
            if (shouldRemoveLastSnackbar) {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
            updateSnackbarColor(color)
            snackbarHostState.showSnackbar(message)
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
                is OnShareQuicklyChecklist -> onShareQuicklyChecklist(it.quicklyChecklistJson)
                is SnackbarError -> {
                    showSnackbar(context.getString(it.errorMessage), negativeColor, true)
                }
                is HomeUiEffects.OpenEditChecklistTagsScreen -> onOpenEditChecklistTagsScreen(it.checklistUuid)
            }
        }
    }
}