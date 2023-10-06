package wottrich.github.io.smartchecklist.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import wottrich.github.io.smartchecklist.navigation.AppNavigator
import wottrich.github.io.smartchecklist.navigation.NavigationHome
import wottrich.github.io.smartchecklist.presentation.ui.StatusBarColor

class InvalidChecklistId : Exception("Checklist id must not be null")

@InternalCoroutinesApi
@OptIn(ExperimentalMaterialNavigationApi::class)
class MainHostActivity : AppCompatActivity() {

    private val appNavigator: AppNavigator by inject()
    private var sharedNavHostController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController().also {
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
        NavHost(
            navController = navHostController,
            startDestination = NavigationHome.route,
            builder = {
                appNavigator.buildNavigators(this, navHostController)
            }
        )
    }
}