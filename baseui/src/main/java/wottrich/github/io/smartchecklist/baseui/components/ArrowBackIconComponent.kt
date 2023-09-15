package wottrich.github.io.smartchecklist.baseui.components

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import wottrich.github.io.smartchecklist.baseui.R

enum class IconType {
    NORMAL, BUTTON
}

@Composable
fun ArrowBackIconComponent(
    type: IconType = IconType.NORMAL,
    action: (() -> Unit)? = null
) {
    BuildWrapperByIconType(
        type,
        action
    )
}

@Composable
private fun BuildWrapperByIconType(type: IconType, action: (() -> Unit)?) {
    when (type) {
        IconType.NORMAL -> ArrowBackIconComponent(Modifier.clickable(enabled = action != null) { action?.invoke() })
        IconType.BUTTON -> IconButton(onClick = { action?.invoke() }) {
            ArrowBackIconComponent()
        }
    }
}

@Composable
private fun ArrowBackIconComponent(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.ArrowBack,
        contentDescription = stringResource(id = R.string.arrow_back_content_description)
    )
}