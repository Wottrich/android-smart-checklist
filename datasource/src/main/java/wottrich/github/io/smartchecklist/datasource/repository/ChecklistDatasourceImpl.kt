package wottrich.github.io.smartchecklist.datasource.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistDTO
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistWithTasksDTO

private fun <T, R> T.mapDataTo(block: (T) -> R): R {
    return block(this)
}

class ChecklistDatasourceImpl(
    private val checklistDao: ChecklistDao
) : ChecklistDatasource {
    override suspend fun insertChecklist(checklist: Checklist): Long? {
        return checklistDao.insert(checklist.mapToDTO())
    }

    private fun Checklist.mapToDTO() =
        ChecklistDTO(
            this.uuid,
            this.name,
            this.isSelected
        )

    override suspend fun getChecklistWithTasksByUuid(uuid: String): ChecklistWithTasks {
        return checklistDao.getChecklistWithTasks(uuid).mapDataTo {
            it.mapToChecklist()
        }
    }

    override suspend fun getSelectedChecklistWithTasks(): ChecklistWithTasks? {
        return checklistDao.getSelectedChecklistWithTasks().getOrSaveSelectedChecklist().mapDataTo {
            it?.mapToChecklist()
        }
    }

    private suspend fun List<ChecklistWithTasksDTO>.getOrSaveSelectedChecklist(): ChecklistWithTasksDTO? {
        return firstOrNull() ?: getFirstChecklist()?.also {
            val checklist = it.checklist.copy(isSelected = true)
            checklistDao.update(checklist)
        }
    }

    private suspend fun getFirstChecklist(): ChecklistWithTasksDTO? {
        return checklistDao.selectAllChecklistWithTasks().firstOrNull()
    }

    private fun ChecklistWithTasksDTO.mapToChecklist() = ChecklistWithTasks(
        this.checklist.mapDataTo {
            Checklist(uuid = it.uuid, name = it.name, isSelected = it.isSelected)
        },
        this.tasks.map {
            Task(
                uuid = it.uuid,
                parentUuid = it.parentUuid,
                name = it.name,
                isCompleted = it.isCompleted
            )
        }
    )

    override suspend fun updateSelectedChecklist(checklistUuid: String) {
        val checklistToBeSelected = checklistDao.getChecklist(checklistUuid).copy(isSelected = true)
        val currentSelectedChecklist =
            checklistDao.selectSelectedChecklist(true)?.copy(isSelected = false)
        if (currentSelectedChecklist == null) {
            checklistDao.update(checklistToBeSelected)
        } else {
            checklistDao.updateChecklists(listOf(currentSelectedChecklist, checklistToBeSelected))
        }
    }

    override suspend fun deleteChecklistByUuid(checklistUuid: String) {
        checklistDao.deleteChecklistByUuid(checklistUuid)
    }

    override fun observeAllChecklistsWithTask(): Flow<List<ChecklistWithTasks>> {
        return checklistDao.observeAllChecklistWithTasks().map {
            it.map { dto -> dto.mapToChecklist() }
        }
    }

    override fun observeSelectedChecklist(): Flow<Checklist?> {
        return checklistDao.observeSelectedChecklist().map { dto ->
            dto ?: return@map null
            Checklist(
                dto.uuid,
                dto.name,
                dto.isSelected
            )
        }
    }

    override fun observeSelectedChecklistUuid(): Flow<String?> {
        return checklistDao.observeSelectedChecklistUuid().mapNotNull { selectedChecklistUuid ->
            selectedChecklistUuid ?: getFirstChecklist()?.run {
                val checklist = this.checklist.copy(isSelected = true)
                checklistDao.update(checklist)
                this.checklist.uuid
            }
        }
    }
}