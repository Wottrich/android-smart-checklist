package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import wottrich.github.io.androidsmartchecklist.view.HomeViewState
import wottrich.github.io.featurenew.R.string

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 07/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */
 
@Composable
fun HomeScreen() {

}

@Composable
fun HomeTopBarActionsContent(
    state: HomeViewState,
    onShowDeleteConfirmDialog: (() -> Unit),
    onChangeState: (HomeViewState) -> Unit
) {
    IconButton(onClick = onShowDeleteConfirmDialog) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(
                id = string.checklist_delete_checklist_content_description
            )
        )
    }
    EditIconStateContent(state = state, onChangeState = onChangeState)
}

@Composable
private fun EditIconStateContent(
    state: HomeViewState,
    onChangeState: (HomeViewState) -> Unit
) {
    when (state) {
        HomeViewState.Edit -> {
            IconButton(
                onClick = {
                    onChangeState(HomeViewState.Overview)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(
                        id = string.checklist_finish_edit_content_description
                    )
                )
            }
        }
        HomeViewState.Overview -> {
            IconButton(
                onClick = {
                    onChangeState(HomeViewState.Edit)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(
                        id = string.checklist_edit_checklist_content_description
                    )
                )
            }
        }
        else -> Unit
    }
}