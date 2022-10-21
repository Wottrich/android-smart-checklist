package wottrich.github.io.quicklychecklist.impl.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import wottrich.github.io.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.quicklychecklist.impl.presentation.ui.InitialQuicklyChecklistScreen
import wottrich.github.io.quicklychecklist.impl.presentation.ui.QuicklyChecklistScreen

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
                onBackPressed = { navHostController.popBackStack() },
                onConfirmButtonClicked = {
                    val route =
                        NavigationQuicklyChecklist.Destinations.QuicklyChecklistScaffold.route
                            .replace(
                                NavigationQuicklyChecklist.Destinations.QuicklyChecklistScaffold.param,
                                it
                            )
                    navHostController.navigate(route)
                }
            )
        }
        defaultComposableAnimation(
            route = NavigationQuicklyChecklist.Destinations.QuicklyChecklistScaffold.route,
            arguments = listOf(
                navArgument("quicklychecklistjson") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val param = navBackStackEntry.arguments?.getString(
                NavigationQuicklyChecklist.Destinations.QuicklyChecklistScaffold.param
            ).orEmpty()
            QuicklyChecklistScreen(
                quicklyChecklistJson = param,
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
        object QuicklyChecklistScaffold :
            Destinations(route = "QuicklyChecklistScaffold/{quicklychecklistjson}") {
            const val param = "{quicklychecklistjson}"
        }
    }
}