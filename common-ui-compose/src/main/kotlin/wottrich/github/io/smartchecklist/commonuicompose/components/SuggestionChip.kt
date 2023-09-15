package wottrich.github.io.smartchecklist.commonuicompose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Chip
import androidx.compose.material.ChipColors
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import wottrich.github.io.smartchecklist.baseui.StyledText
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme

@Composable
fun SuggestionChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean = true,
    label: String,
    onClick: (() -> Unit)? = null,
) {
    val borderColorByIsSelected = if (isSelected) ContentAlpha.high else ContentAlpha.disabled
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(getBackgroundColorByIsSelected(isSelected))
            .border(BorderStroke(1.dp, Color.Black.copy(alpha = borderColorByIsSelected)), shape = MaterialTheme.shapes.small)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .semantics { role = Role.Button }
    ) {
        Box(modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeOne, horizontal = Dimens.BaseFour.SizeThree)) {
            StyledText(
                textStyle = MaterialTheme.typography.h6,
                contentColor = getTextColorByIsSelected(isSelected)
            ) {
                Text(
                    text = label,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun getBackgroundColorByIsSelected(isSelected: Boolean): Color {
    return if (isSelected) SmartChecklistTheme.colors.primary
    else SmartChecklistTheme.colors.primary.copy(alpha = 0.38f)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun getChipColors(isSelected: Boolean): ChipColors {
    return if (isSelected) {
        ChipDefaults.chipColors(
            backgroundColor = SmartChecklistTheme.colors.primary,
            contentColor = SmartChecklistTheme.colors.onPrimary
        )
    } else {
        ChipDefaults.chipColors(
            backgroundColor = SmartChecklistTheme.colors.primary.copy(alpha = 0.38f),
            contentColor = SmartChecklistTheme.colors.onPrimary.copy(alpha = 0.38f)
        )
    }
}

@Composable
private fun getTextColorByIsSelected(isSelected: Boolean): Color {
    return if (isSelected) SmartChecklistTheme.colors.onPrimary
    else SmartChecklistTheme.colors.onPrimary.copy(alpha = 0.38f)
}

@Preview(showBackground = true)
@Composable
fun SuggestionChipPreviewDarkMode() {
    ApplicationTheme(isSystemInDarkTheme = true) {
        Surface {
            val modifier = Modifier.padding(horizontal = 4.dp)
            Row {
                SuggestionChip(label = "Mercado", modifier = modifier) {}
                SuggestionChip(label = "Shopping", modifier = modifier, isSelected = false) {}
                SuggestionChip(label = "Feira", modifier = modifier, isSelected = false) {}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuggestionChipPreview() {
    ApplicationTheme(isSystemInDarkTheme = false) {
        Surface {
            val modifier = Modifier.padding(horizontal = 4.dp)
            Row {
                SuggestionChip(label = "Mercado", modifier = modifier) {}
                SuggestionChip(label = "Shopping", modifier = modifier, isSelected = false) {}
                SuggestionChip(label = "Feira", modifier = modifier, isSelected = false) {}
            }
        }
    }
}