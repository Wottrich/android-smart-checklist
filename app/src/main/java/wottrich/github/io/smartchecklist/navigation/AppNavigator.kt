package wottrich.github.io.smartchecklist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation

class AppNavigator(private val navigators: List<SmartChecklistNavigation>) {
    fun buildNavigators(navGraphBuilder: NavGraphBuilder, navHostController: NavHostController) {
        navigators.forEach {
            it.startNavigation(navGraphBuilder, navHostController)
        }
    }
}