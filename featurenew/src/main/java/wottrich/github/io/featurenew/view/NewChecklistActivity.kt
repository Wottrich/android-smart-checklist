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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.baseui.TitleRow
import wottrich.github.io.baseui.TopBarContent
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.featurenew.R
import wottrich.github.io.featurenew.view.screens.checklistname.ChecklistNameScreen
import wottrich.github.io.featurenew.view.screens.tasklist.TaskListScreen
import wottrich.github.io.tools.extensions.isResumedState
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
                        if (navBackStackEntry.lifecycle.isResumedState()) {
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
                    TaskListScreen(it.arguments?.getString(CHECKLIST_ID_ARGUMENT).orEmpty())
                }
            }
        )
    }

    private fun getStartDestination() =
        NewChecklistFlow.ChecklistNameProperties.route

    companion object {
        const val CHECKLIST_ID_ARGUMENT = "checklistId"

        fun launch(activity: Activity) {
            activity.startActivity<NewChecklistActivity>()
        }
    }

}