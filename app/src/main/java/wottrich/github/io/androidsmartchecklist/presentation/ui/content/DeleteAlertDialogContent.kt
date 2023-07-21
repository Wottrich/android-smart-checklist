package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.baseui.R as BaseUiR

enum class DeleteAlertDialogState {
    SHOW, HIDE;
}

@Composable
fun DeleteAlertDialogContent(
    deleteAlertDialogState: DeleteAlertDialogState,
    onConfirmDeleteChecklist: () -> Unit,
    onDismiss: () -> Unit
) {
    if (deleteAlertDialogState == DeleteAlertDialogState.SHOW) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(stringResource(id = BaseUiR.string.attention)) },
            text = { Text(text = stringResource(id = R.string.checklist_delete_checklist_confirm_label)) },
            confirmButton = {
                Button(onClick = { onConfirmDeleteChecklist() }) {
                    Text(text = stringResource(id = BaseUiR.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { onDismiss() }) {
                    Text(text = stringResource(id = BaseUiR.string.no))
                }
            }
        )
    }
}