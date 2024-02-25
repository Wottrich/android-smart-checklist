package wottrich.github.io.smartchecklist.baseui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun TopBarContent(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    navigationIconAction: () -> Unit = {},
    actionsContent: @Composable (RowScope.() -> Unit)? = null
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
        actions = actionsContent ?: {},
        backgroundColor = SmartChecklistTheme.colors.background
    )
}