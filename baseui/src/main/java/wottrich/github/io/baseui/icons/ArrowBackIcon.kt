package wottrich.github.io.baseui.icons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun ArrowBackClickable(
    contentDescription: String,
    onClick: () -> Unit
) {
    ArrowBackIcon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        contentDescription = contentDescription
    )
}

@Composable
fun ArrowBackIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    VectorIcon(
        modifier = modifier,
        imageVector = Icons.Default.ArrowBack,
        contentDescription = contentDescription,
        tint = SmartChecklistTheme.colors.onSurface
    )
}