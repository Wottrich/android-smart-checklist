package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
fun RowScope.HomeTopBarActionsContent(
    isEditMode: Boolean,
    onShowDeleteConfirmDialog: (() -> Unit),
    onShare: () -> Unit,
    onChangeState: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    EditIconStateContent(isEditMode = isEditMode, onChangeState = onChangeState)
    IconButton(onClick = { isExpanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = ""
        )
    }
    DropdownMenuActions(
        isExpanded = isExpanded,
        dismissDropdownMenu = { isExpanded = false },
        onShowDeleteConfirmDialog = onShowDeleteConfirmDialog,
        onShare = onShare
    )
}

@Composable
private fun DropdownMenuActions(
    isExpanded: Boolean,
    dismissDropdownMenu: () -> Unit,
    onShowDeleteConfirmDialog: () -> Unit,
    onShare: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { dismissDropdownMenu() }
    ) {
        DeleteDropdownMenuItem {
            dismissDropdownMenu()
            onShowDeleteConfirmDialog()
        }
        ShareDropdownMenuItem {
            dismissDropdownMenu()
            onShare()
        }
    }
}

@Composable
private fun DeleteDropdownMenuItem(onShowDeleteConfirmDialog: () -> Unit) {
    val onShowDeleteConfirmDialogContentDescription = stringResource(
        id = R.string.checklist_delete_checklist_content_description
    )
    DropdownMenuItem(
        modifier = Modifier.semantics {
            contentDescription = onShowDeleteConfirmDialogContentDescription
        },
        onClick = onShowDeleteConfirmDialog
    ) {
        Text(text = stringResource(id = R.string.checklist_delete_label))
    }
}

@Composable
private fun ShareDropdownMenuItem(onShare: () -> Unit) {
    val onShareClickContentDescription = stringResource(
        id = R.string.checklist_share_content_description
    )
    DropdownMenuItem(
        modifier = Modifier.semantics {
            contentDescription = onShareClickContentDescription
        },
        onClick = onShare
    ) {
        Text(text = stringResource(id = R.string.checklist_share_label))
    }
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