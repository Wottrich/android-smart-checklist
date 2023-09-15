package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagchecklist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagSuggestionEmbedded
import wottrich.github.io.smartchecklist.suggestion.R
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.TagMoleculeState
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.TagsListingComponent

sealed class ChecklistTagsBottomSheetNavigationAction {
    object OnCloseScreen : ChecklistTagsBottomSheetNavigationAction()
}

@Composable
fun ChecklistTagsBottomSheetScreen(
    onNavigationAction: (ChecklistTagsBottomSheetNavigationAction) -> Unit
) {
    ApplicationTheme {
        Screen {
            onNavigationAction(ChecklistTagsBottomSheetNavigationAction.OnCloseScreen)
        }
    }
}

@Composable
private fun Screen(
    onAction: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        color = MaterialTheme.colors.background
    ) {
        Column {
            ChecklistTagsTopBar { onAction() }
            ListContent()
        }
    }
}

@Composable
private fun ChecklistTagsTopBar(onAction: () -> Unit) {
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.tags_checklist_tags_bottom_sheet_title))
        },
        navigationIcon = {
            IconButton(onClick = { onAction() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp
    )
}

@Composable
private fun ListContent() {
    TagsListingComponent(
        modifier = Modifier
            .padding(horizontal = Dimens.BaseFour.SizeThree)
            .padding(bottom = Dimens.BaseFour.SizeThree),
        tags = listOf(
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
            TagMoleculeState.SelectableTags(
                true,
                tagSuggestionEmbeddedContract = TagSuggestionEmbedded(
                    TagEntity(name = "Mercado"),
                    listOf()
                )
            ),
        ),
        onTagDetailClick = {}
    )
}

@Preview
@Composable
internal fun TagChecklistBottomSheetScreenPreview() {
    ApplicationTheme {
        Screen { }
    }
}