package wottrich.github.io.smartchecklist.suggestion.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import wottrich.github.io.smartchecklist.baseui.RowComponent
import wottrich.github.io.smartchecklist.baseui.previewparameters.BooleanPreviewParameter
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.ListItemStartTextContent
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagSuggestionEmbedded
import wottrich.github.io.smartchecklist.suggestion.R
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract

sealed class TagMoleculeState(open val tagSuggestionEmbeddedContract: TagSuggestionEmbeddedContract) {
    data class SelectableTags(
        val isSelected: Boolean,
        override val tagSuggestionEmbeddedContract: TagSuggestionEmbeddedContract
    ) : TagMoleculeState(tagSuggestionEmbeddedContract)

    data class SettingTags(override val tagSuggestionEmbeddedContract: TagSuggestionEmbeddedContract) : TagMoleculeState(tagSuggestionEmbeddedContract)
}

@Composable
internal fun TagMolecule(
    tagMoleculeState: TagMoleculeState,
    onClick: (TagContract) -> Unit
) {
    val tagName = tagMoleculeState.tagSuggestionEmbeddedContract.tag.name
    val suggestionCount = tagMoleculeState.tagSuggestionEmbeddedContract.suggestions.size
    RowComponent(
        modifier = Modifier
            .clickable { onClick(tagMoleculeState.tagSuggestionEmbeddedContract.tag) }
            .clip(TagComponentShape),
        leftContent = {
            ListItemStartTextContent(
                primary = RowDefaults.title(text = tagName),
                secondary = RowDefaults.description(
                    text = stringResource(
                        id = R.string.tag_component_suggestion_label,
                        suggestionCount.toString()
                    )
                )
            )
        },
        rightIconContent = {
            BuildRightIconContent(tagMoleculeState) {
                onClick(tagMoleculeState.tagSuggestionEmbeddedContract.tag)
            }
        }
    )
}

@Composable
private fun BuildRightIconContent(
    tagMoleculeState: TagMoleculeState,
    onCheckChange: () -> Unit
) {
    when (tagMoleculeState) {
        is TagMoleculeState.SelectableTags -> BuildSelectableTagsIcon(
            isSelected = tagMoleculeState.isSelected,
            onCheckChange = onCheckChange
        )

        is TagMoleculeState.SettingTags -> BuildSettingTagIcon(tagMoleculeState.tagSuggestionEmbeddedContract.tag.name)
    }
}

@Composable
private fun BuildSelectableTagsIcon(isSelected: Boolean, onCheckChange: () -> Unit) {
    val checkboxContentDescription = if (isSelected) R.string.tag_selectable_checkbox_check_to_uncheck_content_description
    else R.string.tag_selectable_checkbox_uncheck_to_check_content_description
    val checkboxIcon = if (isSelected) R.drawable.check_box_checked
    else R.drawable.check_box_unchecked
    Icon(
        tint = SmartChecklistTheme.colors.status.positive,
        painter = painterResource(id = checkboxIcon),
        contentDescription = stringResource(id = checkboxContentDescription),
        modifier = Modifier
            .size(Dimens.BaseFour.SizeEight)
            .clip(CircleShape)
            .clickable { onCheckChange() }
    )
}

@Composable
private fun BuildSettingTagIcon(tagName: String) {
    Icon(
        imageVector = Icons.Default.KeyboardArrowRight,
        contentDescription = stringResource(id = R.string.tag_setting_open_detail_content_description, tagName)
    )
}



@Preview
@Composable
fun TagSelectableMoleculePreview(
    @PreviewParameter(BooleanPreviewParameter::class) isSelected: Boolean
) {
    ApplicationTheme {
        Column {
            TagMolecule(
                TagMoleculeState.SelectableTags(
                    isSelected = isSelected,
                    tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                        tag = TagEntity(name = "Mercado"),
                        suggestions = listOf()
                    )
                )
            ) { }
        }
    }
}

@Preview
@Composable
fun TagSettingMoleculePreview() {
    ApplicationTheme {
        Column {
            TagMolecule(
                TagMoleculeState.SettingTags(
                    tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                        tag = TagEntity(name = "Mercado"),
                        suggestions = listOf()
                    )
                )
            ) { }
        }
    }
}