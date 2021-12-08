package wottrich.github.io.baseui.ui.color

import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun defaultOutlinedTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    cursorColor = MaterialTheme.colors.onPrimary,
    unfocusedBorderColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.medium),
    focusedBorderColor = MaterialTheme.colors.secondary.copy(alpha = ContentAlpha.high)
)