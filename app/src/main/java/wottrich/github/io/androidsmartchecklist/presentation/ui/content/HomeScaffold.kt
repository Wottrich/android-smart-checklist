package wottrich.github.io.androidsmartchecklist.presentation.ui.content

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import wottrich.github.io.androidsmartchecklist.R

@Composable
fun HomeScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = scaffoldState.drawerState,
    drawerContent: @Composable () -> Unit,
    onFloatingActionButtonClick: () -> Unit,
    onTitleContent: @Composable () -> Unit,
    actionContent: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
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
        },
        bottomBar = { BottomAppBar(cutoutShape = CircleShape, content = { /*empty*/ }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onFloatingActionButtonClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_add),
                    contentDescription = stringResource(
                        id = R.string.floating_action_content_description
                    ),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        drawerContent = {
            drawerContent()
        }
    ) {
        content()
    }
}