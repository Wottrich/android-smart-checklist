package wottrich.github.io.smartchecklist.baseui.components.completable

import androidx.compose.material.LocalRippleConfiguration
import androidx.compose.material.RippleConfiguration
import androidx.compose.material.RippleDefaults
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
internal fun CompletableComponentRippleLayer(isCompleted: Boolean, content: @Composable () -> Unit) {
    val rippleConfiguration = getRippleConfiguration(isPositive = !isCompleted)
    CompositionLocalProvider(
        LocalRippleConfiguration provides rippleConfiguration,
        content = content
    )
}

@Composable
private fun getRippleConfiguration(isPositive: Boolean): RippleConfiguration {
    return RippleConfiguration(
        color = if (isPositive) {
            SmartChecklistTheme.colors.status.positive
        } else {
            SmartChecklistTheme.colors.status.negative
        },
        rippleAlpha = RippleDefaults.rippleAlpha(
            contentColor = SmartChecklistTheme.colors.secondary,
            lightTheme = SmartChecklistTheme.colors.isLight
        )
    )
}