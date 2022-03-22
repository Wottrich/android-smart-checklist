package wottrich.github.io.androidsmartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.androidsmartchecklist.presentation.ui.shared.EditIconStateContent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEffect
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerEvent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeDrawerViewModel
import wottrich.github.io.baseui.TextOneLine
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
        is HomeDrawerEffect.CloseDrawer -> {
            onCloseDrawer()
            viewModel.clearEffect()
        }
        null -> Unit
    }

    HomeDrawerStateless(
        state = state,
        onItemClick = {
            viewModel.processEvent(HomeDrawerEvent.ItemClicked(it))
        },
        onDeleteChecklist = {
            viewModel.processEvent(HomeDrawerEvent.DeleteChecklistClicked(it))
        },
        onAddNewChecklist = onAddNewChecklist,
        onEditMode = {
            viewModel.processEvent(HomeDrawerEvent.EditModeClicked)
        }
    )

}

@Composable
private fun HomeDrawerStateless(
    state: HomeDrawerState,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit,
    onDeleteChecklist: (checklist: ChecklistWithTasks) -> Unit,
    onAddNewChecklist: () -> Unit,
    onEditMode: () -> Unit
) {
    when (state) {
        is HomeDrawerState.Loading -> CircularProgressIndicator()
        is HomeDrawerState.Content -> HomeDrawerSuccessContent(
            checklists = state.checklists,
            isEditModeEnabled = state.isEditing,
            onItemClick = onItemClick,
            onDeleteChecklist = onDeleteChecklist,
            onAddNewChecklist = onAddNewChecklist,
            onEditMode = onEditMode
        )
    }
}

@Composable
private fun HomeDrawerSuccessContent(
    checklists: List<ChecklistWithTasks>,
    isEditModeEnabled: Boolean,
    onItemClick: (checklist: ChecklistWithTasks) -> Unit,
    onDeleteChecklist: (checklist: ChecklistWithTasks) -> Unit,
    onAddNewChecklist: () -> Unit,
    onEditMode: () -> Unit
) {
    val buttonContentDescription = stringResource(id = R.string.floating_action_content_description)
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            content = {
                item {
                    Column {
                        TextOneLine(
                            modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree),
                            primary = {
                                Text(
                                    text = stringResource(
                                        id = R.string.drawer_your_checklists_header_label
                                    )
                                )
                            }
                        )
                    }
                }
                items(checklists) { item ->
                    HomeDrawerChecklistItemComponent(
                        isEditModeEnabled = isEditModeEnabled,
                        checklistWithTasks = item,
                        onItemClick = { onItemClick(item) },
                        onDeleteItemClicked = { onDeleteChecklist(item) }
                    )
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
        Row {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = Dimens.BaseFour.SizeTwo)
                    .semantics {
                        contentDescription = buttonContentDescription
                    },
                onClick = { onAddNewChecklist() },
                colors = defaultButtonColors(),
            ) {
                Text(
                    text = stringResource(
                        id = R.string.new_checklist_activity_screen_title
                    ).uppercase()
                )
            }
            EditableComponent(isEditModeEnabled = isEditModeEnabled, onEditMode = onEditMode)
        }
        Divider(
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Sobre nós")
            }

            TextButton(
                modifier = Modifier.weight(1f),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "Ajuda")
            }
        }
    }
}

@Composable
private fun RowScope.EditableComponent(
    isEditModeEnabled: Boolean,
    onEditMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(all = Dimens.BaseFour.SizeTwo),
        horizontalArrangement = Arrangement.End
    ) {
        EditIconStateContent(
            isEditMode = isEditModeEnabled,
            onChangeState = onEditMode
        )
    }
}