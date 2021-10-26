package wottrich.github.io.featurenew.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.TopBarContent
import wottrich.github.io.components.ui.ApplicationTheme
import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistDetailScreen
import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistDetailState
import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistDetailViewModel
import wottrich.github.io.tools.extensions.startActivity

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class ChecklistDetailActivity : AppCompatActivity() {

    private val viewModel by viewModel<ChecklistDetailViewModel> {
        parametersOf(getChecklistId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                var showDeleteDialog by remember { mutableStateOf(false) }
                val state by viewModel.stateScreen.collectAsState()
                if (state == ChecklistDetailState.Delete) {
                    finish()
                }
                Scaffold(
                    topBar = {
                        TopBarContent(
                            title = {
                                TitleRow(text = getChecklistName())
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            },
                            navigationIconAction = ::onBackPressed,
                            actionsContent = {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar"
                                    )
                                }
                                EditIconStateContent(state)
                            }
                        )
                    }
                ) {
                    ChecklistDetailScreen(
                        viewModel = viewModel,
                        state = state
                    )
                }
                DeleteAlertDialogContent(
                    showDeleteDialog = showDeleteDialog,
                    onDismiss = {
                        showDeleteDialog = false
                    }
                )
            }
        }
    }

    @Composable
    private fun EditIconStateContent(state: ChecklistDetailState) {
        when (state) {
            ChecklistDetailState.Edit -> {
                IconButton(
                    onClick = {
                        viewModel.changeState(ChecklistDetailState.Overview)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluir edição"
                    )
                }
            }
            ChecklistDetailState.Overview -> {
                IconButton(
                    onClick = {
                        viewModel.changeState(ChecklistDetailState.Edit)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar"
                    )
                }
            }
            else -> Unit
        }
    }

    @Composable
    private fun DeleteAlertDialogContent(showDeleteDialog: Boolean, onDismiss: () -> Unit) {
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { onDismiss() },
                title = { Text("Atenção!") },
                text = { Text(text = "Você está prestes a deletar esse checklist para sempre, deseja continuar?") },
                confirmButton = {
                    Button(onClick = { viewModel.deleteChecklist() }) {
                        Text(text = "Sim")
                    }
                },
                dismissButton = {
                    Button(onClick = { onDismiss() }) {
                        Text(text = "Não")
                    }
                }
            )
        }
    }

    private fun getChecklistId() =
        intent.getStringExtra(CHECKLIST_ID).orEmpty()

    private fun getChecklistName() =
        intent.getStringExtra(CHECKLIST_NAME).orEmpty()

    companion object {
        private const val CHECKLIST_ID = "CHECKLIST_ID"
        private const val CHECKLIST_NAME = "CHECKLIST_NAME"

        fun launch(activity: Activity, checklistId: String, checklistName: String) {
            activity.startActivity<ChecklistDetailActivity> {
                this.putExtra(CHECKLIST_ID, checklistId)
                this.putExtra(CHECKLIST_NAME, checklistName)
            }
        }
    }

}