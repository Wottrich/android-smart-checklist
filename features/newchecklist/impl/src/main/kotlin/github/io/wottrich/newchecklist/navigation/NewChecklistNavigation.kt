package github.io.wottrich.newchecklist.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import github.io.wottrich.newchecklist.presentation.ui.NewChecklistNameScreen
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation

@ExperimentalAnimationApi
fun NavGraphBuilder.newChecklistNavigation(navHostController: NavHostController) {
    navigation(
        startDestination = NavigatorNewChecklist.startDestination,
        route = NavigatorNewChecklist.route
    ) {
        defaultComposableAnimation(
            route = NavigatorNewChecklist.Destinations.NewChecklistName.route
        ) {
            NewChecklistNameScreen(
                onCloseScreen = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}

object NavigatorNewChecklist {
    val route = "NavigatorNewChecklist"
    val startDestination = Destinations.NewChecklistName.route

    sealed class Destinations(val route: String) {
        object NewChecklistName : Destinations("NewChecklistName")
    }
}