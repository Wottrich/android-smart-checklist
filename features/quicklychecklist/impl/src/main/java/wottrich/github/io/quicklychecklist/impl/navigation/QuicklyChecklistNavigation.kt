package wottrich.github.io.quicklychecklist.impl.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.quicklychecklist.impl.presentation.ui.InitialQuicklyChecklistScreen
import wottrich.github.io.quicklychecklist.impl.presentation.ui.QuicklyChecklistAddNewChecklistBottomSheetContent
import wottrich.github.io.quicklychecklist.impl.presentation.ui.QuicklyChecklistConfirmBottomSheetContent
import wottrich.github.io.quicklychecklist.impl.presentation.ui.QuicklyChecklistScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
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
            route = NavigationQuicklyChecklist.Destinations.InitialQuicklyChecklistScreen.route,
            deepLinks = NavigationQuicklyChecklist.Destinations.InitialQuicklyChecklistScreen.deepLinks
        ) { navBackStackEntry ->

            val savedStateHandle = navBackStackEntry.savedStateHandle
            val invalidChecklist = savedStateHandle.get<Boolean>("invalidChecklist") ?: false
            if (invalidChecklist) {
                navBackStackEntry.savedStateHandle.remove<Boolean>("invalidChecklist")
            }
            val encodedQuicklyChecklist = navBackStackEntry.arguments?.getString("checklist", null)

            InitialQuicklyChecklistScreen(
                encodedQuicklyChecklist = encodedQuicklyChecklist,
                isInvalidChecklistError = invalidChecklist,
                onBackPressed = { navHostController.popBackStack() },
                onConfirmButtonClicked = {
                    val route =
                        NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen
                            .routeWithParam(it)
                    navHostController.navigate(route)
                }
            )
        }
        defaultComposableAnimation(
            route = NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.route,
            arguments = NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.arguments
        ) { navBackStackEntry ->
            val param = navBackStackEntry.arguments?.getString(
                "quicklychecklistjson"
            ).orEmpty()
            QuicklyChecklistScreen(
                quicklyChecklistJson = param,
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
                },
                onConfirmBottomSheetEdit = {
                    val route =
                        NavigationQuicklyChecklist.Destinations.QuicklyChecklistConfirmBottomSheet
                            .routeWithParam(it)
                    navHostController.navigate(route)
                }
            )
        }
        bottomSheet(
            route = NavigationQuicklyChecklist.Destinations.QuicklyChecklistConfirmBottomSheet.route,
            arguments = NavigationQuicklyChecklist.Destinations.QuicklyChecklistConfirmBottomSheet.arguments
        ) { navBackStackEntry ->
            val param = navBackStackEntry.arguments?.getString(
                "quicklychecklistjson"
            ).orEmpty()
            QuicklyChecklistConfirmBottomSheetContent(
                quicklyChecklistJson = param,
                hasExistentChecklist = false,
                onShareBackClick = onShareChecklistBack,
                onSaveChecklist = {
                    val route =
                        NavigationQuicklyChecklist.Destinations.AddNewQuicklyChecklistBottomSheet
                            .routeWithParam(it)
                    navHostController.navigate(
                        route,
                        navOptions {
                            popUpTo(
                                NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.route
                            )
                        }
                    )
                },
                onReplaceExistentChecklist = {}
            )
        }
        bottomSheet(
            route = NavigationQuicklyChecklist.Destinations.AddNewQuicklyChecklistBottomSheet.route,
            arguments = NavigationQuicklyChecklist.Destinations.AddNewQuicklyChecklistBottomSheet.arguments
        ) { navBackStackEntry ->
            val param = navBackStackEntry.arguments?.getString(
                "quicklychecklistjson"
            ).orEmpty()
            QuicklyChecklistAddNewChecklistBottomSheetContent(
                quicklyChecklistJson = param,
                onConfirmButtonClick = {
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
        object InitialQuicklyChecklistScreen :
            Destinations(route = "InitialQuicklyChecklistScreen?checklist={checklist}") {
            val deepLinks = listOf(
                navDeepLink {
                    uriPattern = "http://wottrich.github.io/quicklychecklist?checklist={checklist}"
                }
            )
        }

        object QuicklyChecklistScreen :
            Destinations(route = "QuicklyChecklistScreen/{quicklychecklistjson}") {
            const val param = "{quicklychecklistjson}"
            val arguments = listOf(
                navArgument("quicklychecklistjson") {
                    type = NavType.StringType
                }
            )
            fun routeWithParam(quicklyChecklistJson: String): String {
                return route.replace(param, quicklyChecklistJson)
            }
        }

        object QuicklyChecklistConfirmBottomSheet :
            Destinations(route = "QuicklyChecklistConfirmBottomSheet/{quicklychecklistjson}") {
            private const val param = "{quicklychecklistjson}"
            val arguments = listOf(
                navArgument("quicklychecklistjson") {
                    type = NavType.StringType
                }
            )
            fun routeWithParam(quicklyChecklistJson: String): String {
                return route.replace(param, quicklyChecklistJson)
            }
        }

        object AddNewQuicklyChecklistBottomSheet :
            Destinations("AddNewQuicklyChecklistBottomSheet/{quicklychecklistjson}") {
            private const val param = "{quicklychecklistjson}"
            val arguments = listOf(
                navArgument("quicklychecklistjson") {
                    type = NavType.StringType
                }
            )
            fun routeWithParam(quicklyChecklistJson: String): String {
                return route.replace(param, quicklyChecklistJson)
            }
        }
    }
}