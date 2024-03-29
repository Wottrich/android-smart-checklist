package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

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