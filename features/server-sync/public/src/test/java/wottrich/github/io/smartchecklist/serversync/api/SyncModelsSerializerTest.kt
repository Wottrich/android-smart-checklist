package wottrich.github.io.smartchecklist.serversync.api

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Golden JSON fixtures copied from `smart-checklist-backend/docs/api/sync-contract.md`
 * (v1) and the backend's test output. Any field drift breaks the wire contract.
 */
class SyncModelsSerializerTest {

    /** Mirrors the client config used by the sync feature (`KtorClientFactory`). */
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `GIVEN a contract push body WHEN it is encoded THEN the JSON matches the contract shape`() {
        val body = SyncPushBody(
            changes = listOf(
                SyncChangeDto(
                    entity = "checklist",
                    uuid = "c-1",
                    op = "upsert",
                    at = 1700000000000L,
                    payload = buildJsonObject {
                        put("uuid", "c-1")
                        put("name", "Root")
                        put("parentUuid", JsonPrimitive(null))
                        put("isSelected", false)
                        put("createdDate", 1700000000000L)
                        put("lastUpdate", 1700000000000L)
                    }
                ),
                SyncChangeDto(
                    entity = "task",
                    uuid = "t-1",
                    op = "delete",
                    at = 1700000001000L,
                    payload = buildJsonObject { put("uuid", "t-1") }
                ),
            )
        )

        val encoded = json.encodeToString(body)

        assertEquals(
            """{"changes":[""" +
                """{"entity":"checklist","uuid":"c-1","op":"upsert","at":1700000000000,""" +
                """"payload":{"uuid":"c-1","name":"Root","parentUuid":null,"isSelected":false,""" +
                """"createdDate":1700000000000,"lastUpdate":1700000000000}},""" +
                """{"entity":"task","uuid":"t-1","op":"delete","at":1700000001000,""" +
                """"payload":{"uuid":"t-1"}}]}""",
            encoded
        )
    }

    @Test
    fun `GIVEN a contract push response WHEN it is decoded THEN acks are read per change`() {
        val decoded = json.decodeFromString<SyncPushResponse>(
            """{"results":[{"uuid":"c-1","status":"applied"},{"uuid":"t-1","status":"ignored"}]}"""
        )

        assertEquals(2, decoded.results.size)
        assertEquals("applied", decoded.results[0].status)
        assertEquals("ignored", decoded.results[1].status)
    }

    @Test
    fun `GIVEN a contract pull response WHEN it is decoded THEN cursor hasMore and serverSeq are read`() {
        val decoded = json.decodeFromString<SyncPullResponse>(
            """{"cursor":42,"hasMore":false,"changes":[""" +
                """{"entity":"checklist","uuid":"c-1","op":"upsert","at":1700000000000,""" +
                """"serverSeq":12,"payload":{"uuid":"c-1","name":"Root","isSelected":true,""" +
                """"createdDate":1700000000000,"lastUpdate":1700000000000}}]}"""
        )

        assertEquals(42L, decoded.cursor)
        assertEquals(false, decoded.hasMore)
        assertEquals(12L, decoded.changes.single().serverSeq)
    }

    @Test
    fun `GIVEN an empty pull page WHEN it is decoded THEN changes default to empty`() {
        val decoded = json.decodeFromString<SyncPullResponse>("""{"cursor":7,"hasMore":false}""")

        assertEquals(7L, decoded.cursor)
        assertEquals(emptyList<SyncChangeEntryDto>(), decoded.changes)
    }

    @Test
    fun `GIVEN a register response WHEN it is decoded THEN userId and token are read`() {
        val decoded = json.decodeFromString<RegisterResponse>(
            """{"userId":"u-1","token":"secret-token"}"""
        )

        assertEquals("u-1", decoded.userId)
        assertEquals("secret-token", decoded.token)
    }

    @Test
    fun `GIVEN a backup status response WHEN it is decoded THEN lastBackupDate may be absent`() {
        val decoded = json.decodeFromString<BackupStatusResponse>("""{"isConnected":true}""")

        assertEquals(true, decoded.isConnected)
        assertNull(decoded.lastBackupDate)
    }

    @Test
    fun `GIVEN a contract error response WHEN it is decoded THEN code and message are read`() {
        val decoded = json.decodeFromString<ServerErrorResponse>(
            """{"code":"unauthenticated","message":"bad token"}"""
        )

        assertEquals("unauthenticated", decoded.code)
        assertEquals("bad token", decoded.message)
    }

    @Test
    fun `GIVEN an error response with params WHEN it is decoded THEN params are ignored safely`() {
        val decoded = json.decodeFromString<ServerErrorResponse>(
            """{"code":"validation","message":"invalid","params":{"name":["blank"]}}"""
        )

        assertEquals("validation", decoded.code)
    }

    @Test
    fun `GIVEN checklist sync payload WHEN it round-trips THEN field names match the Room columns`() {
        val dto = ChecklistSyncDto(
            uuid = "c-1",
            name = "Root",
            parentUuid = null,
            isSelected = false,
            createdDate = 1700000000000L,
            lastUpdate = 1700000000000L,
        )

        val decoded = json.decodeFromString<ChecklistSyncDto>(json.encodeToString(dto))

        assertEquals(dto, decoded)
    }

    @Test
    fun `GIVEN task sync payload WHEN it round-trips THEN field names match the Room columns`() {
        val dto = TaskSyncDto(
            uuid = "t-1",
            parentUuid = "c-1",
            name = "Buy milk",
            isCompleted = true,
            dateCreated = 1700000001000L,
        )

        val decoded = json.decodeFromString<TaskSyncDto>(json.encodeToString(dto))

        assertEquals(dto, decoded)
    }
}
