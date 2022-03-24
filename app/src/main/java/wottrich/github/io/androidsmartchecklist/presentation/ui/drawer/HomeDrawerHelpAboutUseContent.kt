package wottrich.github.io.androidsmartchecklist.presentation.ui.drawer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HelpAboutUsContent(
    onAboutUsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(
            modifier = Modifier.weight(1f),
            onClick = { onAboutUsClick() }
        ) {
            Text(text = "Sobre nós")
        }

        TextButton(
            modifier = Modifier.weight(1f),
            onClick = { onHelpClick() }
        ) {
            Text(text = "Ajuda")
        }
    }
}