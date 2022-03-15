package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import github.io.wottrich.checklist.presentation.activity.NewChecklistActivity
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.androidsmartchecklist.presentation.ui.checklistsettings.ChecklistSettingsScreen
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.tools.extensions.shareIntentText

class InvalidChecklistId : Exception("Checklist id must not be null")

@InternalCoroutinesApi
@OptIn(ExperimentalAnimationApi::class)
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
            AppNavigator(navHostController = navController)
        }
    }

    @Composable
    private fun AppNavigator(navHostController: NavHostController) {
        AnimatedNavHost(
            navController = navHostController,
            startDestination = NavigationHome.route,
            builder = {
                navigation(
                    startDestination = NavigationHome.startDestination,
                    route = NavigationHome.route
                ) {
                    composable(
                        route = NavigationHome.Destinations.HomeScreen.route
                    ) {
                        HomeScreen(
                            onAddNewChecklist = ::startNewChecklistActivity,
                            onCopyChecklist = ::shareIntentText,
                            onChecklistSettings = {
                                val route =
                                    NavigationHome.Destinations.ChecklistSettingsScreen.route
                                        .replace("{checklistId}", it)
                                navHostController.navigate(route)
                            }
                        )
                    }
                    defaultComposableAnimation(
                        route = NavigationHome.Destinations.ChecklistSettingsScreen.route,
                        arguments = listOf(
                            navArgument("checklistId") { type = NavType.StringType }
                        )
                    ) { navBackStackEntry ->
                        val checklistId = navBackStackEntry.arguments?.getString("checklistId")
                            ?: throw InvalidChecklistId()
                        ChecklistSettingsScreen(
                            checklistId,
                            onCloseScreen = {
                                navHostController.popBackStack()
                            }
                        )
                    }
                }
            }
        )
    }

    private fun startNewChecklistActivity() {
        NewChecklistActivity.launch(this)
    }

    private fun NavGraphBuilder.defaultComposableAnimation(
        route: String,
        arguments: List<NamedNavArgument> = emptyList(),
        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
    ) {
        composable(
            route = route,
            arguments = arguments,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(700))
            }
        ) {
            content(it)
        }
    }
}

object NavigationHome {
    val route = "NavigationHome"
    val startDestination = Destinations.HomeScreen.route

    sealed class Destinations(val route: String) {
        object HomeScreen : Destinations(route = "HomeScreen")
        object ChecklistSettingsScreen :
            Destinations(route = "ChecklistSettingsScreen/{checklistId}")
    }
}