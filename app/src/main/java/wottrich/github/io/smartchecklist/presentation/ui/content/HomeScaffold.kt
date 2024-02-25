package wottrich.github.io.smartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.DrawerState
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun HomeScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = scaffoldState.drawerState,
    drawerContent: @Composable () -> Unit,
    onTitleContent: @Composable () -> Unit,
    actionContent: @Composable RowScope.() -> Unit,
    snackbarHost: @Composable (SnackbarHostState) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = snackbarHost,
        topBar = {},
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        drawerBackgroundColor = SmartChecklistTheme.colors.background,
        drawerContent = {
            drawerContent()
        },
        bottomBar = {
            HomeTopBar(coroutineScope, drawerState, onTitleContent, actionContent)
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun HomeTopBar(
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    onTitleContent: @Composable () -> Unit,
    actionContent: @Composable() (RowScope.() -> Unit)
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        title = {
            onTitleContent()
        },
        actions = actionContent
    )
}