package wottrich.github.io.smartchecklist.share.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.share.presentation.ui.NearbyScreen
import wottrich.github.io.smartchecklist.share.presentation.ui.ShareScreen

class ShareContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationShare.startDestination,
                route = NavigationShare.route
            ) {
                defaultComposableAnimation(
                    route = NavigationShare.Destinations.ShareScreen.route
                ) {
                    ShareScreen(
                        onBackPressed = { navHostController.popBackStack() }
                    )
                }
                defaultComposableAnimation(
                    route = NavigationShare.Destinations.NearbyScreen.route
                ) {
                    NearbyScreen(
                        onBackPressed = { navHostController.popBackStack() }
                    )
                }
            }
        }
    }
}

object NavigationShare {
    val route = "NavigationShare"
    val startDestination = Destinations.ShareScreen.route

    sealed class Destinations(val route: String) {
        object ShareScreen : Destinations(route = "ShareScreen")
        object NearbyScreen : Destinations(route = "NearbyScreen")
    }
}
