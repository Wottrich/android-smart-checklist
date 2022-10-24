package wottrich.github.io.androidsmartchecklist.presentation.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import github.io.wottrich.newchecklist.navigation.NavigatorNewChecklist
import github.io.wottrich.newchecklist.navigation.newChecklistNavigation
import github.io.wottrich.ui.aboutus.data.model.AboutUsContentModel
import github.io.wottrich.ui.aboutus.presentation.ui.AboutUsScreen
import github.io.wottrich.ui.support.navigation.NavigationSupport
import github.io.wottrich.ui.support.navigation.supportNavigation
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.androidsmartchecklist.BuildConfig
import wottrich.github.io.androidsmartchecklist.presentation.activity.NavigationHome.Destinations
import wottrich.github.io.androidsmartchecklist.presentation.ui.StatusBarColor
import wottrich.github.io.androidsmartchecklist.presentation.ui.checklistsettings.ChecklistSettingsScreen
import wottrich.github.io.androidsmartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.quicklychecklist.impl.navigation.NavigationQuicklyChecklist
import wottrich.github.io.quicklychecklist.impl.navigation.quicklyChecklistNavigation
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
            StatusBarColor()
        }
    }

    @Composable
    private fun AppNavigator(navHostController: NavHostController) {
        AnimatedNavHost(
            navController = navHostController,
            startDestination = NavigationHome.route,
            builder = {
                homeNavigation(navHostController)
                newChecklistNavigation(navHostController)
                supportNavigation(navHostController)
                quicklyChecklistNavigation(navHostController)
            }
        )
    }

    private fun NavGraphBuilder.homeNavigation(navHostController: NavHostController) {
        navigation(
            startDestination = NavigationHome.startDestination,
            route = NavigationHome.route
        ) {
            composable(
                route = Destinations.HomeScreen.route
            ) {
                HomeScreen(
                    onAddNewChecklist = {
                        navHostController.navigate(NavigatorNewChecklist.route)
                    },
                    onShareText = ::shareIntentText,
                    onChecklistSettings = {
                        val route =
                            Destinations.ChecklistSettingsScreen.route
                                .replace("{checklistId}", it)
                        navHostController.navigate(route)
                    },
                    onAboutUsClick = {
                        navHostController.navigate(Destinations.AboutUsScreen.route)
                    },
                    onHelpClick = {
                        navHostController.navigate(NavigationSupport.route)
                    },
                    onQuicklyChecklist = {
                        navHostController.navigate(NavigationQuicklyChecklist.route)
                    }
                )
            }
            defaultComposableAnimation(
                route = Destinations.ChecklistSettingsScreen.route,
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
            defaultComposableAnimation(
                route = Destinations.AboutUsScreen.route
            ) {
                AboutUsScreen(
                    model = AboutUsContentModel(
                        versionName = BuildConfig.VERSION_NAME,
                        versionNumber = BuildConfig.VERSION_CODE.toString()
                    ),
                    onBackButton = {
                        navHostController.popBackStack()
                    },
                    onVersionAppClick = { openPlayStore() }
                )
            }
        }
    }

    private fun openPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
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

        object AboutUsScreen : Destinations(route = "AboutUsScreen")
    }
}