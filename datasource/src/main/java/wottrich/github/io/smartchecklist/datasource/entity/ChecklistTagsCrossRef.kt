package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["uuid", "tag_uuid"])
data class ChecklistTagsCrossRef(
    @ColumnInfo(name = "uuid")
    val checklistUuid: String,
    @ColumnInfo(name = "tag_uuid")
    val tagsUuid: String
)
