package wottrich.github.io.smartchecklist.baseui.ui.color

import androidx.compose.material.ButtonColors
import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun defaultButtonColors(): ButtonColors = DefaultButtonColor(
    backgroundColor = SmartChecklistTheme.colors.secondary,
    contentColor = Color.White,
    disabledBackgroundColor = SmartChecklistTheme.colors.secondaryVariant,
    disabledContentColor = Color.White.copy(alpha = ContentAlpha.disabled)
)

class DefaultButtonColor(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val disabledBackgroundColor: Color,
    private val disabledContentColor: Color
) : ButtonColors {

    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) backgroundColor else disabledBackgroundColor
        )
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(
            if (enabled) contentColor else disabledContentColor
        )
    }

}