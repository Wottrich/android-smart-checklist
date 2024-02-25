package wottrich.github.io.smartchecklist.baseui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.TextTwoLine
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    endIcon: @Composable (RowScope.() -> Unit)? = null,
    startContent: @Composable () -> Unit,
    endContent: @Composable (() -> Unit)? = null,
    startIcon: @Composable (RowScope.() -> Unit)? = null,
) {
    CoreRow(
        modifier = modifier.wrapContentHeight()
    ) {
        startIcon?.let {
            startIcon()
            Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeFour))
        }
        Box(
            modifier = Modifier.weight(1F),
        ) {
            StyledText(textStyle = MaterialTheme.typography.h6, content = startContent)
        }
        endContent?.let {
            Box(
                modifier = Modifier.wrapContentWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                StyledText(textStyle = MaterialTheme.typography.h6, content = endContent)
            }
        }

        endIcon?.let {
            Spacer(modifier = Modifier.width(Dimens.BaseFour.SizeFour))
            endIcon()
        }
    }
}

@Composable
fun CoreRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.wrapContentHeight()
            .fillMaxWidth()
            .padding(
                horizontal = Dimens.BaseFour.SizeFour,
                vertical = Dimens.BaseFour.SizeFour
            )
    ) {
        content()
    }
}

@Composable
fun ListItemStartTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null,
) {
    RowTextContent(primary = primary, secondary = secondary, alignment = Alignment.Start)
}

@Composable
fun ListItemEndTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null
) {
    RowTextContent(primary = primary, secondary = secondary, alignment = Alignment.End)
}

@Composable
fun CenterTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null
) {
    RowTextContent(primary = primary, secondary = secondary, alignment = Alignment.CenterHorizontally)
}


@Composable
private fun RowTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null,
    alignment: Alignment.Horizontal
) {
    TextTwoLine(
        primary = {
            Text(
                text = primary.text,
                color = primary.color.copy(alpha = primary.alpha),
                fontWeight = primary.fontWeight,
                style = primary.style
            )
        },
        secondary = {
            secondary?.let {
                Text(
                    text = secondary.text,
                    color = secondary.color.copy(alpha = secondary.alpha),
                    fontWeight = secondary.fontWeight,
                    style = secondary.style
                )
            }
        }, horizontalAlignment = alignment
    )
}

@Composable
fun TextStateComponent(
    modifier: Modifier = Modifier,
    textState: RowDefaults.TextState
) {
    Text(
        modifier = modifier,
        text = textState.text,
        color = textState.color.copy(alpha = textState.alpha),
        fontWeight = textState.fontWeight,
        textAlign = textState.textAlign,
        style = textState.style
    )
}


object RowDefaults {

    @Composable
    fun title(
        text: String,
        alpha: Float = LocalContentAlpha.current,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal,
        textAlign: TextAlign? = null
    ): TextState {
        return TextState(
            text,
            alpha = alpha,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
            style = MaterialTheme.typography.h6
        )
    }

    @Composable
    fun description(
        text: String,
        alpha: Float = LocalContentAlpha.current,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal,
        textAlign: TextAlign? = null
    ): TextState {
        return TextState(
            text,
            alpha = alpha,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
            style = MaterialTheme.typography.subtitle1
        )
    }

    @Composable
    fun smallDescription(
        text: String,
        alpha: Float = LocalContentAlpha.current,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal,
        textAlign: TextAlign? = null
    ): TextState {
        return TextState(
            text,
            alpha = alpha,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
            style = MaterialTheme.typography.subtitle2
        )
    }

    @Composable
    fun text(
        text: String,
        alpha: Float = LocalContentAlpha.current,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal,
        textAlign: TextAlign? = null,
        style: TextStyle
    ): TextState {
        return TextState(
            text = text,
            alpha = alpha,
            color = color,
            fontWeight = fontWeight,
            textAlign = textAlign,
            style = style
        )
    }

    data class TextState(
        val text: String,
        val alpha: Float,
        val color: Color,
        val fontWeight: FontWeight,
        val textAlign: TextAlign?,
        val style: TextStyle,
    )
}