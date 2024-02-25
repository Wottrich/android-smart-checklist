package wottrich.github.io.smartchecklist.baseui.components.completable

import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import wottrich.github.io.smartchecklist.baseui.behaviour.ripple.RippleStatusColor

@Composable
internal fun CompletableComponentRippleLayer(isCompleted: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRippleTheme provides RippleStatusColor(!isCompleted),
        content = content
    )
}