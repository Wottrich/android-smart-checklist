package wottrich.github.io.smartchecklist.presentation.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import wottrich.github.io.smartchecklist.newchecklist.navigation.NavigatorNewChecklist
import wottrich.github.io.smartchecklist.newchecklist.navigation.newChecklistNavigation
import wottrich.github.io.smartchecklist.uiaboutus.data.model.AboutUsContentModel
import wottrich.github.io.smartchecklist.uiaboutus.presentation.ui.AboutUsScreen
import wottrich.github.io.smartchecklist.uisupport.navigation.NavigationSupport
import wottrich.github.io.smartchecklist.uisupport.navigation.supportNavigation
import kotlinx.coroutines.InternalCoroutinesApi
import wottrich.github.io.smartchecklist.BuildConfig
import wottrich.github.io.smartchecklist.presentation.activity.NavigationHome.Destinations
import wottrich.github.io.smartchecklist.presentation.ui.StatusBarColor
import wottrich.github.io.smartchecklist.presentation.ui.checklistsettings.ChecklistSettingsScreen
import wottrich.github.io.smartchecklist.presentation.ui.content.HomeScreen
import wottrich.github.io.smartchecklist.baseui.navigation.defaultComposableAnimation
import wottrich.github.io.smartchecklist.quicklychecklist.navigation.quicklyChecklistNavigation
import wottrich.github.io.smartchecklist.intent.shareIntentText

class InvalidChecklistId : Exception("Checklist id must not be null")

@InternalCoroutinesApi
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
class HomeActivity : AppCompatActivity() {

    private var sharedNavHostController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController().also {
                sharedNavHostController = it
            }
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            navController.navigatorProvider += bottomSheetNavigator
            BottomSheetNavigator(bottomSheetNavigator = bottomSheetNavigator) {
                AppNavigator(navHostController = navController)
            }
            StatusBarColor()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        sharedNavHostController?.handleDeepLink(intent)
    }

    @Composable
    private fun BottomSheetNavigator(
        bottomSheetNavigator: BottomSheetNavigator,
        content: @Composable () -> Unit
    ) {
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            content = content
        )
    }

    @Composable
    private fun AppNavigator(navHostController: NavHostController) {
        AnimatedNavHost(
            navController = navHostController,
            startDestination = NavigationHome.route,
            builder = {
                homeNavigation(navHostController)
                newChecklistNavigation(navHostController)
                supportNavigation(navHostController)
                quicklyChecklistNavigation(
                    navHostController = navHostController,
                    onShareChecklistBack = {
                        shareIntentText(it)
                    }
                )
            }
        )
    }

    private fun NavGraphBuilder.homeNavigation(navHostController: NavHostController) {
        navigation(
            startDestination = NavigationHome.startDestination,
            route = NavigationHome.route
        ) {
            composable(
                route = Destinations.HomeScreen.route
            ) {
                HomeScreen(
                    onAddNewChecklist = {
                        navHostController.navigate(NavigatorNewChecklist.route)
                    },
                    onShareText = ::shareIntentText,
                    onChecklistSettings = {
                        val route =
                            Destinations.ChecklistSettingsScreen.route
                                .replace("{checklistId}", it)
                        navHostController.navigate(route)
                    },
                    onAboutUsClick = {
                        navHostController.navigate(Destinations.AboutUsScreen.route)
                    },
                    onHelpClick = {
                        navHostController.navigate(NavigationSupport.route)
                    }
                )
            }
            defaultComposableAnimation(
                route = Destinations.ChecklistSettingsScreen.route,
                arguments = listOf(
                    navArgument("checklistId") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val checklistId = navBackStackEntry.arguments?.getString("checklistId")
                    ?: throw InvalidChecklistId()
                ChecklistSettingsScreen(
                    checklistId,
                    onCloseScreen = {
                        navHostController.popBackStack()
                    }
                )
            }
            defaultComposableAnimation(
                route = Destinations.AboutUsScreen.route
            ) {
                AboutUsScreen(
                    model = AboutUsContentModel(
                        versionName = BuildConfig.VERSION_NAME,
                        versionNumber = BuildConfig.VERSION_CODE.toString()
                    ),
                    onBackButton = {
                        navHostController.popBackStack()
                    },
                    onVersionAppClick = { openPlayStore() }
                )
            }
        }
    }

    private fun openPlayStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }
}

object NavigationHome {
    val route = "NavigationHome"
    val startDestination = Destinations.HomeScreen.route

    sealed class Destinations(val route: String) {
        object HomeScreen : Destinations(route = "HomeScreen")
        object ChecklistSettingsScreen :
            Destinations(route = "ChecklistSettingsScreen/{checklistId}")

        object AboutUsScreen : Destinations(route = "AboutUsScreen")
    }
}