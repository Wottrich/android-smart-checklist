package wottrich.github.io.smartchecklist.suggestion.domain.repository

import wottrich.github.io.smartchecklist.datasource.entity.ChecklistTagsEmbeddedContract
import wottrich.github.io.smartchecklist.suggestion.SuggestionContract
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract

interface SuggestionRepository {
    suspend fun getChecklistTags(checklistUuid: String): ChecklistTagsEmbeddedContract
    suspend fun getAllTags(): List<TagContract>
    suspend fun getAllTagsWithSuggestions(): List<TagSuggestionEmbeddedContract>
    suspend fun addNewTag(tag: TagContract)
    suspend fun deleteTag(tag: TagContract)
    suspend fun addNewSuggestionToTag(suggestion: SuggestionContract)
    suspend fun deleteSuggestion(suggestion: SuggestionContract)
}