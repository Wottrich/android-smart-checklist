package wottrich.github.io.androidsmartchecklist.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.ListItem
import wottrich.github.io.baseui.ui.ListItemStartTextContent
import wottrich.github.io.baseui.ui.RowDefaults
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

private val ChecklistItemBorderStroke = 1.dp
private val ChecklistItemShape = RoundedCornerShape(BaseFour.SizeTwo)

@Composable
fun HomeDrawerChecklistItemComponent(
    checklist: Checklist,
    onItemClick: () -> Unit
) {
    val modifier = Modifier
        .clickable(enabled = checklist.checklistId != null) {
            onItemClick()
        }

    ItemContent(modifier, checklist)
}

@Composable
private fun ItemContent(
    modifier: Modifier,
    checklist: Checklist
) {

    val surfaceModifier = Modifier.checklistItemPadding().addBorderIfSelected(checklist.isSelected)

    Surface(
        modifier = surfaceModifier,
        shape = ChecklistItemShape,
        elevation = BaseFour.SizeOne,
        color = SmartChecklistTheme.colors.onSurface
    ) {
        ListItem(
            modifier = modifier,
            startContent = {
                ListItemStartTextContent(
                    primary = RowDefaults.title(text = checklist.name),
                    secondary = RowDefaults.description(text = checklist.latestUpdateFormatted.orEmpty()),
                )
            }
        )
    }
}

private fun Modifier.addBorderIfSelected(isSelected: Boolean): Modifier =
    composed {
        if (isSelected) {
            this.border(
                width = ChecklistItemBorderStroke,
                color = SmartChecklistTheme.colors.onPrimary,
                shape = ChecklistItemShape
            )
        } else this
    }

fun Modifier.checklistItemPadding(): Modifier =
    composed { Modifier.padding(
        top = BaseFour.SizeTwo,
        start = BaseFour.SizeTwo,
        end = BaseFour.SizeTwo
    ) }