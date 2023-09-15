package wottrich.github.io.smartchecklist.suggestion.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.datasource.entity.SuggestionEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagSuggestionEmbedded
import wottrich.github.io.smartchecklist.suggestion.TagContract

@Composable
fun TagsListingComponent(
    tags: List<TagMoleculeState>,
    modifier: Modifier = Modifier,
    onTagDetailClick: (tagUuid: String) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(tags, key = { it.tagSuggestionEmbeddedContract.tag.tagUuid }) {
            TagItem(
                state = it,
                onTagDetailClick = { tag ->
                    onTagDetailClick(tag.tagUuid)
                }
            )
        }
    }
}

@Composable
private fun TagItem(
    state: TagMoleculeState,
    onTagDetailClick: (TagContract) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
        TagMolecule(state, onClick = onTagDetailClick)
    }
}

@Preview
@Composable
fun TagsListingComponentPreview() {
    ApplicationTheme {
        TagsListingComponent(
            tags = listOf(
                TagMoleculeState.SettingTags(
                    TagSuggestionEmbedded(
                        TagEntity(name = "Mercado"),
                        listOf()
                    )
                ),
                TagMoleculeState.SelectableTags(
                    isSelected = true,
                    TagSuggestionEmbedded(
                        TagEntity(name = "Feira"),
                        listOf(
                            SuggestionEntity(name = "Tomate", parentUuid = "")
                        )
                    )
                ),
                TagMoleculeState.SelectableTags(
                    isSelected = false,
                    TagSuggestionEmbedded(
                        TagEntity(name = "Feira"),
                        listOf(
                            SuggestionEntity(name = "Tomate", parentUuid = "")
                        )
                    )
                )
            ),
            onTagDetailClick = {}
        )
    }
}