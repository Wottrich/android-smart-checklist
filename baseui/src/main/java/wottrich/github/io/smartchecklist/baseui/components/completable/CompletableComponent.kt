package wottrich.github.io.smartchecklist.baseui.components.completable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

@Composable
fun CompletableComponent(
    name: String,
    isCompleted: Boolean,
    onCheckChange: () -> Unit,
    leftIconContent: @Composable (RowScope.() -> Unit)? = null
) {

    CompletableComponentRippleLayer(isCompleted = isCompleted) {
        CompletableComponentSurface(isCompleted = isCompleted) {
            CompletableComponentMolecule(
                name = name,
                isCompleted = isCompleted,
                onCheckChange = onCheckChange,
                leftIconContent = leftIconContent
            )
        }
    }
}

