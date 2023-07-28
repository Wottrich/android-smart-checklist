package wottrich.github.io.smartchecklist.presentation.ui

import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import github.io.wottrich.smartchecklist.commonuicompose.ripple.RippleStatusColor

@Composable
internal fun TaskRippleLayer(isTaskComplete: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRippleTheme provides RippleStatusColor(!isTaskComplete),
        content = content
    )
}