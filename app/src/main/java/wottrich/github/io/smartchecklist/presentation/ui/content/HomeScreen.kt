package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DrawerState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.R.string
import wottrich.github.io.smartchecklist.presentation.state.HomeState
import wottrich.github.io.smartchecklist.presentation.state.HomeUiActions
import wottrich.github.io.smartchecklist.presentation.state.HomeUiState
import wottrich.github.io.smartchecklist.presentation.state.TopBarHomeUiEffects
import wottrich.github.io.smartchecklist.presentation.ui.content.DeleteAlertDialogState.HIDE
import wottrich.github.io.smartchecklist.presentation.ui.content.DeleteAlertDialogState.SHOW
import wottrich.github.io.smartchecklist.presentation.ui.drawer.HomeDrawerStatefulContent
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeViewModel
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun HomeScreen(
    onAddNewChecklist: () -> Unit,
    onShareText: (String) -> Unit,
    onChecklistSettings: (checklistId: String) -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    ApplicationTheme {
        Screen(
            onAddNewChecklist = onAddNewChecklist,
            onShareText = onShareText,
            onChecklistSettings = onChecklistSettings,
            onAboutUsClick = onAboutUsClick,
            onHelpClick = onHelpClick
        )
    }
}

@Composable
private fun Screen(
    onAddNewChecklist: () -> Unit,
    onShareText: (String) -> Unit,
    onChecklistSettings: (checklistId: String) -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
    homeViewModel: HomeViewModel = getViewModel()
) {
    val checklistState by homeViewModel.homeStateFlow.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val drawerState = scaffoldState.drawerState
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(HIDE) }

    val defaultColor = SnackbarDefaults.backgroundColor
    var snackbarColor by remember { mutableStateOf(defaultColor) }

    HomeScreenEffects(
        coroutineScope = coroutineScope,
        snackbarHostState = scaffoldState.snackbarHostState,
        effects = homeViewModel.uiEffects,
        updateSnackbarColor = {
            snackbarColor = it
        },
        onShareQuicklyChecklist = onShareText
    )

    HomeScaffold(
        scaffoldState = scaffoldState,
        drawerState = drawerState,
        coroutineScope = coroutineScope,
        drawerContent = {
            DrawerContent(
                rememberCoroutineScope = coroutineScope,
                drawerState = drawerState,
                onAddNewChecklist = onAddNewChecklist,
                onAboutUsClick = onAboutUsClick,
                onHelpClick = onHelpClick,
            )
        },
        onTitleContent = {
            TopBarTitleContent(checklistState)
        },
        actionContent = {
            TopBarActionContent(
                checklistState = checklistState,
                onCopyChecklist = onShareText,
                onChecklistSettings = {
                    checklistState.checklist?.uuid?.let {
                        onChecklistSettings(it)
                    }
                },
                onShareQuicklyChecklist = { homeViewModel.sendAction(HomeUiActions.Action.OnShareQuicklyChecklistAction) },
                homeViewModel = homeViewModel,
                onShowDeleteDialog = { showDeleteDialog = SHOW }
            )
        },
        snackbarHost = { snackbarHostState ->
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    backgroundColor = snackbarColor
                )
            }
        }
    ) { paddingValues ->
        HomeContentScreen(
            paddingValues = paddingValues,
            homeViewModel = homeViewModel,
            checklistState = checklistState,
            showDeleteDialog = showDeleteDialog,
            onAddNewChecklist = onAddNewChecklist,
            onHideDeleteDialog = {
                showDeleteDialog = HIDE
            }
        )
    }
}

@Composable
private fun DrawerContent(
    rememberCoroutineScope: CoroutineScope,
    drawerState: DrawerState,
    onAddNewChecklist: () -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    fun closeDrawerState() {
        rememberCoroutineScope.launch {
            drawerState.close()
        }
    }
    HomeDrawerStatefulContent(
        onCloseDrawer = {
            closeDrawerState()
        },
        onAddNewChecklist = {
            onAddNewChecklist()
            closeDrawerState()
        },
        onAboutUsClick = {
            onAboutUsClick()
            closeDrawerState()
        },
        onHelpClick = {
            onHelpClick()
            closeDrawerState()
        },
    )
}

@Composable
private fun TopBarTitleContent(checklistState: HomeState) {
    when {
        checklistState.homeUiState == HomeUiState.Loading -> Unit
        checklistState.checklist == null -> {
            Text(text = stringResource(id = string.label_home_fragment))
        }
        else -> {
            val checklist = checkNotNull(checklistState.checklist)
            Text(text = checklist.name)
        }
    }
}

@Composable
private fun RowScope.TopBarActionContent(
    checklistState: HomeState,
    onCopyChecklist: (String) -> Unit,
    onChecklistSettings: () -> Unit,
    onShareQuicklyChecklist: () -> Unit,
    homeViewModel: HomeViewModel,
    onShowDeleteDialog: () -> Unit
) {
    if (checklistState.shouldShowActionContent()) {
        HomeTopBarActionsEffect(
            effects = homeViewModel.topBarUiEffect,
            onCopyChecklist = onCopyChecklist
        )
        HomeTopBarActionsContent(
            isEditMode = checklistState.isEditUiState,
            onShowDeleteConfirmDialog = onShowDeleteDialog,
            onCopyChecklist = {
                homeViewModel.sendAction(HomeUiActions.Action.OnShareChecklistAsText)
            },
            onChecklistSettings = onChecklistSettings,
            onShareQuicklyChecklist = onShareQuicklyChecklist,
            onChangeState = { homeViewModel.sendAction(HomeUiActions.Action.OnChangeEditModeAction) }
        )
    }
}

@Composable
private fun HomeTopBarActionsEffect(
    effects: Flow<TopBarHomeUiEffects>,
    onCopyChecklist: (String) -> Unit
) {
    LaunchedEffect(key1 = effects) {
        effects.collectLatest {
            when (it) {
                is TopBarHomeUiEffects.ShareChecklistAsText -> onCopyChecklist(it.checklistAsText)
            }
        }
    }
}


@Preview
@Composable
fun Preview() {
}