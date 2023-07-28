package wottrich.github.io.impl.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import wottrich.github.io.smartchecklist.baseui.ui.Dimens.BaseFour

internal val TaskItemShape = RoundedCornerShape(BaseFour.SizeTwo)

@Composable
internal fun TaskSurface(isTaskComplete: Boolean, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = BaseFour.SizeThree)
            .alpha(getItemAlpha(isCompleted = isTaskComplete)),
        shape = TaskItemShape,
        elevation = 1.dp,
        content = content
    )
}

@Composable
private fun getItemAlpha(isCompleted: Boolean): Float {
    return if (isCompleted) ContentAlpha.medium else ContentAlpha.high
}