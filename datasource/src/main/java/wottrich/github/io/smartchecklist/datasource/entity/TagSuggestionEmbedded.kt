package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.Embedded
import androidx.room.Relation
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract

data class TagSuggestionEmbedded(
    @Embedded
    override val tag: TagEntity,
    @Relation(
        parentColumn = "tag_uuid",
        entityColumn = "parent_uuid"
    )
    override val suggestions: List<SuggestionEntity>
) : TagSuggestionEmbeddedContract