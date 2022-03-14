package github.io.wottrich.checklist.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import wottrich.github.io.tools.BaseNavigationModelImpl

private fun suffixChecklistFlow(route: String) = "newchecklist/$route"

sealed class NewChecklistFlow {

    object ChecklistNameProperties : BaseNavigationModelImpl(
        route = suffixChecklistFlow("name")
    )

    object ChecklistTasksProperties : BaseNavigationModelImpl(
        route = suffixChecklistFlow("tasks"),
        arguments = listOf(
            navArgument("checklistId") { type = NavType.StringType }
        )
    ) {
        override val routeWithArgument: String
            get() = "$route/{checklistId}"

        fun route(checklistId: String) = "$route/$checklistId"
    }

}