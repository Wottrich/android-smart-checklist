package wottrich.github.io.smartchecklist.serversync.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.serversync.api.SyncScheduler

class SyncOutboxImplTest {

    private val serverSettingsDatasource: ServerSettingsDatasource = mockk(relaxed = true)
    private val syncDatasource: SyncDatasource = mockk(relaxed = true)
    private val syncOutboxDatasource: SyncOutboxDatasource = mockk(relaxed = true)
    private val syncScheduler: SyncScheduler = mockk(relaxed = true)
    private lateinit var sut: SyncOutboxImpl

    @Before
    fun setUp() {
        sut = SyncOutboxImpl(
            serverSettingsDatasource = serverSettingsDatasource,
            syncDatasource = syncDatasource,
            syncOutboxDatasource = syncOutboxDatasource,
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
        val slot = slot<wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry>()
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
        verify(exactly = 1) { syncScheduler.scheduleOneShotSync() }
    }

    @Test
    fun `GIVEN configured server WHEN checklist deleted THEN only the root tombstone is enqueued`() = runTest {
        coEvery { serverSettingsDatasource.isConfigured() } returns true
        val slot = slot<wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onChecklistDeleted("c-gone")

        assertEquals(SyncOutboxEntry.ENTITY_CHECKLIST, slot.captured.entity)
        assertEquals(SyncOutboxEntry.OP_DELETE, slot.captured.op)
        assertEquals("""{"uuid": "c-gone"}""", slot.captured.payload)
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
        val slot = slot<wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry>()
        coEvery { syncOutboxDatasource.enqueue(capture(slot)) } returns Unit

        sut.onTaskDeleted("t-1")

        assertEquals(SyncOutboxEntry.ENTITY_TASK, slot.captured.entity)
        assertEquals(SyncOutboxEntry.OP_DELETE, slot.captured.op)
        assertTrue(slot.captured.payload.contains("t-1"))
    }
}
