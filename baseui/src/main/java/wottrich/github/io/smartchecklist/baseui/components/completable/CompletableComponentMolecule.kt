package wottrich.github.io.smartchecklist.baseui.components.completable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.RowComponent
import wottrich.github.io.smartchecklist.baseui.TextOneLine
import wottrich.github.io.smartchecklist.baseui.icons.IconCompletableContent

@Composable
fun CompletableComponentMolecule(
    name: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit,
    leftIconContent: @Composable (RowScope.() -> Unit)? = null
) {
    RowComponent(
        modifier = Modifier.taskMoleculeModifier(onCheckChange),
        leftIconContent = leftIconContent,
        leftContent = { LeftContent(name = name, isCompleted = isCompleted) },
        rightIconContent = {
            RightIconContent(
                name = name,
                isCompleted = isCompleted,
                onCheckChange = onCheckChange
            )
        }
    )
}

private fun Modifier.taskMoleculeModifier(
    onCheckChange: () -> Unit,
): Modifier {
    return this
        .clickable { onCheckChange() }
        .clip(CompletableItemShape)
}

@Composable
private fun LeftContent(name: String, isCompleted: Boolean) {
    TextOneLine(
        primary = {
            Text(
                text = name,
                textDecoration = getTextDecoration(isCompleted)
            )
        }
    )
}

@Composable
private fun RightIconContent(name: String, isCompleted: Boolean, onCheckChange: () -> Unit) {
    IconCompletableContent(
        name = name,
        isCompleted = isCompleted
    ) {
        onCheckChange()
    }
}

private fun getTextDecoration(isCompleted: Boolean): TextDecoration {
    return if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
}

@Preview
@Composable
fun TaskMoleculePreview() {
    CompletableComponentMolecule(
        name = "Task 1",
        isCompleted = false,
        onCheckChange = {},
    )
}