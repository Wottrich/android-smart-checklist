package wottrich.github.io.smartchecklist.baseui.ui.pallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import wottrich.github.io.smartchecklist.baseui.ui.LocalSmartChecklistColors

object SmartChecklistTheme {

    val colors: SmartChecklistColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSmartChecklistColors.current

}