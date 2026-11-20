package wottrich.github.io.smartchecklist.serversync.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMeta
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMetaDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.serversync.api.SyncScheduler

class SyncOutboxImplTest {

    private val serverSettingsDatasource: ServerSettingsDatasource = mockk(relaxed = true)
    private val syncDatasource: SyncDatasource = mockk(relaxed = true)
    private val syncOutboxDatasource: SyncOutboxDatasource = mockk(relaxed = true)
    private val sharedChecklistMetaDatasource: SharedChecklistMetaDatasource = mockk(relaxed = true)
    private val syncScheduler: SyncScheduler = mockk(relaxed = true)
    private lateinit var sut: SyncOutboxImpl

    @Before
    fun setUp() {
        // no shared meta by default — personal checklists
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid(any()) } returns null
        sut = SyncOutboxImpl(
            serverSettingsDatasource = serverSettingsDatasource,
            syncDatasource = syncDatasource,
            syncOutboxDatasource = syncOutboxDatasource,
            sharedChecklistMetaDatasource = sharedChecklistMetaDatasource,
            syncScheduler = syncScheduler,
        )
    }

    @Test
    fun `GIVEN no server configured WHEN a mutation hook fires THEN nothing is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns false

        sut.onChecklistUpserted("c-1")

        coVerify(exactly = 0) { syncOutboxDatasource.enqueue(any()) }
        verify(exactly = 0) { syncScheduler.scheduleOneShotSync() }
    }

    @Test
    fun `GIVEN configured server and an existing row WHEN checklist upserted THEN payload is frozen with final row state`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getChecklistRow("c-1") } returns Checklist(
            uuid = "c-1",
            parentUuid = null,
            name = "Root",
            isSelected = true,
            createdDate = 1700000000000L,
            lastUpdate = 1700000000001L,
        )
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onChecklistUpserted("c-1")

        assertEquals(SyncOutboxEntry.ENTITY_CHECKLIST, slot.captured.entity)
        assertEquals("c-1", slot.captured.entityUuid)
        assertEquals(SyncOutboxEntry.OP_UPSERT, slot.captured.op)
        // `at` is the device time of the mutation (now), not a row date
        assertTrue(slot.captured.at > 0L)
        assertEquals(
            """{"uuid":"c-1","name":"Root","parentUuid":null,"isSelected":true,""" +
                """"createdDate":1700000000000,"lastUpdate":1700000000001}""",
            slot.captured.payload
        )
        assertNull(slot.captured.hostId)
        verify(exactly = 1) { syncScheduler.scheduleOneShotSync() }
    }

    @Test
    fun `GIVEN configured server WHEN checklist deleted THEN only the root tombstone is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onChecklistDeleted("c-gone", parentUuid = null)

        assertEquals(SyncOutboxEntry.ENTITY_CHECKLIST, slot.captured.entity)
        assertEquals(SyncOutboxEntry.OP_DELETE, slot.captured.op)
        assertEquals("""{"uuid": "c-gone"}""", slot.captured.payload)
        assertNull(slot.captured.hostId)
    }

    @Test
    fun `GIVEN row missing after the write WHEN upsert hook fires THEN nothing is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getChecklistRow("c-1") } returns null

        sut.onChecklistUpserted("c-1")

        coVerify(exactly = 0) { syncOutboxDatasource.enqueue(any()) }
    }

    @Test
    fun `GIVEN configured server WHEN task deleted THEN task tombstone is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onTaskDeleted("t-1", parentUuid = "c-1")

        assertEquals(SyncOutboxEntry.ENTITY_TASK, slot.captured.entity)
        assertEquals(SyncOutboxEntry.OP_DELETE, slot.captured.op)
        assertTrue(slot.captured.payload.contains("t-1"))
    }

    // ---------- routing (Phase B — Wi-Fi sharing) ----------

    @Test
    fun `GIVEN the checklist root is shared WHEN a checklist upsert hook fires THEN the entry routes to the owner host`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getChecklistRow("c-root") } returns rootChecklistRow(isSelected = true)
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") } returns sharedMeta()
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onChecklistUpserted("c-root")

        assertEquals("device-owner", slot.captured.hostId)
        // shared payloads never carry the device-local selection flag
        assertTrue(slot.captured.payload.contains(""""isSelected":false"""))
    }

    @Test
    fun `GIVEN a task of a shared checklist WHEN the task upsert hook fires THEN the entry routes to the owner host`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getTaskRow("t-1") } returns taskRow()
        coEvery { syncDatasource.getChecklistRow("c-section") } returns sectionRow()
        coEvery { syncDatasource.getChecklistRow("c-root") } returns rootChecklistRow(isSelected = false)
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") } returns sharedMeta()
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onTaskUpserted("t-1")

        assertEquals("device-owner", slot.captured.hostId)
        coVerify { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") }
    }

    @Test
    fun `GIVEN a task of a shared checklist is deleted WHEN the hook fires THEN the tombstone routes to the owner host`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getTaskRow("t-gone") } returns null
        coEvery { syncDatasource.getChecklistRow("c-root") } returns null
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") } returns sharedMeta()
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        // row already gone — the parent hint carries the routing context
        sut.onTaskDeleted("t-gone", parentUuid = "c-root")

        assertEquals("device-owner", slot.captured.hostId)
    }

    @Test
    fun `GIVEN a shared checklist WHEN the selection changes THEN nothing is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getChecklistRow("c-root") } returns rootChecklistRow(isSelected = true)
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") } returns sharedMeta()

        sut.onChecklistSelectionChanged("c-root")

        coVerify(exactly = 0) { syncOutboxDatasource.enqueue(any()) }
    }

    @Test
    fun `GIVEN a personal checklist WHEN the selection changes THEN a regular upsert is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getChecklistRow("c-1") } returns rootChecklistRow(isSelected = true)
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onChecklistSelectionChanged("c-1")

        assertTrue(slot.captured.payload.contains(""""isSelected":true"""))
        assertNull(slot.captured.hostId)
    }

    @Test
    fun `GIVEN a detached share copy WHEN a mutation hook fires THEN nothing is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        coEvery { syncDatasource.getChecklistRow("c-root") } returns rootChecklistRow(isSelected = false)
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") } returns sharedMeta(detached = true)

        sut.onChecklistUpserted("c-root")

        coVerify(exactly = 0) { syncOutboxDatasource.enqueue(any()) }
    }

    @Test
    fun `GIVEN no server configured but the checklist is shared WHEN a mutation hook fires THEN the entry queues for the share host`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns false
        coEvery { syncDatasource.getChecklistRow("c-root") } returns rootChecklistRow(isSelected = false)
        coEvery { sharedChecklistMetaDatasource.getByChecklistUuid("c-root") } returns sharedMeta()
        val slot = slot<SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onChecklistUpserted("c-root")

        assertEquals("device-owner", slot.captured.hostId)
        coVerify(exactly = 1) { syncScheduler.scheduleOneShotSync() }
    }

    // ---------- helpers ----------

    private fun rootChecklistRow(isSelected: Boolean) = Checklist(
        uuid = "c-root",
        parentUuid = null,
        name = "Shared list",
        isSelected = isSelected,
        createdDate = 1700000000000L,
        lastUpdate = 1700000000001L,
    )

    private fun sectionRow() = Checklist(
        uuid = "c-section",
        parentUuid = "c-root",
        name = "Section",
        createdDate = 1700000000000L,
        lastUpdate = 1700000000001L,
    )

    private fun taskRow() = wottrich.github.io.smartchecklist.datasource.data.model.Task(
        uuid = "t-1",
        parentUuid = "c-section",
        name = "Buy milk",
        dateCreated = 1700000000002L,
    )

    private fun sharedMeta(detached: Boolean = false) = SharedChecklistMeta(
        checklistUuid = "c-root",
        hostDeviceId = "device-owner",
        hostAddress = "192.168.0.10:8090",
        shareId = "share-1",
        hostToken = "guest-token",
        isOwner = false,
        detached = detached,
    )
}
