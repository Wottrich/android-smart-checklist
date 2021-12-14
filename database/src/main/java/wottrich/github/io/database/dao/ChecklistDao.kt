package wottrich.github.io.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.database.entity.ChecklistWithTasks

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
    @Query("SELECT * FROM checklist")
    fun observeChecklistsUpdate() : Flow<List<Checklist>>

    @Transaction
    @Query("SELECT * FROM checklist")
    fun observeChecklistsWithTaskUpdate() : Flow<List<ChecklistWithTasks>>

    @Transaction
    @Query("SELECT * FROM checklist")
    fun selectAllChecklistWithTasks(): List<ChecklistWithTasks>

    @Transaction
    @Query("SELECT * FROM checklist WHERE is_selected=:isSelected")
    suspend fun selectSelectedChecklist(isSelected: Boolean): Checklist?

    @Transaction
    @Query("SELECT * FROM checklist WHERE is_selected=:isSelected")
    fun observeSelectedChecklistWithTasks(isSelected: Boolean): Flow<List<ChecklistWithTasks>>

    @Transaction
    @Query("SELECT * FROM checklist WHERE checklistId=:checklistId")
    suspend fun getChecklist(checklistId: String): Checklist

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(checklist: Checklist): Long?

    @Update
    suspend fun update(checklist: Checklist)

    @Update
    suspend fun updateChecklists(checklists: List<Checklist>)

    @Delete
    suspend fun delete(checklist: Checklist)

}