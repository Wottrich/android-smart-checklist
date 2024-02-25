package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistDTO
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistWithTasksDTO

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

@Dao
interface ChecklistDao {

    @Transaction
    @Query("SELECT * FROM new_checklist")
    suspend fun selectAllChecklistWithTasks(): List<ChecklistWithTasksDTO>

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE is_selected='1' LIMIT 1")
    suspend fun getSelectedChecklist(): ChecklistDTO?

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE is_selected='1' LIMIT 1")
    fun getSelectedChecklistWithTasks(): List<ChecklistWithTasksDTO>

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE is_selected='1' LIMIT 1")
    fun observeSelectedChecklistWithTasks(): Flow<List<ChecklistWithTasksDTO>>

    @Transaction
    @Query("SELECT uuid FROM new_checklist WHERE is_selected='1' LIMIT 1")
    fun observeSelectedChecklistUuid(): Flow<String?>

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE is_selected='1'")
    fun observeSelectedChecklist(): Flow<ChecklistDTO?>

    @Transaction
    @Query("SELECT * FROM new_checklist")
    fun observeAllChecklistWithTasks(): Flow<List<ChecklistWithTasksDTO>>

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE uuid=:uuid")
    suspend fun getChecklist(uuid: String): ChecklistDTO

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE uuid=:uuid")
    suspend fun getChecklistWithTasks(uuid: String): ChecklistWithTasksDTO

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(new_checklist: ChecklistDTO): Long?

    @Update
    suspend fun update(new_checklist: ChecklistDTO)

    @Update
    suspend fun updateChecklists(checklists: List<ChecklistDTO>)

    suspend fun deleteChecklistByUuid(checklistUuid: String) {
        val checklist = getChecklist(checklistUuid)
        delete(checklist)
    }

    @Delete
    suspend fun delete(checklist: ChecklistDTO)
}