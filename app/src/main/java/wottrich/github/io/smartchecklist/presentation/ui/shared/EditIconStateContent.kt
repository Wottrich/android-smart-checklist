package wottrich.github.io.smartchecklist.presentation.ui.shared

import androidx.compose.animation.Crossfade
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.R

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 30/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun EditIconStateContent(
    isEditMode: Boolean,
    onChangeState: () -> Unit
) {
    val contentDescription = if (isEditMode) R.string.checklist_finish_edit_content_description
    else R.string.checklist_edit_checklist_content_description
    IconButton(
        onClick = {
            onChangeState()
        }
    ) {
        Crossfade(targetState = isEditMode, label = "EditIconStateContentAnimation") {
            Icon(
                imageVector = if (it) Icons.Default.Check else Icons.Default.Edit,
                contentDescription = stringResource(id = contentDescription)
            )
        }
    }
}