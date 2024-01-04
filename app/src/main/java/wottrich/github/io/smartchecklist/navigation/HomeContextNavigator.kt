package wottrich.github.io.smartchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.presentation.ui.checklistsettings.ChecklistSettingsScreen
import wottrich.github.io.smartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.smartchecklist.uiaboutus.data.model.AboutUsContentModel
import wottrich.github.io.smartchecklist.uiaboutus.presentation.ui.AboutUsScreen

class HomeContextNavigator(
    private val openPlayStoreNavigator: OpenPlayStoreNavigator,
    private val versionName: String,
    private val versionCode: String
) : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationHome.startDestination,
                route = NavigationHome.route
            ) {
                composable(
                    route = NavigationHome.Destinations.HomeScreen.route
                ) {
                    HomeScreen(navHostController = navHostController)
                }
                defaultComposableAnimation(
                    route = NavigationHome.Destinations.ChecklistSettingsScreen.route
                ) {
                    ChecklistSettingsScreen(
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