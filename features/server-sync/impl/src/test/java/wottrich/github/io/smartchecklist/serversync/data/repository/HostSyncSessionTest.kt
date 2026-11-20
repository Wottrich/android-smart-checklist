package wottrich.github.io.smartchecklist.serversync.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.SyncAckResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeEntryDto
import wottrich.github.io.smartchecklist.serversync.api.SyncCursorStore
import wottrich.github.io.smartchecklist.serversync.api.SyncPullResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncPushResponse

/**
 * The extracted push/pull/apply core — one session per sync target (PC backend =
 * `hostId = null`; Wi-Fi share host = its device id, scoped to one share).
 */
class HostSyncSessionTest {

    private val syncOutboxDatasource: SyncOutboxDatasource = mockk(relaxed = true)
    private val syncDatasource: SyncDatasource = mockk(relaxed = true)
    private val serverApi: ServerApi = mockk(relaxed = true)
    private lateinit var sut: HostSyncSession

    @Before
    fun setUp() {
        sut = HostSyncSession(
            syncOutboxDatasource = syncOutboxDatasource,
            syncDatasource = syncDatasource,
            serverApi = serverApi,
        )
    }

    // ---------- push ----------

    @Test
    fun `GIVEN only the given host's entries are pending WHEN pushOutbox is called THEN the host-scoped queue is drained`() = runTest {
        coEvery { syncOutboxDatasource.pending("device-owner", any()) } returnsMany listOf(
            listOf(entry(uuid = "c-1", hostId = "device-owner")),
            emptyList()
        )
        coEvery { serverApi.push(any(), any(), any()) } returns SyncPushResponse(
            results = listOf(SyncAckResponse(uuid = "c-1", status = "applied"))
        )

        sut.pushOutbox(hostId = "device-owner", baseUrl = "http://192.168.0.10:8090", token = "host-token")

        coVerify(exactly = 1) { syncOutboxDatasource.pending("device-owner", any()) }
        coVerify(exactly = 0) { syncOutboxDatasource.pending(null, any()) }
        coVerify { syncOutboxDatasource.deleteByIds(listOf(1L)) }
    }

    @Test
    fun `GIVEN the PC backend queue WHEN pushOutbox is called with null host THEN pending(null) is drained`() = runTest {
        coEvery { syncOutboxDatasource.pending(null, any()) } returnsMany listOf(
            listOf(entry(uuid = "c-1", hostId = null)),
            emptyList()
        )
        coEvery { serverApi.push(any(), any(), any()) } returns SyncPushResponse(
            results = listOf(SyncAckResponse(uuid = "c-1", status = "ignored"))
        )

        sut.pushOutbox(hostId = null, baseUrl = "http://192.168.0.10:8080", token = "backend-token")

        coVerify(exactly = 0) { syncOutboxDatasource.pending("device-owner", any()) }
        coVerify { syncOutboxDatasource.deleteByIds(listOf(1L)) }
    }

    @Test
    fun `GIVEN the target rejects the batch WHEN pushOutbox is called THEN nothing is deleted`() = runTest {
        coEvery { syncOutboxDatasource.pending("device-owner", any()) } returns
            listOf(entry(uuid = "c-1", hostId = "device-owner"))
        coEvery { serverApi.push(any(), any(), any()) } returns SyncPushResponse(results = emptyList())

        sut.pushOutbox(hostId = "device-owner", baseUrl = "http://192.168.0.10:8090", token = "host-token")

        // nothing acked → the batch stays queued (the delete call itself is an empty no-op)
        coVerify(exactly = 0) { syncOutboxDatasource.deleteByIds(match { it.isNotEmpty() }) }
    }

    // ---------- pull ----------

    @Test
    fun `GIVEN a share-scoped pull WHEN pullChanges is called THEN the shareId is forwarded and the share cursor is used`() = runTest {
        val cursorStore = RecordingCursorStore(start = 0L)
        coEvery {
            serverApi.pull(any(), any(), since = any(), limit = any(), shareId = "share-1")
        } returnsMany listOf(
            SyncPullResponse(cursor = 100, hasMore = true, changes = listOf(checklistUpsertChange(seq = 1))),
            SyncPullResponse(cursor = 250, hasMore = false, changes = emptyList()),
        )

        sut.pullChanges(
            baseUrl = "http://192.168.0.10:8090",
            token = "host-token",
            shareId = "share-1",
            cursorStore = cursorStore,
        )

        coVerifyOrder {
            serverApi.pull(any(), any(), since = 0L, limit = any(), shareId = "share-1")
            serverApi.pull(any(), any(), since = 100L, limit = any(), shareId = "share-1")
        }
        assertEquals(listOf(100L, 250L), cursorStore.writes)
    }

    @Test
    fun `GIVEN a full-log pull WHEN pullChanges is called with null shareId THEN no shareId reaches the API`() = runTest {
        val cursorStore = RecordingCursorStore(start = 0L)
        coEvery {
            serverApi.pull(any(), any(), since = any(), limit = any(), shareId = null)
        } returns SyncPullResponse(cursor = 10, hasMore = false, changes = emptyList())

        sut.pullChanges(
            baseUrl = "http://192.168.0.10:8080",
            token = "backend-token",
            shareId = null,
            cursorStore = cursorStore,
        )

        coVerify {
            serverApi.pull(any(), any(), since = 0L, limit = any(), shareId = null)
        }
        assertEquals(listOf(10L), cursorStore.writes)
    }

    @Test
    fun `GIVEN pulled changes WHEN the page is applied THEN rows go through SyncDatasource atomically`() = runTest {
        val cursorStore = RecordingCursorStore(start = 0L)
        coEvery { serverApi.pull(any(), any(), since = any(), limit = any(), shareId = "share-1") } returns
            SyncPullResponse(
                cursor = 10,
                hasMore = false,
                changes = listOf(
                    checklistUpsertChange(seq = 1),
                    SyncChangeEntryDto(
                        entity = SyncOutboxEntry.ENTITY_CHECKLIST,
                        uuid = "c-gone",
                        op = SyncOutboxEntry.OP_DELETE,
                        at = 1700000000001L,
                        serverSeq = 2,
                        payload = buildJsonObject { put("uuid", "c-gone") },
                    ),
                )
            )

        sut.pullChanges(
            baseUrl = "http://192.168.0.10:8090",
            token = "host-token",
            shareId = "share-1",
            cursorStore = cursorStore,
        )

        val upsertSlot = slot<List<Checklist>>()
        coVerify { syncDatasource.applyPage(capture(upsertSlot), any(), listOf("c-gone"), any()) }
        assertEquals("c-1", upsertSlot.captured.single().uuid)
    }

    // ---------- helpers ----------

    private fun entry(uuid: String, hostId: String?) = SyncOutboxEntry(
        id = 1,
        entity = SyncOutboxEntry.ENTITY_CHECKLIST,
        entityUuid = uuid,
        op = SyncOutboxEntry.OP_UPSERT,
        at = 1700000000000L,
        payload = """{"uuid":"$uuid","name":"Root","parentUuid":null,"isSelected":false,""" +
            """"createdDate":1700000000000,"lastUpdate":1700000000000}""",
        hostId = hostId,
    )

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

    /** Records every persisted cursor so ordering can be asserted. */
    private class RecordingCursorStore(start: Long) : SyncCursorStore {
        private var cursor = start
        val writes = mutableListOf<Long>()
        override suspend fun get(): Long = cursor
        override suspend fun set(cursor: Long) {
            this.cursor = cursor
            writes += cursor
        }
    }
}
