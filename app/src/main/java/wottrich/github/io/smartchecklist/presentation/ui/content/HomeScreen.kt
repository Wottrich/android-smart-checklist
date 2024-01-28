package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DrawerState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.R.string
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.navigation.NavigationHome
import wottrich.github.io.smartchecklist.navigation.NavigatorTask
import wottrich.github.io.smartchecklist.newchecklist.navigation.NavigatorNewChecklist
import wottrich.github.io.smartchecklist.presentation.state.HomeState
import wottrich.github.io.smartchecklist.presentation.state.HomeUiActions
import wottrich.github.io.smartchecklist.presentation.state.HomeUiState
import wottrich.github.io.smartchecklist.presentation.ui.drawer.HomeDrawerStatefulContent
import wottrich.github.io.smartchecklist.presentation.viewmodel.HomeViewModel
import wottrich.github.io.smartchecklist.uisupport.navigation.NavigationSupport

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
    navHostController: NavHostController
) {
    ApplicationTheme {
        Screen(
            onAddNewChecklist = {
                navHostController.navigate(NavigatorNewChecklist.route)
            },
            onChecklistSettings = {
                val route = NavigationHome.Navigation.Settings.route
                navHostController.navigate(route)
            },
            onAboutUsClick = {
                navHostController.navigate(NavigationHome.Destinations.AboutUsScreen.route)
            },
            onHelpClick = {
                navHostController.navigate(NavigationSupport.route)
            },
            onTaskCounterClicked = {
                navHostController.navigate(NavigatorTask.Destinations.CompletableCountBottomSheetScreen.route)
            },
            onOpenSortTaskList = {
                navHostController.navigate(NavigatorTask.Destinations.SortTaskListBottomSheetScreen.route)
            }
        )
    }
}

@Composable
private fun Screen(
    onAddNewChecklist: () -> Unit,
    onChecklistSettings: (checklistId: String) -> Unit,
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onTaskCounterClicked: () -> Unit,
    onOpenSortTaskList: () -> Unit,
    homeViewModel: HomeViewModel = getViewModel()
) {
    val checklistState by homeViewModel.homeStateFlow.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val drawerState = scaffoldState.drawerState
    val coroutineScope = rememberCoroutineScope()

    val defaultColor = SnackbarDefaults.backgroundColor
    var snackbarColor by remember { mutableStateOf(defaultColor) }

    HomeScreenEffects(
        coroutineScope = coroutineScope,
        snackbarHostState = scaffoldState.snackbarHostState,
        effects = homeViewModel.uiEffects,
        updateSnackbarColor = {
            snackbarColor = it
        }
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
                onChecklistSettings = {
                    checklistState.checklist?.uuid?.let {
                        onChecklistSettings(it)
                    }
                },
                homeViewModel = homeViewModel,
                onOpenSortTaskList = onOpenSortTaskList
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
            onAddNewChecklist = onAddNewChecklist,
            onTaskCounterClicked = onTaskCounterClicked,
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
    onChecklistSettings: () -> Unit,
    homeViewModel: HomeViewModel,
    onOpenSortTaskList: () -> Unit
) {
    if (checklistState.shouldShowActionContent()) {
        HomeTopBarActionsContent(
            isEditMode = checklistState.isEditUiState,
            onChecklistSettings = onChecklistSettings,
            onChangeState = { homeViewModel.sendAction(HomeUiActions.Action.OnChangeEditModeAction) },
            onOpenSortTaskList = onOpenSortTaskList
        )
    }
}


@Preview
@Composable
fun Preview() {
}