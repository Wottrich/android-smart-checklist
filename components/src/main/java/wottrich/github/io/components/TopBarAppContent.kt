package wottrich.github.io.components

import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun TopBarContent(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    navigationIconAction: () -> Unit
) {
    TopAppBar(
        title = {
            title()
        },
        navigationIcon = {
            IconButton(onClick = { navigationIconAction() }) {
                navigationIcon()
            }
        }
    )
}