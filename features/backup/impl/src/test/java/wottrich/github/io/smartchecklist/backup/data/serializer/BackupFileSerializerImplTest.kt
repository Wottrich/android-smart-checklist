package wottrich.github.io.smartchecklist.backup.data.serializer

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest

class BackupFileSerializerImplTest : BaseUnitTest() {

    private val sut = BackupFileSerializerImpl()

    @Test
    fun `GIVEN a backup file model WHEN it is serialized THEN it must contain the current schema version`() {
        val backupFile = BackupFileModel(createdAt = 1000L, checklists = listOf())

        val serialized = sut.serialize(backupFile)

        assertTrue(serialized.contains("\"schemaVersion\":${BackupFileModel.CURRENT_SCHEMA_VERSION}"))
    }

    @Test
    fun `GIVEN a serialized backup file WHEN it is deserialized THEN it must be equivalent`() {
        val backupFile = BackupFileModel(createdAt = 1000L, checklists = listOf())

        val result = sut.deserialize(sut.serialize(backupFile))

        assertEquals(backupFile, result)
    }

    @Test
    fun `GIVEN a backup from an unknown future schema version WHEN it is deserialized THEN it must be refused`() {
        val futureSchemaJson = """
            {"schemaVersion":9999,"createdAt":0,"checklists":[]}
        """.trimIndent()

        val result = runCatching { sut.deserialize(futureSchemaJson) }

        assertTrue(result.exceptionOrNull() is BackupFileSerializerException)
    }

    @Test
    fun `GIVEN corrupted json WHEN it is deserialized THEN it must be refused`() {
        val result = runCatching { sut.deserialize("{ not a backup") }

        assertTrue(result.exceptionOrNull() is BackupFileSerializerException)
        assertTrue(result.exceptionOrNull()?.cause is SerializationException)
    }
}
