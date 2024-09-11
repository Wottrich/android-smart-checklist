package wottrich.github.io.smartchecklist.baseui.components.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.RowComponent
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.components.completable.CompletableItemShape
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.fonts.robotoFontFamily
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun CheckboxItemMolecule(
    label: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit,
    rightIconContent: @Composable (RowScope.() -> Unit)? = null
) {
    RowComponent(
        modifier = Modifier.checkboxItemMoleculeModifier(isCompleted, onCheckChange),
        leftIconContent = {
            IconCheckboxSelectorContent(
                label = label,
                isCompleted = isCompleted,
                onCheckChange = onCheckChange
            )
        },
        leftContent = { LeftContent(name = label, isCompleted = isCompleted) },
        rightIconContent = rightIconContent
    )
}

@Composable
private fun LeftContent(name: String, isCompleted: Boolean) {
    TextOneLine(
        primary = {
            Text(
                text = name,
                textDecoration = getTextDecoration(isCompleted),
                fontFamily = robotoFontFamily,
                fontWeight = FontWeight.Light
            )
        }
    )
}

private fun getTextDecoration(isCompleted: Boolean): TextDecoration {
    return if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
}

@Composable
private fun Modifier.checkboxItemMoleculeModifier(
    isCompleted: Boolean,
    onCheckChange: () -> Unit,
): Modifier {
    return this
        .clickable { onCheckChange() }
        .clip(CompletableItemShape)
        .backgroundByCompletedStatus(isCompleted)
}

private fun Modifier.backgroundByCompletedStatus(isCompleted: Boolean) =
    this.composed {
        background(
            color = if (isCompleted) {
                SmartChecklistTheme.colors.status.positive.copy(alpha = ContentAlpha.medium)
            } else {
                SmartChecklistTheme.colors.background
            }
        )
    }

@Preview
@Composable
fun CheckboxItemMoleculePreview() {
    ApplicationTheme {
        Column(
            modifier = Modifier
                .background(SmartChecklistTheme.colors.background)
                .fillMaxWidth()
        ) {
            CheckboxItemMolecule(
                label = "Checkbox Item",
                isCompleted = false,
                onCheckChange = {}
            )
        }
    }
}