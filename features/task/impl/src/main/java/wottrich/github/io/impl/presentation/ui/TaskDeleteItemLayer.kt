package wottrich.github.io.impl.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
internal fun TaskDeleteItemLayer(
    width: Float,
    height: Float,
    progress: Float
) {
    val deleteColor = SmartChecklistTheme.colors.status.negative.copy(alpha = 0.5f)
    Canvas(
        modifier = Modifier,
        onDraw = {
            val topLeft = Offset(width * progress, height)
            drawRoundRect(
                color = deleteColor,
                topLeft = topLeft,
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    )
}