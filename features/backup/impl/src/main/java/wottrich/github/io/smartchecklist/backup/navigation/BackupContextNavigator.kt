package wottrich.github.io.smartchecklist.backup.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.backup.presentation.ui.BackupScreen
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation

class BackupContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigationBackup.startDestination,
                route = NavigationBackup.route
            ) {
                defaultComposableAnimation(
                    route = NavigationBackup.Destinations.BackupScreen.route
                ) {
                    BackupScreen(
                        onBackPressed = { navHostController.popBackStack() }
                    )
                }
            }
        }
    }
}

object NavigationBackup {
    val route = "NavigationBackup"
    val startDestination = Destinations.BackupScreen.route

    sealed class Destinations(val route: String) {
        object BackupScreen : Destinations(route = "BackupScreen")
    }
}
