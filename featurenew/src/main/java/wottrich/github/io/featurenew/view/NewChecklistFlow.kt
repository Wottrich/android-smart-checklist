package wottrich.github.io.featurenew.view

import androidx.navigation.NavType
import androidx.navigation.compose.navArgument
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.tools.BaseNavigationModelImpl

private fun suffixChecklistFlow(route: String) = "newchecklist/$route"

sealed class NewChecklistFlow {

    object ChecklistNameProperties : BaseNavigationModelImpl(
        route = suffixChecklistFlow("name")
    )

    object ChecklistTasksProperties : BaseNavigationModelImpl(
        route = suffixChecklistFlow("tasks"),
        arguments = listOf(
            navArgument("checklist") { type = NavType.ParcelableType(Checklist::class.java) }
        )
    ) {
        override val routeWithArgument: String
            get() = "$route/{checklist}"
    }

}