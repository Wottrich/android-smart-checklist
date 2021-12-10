package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import wottrich.github.io.androidsmartchecklist.R

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 07/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun HomeTopBarActionsContent(
    isEditMode: Boolean,
    onShowDeleteConfirmDialog: (() -> Unit),
    onChangeState: () -> Unit
) {
    IconButton(onClick = onShowDeleteConfirmDialog) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(
                id = R.string.checklist_delete_checklist_content_description
            )
        )
    }
    EditIconStateContent(isEditMode = isEditMode, onChangeState = onChangeState)
}

@Composable
private fun EditIconStateContent(
    isEditMode: Boolean,
    onChangeState: () -> Unit
) {
    when (isEditMode) {
        true -> {
            IconButton(
                onClick = {
                    onChangeState()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(
                        id = R.string.checklist_finish_edit_content_description
                    )
                )
            }
        }
        false -> {
            IconButton(
                onClick = {
                    onChangeState()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(
                        id = R.string.checklist_edit_checklist_content_description
                    )
                )
            }
        }
    }
}