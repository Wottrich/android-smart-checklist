package wottrich.github.io.smartchecklist.share.host

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeDto
import wottrich.github.io.smartchecklist.share.domain.ShareError
import wottrich.github.io.smartchecklist.share.host.db.HostChecklistDTO
import wottrich.github.io.smartchecklist.share.host.db.HostTaskDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareChangeLogDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDao
import wottrich.github.io.smartchecklist.share.host.db.ShareHostDatabase
import wottrich.github.io.smartchecklist.share.host.db.ShareTombstoneDTO

/**
 * Owner-side ingest rules ported from the PC backend (`share-contract.md`):
 * parents-first, LWW by client `at`, cascading tombstones, per-share scoping,
 * whole-batch atomicity. The DAO mock carries an in-memory mirror so ingest
 * decisions (LWW, parent walks) see the effects of the batch.
 */
class ShareHostIngestServiceTest {

    private val database: ShareHostDatabase = mockk(relaxed = true)
    private val dao: ShareDao = mockk(relaxed = true)
    private lateinit var sut: ShareHostIngestService

    private val mirror = mutableMapOf<String, HostChecklistDTO>()
    private val taskMirror = mutableMapOf<String, HostTaskDTO>()
    private val loggedChanges = mutableListOf<ShareChangeLogDTO>()

    @Before
    fun setUp() {
        sut = ShareHostIngestService(database, dao)
        // JVM tests: no Room transaction runtime — run directly
        sut.transactionRunner = { it() }
        coEvery { dao.getMemberShareIds(GUEST) } returns listOf(SHARE)
        val activeShare = ShareDTO(
            shareId = SHARE, rootUuid = ROOT_UUID, name = "Groceries", createdAt = 1_700_000_000_000L,
        )
        coEvery { dao.getShare(any()) } answers {
            if (firstArg<String>() == SHARE) activeShare else null
        }
        // relaxed mocks return dummy objects for nullable returns — these must be null
        // unless a test explicitly tombstones/registers something
        coEvery { dao.findTombstone(any()) } returns null
        coEvery { dao.getActiveShares() } returns listOf(activeShare)
        // stateful mirror: upserts become readable to LWW/parent walks mid-batch
        coEvery { dao.findChecklist(any()) } answers { mirror[firstArg()] }
        coEvery { dao.findTask(any()) } answers { taskMirror[firstArg()] }
        coEvery { dao.applyChange(any(), any(), any(), any(), any(), any()) } answers {
            loggedChanges += arg<ShareChangeLogDTO>(0)
            arg<HostChecklistDTO?>(1)?.let { mirror[it.uuid] = it }
            arg<HostTaskDTO?>(2)?.let { taskMirror[it.uuid] = it }
            arg<String?>(4)?.let { mirror.remove(it) }
            arg<String?>(5)?.let { taskMirror.remove(it) }
        }
        coEvery { dao.insertChange(any()) } answers { loggedChanges += firstArg<ShareChangeLogDTO>() }
        coEvery { dao.deleteChecklists(any()) } answers { firstArg<List<String>>().forEach { mirror.remove(it) } }
        coEvery { dao.deleteTasks(any()) } answers { firstArg<List<String>>().forEach { taskMirror.remove(it) } }
    }

    // ---------- scoping ----------

    @Test
    fun `GIVEN a non-member caller WHEN a change is pushed THEN the batch is rejected as not accepted`() = runTest {
        coEvery { dao.getMemberShareIds("stranger") } returns emptyList()

        try {
            sut.ingest("stranger", listOf(checklistUpsert(uuid = ROOT_UUID)))
            throw AssertionError("expected ShareNotAccepted")
        } catch (expected: ShareError.ShareNotAccepted) {
            assertEquals("share_not_accepted", expected.kind)
        }
    }

    @Test
    fun `GIVEN an unknown entity WHEN a change is pushed THEN the batch is rejected as validation`() = runTest {
        try {
            sut.ingest(
                GUEST,
                listOf(SyncChangeDto(entity = "section", uuid = "x", op = "upsert", at = NOW, payload = EMPTY_OBJECT)),
            )
            throw AssertionError("expected ValidationRejected")
        } catch (expected: ShareError.ValidationRejected) {
            assertEquals("validation", expected.kind)
        }
    }

    @Test
    fun `GIVEN a new checklist whose parent is missing WHEN a change is pushed THEN the batch is rejected`() = runTest {
        try {
            sut.ingest(GUEST, listOf(checklistUpsert(uuid = "section-new", parentUuid = ROOT_UUID)))
            throw AssertionError("expected ValidationRejected")
        } catch (expected: ShareError.ValidationRejected) {
            assertTrue(expected.message!!.contains("missing parent"))
        }
    }

    // ---------- LWW ----------

    @Test
    fun `GIVEN a stale upsert WHEN the stored mirror is newer THEN the change is acked ignored`() = runTest {
        mirror[ROOT_UUID] = mirrorRow(at = NOW + 1)

        val acks = sut.ingest(GUEST, listOf(checklistUpsert(uuid = ROOT_UUID)))

        assertEquals("ignored", acks.single().status)
        coVerify(exactly = 0) { dao.insertChange(any()) }
    }

