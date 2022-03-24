package wottrich.github.io.androidsmartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.androidsmartchecklist.R.string

@Composable
fun HelpAboutUsContent(
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        AboutUsTextButtonComponent(onAboutUsClick)
        HelpTextButtonContent(onHelpClick)
    }
}

@Composable
private fun RowScope.AboutUsTextButtonComponent(onAboutUsClick: () -> Unit) {
    TextButton(
        modifier = Modifier.weight(1f),
        onClick = { onAboutUsClick() }
    ) {
        Text(text = stringResource(id = string.drawer_bottom_items_about_us))
    }
}

@Composable
private fun RowScope.HelpTextButtonContent(onHelpClick: () -> Unit) {
    TextButton(
        modifier = Modifier.weight(1f),
        onClick = { onHelpClick() }
    ) {
        Text(text = stringResource(id = string.drawer_bottom_items_help))
    }
}