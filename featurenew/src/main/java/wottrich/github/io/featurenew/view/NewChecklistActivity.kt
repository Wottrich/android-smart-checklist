package wottrich.github.io.featurenew.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.TopBarContent
import wottrich.github.io.components.ui.ApplicationTheme
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
                                TitleRow(text = "Nova checklist")
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            },
                            navigationIconAction = ::onBackPressed,
                            actionsContent = {
                                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route
                                val editRoute = NewChecklistFlow.ChecklistTasksProperties.routeWithArgument
                                if (currentRoute == editRoute) {
                                    IconButton(onClick = { finish() }) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Salvar alterações"
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
                ) {
                    ChecklistNameScreen(scaffoldState) {
                        navHostController.popBackStack()
                        navHostController.navigate(
                            NewChecklistFlow.ChecklistTasksProperties.route(it)
                        )
                    }
                }
                composable(
                    route = NewChecklistFlow.ChecklistTasksProperties.routeWithArgument
                ) {
                    val checklistId = if (isEditFlow()) {
                        getChecklistIdToEdit()
                    } else {
                        it.arguments?.getString("checklistId").orEmpty()
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
        const val CHECKLIST_ID = "CHECKLIST_ID"
        const val IS_EDIT_FLOW = "IS_EDIT_FLOW"

        fun launch(activity: Activity) {
            activity.startActivity<NewChecklistActivity>()
        }

        fun launchEditFlow(activity: Activity, checklistId: String) {
            activity.startActivity<NewChecklistActivity> {
                this.putExtra(CHECKLIST_ID, checklistId)
                this.putExtra(IS_EDIT_FLOW, true)
            }
        }

    }

}