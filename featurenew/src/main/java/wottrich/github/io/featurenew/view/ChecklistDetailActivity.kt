package wottrich.github.io.featurenew.view

//import android.app.Activity
//import android.os.Bundle
//import androidx.activity.compose.setContent
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.material.AlertDialog
//import androidx.compose.material.Button
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.Scaffold
//import androidx.compose.material.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.res.stringResource
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import org.koin.core.parameter.parametersOf
//import wottrich.github.io.baseui.SingleRow
//import wottrich.github.io.baseui.TopBarContent
//import wottrich.github.io.baseui.ui.ApplicationTheme
//import wottrich.github.io.featurenew.R
//import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistDetailViewModel
//import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListComponent
//import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListState
//import wottrich.github.io.tools.extensions.startActivity

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

//class ChecklistDetailActivity : AppCompatActivity() {
//
//    private val viewModel by viewModel<ChecklistDetailViewModel> {
//        parametersOf(getChecklistId())
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            ApplicationTheme {
//                var showDeleteDialog by remember { mutableStateOf(false) }
//                val screenState by viewModel.stateScreen.collectAsState()
//                if (screenState == TaskListState.Delete) {
//                    finish()
//                }
//                Scaffold(
//                    topBar = {
//                        TopBarContent(
//                            title = {
//                                SingleRow(primary = { Text(text = getChecklistName()) })
//                            },
//                            navigationIcon = {
//                                Icon(
//                                    imageVector = Icons.Default.ArrowBack,
//                                    contentDescription = stringResource(
//                                        id = R.string.arrow_back_content_description
//                                    )
//                                )
//                            },
//                            navigationIconAction = ::onBackPressed,
//                            actionsContent = {
//                                TopBarActionsContent(
//                                    screenState = screenState,
//                                    onDelete = { showDeleteDialog = true }
//                                )
//                            }
//                        )
//                    }
//                ) {
//                    TaskListComponent(
//                        state = screenState,
//                        tasks = viewModel.tasks,
//                        onAddClicked = viewModel::onAddClicked,
//                        onUpdateClicked = viewModel::onUpdateClicked,
//                        onDeleteClicked = viewModel::onDeleteClicked
//                    )
//                }
//                DeleteAlertDialogContent(
//                    showDeleteDialog = showDeleteDialog,
//                    onDismiss = {
//                        showDeleteDialog = false
//                    }
//                )
//            }
//        }
//    }
//
//    @Composable
//    private fun TopBarActionsContent(
//        screenState: TaskListState,
//        onDelete: (() -> Unit)
//    ) {
//        IconButton(onClick = onDelete) {
//            Icon(
//                imageVector = Icons.Default.Delete,
//                contentDescription = stringResource(
//                    id = R.string.checklist_delete_checklist_content_description
//                )
//            )
//        }
//        EditIconStateContent(screenState)
//    }
//
//    @Composable
//    private fun EditIconStateContent(state: TaskListState) {
//        when (state) {
//            TaskListState.Edit -> {
//                IconButton(
//                    onClick = {
//                        viewModel.changeState(TaskListState.Overview)
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Check,
//                        contentDescription = stringResource(
//                            id = R.string.checklist_finish_edit_content_description
//                        )
//                    )
//                }
//            }
//            TaskListState.Overview -> {
//                IconButton(
//                    onClick = {
//                        viewModel.changeState(TaskListState.Edit)
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Edit,
//                        contentDescription = stringResource(
//                            id = R.string.checklist_edit_checklist_content_description
//                        )
//                    )
//                }
//            }
//            else -> Unit
//        }
//    }
//
//    @Composable
//    private fun DeleteAlertDialogContent(showDeleteDialog: Boolean, onDismiss: () -> Unit) {
//        if (showDeleteDialog) {
//            AlertDialog(
//                onDismissRequest = { onDismiss() },
//                title = { Text(stringResource(id = R.string.attention)) },
//                text = { Text(text = stringResource(id = R.string.checklist_delete_checklist_confirm_label)) },
//                confirmButton = {
//                    Button(onClick = { viewModel.deleteChecklist() }) {
//                        Text(text = stringResource(id = R.string.yes))
//                    }
//                },
//                dismissButton = {
//                    Button(onClick = { onDismiss() }) {
//                        Text(text = stringResource(id = R.string.no))
//                    }
//                }
//            )
//        }
//    }
//
//    private fun getChecklistId() =
//        intent.getStringExtra(CHECKLIST_ID).orEmpty()
//
//    private fun getChecklistName() =
//        intent.getStringExtra(CHECKLIST_NAME).orEmpty()
//
//    companion object {
//        private const val CHECKLIST_ID = "CHECKLIST_ID"
//        private const val CHECKLIST_NAME = "CHECKLIST_NAME"
//
//        fun launch(activity: Activity, checklistId: String, checklistName: String) {
//            activity.startActivity<ChecklistDetailActivity> {
//                this.putExtra(CHECKLIST_ID, checklistId)
//                this.putExtra(CHECKLIST_NAME, checklistName)
//            }
//        }
//    }
//
//}