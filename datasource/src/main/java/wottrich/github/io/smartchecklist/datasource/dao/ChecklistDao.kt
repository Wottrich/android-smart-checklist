package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklistWithNewTasks

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
    suspend fun selectAllChecklistWithTasks(): List<NewChecklistWithNewTasks>

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE is_selected='1' LIMIT 1")
    suspend fun getSelectedChecklist(): NewChecklist?

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE is_selected='1' LIMIT 1")
    fun observeSelectedChecklistWithTasks(): Flow<List<NewChecklistWithNewTasks>>

    @Transaction
    @Query("SELECT * FROM new_checklist")
    fun observeAllChecklistWithTasks(): Flow<List<NewChecklistWithNewTasks>>

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE uuid=:uuid")
    suspend fun getChecklist(uuid: String): NewChecklist

    @Transaction
    @Query("SELECT * FROM new_checklist WHERE uuid=:uuid")
    suspend fun getChecklistWithTasks(uuid: String): NewChecklistWithNewTasks

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(new_checklist: NewChecklist): Long?

    @Update
    suspend fun update(new_checklist: NewChecklist)

    @Update
    suspend fun updateChecklists(checklists: List<NewChecklist>)

    suspend fun deleteChecklistByUuid(checklistUuid: String) {
        val checklist = getChecklist(checklistUuid)
        delete(checklist)
    }

    @Delete
    suspend fun delete(checklist: NewChecklist)
}