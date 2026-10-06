package wottrich.github.io.smartchecklist.backup.data.mapper

import wottrich.github.io.smartchecklist.backup.data.backupfile.ChecklistBackupModel
import wottrich.github.io.smartchecklist.backup.data.backupfile.TaskBackupModel
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupModelsMapperTest {

    @Test
    fun `GIVEN a checklist with tasks WHEN it is mapped to backup THEN every field must be preserved`() {
        val checklistWithTasks = checklistWithTasks()

        val result = checklistWithTasks.toBackupModel()

        assertEquals("checklist-uuid", result.uuid)
        assertEquals(null, result.parentUuid)
        assertEquals("Checklist", result.name)
        assertEquals(true, result.isSelected)
        assertEquals(1000L, result.createdDate)
        assertEquals(2000L, result.lastUpdate)
        assertEquals(1, result.tasks.size)
        assertEquals("task-uuid", result.tasks.first().uuid)
        assertEquals("checklist-uuid", result.tasks.first().parentUuid)
        assertEquals("Task", result.tasks.first().name)
        assertEquals(true, result.tasks.first().isCompleted)
        assertEquals(3000L, result.tasks.first().dateCreated)
    }

    @Test
    fun `GIVEN a backup checklist model WHEN it is mapped back to domain THEN every field must be preserved`() {
        val backupModel = ChecklistBackupModel(
            uuid = "section-uuid",
            parentUuid = "checklist-uuid",
            name = "Section",
            isSelected = false,
            createdDate = 1000L,
            lastUpdate = 2000L,
            tasks = listOf(
                TaskBackupModel(
                    uuid = "task-uuid",
                    parentUuid = "section-uuid",
                    name = "Task",
                    isCompleted = true,
                    dateCreated = 3000L,
                )
            ),
        )

        val result = backupModel.toChecklistWithTasks()

        assertEquals("section-uuid", result.checklist.uuid)
        assertEquals("checklist-uuid", result.checklist.parentUuid)
        assertEquals("Section", result.checklist.name)
        assertEquals(false, result.checklist.isSelected)
        assertEquals(1000L, result.checklist.createdDate)
        assertEquals(2000L, result.checklist.lastUpdate)
        assertEquals(1, result.tasks.size)
        assertEquals("task-uuid", result.tasks.first().uuid)
        assertEquals("section-uuid", result.tasks.first().parentUuid)
        assertEquals(true, result.tasks.first().isCompleted)
        assertEquals(3000L, result.tasks.first().dateCreated)
    }

    @Test
    fun `GIVEN a checklist with tasks WHEN it is mapped to backup and back THEN it must be equivalent`() {
        val checklistWithTasks = checklistWithTasks()

        val result = checklistWithTasks.toBackupModel().toChecklistWithTasks()

        assertEquals(checklistWithTasks, result)
    }

    private fun checklistWithTasks() = ChecklistWithTasks(
        checklist = Checklist(
            uuid = "checklist-uuid",
            parentUuid = null,
            name = "Checklist",
            isSelected = true,
            createdDate = 1000L,
            lastUpdate = 2000L,
        ),
        tasks = listOf(
            Task(
                uuid = "task-uuid",
                parentUuid = "checklist-uuid",
                name = "Task",
                isCompleted = true,
                dateCreated = 3000L,
            )
        ),
        checklistSectionEmbedded = listOf()
    )
}
