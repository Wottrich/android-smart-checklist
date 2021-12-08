package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
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
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.ui.ChecklistSelectedContent
import wottrich.github.io.androidsmartchecklist.ui.DrawerContent
import wottrich.github.io.androidsmartchecklist.ui.HomeScaffold
import wottrich.github.io.androidsmartchecklist.ui.HomeTopBarActionsContent
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.view.NewChecklistActivity

@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val drawerViewModel by viewModel<DrawerViewModel>()
    private val homeViewModel by viewModel<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                val checklistState by homeViewModel.homeStateFlow.collectAsState()
                val homeDrawerState by drawerViewModel.drawerStateFlow.collectAsState()
                val scaffoldState = rememberScaffoldState()
                val drawerState = scaffoldState.drawerState
                val rememberCoroutineScope = rememberCoroutineScope()
                var showDeleteDialog by remember { mutableStateOf(false) }
                HomeScaffold(
                    scaffoldState = scaffoldState,
                    drawerState = drawerState,
                    coroutineScope = rememberCoroutineScope,
                    drawerContent = {
                        DrawerContent(
                            state = homeDrawerState,
                            onItemClick = {
                                homeViewModel.onChecklistClicked(it)
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
                            state = checklistState.homeViewState,
                            onShowDeleteConfirmDialog = { showDeleteDialog = true },
                            onChangeState = homeViewModel::onChangeState
                        )
                    }
                ) {
                    ChecklistSelectedContent(
                        tasks = homeViewModel.tasks,
                        checklistState = checklistState,
                        onAddItemClicked = homeViewModel::onAddItemClicked,
                        onUpdateItemClicked = homeViewModel::onUpdateItemClicked,
                        onDeleteItemClicked = homeViewModel::onDeleteItemClicked,
                        onNewChecklistClicked = ::startNewChecklistActivity
                    )
                    DeleteAlertDialogContent(
                        showDeleteDialog = showDeleteDialog,
                        onConfirmDeleteChecklist = {
                            homeViewModel.onDeleteChecklist()
                            showDeleteDialog = false
                        },
                        onDismiss = {
                            showDeleteDialog = false
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

    @Composable
    private fun DeleteAlertDialogContent(
        showDeleteDialog: Boolean,
        onConfirmDeleteChecklist: () -> Unit,
        onDismiss: () -> Unit
    ) {
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { onDismiss() },
                title = { Text(stringResource(id = R.string.attention)) },
                text = { Text(text = stringResource(id = R.string.checklist_delete_checklist_confirm_label)) },
                confirmButton = {
                    Button(onClick = { onConfirmDeleteChecklist() }) {
                        Text(text = stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    Button(onClick = { onDismiss() }) {
                        Text(text = stringResource(id = R.string.no))
                    }
                }
            )
        }
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.launch(this)
    }
}