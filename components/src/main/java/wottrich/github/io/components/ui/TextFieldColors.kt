package wottrich.github.io.components.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun defaultOutlinedTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedBorderColor = MaterialTheme.colors.secondaryVariant,
    cursorColor = MaterialTheme.colors.secondaryVariant
)