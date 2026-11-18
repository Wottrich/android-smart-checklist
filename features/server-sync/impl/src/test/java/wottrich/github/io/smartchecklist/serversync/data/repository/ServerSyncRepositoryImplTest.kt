package wottrich.github.io.smartchecklist.serversync.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.slot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Test
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncRepository
import wottrich.github.io.smartchecklist.serversync.api.SyncAckResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeEntryDto
import wottrich.github.io.smartchecklist.serversync.api.SyncPullResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncPushBody
import wottrich.github.io.smartchecklist.serversync.api.SyncPushResponse
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasource
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.serversync.model.ServerConnectionState
import java.net.ConnectException

class ServerSyncRepositoryImplTest : BaseUnitTest() {

    private val serverSettingsDatasource: ServerSettingsDatasource = mockk(relaxed = true)
    private val syncOutboxDatasource: SyncOutboxDatasource = mockk(relaxed = true)
    private val syncDatasource: SyncDatasource = mockk(relaxed = true)
    private val serverApi: ServerApi = mockk(relaxed = true)
    private lateinit var sut: ServerSyncRepository

    @Before
    override fun setUp() {
        super.setUp()
        // dispatchersProviders come from Koin via InjectionTestRule (test dispatchers)
        sut = ServerSyncRepositoryImpl(
            dispatchersProviders = org.koin.core.context.GlobalContext.get().get(),
            serverSettingsDatasource = serverSettingsDatasource,
            syncOutboxDatasource = syncOutboxDatasource,
            syncDatasource = syncDatasource,
            serverApi = serverApi,
        )
    }

    // ---------- syncNow ----------

    @Test
    fun `GIVEN no server configured WHEN syncNow is called THEN success with no server calls`() = runBlockingUnitTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns false

