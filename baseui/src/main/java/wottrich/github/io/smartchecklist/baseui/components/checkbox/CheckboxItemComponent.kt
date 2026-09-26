package wottrich.github.io.smartchecklist.baseui.components.checkbox

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import wottrich.github.io.smartchecklist.baseui.components.completable.CompletableComponentRippleLayer
import wottrich.github.io.smartchecklist.baseui.components.completable.CompletableComponentSurface

@Composable
fun CheckboxItemComponent(
    label: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit,
    rightIconContent: @Composable (RowScope.() -> Unit)? = null
) {
    CompletableComponentRippleLayer(isCompleted = isCompleted) {
        CompletableComponentSurface(isCompleted = isCompleted) {
            CheckboxItemMolecule(
                label = label,
                isCompleted = isCompleted,
                onCheckChange = onCheckChange,
                rightIconContent = rightIconContent
            )
        }
    }
}