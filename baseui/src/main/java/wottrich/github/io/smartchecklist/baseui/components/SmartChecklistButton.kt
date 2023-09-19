package wottrich.github.io.smartchecklist.baseui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultButtonColors

@Composable
fun SmartChecklistButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonContentDescription: String? = null,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier
            .padding(all = Dimens.BaseFour.SizeTwo)
            .semantics {
                contentDescription = buttonContentDescription.orEmpty()
            },
        enabled = enabled,
        onClick = onClick,
        colors = defaultButtonColors(),
        content = content
    )
}