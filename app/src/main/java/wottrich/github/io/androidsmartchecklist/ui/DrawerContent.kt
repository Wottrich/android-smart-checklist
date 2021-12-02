package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wottrich.github.io.androidsmartchecklist.view.HomeDrawerState
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
fun DrawerContent(
    state: HomeDrawerState,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit
) {
    when {
        state.isLoading -> CircularProgressIndicator()
        else -> HomeDrawerSuccessContent(checklists = state.checklists, onItemClick = onItemClick)
    }
}

@Composable
private fun HomeDrawerSuccessContent(
    checklists: List<ChecklistWithTasks>,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit
) {
    LazyColumn(content = {
        items(checklists) { item ->
            val itemComposable = remember<@Composable () -> Unit>(item.checklist.checklistId) {
                {
                    ChecklistTaskItem(checklist = item.checklist) {
                        onItemClick(item)
                    }
                }
            }
            itemComposable()
        }
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    })
}