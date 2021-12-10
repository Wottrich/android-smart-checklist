package wottrich.github.io.androidsmartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEffect
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEvent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerViewModel
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
fun HomeDrawerStatefulContent(
    onCloseDrawer: () -> Unit,
    viewModel: HomeDrawerViewModel = getViewModel()
) {
    val state by viewModel.drawerStateFlow.collectAsState()
    val effect by viewModel.drawerEffectFlow.collectAsState(initial = null)

    when (effect) {
        is HomeDrawerEffect.CloseDrawer -> onCloseDrawer()
        null -> Unit
    }

    HomeDrawerStateless(
        state = state,
        onItemClick = {
            viewModel.processEvent(HomeDrawerEvent.ItemClicked(it))
        }
    )

}

@Composable
private fun HomeDrawerStateless(
    state: HomeDrawerState,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit
) {
    when (state) {
        is HomeDrawerState.Loading -> CircularProgressIndicator()
        is HomeDrawerState.Content -> HomeDrawerSuccessContent(
            checklists = state.checklists,
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun HomeDrawerSuccessContent(
    checklists: List<ChecklistWithTasks>,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit
) {
    LazyColumn(content = {
        items(checklists) { item ->
            HomeDrawerChecklistItemComponent(checklist = item.checklist) {
                onItemClick(item)
            }
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