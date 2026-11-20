package wottrich.github.io.smartchecklist.share.host

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.share.api.PairRequest
import wottrich.github.io.smartchecklist.share.domain.ShareError
import wottrich.github.io.smartchecklist.share.host.db.ShareChangeLogDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDao
import wottrich.github.io.smartchecklist.share.host.db.ShareGuestDTO

/**
 * Pairing lifecycle, auth and share-scoped pull of the host service (v2 — there
 * is no self-service registration). Ingest is covered by
 * [ShareHostIngestServiceTest].
 */
class ShareHostServiceTest {

    private val dao: ShareDao = mockk(relaxed = true)
    private val ingestService: ShareHostIngestService = mockk(relaxed = true)
    private val prefs: ShareHostPrefs = mockk(relaxed = true)
    private lateinit var sut: ShareHostService

    private val masterSecret = ByteArray(32).also { java.security.SecureRandom().nextBytes(it) }

    @Before
    fun setUp() {
        coEvery { prefs.getDeviceName() } returns "Pixel 8"
        coEvery { prefs.getMasterPairingSecret() } returns masterSecret
        sut = ShareHostService(dao, ingestService, prefs)
        coEvery { dao.findGuestByTokenHash(any()) } returns pairedGuest(
            tokenHash = PairingCode.tokenHash(TOKEN),
            expiresAt = System.currentTimeMillis() + 60_000,
        )
        coEvery { dao.getShare(SHARE) } returns ShareDTO(
            shareId = SHARE, rootUuid = "c-root", name = "Groceries", createdAt = 1_700_000_000_000L,
        )
        coEvery { dao.isMember(SHARE, GUEST) } returns true
        coEvery { dao.maxServerSeq(SHARE) } returns 10L
    }

    // ---------- pairing ----------

    @Test
    fun `GIVEN the displayed code WHEN pairing THEN a device token is issued`() = runTest {
        val code = sut.currentPairingCode()

        val response = sut.pair(pairRequestFor(code))

        assertTrue(response.token.isNotBlank())
        assertTrue(response.expiresAt > System.currentTimeMillis())
        coVerify { dao.insertGuest(any()) }
    }

    @Test
    fun `GIVEN the previous window code WHEN pairing THEN typing delay is tolerated`() = runTest {
        val previousWindowIndex = PairingCode.windowIndex() - 1
        val staleCode = PairingCode.displayCode(masterSecret, previousWindowIndex)

        val response = sut.pair(pairRequestFor(staleCode))

        assertTrue(response.token.isNotBlank())
    }

    @Test
    fun `GIVEN a wrong code WHEN pairing THEN pairing fails without a token`() = runTest {
        try {
            sut.pair(pairRequestFor("AAAA-BBBB"))
            throw AssertionError("expected PairingFailed")
        } catch (expected: ShareError.PairingFailed) {
            assertEquals(false, expected.retryLater)
            coVerify(exactly = 0) { dao.insertGuest(any()) }
        }
    }

    @Test
    fun `GIVEN proof of a different code WHEN pairing THEN it does not verify`() = runTest {
        val realCode = sut.currentPairingCode()

        try {
            sut.pair(pairRequestFor(realCode, signedForCode = "ZZZZ-ZZZZ"))
            throw AssertionError("expected PairingFailed")
        } catch (expected: ShareError.PairingFailed) {
            // proof is bound to the exact code — no substitution
        }
    }

    @Test
    fun `GIVEN more attempts than the cap WHEN pairing within a minute THEN later attempts are rate limited`() = runTest {
        repeat(MAX_ATTEMPTS) { runCatching { sut.pair(pairRequestFor("AAAA-BBBB")) } }

        try {
            sut.pair(pairRequestFor(sut.currentPairingCode()))
            throw AssertionError("expected rate limit")
        } catch (expected: ShareError.PairingFailed) {
            assertEquals(true, expected.retryLater)
        }
    }

    @Test
    fun `GIVEN reset pairing WHEN called THEN all guests are revoked and the master secret rotates`() = runTest {
        sut.resetPairing()

        coVerify { dao.revokeAllGuests() }
        coVerify { prefs.setOwnerToken(null) }
        coVerify { prefs.setMasterPairingSecret(match { it.size == 32 && it.any { byte -> byte != 0.toByte() } }) }
    }

    // ---------- auth ----------

    @Test
    fun `GIVEN a valid paired token WHEN authenticating THEN the userId is returned and expiry slides`() = runTest {
        val userId = sut.authenticate(TOKEN)

        assertEquals(GUEST, userId)
        coVerify { dao.touchGuest(GUEST, any(), any()) }
    }

    @Test
    fun `GIVEN a missing or unknown token WHEN authenticating THEN unauthenticated is thrown`() = runTest {
        try {
            sut.authenticate(null)
            throw AssertionError("expected Unauthenticated")
        } catch (expected: ShareError.Unauthenticated) {
            assertEquals("unauthenticated", expected.kind)
        }
        coEvery { dao.findGuestByTokenHash(any()) } returns null
        try {
            sut.authenticate("wrong-token")
            throw AssertionError("expected Unauthenticated")
        } catch (expected: ShareError.Unauthenticated) {
            // same shape for unknown tokens
        }
    }

    @Test
    fun `GIVEN an expired token WHEN authenticating THEN token_expired is thrown`() = runTest {
        coEvery { dao.findGuestByTokenHash(any()) } returns pairedGuest(
            tokenHash = PairingCode.tokenHash(TOKEN),
            expiresAt = System.currentTimeMillis() - 1_000,
        )

        try {
            sut.authenticate(TOKEN)
            throw AssertionError("expected TokenExpired")
        } catch (expected: ShareError.TokenExpired) {
            assertEquals("token_expired", expected.kind)
        }
    }

