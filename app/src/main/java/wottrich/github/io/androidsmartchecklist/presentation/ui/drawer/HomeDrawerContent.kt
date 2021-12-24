package wottrich.github.io.androidsmartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEffect
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEvent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerViewModel
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.color.defaultButtonColors
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
    onAddNewChecklist: () -> Unit,
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
        },
        onAddNewChecklist = onAddNewChecklist
    )

}

@Composable
private fun HomeDrawerStateless(
    state: HomeDrawerState,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit,
    onAddNewChecklist: () -> Unit
) {
    when (state) {
        is HomeDrawerState.Loading -> CircularProgressIndicator()
        is HomeDrawerState.Content -> HomeDrawerSuccessContent(
            checklists = state.checklists,
            onItemClick = onItemClick,
            onAddNewChecklist = onAddNewChecklist
        )
    }
}

@Composable
private fun HomeDrawerSuccessContent(
    checklists: List<ChecklistWithTasks>,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit,
    onAddNewChecklist: () -> Unit
) {
    val buttonContentDescription = stringResource(id = R.string.floating_action_content_description)
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            content = {
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
            }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = Dimens.BaseFour.SizeTwo)
                .semantics {
                    contentDescription = buttonContentDescription
                },
            onClick = { onAddNewChecklist() },
            colors = defaultButtonColors(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_round_add),
                contentDescription = stringResource(
                    id = R.string.floating_action_content_description
                ),
                tint = Color.White
            )
        }
    }

}