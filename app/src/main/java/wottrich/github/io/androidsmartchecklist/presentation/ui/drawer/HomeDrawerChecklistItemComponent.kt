package wottrich.github.io.androidsmartchecklist.presentation.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.baseui.ui.ListItem
import wottrich.github.io.baseui.ui.ListItemEndTextContent
import wottrich.github.io.baseui.ui.ListItemStartTextContent
import wottrich.github.io.baseui.ui.RowDefaults
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.database.entity.ChecklistWithTasks

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun HomeDrawerChecklistItemComponent(
    isEditModeEnabled: Boolean,
    checklistWithTasks: ChecklistWithTasks,
    onItemClick: () -> Unit,
    onDeleteItemClicked: () -> Unit
) {
    val checklist = checklistWithTasks.checklist
    val tasks = checklistWithTasks.tasks
    val hasIncompleteItem = tasks.any { !it.isCompleted }
    val numberOfItems = tasks.size
    val numberOfCompleteItems = tasks.filter { it.isCompleted }.size
    val description = "$numberOfCompleteItems / $numberOfItems"
    val modifier = Modifier
        .clickable(enabled = checklist.checklistId != null) {
            onItemClick()
        }

    ItemContent(
        modifier,
        isEditModeEnabled,
        checklist,
        hasIncompleteItem,
        description,
        onDeleteItemClicked
    )
    Divider()
}

@Composable
private fun ItemContent(
    modifier: Modifier,
    isEditModeEnabled: Boolean,
    checklist: Checklist,
    hasIncompleteItem: Boolean,
    description: String,
    onDeleteItemClicked: () -> Unit
) {
    ListItem(
        modifier = modifier.background(Color.Transparent),
        startIcon = {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                DeleteIcon(
                    isEditModeEnabled = isEditModeEnabled,
                    onDeleteItemClicked = onDeleteItemClicked
                )
            }
        },
        startContent = {
            ListItemStartTextContent(
                primary = RowDefaults.title(text = checklist.name),
                secondary = RowDefaults.description(
                    text = getSecondaryText(hasIncompleteItem = hasIncompleteItem),
                    color = getSecondaryColor(hasIncompleteItem = hasIncompleteItem)
                )
            )
        },
        endContent = {
            ListItemEndTextContent(
                primary = RowDefaults.description(text = description)
            )
        },
        endIcon = {
            if (checklist.isSelected) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        }
    )
}

@Composable
private fun getSecondaryText(hasIncompleteItem: Boolean): String {
    val textId = if (hasIncompleteItem) {
        R.string.drawer_checklist_uncompleted
    } else {
        R.string.drawer_checklist_completed
    }
    return stringResource(id = textId)
}

@Composable
private fun getSecondaryColor(hasIncompleteItem: Boolean): Color {
    return if (hasIncompleteItem) {
        SmartChecklistTheme.colors.status.negative
    } else {
        SmartChecklistTheme.colors.status.positive
    }
}

@Composable
private fun RowScope.DeleteIcon(
    isEditModeEnabled: Boolean,
    onDeleteItemClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = isEditModeEnabled
    ) {
        Icon(
            modifier = Modifier.clickable { onDeleteItemClicked() },
            imageVector = Icons.Default.Delete,
            contentDescription = null
        )
    }
}
