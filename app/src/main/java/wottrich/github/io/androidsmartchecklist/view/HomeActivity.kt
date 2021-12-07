package wottrich.github.io.androidsmartchecklist.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import wottrich.github.io.androidsmartchecklist.R.string
import wottrich.github.io.androidsmartchecklist.ui.ChecklistSelectedContent
import wottrich.github.io.androidsmartchecklist.ui.DrawerContent
import wottrich.github.io.androidsmartchecklist.ui.HomeScaffold
import wottrich.github.io.baseui.ui.ApplicationTheme
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
                HomeScaffold(
                    scaffoldState = scaffoldState,
                    drawerState = drawerState,
                    coroutineScope = rememberCoroutineScope,
                    drawerContent = {
                        DrawerContent(
                            state = homeDrawerState,
                            onItemClick = {
                                homeViewModel.onChecklistClicked(it)
                            }
                        )
                    },
                    onFloatingActionButtonClick = { startNewChecklistActivity() },
                    onTitleContent = {
                        TopBarTitleContent(checklistState)
                    }
                ) {
                    ChecklistSelectedContent(
                        checklistState = checklistState,
                        onChangeState = homeViewModel::onChangeState,
                        onAddItemClicked = homeViewModel::onAddItemClicked,
                        onUpdateItemClicked = homeViewModel::onUpdateItemClicked,
                        onDeleteItemClicked = homeViewModel::onDeleteItemClicked,
                        onConfirmDeleteChecklist = homeViewModel::onDeleteChecklist
                    )
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