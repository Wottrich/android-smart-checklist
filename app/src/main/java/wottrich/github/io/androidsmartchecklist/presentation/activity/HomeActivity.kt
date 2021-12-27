package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.io.wottrich.checklist.presentation.activity.NewChecklistActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogContent
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.DeleteAlertDialogState
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.HomeContentComponent
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.HomeScaffold
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.HomeTopBarActionsContent
import wottrich.github.io.androidsmartchecklist.presentation.ui.drawer.HomeDrawerStatefulContent
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeUiState
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.HomeViewModel
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
                            },
                            onAddNewChecklist = {
                                startNewChecklistActivity()
                                rememberCoroutineScope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    },
                    onTitleContent = {
                        TopBarTitleContent(checklistState)
                    },
                    actionContent = {
                        if (checklistState.shouldShowActionContent()) {
                            HomeTopBarActionsContent(
                                isEditMode = checklistState.isEditUiState,
                                onShowDeleteConfirmDialog = {
                                    showDeleteDialog = DeleteAlertDialogState.SHOW
                                },
                                onShare = {
                                    homeViewModel.onShareChecklist()
                                },
                                onChangeState = homeViewModel::onChangeEditModeClicked
                            )
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.padding(it)
                    ) {
                        HomeContentComponent(
                            checklistState = checklistState,
                            tasks = homeViewModel.tasks,
                            onAddItemClicked = homeViewModel::onAddItemClicked,
                            onUpdateItemClicked = homeViewModel::onUpdateItemClicked,
                            onDeleteItemClicked = homeViewModel::onDeleteItemClicked,
                            onNewChecklistClicked = ::startNewChecklistActivity
                        )
                    }
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
            checklistState.homeUiState == HomeUiState.Loading -> Unit
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