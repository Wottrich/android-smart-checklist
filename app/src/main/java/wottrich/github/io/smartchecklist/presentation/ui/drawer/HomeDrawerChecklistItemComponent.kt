package wottrich.github.io.smartchecklist.presentation.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.ListItem
import wottrich.github.io.smartchecklist.baseui.ui.ListItemEndTextContent
import wottrich.github.io.smartchecklist.baseui.ui.ListItemStartTextContent
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
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
fun HomeDrawerChecklistItemComponent(
    isEditModeEnabled: Boolean,
    checklistItemModel: HomeDrawerChecklistItemModel,
    onItemClick: () -> Unit,
    onDeleteItemClicked: () -> Unit
) {
    val modifier = Modifier
        .clickable { onItemClick() }

    ItemContent(
        modifier,
        isEditModeEnabled,
        checklistItemModel,
        onDeleteItemClicked
    )
    Divider(Modifier.padding(start = Dimens.BaseFour.SizeFive))
}

@Composable
private fun ItemContent(
    modifier: Modifier,
    isEditModeEnabled: Boolean,
    checklistItemModel: HomeDrawerChecklistItemModel,
    onDeleteItemClicked: () -> Unit
) {
    ListItem(
        modifier = modifier.background(Color.Transparent),
        startIcon = {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                DeleteIcon(
                    isEditModeEnabled = isEditModeEnabled,
                    onDeleteItemClicked = onDeleteItemClicked
                )
            }
        },
        startContent = {
            ListItemStartTextContent(
                primary = RowDefaults.title(text = checklistItemModel.checklistName),
                secondary = RowDefaults.description(
                    text = getSecondaryText(hasIncompleteItem = checklistItemModel.hasUncompletedTask),
                    color = getSecondaryColor(hasIncompleteItem = checklistItemModel.hasUncompletedTask)
                )
            )
        },
        endContent = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                ListItemEndTextContent(
                    primary = RowDefaults.description(text = checklistItemModel.completeCountLabel)
                )
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
                if (checklistItemModel.isChecklistSelected) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SmartChecklistTheme.colors.secondary
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                horizontal = Dimens.BaseFour.SizeThree,
                                vertical = Dimens.BaseFour.SizeOne
                            )
                        ) {
                            ListItemEndTextContent(
                                primary = RowDefaults.description(
                                    text = stringResource(id = R.string.home_drawer_checklist_selected_label),
                                    color = SmartChecklistTheme.colors.onSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun getSecondaryText(hasIncompleteItem: Boolean): String {
    val textId = if (hasIncompleteItem) {
        R.string.drawer_checklist_uncompleted
    } else {
        R.string.drawer_checklist_completed
    }
    return stringResource(id = textId)
}

@Composable
private fun getSecondaryColor(hasIncompleteItem: Boolean): Color {
    return if (hasIncompleteItem) {
        SmartChecklistTheme.colors.status.negative
    } else {
        SmartChecklistTheme.colors.status.positive
    }
}

@Composable
private fun RowScope.DeleteIcon(
    isEditModeEnabled: Boolean,
    onDeleteItemClicked: () -> Unit
) {
    AnimatedVisibility(
        visible = isEditModeEnabled
    ) {
        Icon(
            modifier = Modifier.clickable { onDeleteItemClicked() },
            imageVector = Icons.Default.Delete,
            contentDescription = null
        )
    }
}
