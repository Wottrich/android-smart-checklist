package wottrich.github.io.featurenew.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.TopBarContent
import wottrich.github.io.components.ui.ApplicationTheme
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.view.screens.checklistname.ChecklistNameScreen
import wottrich.github.io.featurenew.view.screens.tasklist.TaskListScreen
import wottrich.github.io.tools.extensions.startActivity

@InternalCoroutinesApi
class NewChecklistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            ApplicationTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        TopBarContent(
                            title = {
                                TitleRow(text = stringResource(id = R.string.checklist_new_screen_title))
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = stringResource(id = R.string.arrow_back_content_description)
                                )
                            },
                            navigationIconAction = ::onBackPressed,
                            actionsContent = {
                                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route
                                val isAddTaskScreen =
                                    NewChecklistFlow.ChecklistTasksProperties.routeWithArgument
                                if (currentRoute == isAddTaskScreen) {
                                    IconButton(onClick = { finish() }) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(
                                                id = R.string.checklist_new_save_changes
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) {
                    AppNavigator(navHostController, scaffoldState)
                }
            }
        }
    }

    @Composable
    private fun AppNavigator(navHostController: NavHostController, scaffoldState: ScaffoldState) {
        NavHost(
            navController = navHostController,
            startDestination = getStartDestination(),
            builder = {
                composable(
                    route = NewChecklistFlow.ChecklistNameProperties.route
                ) { navBackStackEntry ->
                    ChecklistNameScreen(scaffoldState) {
                        if (navBackStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            navHostController.popBackStack()
                            navHostController.navigate(
                                NewChecklistFlow.ChecklistTasksProperties.route(it)
                            )
                        }
                    }
                }
                composable(
                    route = NewChecklistFlow.ChecklistTasksProperties.routeWithArgument
                ) {
                    val checklistId = if (isEditFlow()) {
                        getChecklistIdToEdit()
                    } else {
                        it.arguments?.getString(CHECKLIST_ID_ARGUMENT).orEmpty()
                    }
                    TaskListScreen(checklistId)
                }
            }
        )
    }

    private fun getStartDestination() =
        if (isEditFlow()) {
            NewChecklistFlow.ChecklistTasksProperties.routeWithArgument
        } else {
            NewChecklistFlow.ChecklistNameProperties.route
        }

    private fun getChecklistIdToEdit() =
        checkNotNull(intent.getStringExtra(CHECKLIST_ID))

    private fun isEditFlow() =
        intent.getBooleanExtra(IS_EDIT_FLOW, false)

    companion object {
        const val CHECKLIST_ID_ARGUMENT = "checklistId"
        const val CHECKLIST_ID = "CHECKLIST_ID"
        const val IS_EDIT_FLOW = "IS_EDIT_FLOW"

        fun launch(activity: Activity) {
            activity.startActivity<NewChecklistActivity>()
        }
    }

}