package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistTagsEmbedded
import wottrich.github.io.smartchecklist.datasource.entity.SuggestionEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagSuggestionEmbedded

@Dao
interface TagDao {

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE uuid=:checklistUuid LIMIT 1")
    suspend fun getChecklistTags(checklistUuid: String) : ChecklistTagsEmbedded

    @Transaction
    @Query("SELECT * FROM tag")
    suspend fun getAllTagsWithSuggestions(): List<TagSuggestionEmbedded>

    @Transaction
    @Query("SELECT * FROM tag")
    suspend fun getAllTags(): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(entity: TagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSuggestion(entity: SuggestionEntity)

    @Delete
    suspend fun deleteTag(entity: TagEntity)

    @Delete
    suspend fun deleteSuggestion(entity: SuggestionEntity)
}