package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.base.onSuccess
import github.io.wottrich.coroutines.failureEmptyResult
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.ChecklistWithTasks
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.base.UuidGenerator

class MigrationViewModel(
    private val migrationUseCase: MigrationUseCase
) : BaseViewModel() {

    private val _uiEffect = SingleShotEventBus<MigrationUiEffect>()
    val uiEffect = _uiEffect.events

    init {
        handleMigration()
    }

    fun tryAgain() {
        handleMigration()
    }

    private fun handleMigration() {
        launchIO {
            migrationUseCase().onSuccess {
                launchMain {
                    _uiEffect.emit(MigrationUiEffect.MigrationSuccessful)
                }
            }
        }
    }

}

sealed class MigrationUiEffect {
    object MigrationSuccessful : MigrationUiEffect()
}

class MigrationUseCase(
    private val checklistDao: ChecklistDao,
    private val taskDao: TaskDao,
    private val convertChecklistToNewChecklist: ConvertChecklistToNewChecklist = ConvertChecklistToNewChecklist()
) : KotlinResultUseCase<UseCase.None, UseCase.Empty>() {
    override suspend fun execute(params: UseCase.None): Result<UseCase.Empty> {
        return try {
            val oldChecklists = checklistDao.getAllOldChecklistWithTasks()
            val newChecklists = oldChecklists.map {
                convertChecklistToNewChecklist.convert(it)
            }
            checklistDao.insertMany(newChecklists.map { it.newChecklist })
            newChecklists.forEach {
                taskDao.insertMany(it.newTasks)
            }
            checklistDao.deleteMany(oldChecklists.map { it.checklist })
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}

class ConvertChecklistToNewChecklist {
    fun convert(checklistWithTasks: ChecklistWithTasks): NewChecklistWithNewTasks {
        val oldChecklist = checklistWithTasks.checklist
        val newChecklistUuid = UuidGenerator.getRandomUuid()
        val newChecklist = NewChecklist(
            uuid = newChecklistUuid,
            name = oldChecklist.name,
            isSelected = oldChecklist.isSelected,
            createdDate = oldChecklist.createdDate,
            lastUpdate = oldChecklist.lastUpdate
        )
        val newTasks = checklistWithTasks.tasks.map {
            val newTaskUuid = UuidGenerator.getRandomUuid()
            NewTask(
                uuid = newTaskUuid,
                parentUuid = newChecklistUuid,
                name = it.name,
                isCompleted = it.isCompleted,
                dateCreated = it.dateCreated
            )
        }
        return NewChecklistWithNewTasks(
            newChecklist = newChecklist,
            newTasks = newTasks
        )
    }
}