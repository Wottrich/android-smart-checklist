package wottrich.github.io.smartchecklist.baseui.components.completable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import wottrich.github.io.smartchecklist.baseui.ui.Dimens.BaseFour

internal val CompletableItemShape = RoundedCornerShape(BaseFour.SizeTwo)

@Composable
internal fun CompletableComponentSurface(isCompleted: Boolean, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = BaseFour.SizeThree)
            .alpha(getItemAlpha(isCompleted = isCompleted)),
        shape = CompletableItemShape,
        elevation = 1.dp,
        content = content
    )
}

@Composable
private fun getItemAlpha(isCompleted: Boolean): Float {
    return if (isCompleted) ContentAlpha.medium else ContentAlpha.high
}