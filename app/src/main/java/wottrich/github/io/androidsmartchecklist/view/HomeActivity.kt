package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.R
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.ui.ChecklistSelectedContent
import wottrich.github.io.androidsmartchecklist.ui.ChecklistTaskItem
import wottrich.github.io.androidsmartchecklist.ui.DrawerContent
import wottrich.github.io.androidsmartchecklist.ui.HomeScaffold
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.database.entity.ChecklistWithTasks
import wottrich.github.io.featurenew.view.NewChecklistActivity
import wottrich.github.io.featurenew.view.screens.checklistdetail.ChecklistScreen
import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListState
import wottrich.github.io.featurenew.view.screens.checklistdetail.TaskListState.Overview


@InternalCoroutinesApi
class HomeActivity : AppCompatActivity() {

    private val viewModel by viewModel<DrawerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                val checklistState by viewModel.homeStateFlow.collectAsState()
                val homeDrawerState by viewModel.drawerStateFlow.collectAsState()
                val scaffoldState = rememberScaffoldState()
                val drawerState = scaffoldState.drawerState
                val rememberCoroutineScope = rememberCoroutineScope()
                HomeScaffold(
                    scaffoldState = scaffoldState,
                    drawerState = drawerState,
                    coroutineScope = rememberCoroutineScope,
                    drawerContent = {
                        DrawerContent(
                            state = homeDrawerState,
                            onItemClick = {
                                viewModel.onChecklistClicked(it)
                            }
                        )
                    },
                    onFloatingActionButtonClick = { startNewChecklistActivity() },
                    onTitleContent = {
                        TopBarTitleContent(checklistState)
                    }
                ) {
                    ChecklistSelectedContent(checklistState)
                }
            }
        }
    }

    @Composable
    private fun TopBarTitleContent(checklistState: HomeState) {
        when {
            checklistState.isLoading -> Unit
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