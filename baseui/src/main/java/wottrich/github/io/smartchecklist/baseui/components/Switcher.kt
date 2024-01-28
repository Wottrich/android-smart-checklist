package wottrich.github.io.smartchecklist.baseui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
private fun <T> Switcher(
    state: T?,
    condition: (T) -> Float,
    firstOption: @Composable() RowScope.() -> Unit,
    secondOption: @Composable() RowScope.() -> Unit,
) {
    val transition = updateTransition(state, "UpdateTransition")
    val animatedFloat = transition.animateFloat(
        transitionSpec = { tween(500) },
        label = "UpdateTransitionAnimation"
    ) {
        if (it == null) -1f else condition(it)
    }
    val align = BiasAlignment(animatedFloat.value, 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.BaseFour.SizeTwo))
            .background(SmartChecklistTheme.colors.surface)
            .height(Dimens.BaseFour.SizeTen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(align)
                .clip(RoundedCornerShape(Dimens.BaseFour.SizeTwo))
                .background(
                    SmartChecklistTheme.colors.secondaryVariant.copy(
                        alpha = if (state != null) ContentAlpha.high else ContentAlpha.disabled
                    )
                )
        )
        Row(modifier = Modifier.fillMaxHeight()) {
            firstOption()
            secondOption()
        }
    }
}

@Composable
private fun RowScope.SwitcherOptionsContent(label: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(RoundedCornerShape(Dimens.BaseFour.SizeTwo))
            .clickable { onClick() }
            .padding(vertical = Dimens.BaseFour.SizeTwo)
            .padding(end = Dimens.BaseFour.SizeTwo),
        text = label,
        color = SmartChecklistTheme.colors.onSurface,
        textAlign = TextAlign.Center
    )
}