    @Test
    fun `GIVEN a revoked device WHEN authenticating THEN device_revoked is thrown`() = runTest {
        coEvery { dao.findGuestByTokenHash(any()) } returns pairedGuest(
            tokenHash = PairingCode.tokenHash(TOKEN),
            expiresAt = System.currentTimeMillis() + 60_000,
            revoked = true,
        )

        try {
            sut.authenticate(TOKEN)
            throw AssertionError("expected DeviceRevoked")
        } catch (expected: ShareError.DeviceRevoked) {
            assertEquals("device_revoked", expected.kind)
        }
    }

    // ---------- offers gating ----------

    @Test
    fun `GIVEN offers WHEN built THEN they carry name device and last activity (paired callers only reach here)`() = runTest {
        coEvery { dao.getActiveShares() } returns listOf(
            ShareDTO(shareId = SHARE, rootUuid = "c-root", name = "Groceries", createdAt = 1_700_000_000_000L),
        )
        coEvery { dao.lastChangeAt(SHARE) } returns 1_700_000_000_999L

        val offers = sut.offers()

        assertEquals(1, offers.size)
        assertEquals("Groceries", offers[0].name)
        assertEquals("Pixel 8", offers[0].ownerDeviceName)
        assertEquals(1_700_000_000_999L, offers[0].updatedAt)
    }

    // ---------- membership ----------

    @Test
    fun `GIVEN an unknown shareId WHEN accept is called THEN share_not_found is thrown`() = runTest {
        try {
            sut.accept(GUEST, shareId = "unknown")
            throw AssertionError("expected ShareNotFound")
        } catch (expected: ShareError.ShareNotFound) {
            assertEquals("share_not_found", expected.kind)
        }
    }

    @Test
    fun `GIVEN a member accepting again WHEN accept is called THEN it is idempotent and returns the current cursor`() = runTest {
        coEvery { dao.isMember(SHARE, GUEST) } returns true

        val response = sut.accept(GUEST, SHARE)

        assertEquals(SHARE, response.shareId)
        assertEquals(10L, response.cursor)
        coVerify(exactly = 0) { dao.insertMember(any()) }
    }

    // ---------- pull ----------

    @Test
    fun `GIVEN a non-member WHEN pulling a share THEN share_not_accepted is thrown`() = runTest {
        coEvery { dao.isMember(SHARE, GUEST) } returns false

        try {
            sut.pull(TOKEN, SHARE, since = 0, limit = 500)
            throw AssertionError("expected ShareNotAccepted")
        } catch (expected: ShareError.ShareNotAccepted) {
            assertEquals("share_not_accepted", expected.kind)
        }
    }

    @Test
    fun `GIVEN a pull without shareId WHEN pulling THEN validation is rejected (pulls are share-scoped)`() = runTest {
        try {
            sut.pull(TOKEN, shareId = null, since = 0, limit = 500)
            throw AssertionError("expected ValidationRejected")
        } catch (expected: ShareError.ValidationRejected) {
            // device hosts never serve a global change log
        }
    }

    @Test
    fun `GIVEN a pending page WHEN pulling THEN changes are served with cursor and hasMore`() = runTest {
        coEvery { dao.pullPage(SHARE, since = 4, limit = 2) } returns listOf(
            changeRow(seq = 5, uuid = "c-a"),
            changeRow(seq = 6, uuid = "t-b"),
        )

        val response = sut.pull(TOKEN, SHARE, since = 4, limit = 2)

        assertEquals(6L, response.cursor)
        assertTrue(response.hasMore) // max seq is 10
        assertEquals(listOf(5L, 6L), response.changes.map { it.serverSeq })
    }

    @Test
    fun `GIVEN the last page WHEN pulling THEN hasMore is false and the cursor catches up`() = runTest {
        coEvery { dao.pullPage(SHARE, since = 9, limit = 500) } returns listOf(changeRow(seq = 10, uuid = "c-a"))

        val response = sut.pull(TOKEN, SHARE, since = 9, limit = 500)

        assertEquals(10L, response.cursor)
        assertEquals(false, response.hasMore)
    }

    // ---------- helpers ----------

    /** Builds a wire-shaped pair request proving knowledge of [signedForCode]. */
    private fun pairRequestFor(code: String, signedForCode: String = code): PairRequest {
        val nonce = "nonce-1"
        return PairRequest(
            deviceName = "Guest phone",
            proof = PairingCode.proofOf(signedForCode, deviceName = "Guest phone", nonce = nonce),
            nonce = nonce,
        )
    }

    private fun pairedGuest(tokenHash: String, expiresAt: Long, revoked: Boolean = false) = ShareGuestDTO(
        userId = GUEST,
        tokenHash = tokenHash,
        name = "Guest phone",
        approvedAt = 0,
        lastSeenAt = 0,
        expiresAt = expiresAt,
        revoked = revoked,
    )

    private fun changeRow(seq: Long, uuid: String) = ShareChangeLogDTO(
        serverSeq = seq,
        entity = "checklist",
        uuid = uuid,
        op = "upsert",
        at = 1_700_000_000_000L,
        shareId = SHARE,
        payload = """{"uuid":"$uuid"}""",
    )

    private companion object {
        const val GUEST = "guest-1"
        const val TOKEN = "guest-token"
        const val SHARE = "share-1"
        const val MAX_ATTEMPTS = 5
    }
}
