package wottrich.github.io.baseui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) {
        DarkPallet.build()
    } else {
        LightPallet.build()
    }

    MaterialTheme(
        colors = colors,
        content = content
    )

}