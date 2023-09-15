package wottrich.github.io.smartchecklist.suggestion.data.repository

import wottrich.github.io.smartchecklist.datasource.dao.TagDao
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistTagsEmbeddedContract
import wottrich.github.io.smartchecklist.datasource.entity.SuggestionEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.suggestion.SuggestionContract
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract
import wottrich.github.io.smartchecklist.suggestion.domain.repository.SuggestionRepository

class SuggestionRepositoryImpl(
    private val tagDao: TagDao
) : SuggestionRepository {
    override suspend fun getChecklistTags(checklistUuid: String): ChecklistTagsEmbeddedContract {
        return tagDao.getChecklistTags(checklistUuid)
    }

    override suspend fun getAllTags(): List<TagContract> {
        return tagDao.getAllTags()
    }

    override suspend fun getAllTagsWithSuggestions(): List<TagSuggestionEmbeddedContract> {
        return tagDao.getAllTagsWithSuggestions()
    }

    override suspend fun addNewTag(tag: TagContract) {
        tagDao.insertTag(tag as TagEntity)
    }

    override suspend fun deleteTag(tag: TagContract) {
        tagDao.deleteTag(tag as TagEntity)
    }

    override suspend fun addNewSuggestionToTag(suggestion: SuggestionContract) {
        tagDao.insertSuggestion(suggestion as SuggestionEntity)
    }

    override suspend fun deleteSuggestion(suggestion: SuggestionContract) {
        tagDao.deleteSuggestion(suggestion as SuggestionEntity)
    }
}