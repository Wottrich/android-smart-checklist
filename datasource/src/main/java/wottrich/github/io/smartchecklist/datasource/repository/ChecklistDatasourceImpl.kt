package wottrich.github.io.smartchecklist.datasource.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.Calendar
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistSectionWithTask
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistDTO
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistSectionWithTaskDTO
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistWithTasksDTO
import wottrich.github.io.smartchecklist.datasource.entity.TaskDTO

private fun <T, R> T.mapDataTo(block: (T) -> R): R {
    return block(this)
}

class ChecklistDatasourceImpl(
    private val checklistDao: ChecklistDao
) : ChecklistDatasource {
    override suspend fun insertChecklist(checklist: Checklist): Long? {
        return checklistDao.insert(checklist.mapToDTO())
    }

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
            Checklist(
                uuid = it.uuid,
                name = it.name,
                isSelected = it.isSelected,
                createdDate = it.createdDate.timeInMillis,
                lastUpdate = it.lastUpdate.timeInMillis
            )
        },
        this.tasks.map {
            Task(
                uuid = it.uuid,
                parentUuid = it.parentUuid,
                name = it.name,
                isCompleted = it.isCompleted,
                dateCreated = it.dateCreated.timeInMillis
            )
        },
        this.checklistSectionEmbedded.map { embedded ->
            ChecklistSectionWithTask(
                checklistSection = embedded.checklistSection.mapDataTo { section ->
                    Checklist(
                        uuid = section.uuid,
                        parentUuid = section.parentUuid,
                        name = section.name,
                        createdDate = section.createdDate.timeInMillis,
                        lastUpdate = section.lastUpdate.timeInMillis
                    )
                },
                tasks = embedded.tasks.map { task ->
                    Task(
                        uuid = task.uuid,
                        parentUuid = task.parentUuid,
                        name = task.name,
                        isCompleted = task.isCompleted,
                        dateCreated = task.dateCreated.timeInMillis
                    )
                }
            )
        }
    )

    override suspend fun updateSelectedChecklist(checklistUuid: String) {
        val checklistToBeSelected = checklistDao.getChecklist(checklistUuid).copy(isSelected = true)
        val currentSelectedChecklist =
            checklistDao.getSelectedChecklist()?.copy(isSelected = false)
        if (currentSelectedChecklist == null) {
            checklistDao.update(checklistToBeSelected)
        } else {
            checklistDao.updateChecklists(listOf(currentSelectedChecklist, checklistToBeSelected))
        }
    }

    override suspend fun deleteChecklistByUuid(checklistUuid: String) {
        checklistDao.deleteChecklistByUuid(checklistUuid)
    }

    override suspend fun getAllChecklistsWithTasks(): List<ChecklistWithTasks> {
        return checklistDao.getAllChecklistsWithTasks().map { it.mapToChecklist() }
    }

    override suspend fun replaceAllChecklists(checklists: List<ChecklistWithTasks>) {
        checklistDao.replaceAllChecklists(checklists.map { it.mapToDTO() })
    }

    private fun ChecklistWithTasks.mapToDTO() = ChecklistWithTasksDTO(
        checklist = checklist.mapDataTo { checklist ->
            ChecklistDTO(
                uuid = checklist.uuid,
                parentUuid = checklist.parentUuid,
                name = checklist.name,
                isSelected = checklist.isSelected,
                createdDate = Calendar.getInstance().apply { timeInMillis = checklist.createdDate },
                lastUpdate = Calendar.getInstance().apply { timeInMillis = checklist.lastUpdate },
            )
        },
        tasks = tasks.map { task ->
            TaskDTO(
                uuid = task.uuid,
                parentUuid = task.parentUuid,
                name = task.name,
                isCompleted = task.isCompleted,
                dateCreated = Calendar.getInstance().apply { timeInMillis = task.dateCreated },
            )
        },
        checklistSectionEmbedded = checklistSectionEmbedded.map { section ->
            ChecklistSectionWithTaskDTO(
                checklistSection = section.checklistSection.mapDataTo { checklist ->
                    ChecklistDTO(
                        uuid = checklist.uuid,
                        parentUuid = checklist.parentUuid,
                        name = checklist.name,
                        isSelected = checklist.isSelected,
                        createdDate = Calendar.getInstance().apply { timeInMillis = checklist.createdDate },
                        lastUpdate = Calendar.getInstance().apply { timeInMillis = checklist.lastUpdate },
                    )
                },
                tasks = section.tasks.map { task ->
                    TaskDTO(
                        uuid = task.uuid,
                        parentUuid = task.parentUuid,
                        name = task.name,
                        isCompleted = task.isCompleted,
                        dateCreated = Calendar.getInstance().apply { timeInMillis = task.dateCreated },
                    )
                }
            )
        }
    )

    override suspend fun getSelectedChecklist(): Checklist? {
        return checklistDao.getSelectedChecklist()?.mapToModel()
    }

    override fun observeSelectedChecklistWithTasks(): Flow<ChecklistWithTasks?> {
        return checklistDao.observeSelectedChecklistWithTasks().map {
            it.getOrSaveSelectedChecklist()?.mapToChecklist()
        }
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
                uuid = dto.uuid,
                name = dto.name,
                isSelected = dto.isSelected
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

    private fun ChecklistDTO.mapToModel() =
        Checklist(
            uuid = this.uuid,
            name = this.name,
            isSelected = this.isSelected
        )

    private fun Checklist.mapToDTO() =
        ChecklistDTO(
            uuid = this.uuid,
            name = this.name,
            isSelected = this.isSelected
        )
}