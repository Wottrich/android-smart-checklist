package wottrich.github.io.smartchecklist.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.navigation.AppNavigator
import wottrich.github.io.smartchecklist.navigation.NavigationHome

@InternalCoroutinesApi
class MainHostActivity : AppCompatActivity() {

    private val appNavigator: AppNavigator by inject()
    private var sharedNavHostController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController().also {
                sharedNavHostController = it
            }
            ApplicationTheme {
                Surface(color = SmartChecklistTheme.colors.background) {
                    Column {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
                        AppNavigator(navHostController = navController)
                        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        sharedNavHostController?.handleDeepLink(intent)
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