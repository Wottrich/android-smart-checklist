package wottrich.github.io.baseui.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import wottrich.github.io.baseui.R
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun ClickableDeleteIcon(
    contentDescription: String,
    onClick: () -> Unit
) {
    DeleteIcon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        contentDescription = contentDescription,
    )
}

@Composable
fun DeleteIcon(
    modifier: Modifier,
    contentDescription: String? = null
) {
    DrawableIcon(
        modifier = modifier,
        drawableRes = R.drawable.ic_delete,
        contentDescription = contentDescription,
        tint = SmartChecklistTheme.colors.onSurface
    )
}