package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.androidsmartchecklist.R

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 31/08/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun HomeScaffold(
    onFloatingActionButtonClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Bem-vindo") 
            })
        },
        bottomBar = { BottomAppBar(cutoutShape = CircleShape, content = { }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onFloatingActionButtonClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_round_add),
                    contentDescription = "Criar checklist"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        content()
    }
}