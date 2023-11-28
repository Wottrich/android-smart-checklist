package wottrich.github.io.smartchecklist.presentation.ui.drawer

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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.smartchecklist.presentation.ui.shared.EditIconStateContent
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEffect
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEvent
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerState
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerViewModel
import wottrich.github.io.smartchecklist.baseui.R as BaseUiR
import wottrich.github.io.smartchecklist.checklist.R as ChecklistR

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
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
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
        },
        onAboutUsClick = onAboutUsClick,
        onHelpClick = onHelpClick,
    )

}

@Composable
private fun HomeDrawerStateless(
    state: HomeDrawerState,
    onItemClick: (checklist: HomeDrawerChecklistItemModel) -> Unit,
    onDeleteChecklist: (checklist: HomeDrawerChecklistItemModel) -> Unit,
    onAddNewChecklist: () -> Unit,
    onEditMode: () -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    when (state) {
        is HomeDrawerState.Loading -> CircularProgressIndicator()
        is HomeDrawerState.Content -> HomeDrawerSuccessContent(
            checklists = state.checklists,
            isEditModeEnabled = state.isEditing,
            onItemClick = onItemClick,
            onDeleteChecklist = onDeleteChecklist,
            onAddNewChecklist = onAddNewChecklist,
            onEditMode = onEditMode,
            onAboutUsClick = onAboutUsClick,
            onHelpClick = onHelpClick,
        )
    }
}

@Composable
private fun HomeDrawerSuccessContent(
    checklists: List<HomeDrawerChecklistItemModel>,
    isEditModeEnabled: Boolean,
    onItemClick: (checklist: HomeDrawerChecklistItemModel) -> Unit,
    onDeleteChecklist: (checklist: HomeDrawerChecklistItemModel) -> Unit,
    onAddNewChecklist: () -> Unit,
    onEditMode: () -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
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
                checklistItems(
                    checklists = checklists,
                    isEditModeEnabled = isEditModeEnabled,
                    onItemClick = onItemClick,
                    onDeleteChecklist = onDeleteChecklist
                )
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
            SmartChecklistButton(
                modifier = Modifier.weight(1f),
                onClick = onAddNewChecklist,
                buttonContentDescription = buttonContentDescription
            ) {
                Text(
                    text = stringResource(
                        id = ChecklistR.string.new_checklist_activity_screen_title
                    ).uppercase()
                )
            }
            EditableComponent(
                isEditModeEnabled = isEditModeEnabled,
                onEditMode = onEditMode
            )
        }
        Divider(
            modifier = Modifier.fillMaxWidth()
        )
        HelpAboutUsContent(
            onAboutUsClick = onAboutUsClick,
            onHelpClick = onHelpClick
        )
    }
}

private fun LazyListScope.checklistItems(
    checklists: List<HomeDrawerChecklistItemModel>,
    isEditModeEnabled: Boolean,
    onItemClick: (checklist: HomeDrawerChecklistItemModel) -> Unit,
    onDeleteChecklist: (checklist: HomeDrawerChecklistItemModel) -> Unit
) {
    items(
        items = checklists,
        key = { it.checklistUuid }
    ) { item ->
        Column(modifier = Modifier.padding(horizontal = Dimens.BaseFour.SizeThree)) {
            Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
            HomeDrawerChecklistItemComponent(
                isEditModeEnabled = isEditModeEnabled,
                checklistItemModel = item,
                onItemClick = { onItemClick(item) },
                onDeleteItemClicked = { onDeleteChecklist(item) }
            )
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

@Composable
private fun RowScope.QuicklyChecklistIcon(
    onQuicklyChecklist: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(all = Dimens.BaseFour.SizeTwo),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                onQuicklyChecklist()
            }
        ) {
            Icon(
                painter = painterResource(id = BaseUiR.drawable.ic_quickly_checklist_24),
                contentDescription = stringResource(
                    id = R.string.checklist_finish_edit_content_description
                )
            )
        }
    }
}