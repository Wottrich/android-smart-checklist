package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistContract
import java.util.Calendar
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

@Entity(
    tableName = "new_checklist",
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = ChecklistDTO::class,
            parentColumns = ["uuid"],
            childColumns = ["parent_uuid"]
        )
    ],
    indices = [
        Index(value = ["uuid"])
    ]
)
data class ChecklistDTO(
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    override val uuid: String = UuidGenerator.getRandomUuid(),
    @ColumnInfo(name = "name")
    override val name: String,
    @ColumnInfo(name = "parent_uuid")
    override val parentUuid: String? = null,
    @ColumnInfo(name = "is_selected", defaultValue = "0")
    override val isSelected: Boolean = false,
    @ColumnInfo(name = "created_date")
    val createdDate: Calendar = Calendar.getInstance(),
    @ColumnInfo(name = "last_update")
    var lastUpdate: Calendar = Calendar.getInstance(),
) : ChecklistContract