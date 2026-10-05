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
import wottrich.github.io.smartchecklist.datasource.entity.TaskDTO

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
    @Query("SELECT * FROM new_checklist WHERE parent_uuid!=null")
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
    @Query("SELECT * FROM new_checklist")
    suspend fun getAllChecklistsWithTasks(): List<ChecklistWithTasksDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChecklists(checklists: List<ChecklistDTO>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<TaskDTO>)

    @Query("DELETE FROM new_task")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM new_checklist")
    suspend fun deleteAllChecklists()

    /**
     * Replaces every checklist/task with [checklists] in a single transaction.
     * Used exclusively by the Google Drive backup restore (issue #94).
     *
     * Rows are inserted parents-first (sections reference their parent checklist
     * through the `parent_uuid` foreign key) and the selection is normalized to at
     * most one selected checklist (the first one marked as selected, when any).
     */
    @Transaction
    suspend fun replaceAllChecklists(checklists: List<ChecklistWithTasksDTO>) {
        deleteAllTasks()
        deleteAllChecklists()
        val selectedUuid = checklists.firstOrNull { it.checklist.isSelected }?.checklist?.uuid
        val normalized = checklists.map {
            val checklist = it.checklist.copy(isSelected = it.checklist.uuid == selectedUuid)
            ChecklistWithTasksDTO(
                checklist = checklist,
                tasks = it.tasks,
                checklistSectionEmbedded = it.checklistSectionEmbedded
            )
        }
        insertAllChecklists(normalized.map { it.checklist }.sortedParentsFirst())
        insertAllTasks(normalized.flatMap { withTasks ->
            withTasks.tasks.map { it.copy(parentUuid = withTasks.checklist.uuid) }
        })
    }

    /** Root checklists before their sections, so the self foreign key never fails. */
    private fun List<ChecklistDTO>.sortedParentsFirst(): List<ChecklistDTO> {
        val insertedUuids = map { it.uuid }.toHashSet()
        return sortedBy { checklist ->
            if (checklist.parentUuid != null && insertedUuids.contains(checklist.parentUuid)) 1 else 0
        }
    }

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