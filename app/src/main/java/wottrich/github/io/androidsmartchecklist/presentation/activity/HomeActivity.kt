package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import github.io.wottrich.checklist.presentation.activity.NewChecklistActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.*
import wottrich.github.io.androidsmartchecklist.presentation.ui.drawer.HomeDrawerStatefulContent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewState
import wottrich.github.io.baseui.ui.ApplicationTheme

@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val homeViewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                val checklistState by homeViewModel.homeStateFlow.collectAsState()
                val scaffoldState = rememberScaffoldState()
                val drawerState = scaffoldState.drawerState
                val rememberCoroutineScope = rememberCoroutineScope()
                var showDeleteDialog by remember { mutableStateOf(DeleteAlertDialogState.HIDE) }
                HomeScaffold(
                    scaffoldState = scaffoldState,
                    drawerState = drawerState,
                    coroutineScope = rememberCoroutineScope,
                    drawerContent = {
                        HomeDrawerStatefulContent(
                            onCloseDrawer = {
                                rememberCoroutineScope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    },
                    onFloatingActionButtonClick = { startNewChecklistActivity() },
                    onTitleContent = {
                        TopBarTitleContent(checklistState)
                    },
                    actionContent = {
                        HomeTopBarActionsContent(
                            isEditMode = checklistState.isEditViewState,
                            onShowDeleteConfirmDialog = {
                                showDeleteDialog = DeleteAlertDialogState.SHOW
                            },
                            onChangeState = homeViewModel::onChangeEditModeClicked
                        )
                    }
                ) {
                    HomeContentComponent(
                        checklistState = checklistState,
                        tasks = homeViewModel.tasks,
                        onAddItemClicked = homeViewModel::onAddItemClicked,
                        onUpdateItemClicked = homeViewModel::onUpdateItemClicked,
                        onDeleteItemClicked = homeViewModel::onDeleteItemClicked,
                        onNewChecklistClicked = ::startNewChecklistActivity
                    )
                    DeleteAlertDialogContent(
                        deleteAlertDialogState = showDeleteDialog,
                        onConfirmDeleteChecklist = {
                            homeViewModel.onDeleteChecklist()
                            showDeleteDialog = DeleteAlertDialogState.HIDE
                        },
                        onDismiss = {
                            showDeleteDialog = DeleteAlertDialogState.HIDE
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun TopBarTitleContent(checklistState: HomeState) {
        when {
            checklistState.homeViewState == HomeViewState.Loading -> Unit
            checklistState.checklistWithTasks == null -> {
                Text(text = stringResource(id = string.label_home_fragment))
            }
            else -> {
                val checklist =
                    checkNotNull(checklistState.checklistWithTasks.checklist)
                Text(text = checklist.name)
            }
        }
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.launch(this)
    }
}