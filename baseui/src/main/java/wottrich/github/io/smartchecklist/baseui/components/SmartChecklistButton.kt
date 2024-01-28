package wottrich.github.io.smartchecklist.baseui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultButtonColors
import wottrich.github.io.smartchecklist.baseui.ui.color.negativeButtonColor

enum class ButtonVariant {
    DEFAULT, NEGATIVE;
}

@Composable
fun SmartChecklistButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonContentDescription: String? = null,
    buttonVariant: ButtonVariant = ButtonVariant.DEFAULT,
    colors: ButtonColors = getColorsByButtonVariant(buttonVariant),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier
            .padding(horizontal = Dimens.BaseFour.SizeTwo)
            .padding(bottom = Dimens.BaseFour.SizeTwo)
            .semantics {
                contentDescription = buttonContentDescription.orEmpty()
            },
        enabled = enabled,
        onClick = onClick,
        colors = colors,
        content = content
    )
}

@Composable
private fun getColorsByButtonVariant(buttonVariant: ButtonVariant) =
    when (buttonVariant) {
        ButtonVariant.DEFAULT -> defaultButtonColors()
        ButtonVariant.NEGATIVE -> negativeButtonColor()
    }

@Composable
@Preview
private fun SmartChecklistButtonPreview() {
    ApplicationTheme {
        SmartChecklistButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar")
        }
    }
}

@Composable
@Preview
private fun SmartChecklistButtonNwgativePreview() {
    ApplicationTheme {
        SmartChecklistButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            buttonVariant = ButtonVariant.NEGATIVE
        ) {
            Text("Confirmar")
        }
    }
}