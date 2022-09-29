package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import github.io.wottrich.common.ui.compose.ripple.RippleStatusColor

@Composable
internal fun TaskRippleLayer(isTaskComplete: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRippleTheme provides RippleStatusColor(!isTaskComplete),
        content = content
    )
}