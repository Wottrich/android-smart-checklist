package wottrich.github.io.smartchecklist.backup.data.backupfile

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure JVM test — `:features:backup:public` is a pure Kotlin module and cannot
 * depend on `:test-tools` (an Android library), so it does not extend `BaseUnitTest`.
 */
class BackupFileModelSerializerTest {

    private val json = Json {
        ignoreUnknownKeys = false
        encodeDefaults = true
    }

    private val sut = BackupFileModel(
        schemaVersion = BackupFileModel.CURRENT_SCHEMA_VERSION,
        createdAt = 1728000000000L,
        checklists = listOf(
            ChecklistBackupModel(
                uuid = "checklist-uuid",
                parentUuid = null,
                name = "Checklist",
                isSelected = true,
                createdDate = 1728000000000L,
                lastUpdate = 1728000001000L,
                tasks = listOf(
                    TaskBackupModel(
                        uuid = "task-uuid",
                        parentUuid = "checklist-uuid",
                        name = "Task",
                        isCompleted = false,
                        dateCreated = 1728000000000L,
                    )
                ),
            )
        ),
    )

    @Test
    fun `GIVEN a backup file model WHEN it is serialized and deserialized THEN it must be equal`() {
        val serialized = json.encodeToString(sut)

        val result = json.decodeFromString<BackupFileModel>(serialized)

        assertEquals(sut, result)
    }

    @Test
    fun `GIVEN a serialized backup WHEN it is encoded THEN it must contain the current schema version`() {
        val serialized = json.encodeToString(sut)

        assertTrue(serialized.contains("\"schemaVersion\":${BackupFileModel.CURRENT_SCHEMA_VERSION}"))
    }

    @Test
    fun `GIVEN a corrupted json WHEN it is deserialized THEN it must throw a serialization exception`() {
        val corruptedJson = "{ this is not a backup file"

        val result = runCatching { json.decodeFromString<BackupFileModel>(corruptedJson) }

        assertTrue(result.exceptionOrNull() is SerializationException)
    }

    @Test
    fun `GIVEN a json missing a required field WHEN it is deserialized THEN it must throw a serialization exception`() {
        val missingFieldJson = """
            {"schemaVersion":1,"createdAt":0}
        """.trimIndent()

        val result = runCatching { json.decodeFromString<BackupFileModel>(missingFieldJson) }

        assertTrue(result.exceptionOrNull() is SerializationException)
    }

}
