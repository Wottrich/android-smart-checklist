package wottrich.github.io.smartchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader.CompletableCountBottomSheetScreen

@OptIn(ExperimentalMaterialNavigationApi::class)
class TaskContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            navigation(
                startDestination = NavigatorTask.startDestination,
                route = NavigatorTask.route
            ) {
                bottomSheet(
                    route = NavigatorTask.Destinations.CompletableCountBottomSheetScreen.route
                ) {
                    CompletableCountBottomSheetScreen(
                        onClose = { navHostController.popBackStack() }
                    )
                }
            }
        }
    }
}

object NavigatorTask {
    val route = "NavigatorTask"
    val startDestination = Destinations.CompletableCountBottomSheetScreen.route

    sealed class Destinations(val route: String) {
        object CompletableCountBottomSheetScreen : Destinations("CompletableCountBottomSheetScreen")
    }
}