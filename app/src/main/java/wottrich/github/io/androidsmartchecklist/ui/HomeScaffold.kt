package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.androidsmartchecklist.R

@Composable
fun HomeScaffold(
    onFloatingActionButtonClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(id = R.string.label_home_fragment))
            })
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
        isFloatingActionButtonDocked = true
    ) {
        content()
    }
}