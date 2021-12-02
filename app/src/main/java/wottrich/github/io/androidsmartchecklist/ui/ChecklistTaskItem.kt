package wottrich.github.io.androidsmartchecklist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.ListItem
import wottrich.github.io.baseui.ui.ListItemStartTextContent
import wottrich.github.io.baseui.ui.RowDefaults
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Composable
fun ChecklistTaskItem(
    checklist: Checklist,
    onItemClick: () -> Unit
) {
    val surfaceModifier = Modifier.padding(
        top = BaseFour.SizeTwo,
        start = BaseFour.SizeTwo,
        end = BaseFour.SizeTwo
    )

    val modifier = Modifier
        .clickable(enabled = checklist.checklistId != null) {
            onItemClick()
        }

    ItemContent(modifier, surfaceModifier, checklist)
}

@Composable
private fun ItemContent(
    modifier: Modifier,
    surfaceModifier: Modifier,
    checklist: Checklist
) {
    Surface(
        modifier = surfaceModifier,
        shape = RoundedCornerShape(BaseFour.SizeTwo),
        elevation = BaseFour.SizeOne
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