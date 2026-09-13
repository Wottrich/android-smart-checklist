package wottrich.github.io.smartchecklist.quicklychecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.intent.navigation.ShareIntentTextNavigator
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.ui.InitialQuicklyChecklistScreen
import wottrich.github.io.smartchecklist.quicklychecklist.presentation.ui.QuicklyChecklistScreen

class QuicklyChecklistContextNavigator(
    private val shareIntentTextNavigator: ShareIntentTextNavigator
) : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationQuicklyChecklist.startDestination,
                route = NavigationQuicklyChecklist.route
            ) {
                defaultComposableAnimation(
                    route = NavigationQuicklyChecklist.Destinations.InitialQuicklyChecklistScreen.route,
                    deepLinks = NavigationQuicklyChecklist.Destinations.InitialQuicklyChecklistScreen.deepLinks
                ) { navBackStackEntry ->

                    val savedStateHandle = navBackStackEntry.savedStateHandle
                    val invalidChecklist =
                        savedStateHandle.get<Boolean>("invalidChecklist") ?: false
                    if (invalidChecklist) {
                        navBackStackEntry.savedStateHandle.remove<Boolean>("invalidChecklist")
                    }
                    val encodedQuicklyChecklist =
                        navBackStackEntry.arguments?.getString("checklist", null)

                    InitialQuicklyChecklistScreen(
                        encodedQuicklyChecklist = encodedQuicklyChecklist,
                        isInvalidChecklistError = invalidChecklist,
                        onBackPressed = { navHostController.popBackStack() },
                        onConfirmButtonClicked = {
                            val route =
                                NavigationQuicklyChecklist.Destinations.QuicklyChecklistScreen.routeWithParam(
                                    it
                                )
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
                                NavigationQuicklyChecklist.Destinations.QuicklyChecklistConfirmBottomSheet.routeWithParam(
                                    it
                                )
                            navHostController.navigate(route)
                        }
                    )
                }
            }
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
                    uriPattern = "https://wottrich.github.io/quicklychecklist?checklist={checklist}"
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