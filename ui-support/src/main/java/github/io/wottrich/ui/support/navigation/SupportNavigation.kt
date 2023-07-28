package github.io.wottrich.ui.support.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import github.io.wottrich.ui.support.presentation.ui.HelpOverviewScreen
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation

@ExperimentalAnimationApi
fun NavGraphBuilder.supportNavigation(navHostController: NavHostController) {
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

object NavigationSupport {
    val route = "NavigationSupport"
    val startDestination = Destinations.HelpOverviewScreen.route

    sealed class Destinations(val route: String) {
        object HelpOverviewScreen : Destinations(route = "HelpOverviewScreen")
    }
}