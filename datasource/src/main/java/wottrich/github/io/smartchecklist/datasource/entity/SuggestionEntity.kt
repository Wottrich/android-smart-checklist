package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import wottrich.github.io.smartchecklist.suggestion.SuggestionContract
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

@Entity(
    tableName = "suggestion",
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = TagEntity::class,
            parentColumns = arrayOf("tag_uuid"),
            childColumns = arrayOf("parent_uuid")
        )
    ]
)
data class SuggestionEntity(
    @PrimaryKey
    override val uuid: String = UuidGenerator.getRandomUuid(),
    @ColumnInfo(name = "parent_uuid")
    override val parentUuid: String,
    override val name: String,
    @ColumnInfo(name = "is_enabled")
    override val isEnabled: Boolean = false
) : SuggestionContract