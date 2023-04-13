package wottrich.github.io.datasource.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks

interface ChecklistRepository {
    fun observeSelectedChecklistsWithTask(): Flow<NewChecklistWithNewTasks>
}