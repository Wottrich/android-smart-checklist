package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import wottrich.github.io.smartchecklist.suggestion.TagContract

data class ChecklistTagsEmbedded(
    @Embedded
    override val checklist: NewChecklist,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "tag_uuid",
        associateBy = Junction(ChecklistTagsCrossRef::class)
    )
    override val tags: List<TagEntity>
) : ChecklistTagsEmbeddedContract