package wottrich.github.io.smartchecklist.commonuicompose.ripple

import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

class RippleStatusColor(private val isPositive: Boolean) : RippleTheme {

    @Composable
    override fun defaultColor(): Color {
        return if (isPositive) {
            SmartChecklistTheme.colors.status.positive
        } else {
            SmartChecklistTheme.colors.status.negative
        }
    }

    @Composable
    override fun rippleAlpha(): RippleAlpha {
        return RippleTheme.defaultRippleAlpha(
            contentColor = SmartChecklistTheme.colors.secondary,
            lightTheme = SmartChecklistTheme.colors.isLight
        )
    }
}