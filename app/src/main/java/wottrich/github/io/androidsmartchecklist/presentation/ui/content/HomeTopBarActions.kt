package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import wottrich.github.io.androidsmartchecklist.presentation.ui.shared.EditIconStateContent

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
    onCopyChecklist: () -> Unit,
    onChangeState: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    EditIconStateContent(isEditMode = isEditMode, onChangeState = onChangeState)
    IconButton(onClick = { isExpanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(id = R.string.checklist_more_options_content_description)
        )
    }
    DropdownMenuActions(
        isExpanded = isExpanded,
        dismissDropdownMenu = { isExpanded = false },
        onShowDeleteConfirmDialog = onShowDeleteConfirmDialog,
        onCopyChecklist = onCopyChecklist
    )
}

@Composable
private fun DropdownMenuActions(
    isExpanded: Boolean,
    dismissDropdownMenu: () -> Unit,
    onShowDeleteConfirmDialog: () -> Unit,
    onCopyChecklist: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { dismissDropdownMenu() }
    ) {
        DeleteDropdownMenuItem {
            dismissDropdownMenu()
            onShowDeleteConfirmDialog()
        }
        CopyChecklistDropdownMenuItem {
            dismissDropdownMenu()
            onCopyChecklist()
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
private fun CopyChecklistDropdownMenuItem(onCopyChecklist: () -> Unit) {
    val onShareClickContentDescription = stringResource(
        id = R.string.checklist_copy_content_description
    )
    DropdownMenuItem(
        modifier = Modifier.semantics {
            contentDescription = onShareClickContentDescription
        },
        onClick = onCopyChecklist
    ) {
        Text(text = stringResource(id = R.string.checklist_copy_label))
    }
}