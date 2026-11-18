package wottrich.github.io.smartchecklist.datasource.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.dao.SyncOutboxDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.entity.SyncOutboxDTO

class SyncOutboxDatasourceImplTest {

    private val dao: SyncOutboxDao = mockk(relaxed = true)
    private lateinit var sut: SyncOutboxDatasourceImpl

    @Before
    fun setUp() {
        sut = SyncOutboxDatasourceImpl(dao)
    }

    @Test
    fun `GIVEN an entry WHEN enqueue is called THEN it is inserted as-is`() = runTest {
        val entry = SyncOutboxEntry(
            entity = SyncOutboxEntry.ENTITY_CHECKLIST,
            entityUuid = "c-1",
            op = SyncOutboxEntry.OP_UPSERT,
            at = 1700000000000L,
            payload = """{"uuid":"c-1"}""",
        )
        val slot = slot<SyncOutboxDTO>()
        coEvery { dao.insert(capture(slot)) } returns Unit

        sut.enqueue(entry)

        assertEquals("c-1", slot.captured.entityUuid)
        assertEquals(SyncOutboxEntry.ENTITY_CHECKLIST, slot.captured.entity)
        assertEquals(SyncOutboxEntry.OP_UPSERT, slot.captured.op)
        assertEquals(entry.payload, slot.captured.payload)
    }

    @Test
    fun `GIVEN pending rows WHEN pending is called THEN limit is forwarded and rows are mapped`() = runTest {
        val rows = listOf(
            SyncOutboxDTO(
                id = 1,
                entity = SyncOutboxEntry.ENTITY_TASK,
                entityUuid = "t-1",
                op = SyncOutboxEntry.OP_DELETE,
                at = 1700000000000L,
                payload = """{"uuid": "t-1"}""",
            )
        )
        coEvery { dao.pending(10) } returns rows

        val result = sut.pending(10)

        coVerify { dao.pending(10) }
        assertEquals(1, result.size)
        assertEquals(1L, result[0].id)
        assertEquals("t-1", result[0].entityUuid)
    }

    @Test
    fun `GIVEN acked ids WHEN deleteByIds is called THEN dao deletes by ids`() = runTest {
        sut.deleteByIds(listOf(1L, 2L))
        coVerify { dao.deleteByIds(listOf(1L, 2L)) }
    }

    @Test
    fun `GIVEN empty acked ids WHEN deleteByIds is called THEN dao is not touched`() = runTest {
        sut.deleteByIds(emptyList())
        coVerify(exactly = 0) { dao.deleteByIds(any()) }
    }

    @Test
    fun `GIVEN clearAll WHEN called THEN dao deletes everything`() = runTest {
        sut.clearAll()
        coVerify(exactly = 1) { dao.deleteAll() }
    }

    @Test
    fun `GIVEN dao count WHEN count is called THEN it is returned`() = runTest {
        coEvery { dao.count() } returns 7
        assertEquals(7, sut.count())
    }
}
