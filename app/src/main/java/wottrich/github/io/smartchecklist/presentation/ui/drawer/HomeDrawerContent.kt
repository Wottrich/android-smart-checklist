package wottrich.github.io.smartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.smartchecklist.presentation.ui.shared.EditIconStateContent
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEffect
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerEvent
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerState
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeDrawerViewModel

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

    ApplicationTheme {
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
    Scaffold { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(innerPaddings)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                content = {
                    header(isEditModeEnabled, onEditMode)
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
            AddChecklistButtonContent(onAddNewChecklist = onAddNewChecklist)
            Divider(modifier = Modifier.fillMaxWidth())
            HelpAboutUsContent(
                onAboutUsClick = onAboutUsClick,
                onHelpClick = onHelpClick
            )
        }
    }
}

private fun LazyListScope.header(
    isEditModeEnabled: Boolean,
    onEditMode: () -> Unit
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextOneLine(
                modifier = Modifier
                    .padding(all = Dimens.BaseFour.SizeThree)
                    .weight(1f),
                primary = {
                    Text(
                        text = stringResource(
                            id = R.string.drawer_your_checklists_header_label
                        )
                    )
                }
            )
            EditableComponent(isEditModeEnabled = isEditModeEnabled, onEditMode = onEditMode)
        }
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
private fun EditableComponent(
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