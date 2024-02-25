package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.presentation.ui.shared.EditIconStateContent

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
    onChecklistSettings: () -> Unit,
    onChangeState: () -> Unit,
    onOpenSortTaskList: () -> Unit
) {
    IconButton(onClick = onChecklistSettings) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.checklist_settings_screen_content_description)
        )
    }
    IconButton(onClick = onOpenSortTaskList) {
        Icon(
            imageVector = Icons.Default.List,
            contentDescription = stringResource(id = R.string.checklist_sort_task_option_content_description)
        )
    }
    EditIconStateContent(isEditMode = isEditMode, onChangeState = onChangeState)
}

@Preview(showBackground = true)
@Composable
private fun HomeTopBarActionsContentPreview() {
    Column {
        Row {
            HomeTopBarActionsContent(
                isEditMode = false,
                onChecklistSettings = { },
                onChangeState = { },
                onOpenSortTaskList = { }
            )
        }
        Row {
            HomeTopBarActionsContent(
                isEditMode = true,
                onChecklistSettings = { },
                onChangeState = { },
                onOpenSortTaskList = { }
            )
        }
    }
}