package wottrich.github.io.smartchecklist.android

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface SmartChecklistNavigation {
    fun startNavigation(navGraphBuilder: NavGraphBuilder, navHostController: NavHostController)
}