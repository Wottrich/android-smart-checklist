package wottrich.github.io.smartchecklist.suggestion.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagchecklist.ChecklistTagsBottomSheetNavigationAction
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagchecklist.ChecklistTagsBottomSheetScreen
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister.RegisterNewTagWithSuggestionsScreen
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagsoverview.TagsOverviewScreen
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagsoverview.TagsScreenNavigationAction

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.suggestionNavigation(navHostController: NavHostController) {
    navigation(
        startDestination = SuggestionNavigation.startDestination,
        route = SuggestionNavigation.route
    ) {
        defaultComposableAnimation(
            route = SuggestionNavigation.Destinations.SuggestionOverviewScreen.route
        ) {
            TagsOverviewScreen {
                when (it) {
                    TagsScreenNavigationAction.CloseScreen -> navHostController.popBackStack()
                    is TagsScreenNavigationAction.OpenTagDetail -> TODO()
                    TagsScreenNavigationAction.AddNewTag -> navHostController.navigate(
                        SuggestionNavigation.Destinations.RegisterNewTagWithSuggestionsScreen.route
                    )
                }
            }
        }
        defaultComposableAnimation(
            SuggestionNavigation.Destinations.RegisterNewTagWithSuggestionsScreen.route
        ) {
            RegisterNewTagWithSuggestionsScreen(
                onBackPressed = { navHostController.popBackStack() }
            )
        }
        bottomSheet(
            route = SuggestionNavigation.Destinations.ChecklistTagsScreen.route,
            arguments = listOf(
                navArgument(SuggestionNavigation.Destinations.ChecklistTagsScreen.routeParam) { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val checklistUuid = navBackStackEntry.arguments?.getString(SuggestionNavigation.Destinations.ChecklistTagsScreen.routeParam)
            println(checklistUuid)
            ChecklistTagsBottomSheetScreen {
                when (it) {
                    ChecklistTagsBottomSheetNavigationAction.OnCloseScreen -> navHostController.popBackStack()
                }
            }
        }
    }
}

object SuggestionNavigation {
    val route = "SuggestionNavigation"
    val startDestination = Destinations.SuggestionOverviewScreen.route

    sealed class Destinations(val route: String) {
        object SuggestionOverviewScreen : Destinations("SuggestionOverviewScreen")
        object RegisterNewTagWithSuggestionsScreen : Destinations("RegisterNewTagWithSuggestionsScreen")
        object ChecklistTagsScreen : Destinations("ChecklistTagsScreen/{checklistUuid}") {
            const val routeParam = "checklistUuid"
            fun navigate(navHostController: NavHostController, checklistUuid: String) {
                navHostController.navigate(route.replace("{checklistUuid}", checklistUuid))
            }
        }
    }
}