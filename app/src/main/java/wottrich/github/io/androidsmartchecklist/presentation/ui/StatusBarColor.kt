package wottrich.github.io.androidsmartchecklist.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun StatusBarColor() {
    ApplicationTheme {
        val rememberSystemUiController = rememberSystemUiController()
        val useDarkItems = SmartChecklistTheme.colors.isLight
        val backgroundStatusBarColor = SmartChecklistTheme.colors.background
        SideEffect {
            rememberSystemUiController.setStatusBarColor(
                color = backgroundStatusBarColor,
                darkIcons = useDarkItems
            )
        }
    }
}