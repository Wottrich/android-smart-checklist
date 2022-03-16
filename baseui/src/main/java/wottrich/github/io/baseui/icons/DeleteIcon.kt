package wottrich.github.io.baseui.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
        tint = SmartChecklistTheme.colors.onSurface
    )
}

@Composable
fun DeleteIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
) {
    Icon(
        tint = tint,
        painter = painterResource(id = R.drawable.ic_delete),
        contentDescription = contentDescription,
        modifier = modifier
    )
}