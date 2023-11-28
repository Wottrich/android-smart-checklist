package wottrich.github.io.smartchecklist.presentation.ui.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import wottrich.github.io.smartchecklist.baseui.RowComponent
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.ListItemEndTextContent
import wottrich.github.io.smartchecklist.baseui.ui.ListItemStartTextContent
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 01/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

private val checklistItemShape = RoundedCornerShape(Dimens.BaseFour.SizeTwo)

@Composable
fun HomeDrawerChecklistItemComponent(
    isEditModeEnabled: Boolean,
    checklistItemModel: HomeDrawerChecklistItemModel,
    onItemClick: () -> Unit,
    onDeleteItemClicked: () -> Unit
) {
    val modifier = Modifier
        .clickable { onItemClick() }

    ChecklistItemContent(
        modifier,
        isEditModeEnabled,
        checklistItemModel,
        onDeleteItemClicked
    )
}

@Composable
private fun ChecklistItemContent(
    modifier: Modifier,
    isEditModeEnabled: Boolean,
    checklistItemModel: HomeDrawerChecklistItemModel,
    onDeleteItemClicked: () -> Unit
) {
    ChecklistItemAlphaProvider(
        isEditModeEnabled,
        checklistItemModel.isChecklistSelected,
    ) {
        ChecklistItemSurface(
            content = {
                ChecklistItemMolecule(
                    modifier = modifier,
                    isEditModeEnabled = isEditModeEnabled,
                    checklistItemModel = checklistItemModel,
                    onDeleteItemClicked = onDeleteItemClicked
                )
            }
        )
    }
}

@Composable
private fun ChecklistItemAlphaProvider(
    isEditModeEnabled: Boolean,
    isChecklistSelected: Boolean,
    content: @Composable () -> Unit
) {
    val alpha = if (isEditModeEnabled || isChecklistSelected) ContentAlpha.high
    else ContentAlpha.disabled
    CompositionLocalProvider(
        LocalContentAlpha provides alpha,
        content = content
    )
}

@Composable
private fun ChecklistItemSurface(
    content: @Composable () -> Unit
) {
    Surface(
        shape = checklistItemShape,
        elevation = 1.dp,
    ) {
        content()
    }
}

@Composable
private fun ChecklistItemMolecule(
    modifier: Modifier,
    isEditModeEnabled: Boolean,
    checklistItemModel: HomeDrawerChecklistItemModel,
    onDeleteItemClicked: () -> Unit
) {
    RowComponent(
        modifier = modifier,
        leftIconContent = {
            if (checklistItemModel.isChecklistSelected) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
            }
        },
        leftContent = {
            ListItemStartTextContent(
                primary = RowDefaults.title(
                    text = checklistItemModel.checklistName,
                    alpha = LocalContentAlpha.current
                )
            )
        },
        rightIconContent = {
            DeleteIcon(
                isEditModeEnabled = isEditModeEnabled,
                onDeleteItemClicked = onDeleteItemClicked
            )
        },
        rightContent = {
            ListItemEndTextContent(
                primary = RowDefaults.smallDescription(
                    text = checklistItemModel.completeCountLabel,
                    color = getSecondaryColor(
                        hasIncompleteItem = checklistItemModel.hasUncompletedTask
                    )
                )
            )
        }
    )
}

@Composable
private fun getSecondaryColor(
    hasIncompleteItem: Boolean
): Color {
    return if (hasIncompleteItem) {
        SmartChecklistTheme.colors.status.negative
    } else {
        SmartChecklistTheme.colors.status.positive
    }.copy(alpha = LocalContentAlpha.current)
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

class HomeDrawerChecklistItemComponentPreviewParam :
    PreviewParameterProvider<Pair<Boolean, HomeDrawerChecklistItemModel>> {
    override val values: Sequence<Pair<Boolean, HomeDrawerChecklistItemModel>> = sequenceOf(
        Pair(
            true,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "2/2",
                isChecklistSelected = false,
                hasUncompletedTask = false
            )
        ),
        Pair(
            true,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "1/2",
                isChecklistSelected = false,
                hasUncompletedTask = true
            )
        ),
        Pair(
            true,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "2/2",
                isChecklistSelected = true,
                hasUncompletedTask = false
            )
        ),
        Pair(
            true,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "1/2",
                isChecklistSelected = true,
                hasUncompletedTask = true
            )
        ),
        Pair(
            false,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "2/2",
                isChecklistSelected = false,
                hasUncompletedTask = false
            )
        ),
        Pair(
            false,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "1/2",
                isChecklistSelected = false,
                hasUncompletedTask = true
            )
        ),
        Pair(
            false,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "2/2",
                isChecklistSelected = true,
                hasUncompletedTask = false
            )
        ),
        Pair(
            false,
            HomeDrawerChecklistItemModel(
                checklistUuid = "123",
                checklistName = "Checklist Name",
                completeCountLabel = "1/2",
                isChecklistSelected = true,
                hasUncompletedTask = true
            )
        )
    )

}

@Preview
@Composable
private fun HomeDrawerChecklistItemComponentPreview(
    @PreviewParameter(HomeDrawerChecklistItemComponentPreviewParam::class) params: Pair<Boolean, HomeDrawerChecklistItemModel>,
) {
    ApplicationTheme(params.first) {
        Surface {
            HomeDrawerChecklistItemComponent(
                isEditModeEnabled = false,
                checklistItemModel = params.second,
                onItemClick = { },
                onDeleteItemClicked = { }
            )
        }
    }
}