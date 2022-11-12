package wottrich.github.io.quicklychecklist.impl.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import wottrich.github.io.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.quicklychecklist.impl.presentation.ui.InitialQuicklyChecklistScreen
import wottrich.github.io.quicklychecklist.impl.presentation.ui.QuicklyChecklistScreen

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalAnimationApi
fun NavGraphBuilder.quicklyChecklistNavigation(
    navHostController: NavHostController,
    onShareChecklistBack: (String) -> Unit
) {
    navigation(
        startDestination = NavigationQuicklyChecklist.startDestination,
        route = NavigationQuicklyChecklist.route
    ) {
        defaultComposableAnimation(
            route = NavigationQuicklyChecklist.Destinations.InitialQuicklyChecklistScreen.route
        ) { navBackStackEntry ->

            val invalidChecklist =
                navBackStackEntry.savedStateHandle.get<Boolean>("invalidChecklist") ?: false
            if (invalidChecklist) {
                navBackStackEntry.savedStateHandle.remove<Boolean>("invalidChecklist")
            }

            InitialQuicklyChecklistScreen(
                isInvalidChecklistError = invalidChecklist,
                onBackPressed = { navHostController.popBackStack() },
                onConfirmButtonClicked = {
                    val route =
                        NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.route
                            .replace(
                                NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.param,
                                it
                            )
                    navHostController.navigate(route)
                }
            )
        }
        defaultComposableAnimation(
            route = NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.route,
            arguments = listOf(
                navArgument("quicklychecklistjson") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val param = navBackStackEntry.arguments?.getString(
                "quicklychecklistjson"
            ).orEmpty()
            QuicklyChecklistScreen(
                quicklyChecklistJson = param,
                onShareBackClick = onShareChecklistBack,
                onBackPressed = {
                    navHostController.previousBackStackEntry?.savedStateHandle?.set(
                        "invalidChecklist",
                        false
                    )
                    navHostController.popBackStack()
                },
                onInvalidChecklist = {
                    navHostController.previousBackStackEntry?.savedStateHandle?.set(
                        "invalidChecklist",
                        true
                    )
                    navHostController.popBackStack()
                }
            )
        }
    }
}

object NavigationQuicklyChecklist {
    val route = "NavigationQuicklyChecklist"
    val startDestination = Destinations.InitialQuicklyChecklistScreen.route

    sealed class Destinations(val route: String) {
        object InitialQuicklyChecklistScreen : Destinations(route = "InitialQuicklyChecklistScreen")
        object QuicklyChecklistScreen :
            Destinations(route = "QuicklyChecklistScreen/{quicklychecklistjson}") {
            const val param = "{quicklychecklistjson}"
        }
    }
}