        val result = sut.syncNow()

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { serverApi.push(any(), any(), any()) }
        coVerify(exactly = 0) { serverApi.pull(any(), any(), since = any(), limit = any()) }
    }

    @Test
    fun `GIVEN pending entries acked applied WHEN syncNow is called THEN outbox entries are dropped`() = runBlockingUnitTest {
        configureServer()
        coEvery { syncOutboxDatasource.pending(any()) } returnsMany listOf(
            listOf(entry(uuid = "c-1", payloadJson = checklistPayloadJson("c-1"))),
            emptyList()
        )
        coEvery { serverApi.push(any(), any(), any()) } returns SyncPushResponse(
            results = listOf(SyncAckResponse(uuid = "c-1", status = "applied"))
        )
        coEvery { serverApi.pull(any(), any(), since = any(), limit = any()) } returns
            SyncPullResponse(cursor = 10, hasMore = false)

        val result = sut.syncNow()

        assertTrue(result.isSuccess)
        coVerify { syncOutboxDatasource.deleteByIds(listOf(1L)) }
        coVerify { serverSettingsDatasource.setLastErrorKind(null) }
        coVerify { serverSettingsDatasource.setLastSyncAt(any()) }
    }

    @Test
    fun `GIVEN every ack status variant WHEN syncNow is called THEN applied ignored and conflict all drop entries`() = runBlockingUnitTest {
        configureServer()
        coEvery { syncOutboxDatasource.pending(any()) } returnsMany listOf(
            listOf(
                entry(id = 1, uuid = "c-1", payloadJson = checklistPayloadJson("c-1")),
                entry(id = 2, uuid = "c-2", payloadJson = checklistPayloadJson("c-2")),
                entry(id = 3, uuid = "t-1", payloadJson = taskDeletePayloadJson("t-1")),
            ),
            emptyList()
        )
        coEvery { serverApi.push(any(), any(), any()) } returns SyncPushResponse(
            results = listOf(
                SyncAckResponse(uuid = "c-1", status = "applied"),
                SyncAckResponse(uuid = "c-2", status = "ignored"),
                SyncAckResponse(uuid = "t-1", status = "conflict"),
            )
        )
        coEvery { serverApi.pull(any(), any(), since = any(), limit = any()) } returns
            SyncPullResponse(cursor = 10, hasMore = false)

        val result = sut.syncNow()

        assertTrue(result.isSuccess)
        coVerify { syncOutboxDatasource.deleteByIds(listOf(1L, 2L, 3L)) }
    }

    @Test
    fun `GIVEN server rejects the batch with validation WHEN syncNow is called THEN batch is kept and failure carries the kind`() = runBlockingUnitTest {
        configureServer()
        coEvery { syncOutboxDatasource.pending(any()) } returns
            listOf(entry(uuid = "c-1", payloadJson = checklistPayloadJson("c-1")))
        coEvery { serverApi.push(any(), any(), any()) } throws
            SyncError.ValidationRejected("missing parent")

        val result = sut.syncNow()

        assertTrue(result.isFailure)
        assertEquals(SyncError.ValidationRejected::class, result.exceptionOrNull()!!::class)
        assertEquals("validation_rejected", (result.exceptionOrNull() as SyncError).kind)
        coVerify(exactly = 0) { syncOutboxDatasource.deleteByIds(any()) }
        coVerify { serverSettingsDatasource.setLastErrorKind("validation_rejected") }
    }

    @Test
    fun `GIVEN server unreachable WHEN syncNow is called THEN failure is ServerUnreachable and kind persisted`() = runBlockingUnitTest {
        configureServer()
        coEvery { syncOutboxDatasource.pending(any()) } returns
            listOf(entry(uuid = "c-1", payloadJson = checklistPayloadJson("c-1")))
        coEvery { serverApi.push(any(), any(), any()) } throws SyncError.ServerUnreachable(ConnectException("refused"))

        val result = sut.syncNow()

        assertTrue(result.isFailure)
        assertEquals("server_unreachable", (result.exceptionOrNull() as SyncError).kind)
        coVerify { serverSettingsDatasource.setLastErrorKind("server_unreachable") }
    }

    @Test
    fun `GIVEN pull pages with hasMore WHEN syncNow is called THEN cursor is persisted after each page`() = runBlockingUnitTest {
        configureServer()
        coEvery { syncOutboxDatasource.pending(any()) } returns emptyList()
        coEvery { serverApi.pull(any(), any(), since = 0L, limit = any()) } returns
            SyncPullResponse(cursor = 100, hasMore = true, changes = listOf(checklistUpsertChange(seq = 1)))
        coEvery { serverApi.pull(any(), any(), since = 100L, limit = any()) } returns
            SyncPullResponse(cursor = 250, hasMore = false, changes = listOf(taskUpsertChange(seq = 2)))

        val result = sut.syncNow()

        assertTrue(result.isSuccess)
        coVerifyOrder {
            serverSettingsDatasource.setSyncCursor(100)
            serverSettingsDatasource.setSyncCursor(250)
        }
    }

    @Test
    fun `GIVEN pulled upsert and delete changes WHEN syncNow is called THEN rows are applied through SyncDatasource`() = runBlockingUnitTest {
        configureServer()
        coEvery { syncOutboxDatasource.pending(any()) } returns emptyList()
        coEvery { serverApi.pull(any(), any(), since = any(), limit = any()) } returns
            SyncPullResponse(
                cursor = 10,
                hasMore = false,
                changes = listOf(
                    checklistUpsertChange(seq = 1),
                    taskUpsertChange(seq = 2),
                    SyncChangeEntryDto(
                        entity = SyncOutboxEntry.ENTITY_CHECKLIST,
                        uuid = "c-gone",
                        op = SyncOutboxEntry.OP_DELETE,
                        at = 1700000000001L,
                        serverSeq = 3,
                        payload = buildJsonObject { put("uuid", "c-gone") },
                    ),
                )
            )

        val result = sut.syncNow()

        assertTrue(result.isSuccess)
        val upsertSlot = slot<List<Checklist>>()
        coVerify { syncDatasource.applyPage(capture(upsertSlot), any(), listOf("c-gone"), any()) }
        assertEquals("c-1", upsertSlot.captured.single().uuid)
        assertEquals("Root", upsertSlot.captured.single().name)
    }

    // ---------- testConnection ----------

    @Test
    fun `GIVEN not configured WHEN testConnection is called THEN NotConfigured`() = runBlockingUnitTest {
        coEvery { serverSettingsDatasource.getServerUrl() } returns null
        coEvery { serverSettingsDatasource.getToken() } returns null

        val result = sut.testConnection()

        assertEquals(ServerConnectionState.NotConfigured, result.getOrNull())
    }

    @Test
    fun `GIVEN valid token WHEN testConnection is called THEN Connected with userId`() = runBlockingUnitTest {
        configureServer()
        coEvery { serverApi.me(any(), any()) } returns MeResponseFake

        val result = sut.testConnection()

        assertEquals(ServerConnectionState.Connected("u-1"), result.getOrNull())
    }

    @Test
    fun `GIVEN rejected token WHEN testConnection is called THEN Unauthorized`() = runBlockingUnitTest {
        configureServer()
        coEvery { serverApi.me(any(), any()) } throws SyncError.ServerUnauthorized()

        val result = sut.testConnection()

        assertEquals(ServerConnectionState.Unauthorized, result.getOrNull())
    }

    @Test
    fun `GIVEN unreachable server WHEN testConnection is called THEN Unreachable`() = runBlockingUnitTest {
        configureServer()
        coEvery { serverApi.me(any(), any()) } throws SyncError.ServerUnreachable(ConnectException("refused"))

        val result = sut.testConnection()

        assertEquals(ServerConnectionState.Unreachable, result.getOrNull())
    }

    // ---------- helpers ----------

    private fun configureServer() {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { serverSettingsDatasource.getServerUrl() } returns "http://192.168.0.10:8080"
        coEvery { serverSettingsDatasource.getToken() } returns "token"
        // the cursor must actually advance when the engine persists it,
        // otherwise the pull loop would replay the same page forever
        var cursor = 0L
        coEvery { serverSettingsDatasource.getSyncCursor() } answers { cursor }
        coEvery { serverSettingsDatasource.setSyncCursor(any()) } answers { cursor = firstArg() }
    }

    private fun entry(
        id: Long = 1,
        uuid: String,
        payloadJson: String,
    ) = SyncOutboxEntry(
        id = id,
        entity = SyncOutboxEntry.ENTITY_CHECKLIST,
        entityUuid = uuid,
        op = SyncOutboxEntry.OP_UPSERT,
        at = 1700000000000L,
        payload = payloadJson,
    )

    private fun checklistPayloadJson(uuid: String): String =
        """{"uuid":"$uuid","name":"Root","parentUuid":null,"isSelected":false,""" +
            """"createdDate":1700000000000,"lastUpdate":1700000000000}"""

    private fun taskDeletePayloadJson(uuid: String): String =
        """{"uuid": "$uuid"}"""

    private fun checklistUpsertChange(seq: Long) = SyncChangeEntryDto(
        entity = SyncOutboxEntry.ENTITY_CHECKLIST,
        uuid = "c-1",
        op = SyncOutboxEntry.OP_UPSERT,
        at = 1700000000000L,
        serverSeq = seq,
        payload = buildJsonObject {
            put("uuid", "c-1")
            put("name", "Root")
            put("isSelected", true)
            put("createdDate", 1700000000000L)
            put("lastUpdate", 1700000000000L)
        },
    )

    private fun taskUpsertChange(seq: Long) = SyncChangeEntryDto(
        entity = SyncOutboxEntry.ENTITY_TASK,
        uuid = "t-1",
        op = SyncOutboxEntry.OP_UPSERT,
        at = 1700000000001L,
        serverSeq = seq,
        payload = buildJsonObject {
            put("uuid", "t-1")
            put("parentUuid", "c-1")
            put("name", "Buy milk")
            put("isCompleted", false)
            put("dateCreated", 1700000000001L)
        },
    )

    private companion object {
        val MeResponseFake = wottrich.github.io.smartchecklist.serversync.api.MeResponse(
            userId = "u-1",
            name = null,
        )
    }
}
