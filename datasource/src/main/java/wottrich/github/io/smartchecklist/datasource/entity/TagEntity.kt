package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

@Entity(tableName = "tag")
data class TagEntity(
    @PrimaryKey
    @ColumnInfo(name = "tag_uuid")
    override val tagUuid: String = UuidGenerator.getRandomUuid(),
    override val name: String,
    @ColumnInfo(name = "is_enabled")
    override val isEnabled: Boolean = false
) : TagContract
