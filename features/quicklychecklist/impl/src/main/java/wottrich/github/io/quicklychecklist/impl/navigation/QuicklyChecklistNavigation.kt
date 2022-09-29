package wottrich.github.io.quicklychecklist.impl.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import wottrich.github.io.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.quicklychecklist.impl.presentation.ui.InitialQuicklyChecklistScreen

@ExperimentalAnimationApi
fun NavGraphBuilder.quicklyChecklistNavigation(navHostController: NavHostController) {
    navigation(
        startDestination = NavigationQuicklyChecklist.startDestination,
        route = NavigationQuicklyChecklist.route
    ) {
        defaultComposableAnimation(
            route = NavigationQuicklyChecklist.Destinations.InitialQuicklyChecklistScreen.route
        ) {
            InitialQuicklyChecklistScreen(
                onBackPressed = { navHostController.popBackStack() }
            )
        }
    }
}

object NavigationQuicklyChecklist {
    val route = "NavigationQuicklyChecklist"
    val startDestination = Destinations.InitialQuicklyChecklistScreen.route

    sealed class Destinations(val route: String) {
        object InitialQuicklyChecklistScreen : Destinations(route = "InitialQuicklyChecklistScreen")
    }
}