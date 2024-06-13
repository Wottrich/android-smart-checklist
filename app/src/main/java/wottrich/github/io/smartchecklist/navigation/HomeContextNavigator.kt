package wottrich.github.io.smartchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.deletechecklist.view.DeleteChecklistBottomSheetScreen
import wottrich.github.io.smartchecklist.presentation.ui.checklistsettings.ChecklistSettingsScreen
import wottrich.github.io.smartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.smartchecklist.uiaboutus.data.model.AboutUsContentModel
import wottrich.github.io.smartchecklist.uiaboutus.presentation.ui.AboutUsScreen
import wottrich.github.io.smartchecklist.uiprivacypolicy.ui.PrivacyPolicyScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
class HomeContextNavigator(
    private val openPlayStoreNavigator: OpenPlayStoreNavigator,
    private val privacyPolicy: PrivacyPolicy,
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
                        },
                        onPrivacyPolicyClick = {
                            navHostController.navigate(NavigationHome.Destinations.PrivacyPolicyScreen.route)
                        }
                    )
                }
                composable(
                    route = NavigationHome.Destinations.PrivacyPolicyScreen.route
                ) {
                    PrivacyPolicyScreen(
                        onOpenPrivacyPolicy = {
                            privacyPolicy.openPrivacyPolicy()
                        },
                        onBackButton = {
                            navHostController.popBackStack()
                        }
                    )
                }
                navigation(
                    startDestination = NavigationSetting.startDestinations,
                    route = NavigationHome.Navigation.Settings.route
                ) {
                    defaultComposableAnimation(
                        route = NavigationSetting.Destinations.ChecklistSettingsScreen.route
                    ) {
                        ChecklistSettingsScreen(
                            onCloseScreen = {
                                navHostController.navigateUp()
                            },
                            onDeleteChecklist = {
                                navHostController.navigate(
                                    NavigationSetting.Destinations.DeleteChecklistBottomSheet.route
                                )
                            }
                        )
                    }
                    bottomSheet(
                        route = NavigationSetting.Destinations.DeleteChecklistBottomSheet.route
                    ) {
                        DeleteChecklistBottomSheetScreen(
                            onCloseFlow = {
                                navHostController.popBackStack(
                                    route = NavigationSetting.startDestinations,
                                    inclusive = true,
                                    saveState = false
                                )
                            },
                            onCloseBottomSheet = { navHostController.navigateUp() })
                    }
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

        object AboutUsScreen : Destinations(route = "AboutUsScreen")

        data object PrivacyPolicyScreen : Destinations(route = "PrivacyPolicyScreen")
    }

    sealed class Navigation(val route: String) {
        data object Settings : Navigation(NavigationSetting.route)
    }
}

object NavigationSetting {
    val route = "Settings"
    val startDestinations = Destinations.ChecklistSettingsScreen.route

    sealed class Destinations(val route: String) {
        data object ChecklistSettingsScreen :
            Destinations(route = "ChecklistSettingsScreen")

        data object DeleteChecklistBottomSheet :
            Destinations(route = "DeleteChecklistBottomSheet")
    }
}