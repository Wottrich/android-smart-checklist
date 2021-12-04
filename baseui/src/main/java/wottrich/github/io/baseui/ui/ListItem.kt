package wottrich.github.io.baseui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import wottrich.github.io.baseui.StyledText
import wottrich.github.io.baseui.TwoLine
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

/**
 * A RegularRow displays a surface area related to raw data, menu items, and any basic list content.
 * It's the most common implementation of [FoundationRow], containing slots for title, detail,
 * rightAction and leftIcon.
 *
 * @param modifier - The modifier to be applied to the RegularRow.
 * @param endIcon - First placed composable. This should typically be an Icon with small size or
 * an Image. The default layout here is a [Row], so icon can be aligned centralized if is an
 * avatar or top aligned if is an small icon.
 * @param endContent - contextual content to complements the title. This should typically be a
 * Text or [TwoLine]
 * display
 * @param startIcon - The right most aligned composable. Used to be an Icon with small size that
 * indicates the action of the clickable Row, such as a navigation icon or a copy paste icon.
 * @param startContent - Second left placed composable and main space, necessary for all rows. This should
 * typically be a Text or [TwoLine]
 */
@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    endIcon: @Composable (RowScope.() -> Unit)? = null,
    startContent: @Composable () -> Unit,
    endContent: @Composable (() -> Unit)? = null,
    startIcon: @Composable (RowScope.() -> Unit)? = null,
) {
    FoundationRow(
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

/**
 * Base implementation of a [Row] used to apply a surface area and default paddings.
 * See (https://www.figma.com/file/POSO4IJ8uhT69HgFTCz27z/Rows?node-id=299%3A2047) for official
 * documentation
 *
 * @param modifier - The modifier to be applied to the FoundationRow.
 */
@Composable
fun FoundationRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface {
        Row(
            modifier = modifier.composed {
                wrapContentHeight()
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.BaseFour.SizeFour,
                        vertical = Dimens.BaseFour.SizeFour
                    )
            }
        ) {
            content()
        }
    }
}


/**
 * Used to create the base content for a row using only texts, the content will  be aligned at the start
 * @param primary - The primary information of the content
 * @param secondary -The complementary information of the primary of content
 */
@Composable
fun ListItemStartTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null,
) {
    RowTextContent(primary = primary, secondary = secondary, alignment = Alignment.Start)
}

/**
 * Used to create the base content for a row using only texts, the content will be aligned at the end
 * @param primary - The primary information of the content
 * @param secondary -The complementary information of the primary of content
 */
@Composable
fun ListItemEndTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null
) {
    RowTextContent(primary = primary, secondary = secondary, alignment = Alignment.End)
}


@Composable
private fun RowTextContent(
    primary: RowDefaults.TextState,
    secondary: RowDefaults.TextState? = null,
    alignment: Alignment.Horizontal
) {
    TwoLine(
        primary = {
            Text(
                text = primary.text,
                color = primary.color,
                fontWeight = primary.fontWeight,
                style = primary.style
            )
        },
        secondary = {
            secondary?.let {
                Text(
                    text = secondary.text,
                    color = secondary.color,
                    fontWeight = secondary.fontWeight,
                    style = secondary.style
                )
            }
        }, horizontalAlignment = alignment
    )
}


object RowDefaults {
    /**
     * Create a TextState with a row's title style
     * @param text - The text that should be displayed
     * @param color - The color of the text
     * @param fontWeight - The weight of the text. Ex.: Normal, Bold, Italic...
     */
    @Composable
    fun title(
        text: String,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal
    ): TextState {
        return TextState(
            text,
            color = color,
            fontWeight = fontWeight,
            style = MaterialTheme.typography.h6
        )
    }

    /**
     * Create a TextState with a row's subtitle style
     * @param text - The text that should be displayed
     * @param color - The color of the text
     * @param fontWeight - The weight of the text. Ex.: Normal, Bold, Italic...
     */
    @Composable
    fun description(
        text: String,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal
    ): TextState {
        return TextState(
            text,
            color = color,
            fontWeight = fontWeight,
            style = MaterialTheme.typography.subtitle1
        )
    }

    /**
     * Create a full customized Text for a row. Use this when you want to create a new Style.
     * @param text - The text that should be displayed
     * @param color - The color of the text
     * @param fontWeight - The weight of the text. Ex.: Normal, Bold, Italic...
     * @param style - The style of the font
     */
    @Composable
    fun text(
        text: String,
        color: Color = SmartChecklistTheme.colors.onPrimary,
        fontWeight: FontWeight = FontWeight.Normal,
        style: TextStyle
    ): TextState {
        return TextState(text = text, color = color, fontWeight = fontWeight, style = style)
    }

    /**
     * Represents all customizations available to setup a text in a row
     * @param text - The text that should be displayed
     * @param color - The color of the text
     * @param fontWeight - The weight of the text. Ex.: Normal, Bold, Italic...
     * @param style - The style of the font
     */
    data class TextState(
        val text: String,
        val color: Color,
        val fontWeight: FontWeight,
        val style: TextStyle
    )
}