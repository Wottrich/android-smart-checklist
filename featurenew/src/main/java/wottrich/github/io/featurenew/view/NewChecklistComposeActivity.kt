package wottrich.github.io.featurenew.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.TopBarContent
import wottrich.github.io.components.ui.ApplicationTheme
import wottrich.github.io.featurenew.view.screens.checklistname.ChecklistNameScreen
import wottrich.github.io.featurenew.view.screens.tasklist.TaskListScreen
import wottrich.github.io.tools.extensions.startActivity

@InternalCoroutinesApi
class NewChecklistComposeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            ApplicationTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        TopBarContent(title = {
                            TitleRow(text = "Nova checklist")
                        }, navigationIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Voltar"
                            )
                        }, navigationIconAction = ::onBackPressed)
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
            startDestination = NewChecklistFlow.ChecklistNameProperties.route,
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
                    val checklistId = it.arguments?.getString("checklistId").orEmpty()
                    TaskListScreen(checklistId)
                }
            }
        )
    }

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity<NewChecklistComposeActivity>()
        }
    }

}