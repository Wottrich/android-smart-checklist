package wottrich.github.io.smartchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.intent.navigation.ShareIntentTextNavigator
import wottrich.github.io.smartchecklist.newchecklist.navigation.NavigatorNewChecklist
import wottrich.github.io.smartchecklist.presentation.activity.InvalidChecklistId
import wottrich.github.io.smartchecklist.presentation.ui.checklistsettings.ChecklistSettingsScreen
import wottrich.github.io.smartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.smartchecklist.uiaboutus.data.model.AboutUsContentModel
import wottrich.github.io.smartchecklist.uiaboutus.presentation.ui.AboutUsScreen
import wottrich.github.io.smartchecklist.uisupport.navigation.NavigationSupport

class HomeContextNavigator(
    private val shareIntentTextNavigator: ShareIntentTextNavigator,
    private val openPlayStoreNavigator: OpenPlayStoreNavigator,
    private val versionName: String,
    private val versionCode: String
) : SmartChecklistNavigation {
    override fun startNavigation(navGraphBuilder: NavGraphBuilder, navHostController: NavHostController) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationHome.startDestination,
                route = NavigationHome.route
            ) {
                composable(
                    route = NavigationHome.Destinations.HomeScreen.route
                ) {
                    HomeScreen(
                        onAddNewChecklist = {
                            navHostController.navigate(NavigatorNewChecklist.route)
                        },
                        onShareText = {
                            shareIntentTextNavigator.shareIntentText(it)
                        },
                        onChecklistSettings = {
                            val route =
                                NavigationHome.Destinations.ChecklistSettingsScreen.route
                                    .replace("{checklistId}", it)
                            navHostController.navigate(route)
                        },
                        onAboutUsClick = {
                            navHostController.navigate(NavigationHome.Destinations.AboutUsScreen.route)
                        },
                        onHelpClick = {
                            navHostController.navigate(NavigationSupport.route)
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
                defaultComposableAnimation(
                    route = NavigationHome.Destinations.AboutUsScreen.route
                ) {
                    AboutUsScreen(
                        model = AboutUsContentModel(
                            versionName = versionName,
                            versionNumber = versionCode
                        ),
                        onBackButton = {
                            navHostController.popBackStack()
                        },
                        onVersionAppClick = {
                            openPlayStoreNavigator.openPlayStore()
                        }
                    )
                }
            }
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