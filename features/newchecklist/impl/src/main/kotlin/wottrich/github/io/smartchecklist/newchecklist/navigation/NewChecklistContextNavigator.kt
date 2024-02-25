package wottrich.github.io.smartchecklist.newchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.newchecklist.presentation.ui.NewChecklistNameScreen
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation

class NewChecklistContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
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
    }
}

object NavigatorNewChecklist {
    val route = "NavigatorNewChecklist"
    val startDestination = Destinations.NewChecklistName.route

    sealed class Destinations(val route: String) {
        object NewChecklistName : Destinations("NewChecklistName")
    }
}