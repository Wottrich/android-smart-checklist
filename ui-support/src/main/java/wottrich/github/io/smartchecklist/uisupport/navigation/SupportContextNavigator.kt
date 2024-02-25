package wottrich.github.io.smartchecklist.uisupport.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.uisupport.presentation.ui.HelpOverviewScreen
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation

class SupportContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationSupport.startDestination,
                route = NavigationSupport.route
            ) {
                defaultComposableAnimation(
                    route = NavigationSupport.Destinations.HelpOverviewScreen.route
                ) {
                    HelpOverviewScreen(
                        onBackPressed = { navHostController.popBackStack() }
                    )
                }
            }
        }
    }
}

object NavigationSupport {
    val route = "NavigationSupport"
    val startDestination = Destinations.HelpOverviewScreen.route

    sealed class Destinations(val route: String) {
        object HelpOverviewScreen : Destinations(route = "HelpOverviewScreen")
    }
}