    @Test
    fun `GIVEN a tombstone newer than the upsert WHEN a deleted row is re-pushed THEN it stays deleted`() = runTest {
        coEvery { dao.findTombstone(ROOT_UUID) } returns ShareTombstoneDTO(
            uuid = ROOT_UUID, entity = ENTITY_CHECKLIST, shareId = SHARE, at = NOW + 1,
        )

        val acks = sut.ingest(GUEST, listOf(checklistUpsert(uuid = ROOT_UUID)))

        assertEquals("ignored", acks.single().status)
        coVerify(exactly = 0) { dao.insertChange(any()) }
    }

    @Test
    fun `GIVEN a strictly newer change WHEN pushed THEN it is applied and logged`() = runTest {
        mirror[ROOT_UUID] = mirrorRow(at = NOW - 1)

        val acks = sut.ingest(GUEST, listOf(checklistUpsert(uuid = ROOT_UUID)))

        assertEquals("applied", acks.single().status)
        val change = loggedChanges.single()
        assertEquals(SHARE, change.shareId)
        assertEquals(SyncOutboxEntry.OP_UPSERT, change.op)
        // selection is device-local — stripped at ingest even if the client sent true
        assertTrue(change.payload.contains(""""isSelected":false"""))
    }

    @Test
    fun `GIVEN the exact same change re-pushed WHEN ingested THEN it is idempotently ignored`() = runTest {
        mirror[ROOT_UUID] = mirrorRow(at = NOW)

        val acks = sut.ingest(GUEST, listOf(checklistUpsert(uuid = ROOT_UUID)))

        assertEquals("ignored", acks.single().status)
    }

    // ---------- cascade ----------

    @Test
    fun `GIVEN a root delete WHEN the subtree has sections and tasks THEN every descendant is tombstoned and logged`() = runTest {
        mirror[ROOT_UUID] = mirrorRow(at = NOW - 1)
        coEvery { dao.findChecklistChildren(ROOT_UUID) } returns listOf(sectionDto())
        coEvery { dao.findChecklistChildren("section-1") } returns emptyList()
        coEvery { dao.findTasks(ROOT_UUID) } returns emptyList()
        coEvery { dao.findTasks("section-1") } returns listOf(taskDto("task-1", parent = "section-1"))

        val acks = sut.ingest(GUEST, listOf(checklistDelete(ROOT_UUID)))

        assertEquals("applied", acks.single().status)
        // root + section + task, in parents-first order
        assertEquals(listOf(ROOT_UUID, "section-1", "task-1"), loggedChanges.map { it.uuid })
        coVerify(exactly = 3) { dao.insertTombstone(any()) }
        assertTrue(mirror.isEmpty())
        assertTrue(taskMirror.isEmpty())
    }

    @Test
    fun `GIVEN parents-first ordering WHEN root and section arrive out of order THEN the parent is applied first`() = runTest {
        val changes = listOf(
            checklistUpsert(uuid = "section-new", parentUuid = ROOT_UUID, at = NOW),
            checklistUpsert(uuid = ROOT_UUID, parentUuid = null, at = NOW - 1),
        )

        val acks = sut.ingest(GUEST, changes)

        assertEquals(listOf("applied", "applied"), acks.map { it.status })
        assertEquals(listOf(ROOT_UUID, "section-new"), loggedChanges.map { it.uuid })
    }

    // ---------- helpers ----------

    private fun mirrorRow(at: Long) = HostChecklistDTO(
        uuid = ROOT_UUID, parentUuid = null, name = "Groceries",
        createdDate = 1_700_000_000_000L, lastUpdate = at, shareId = SHARE, at = at,
    )

    private fun sectionDto() = HostChecklistDTO(
        uuid = "section-1", parentUuid = ROOT_UUID, name = "Section",
        createdDate = 1_700_000_000_000L, lastUpdate = NOW, shareId = SHARE, at = NOW,
    )

    private fun taskDto(uuid: String, parent: String) = HostTaskDTO(
        uuid = uuid, parentUuid = parent, name = "Buy milk",
        isCompleted = false, dateCreated = NOW, shareId = SHARE, at = NOW,
    )

    private fun checklistUpsert(
        uuid: String,
        parentUuid: String? = null,
        at: Long = NOW,
    ) = SyncChangeDto(
        entity = ENTITY_CHECKLIST,
        uuid = uuid,
        op = SyncOutboxEntry.OP_UPSERT,
        at = at,
        payload = buildJsonObject {
            put("uuid", uuid)
            put("name", "Groceries")
            put("parentUuid", parentUuid?.let { JsonPrimitive(it) } ?: JsonPrimitive(null))
            put("isSelected", true) // clients must not be able to push selection
            put("createdDate", 1_700_000_000_000L)
            put("lastUpdate", at)
        },
    )

    private fun checklistDelete(uuid: String) = SyncChangeDto(
        entity = ENTITY_CHECKLIST,
        uuid = uuid,
        op = SyncOutboxEntry.OP_DELETE,
        at = NOW,
        payload = buildJsonObject { put("uuid", uuid) },
    )

    private companion object {
        const val GUEST = "guest-1"
        const val SHARE = "share-1"
        const val ROOT_UUID = "c-root"
        const val ENTITY_CHECKLIST = "checklist"
        const val NOW = 1_700_000_000_500L
        val EMPTY_OBJECT = buildJsonObject {}
    }
}
