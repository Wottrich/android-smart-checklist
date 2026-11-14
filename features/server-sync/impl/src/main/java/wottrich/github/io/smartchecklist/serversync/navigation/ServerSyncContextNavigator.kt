package wottrich.github.io.smartchecklist.serversync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.serversync.presentation.ui.ServerSettingsScreen

class ServerSyncContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationServerSync.startDestination,
                route = NavigationServerSync.route
            ) {
                defaultComposableAnimation(
                    route = NavigationServerSync.Destinations.ServerSettingsScreen.route
                ) {
                    ServerSettingsScreen(
                        onBackPressed = { navHostController.popBackStack() }
                    )
                }
            }
        }
    }
}

object NavigationServerSync {
    val route = "NavigationServerSync"
    val startDestination = Destinations.ServerSettingsScreen.route

    sealed class Destinations(val route: String) {
        object ServerSettingsScreen : Destinations(route = "ServerSettingsScreen")
    }
}
