package wottrich.github.io.smartchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.presentation.ui.checklistinformationheader.CompletableCountBottomSheetScreen
import wottrich.github.io.smartchecklist.presentation.ui.sort.SortTaskListBottomSheet

@OptIn(ExperimentalMaterialNavigationApi::class)
class TaskContextNavigator : SmartChecklistNavigation {
    override fun startNavigation(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.apply {
            bottomSheet(
                route = NavigatorTask.Destinations.CompletableCountBottomSheetScreen.route
            ) {
                CompletableCountBottomSheetScreen(
                    onClose = { navHostController.popBackStack() }
                )
            }
            bottomSheet(
                route = NavigatorTask.Destinations.SortTaskListBottomSheetScreen.route
            ) {
                SortTaskListBottomSheet(navHostController)
            }
        }
    }
}

object NavigatorTask {
    val route = "NavigatorTask"
    val startDestination = Destinations.CompletableCountBottomSheetScreen.route

    sealed class Destinations(val route: String) {
        data object CompletableCountBottomSheetScreen : Destinations("CompletableCountBottomSheetScreen")
        data object SortTaskListBottomSheetScreen : Destinations("SortTaskListBottomSheetScreen")
    }
}