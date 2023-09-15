package wottrich.github.io.smartchecklist.baseui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun TopBarContent(
    navigationIcon: @Composable () -> Unit = {},
    navigationIconAction: () -> Unit = {},
    actionsContent: @Composable (RowScope.() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    TopAppBar(
        title = {
            title()
        },
        navigationIcon = {
            IconButton(onClick = { navigationIconAction() }) {
                navigationIcon()
            }
        },
        actions = actionsContent ?: {}
    )
}