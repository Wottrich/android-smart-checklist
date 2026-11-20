package wottrich.github.io.smartchecklist.share.host

import wottrich.github.io.smartchecklist.serversync.api.RegisterResponse
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeDto
import wottrich.github.io.smartchecklist.serversync.api.SyncChangeEntryDto
import wottrich.github.io.smartchecklist.serversync.api.SyncPullResponse
import wottrich.github.io.smartchecklist.serversync.api.SyncPushResponse
import wottrich.github.io.smartchecklist.share.api.PairRequest
import wottrich.github.io.smartchecklist.share.api.PairResponse
import wottrich.github.io.smartchecklist.share.api.ShareAcceptResponse
import wottrich.github.io.smartchecklist.share.domain.ShareError
import wottrich.github.io.smartchecklist.share.host.db.ShareChangeLogDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareDao
import wottrich.github.io.smartchecklist.share.host.db.ShareGuestDTO
import wottrich.github.io.smartchecklist.share.host.db.ShareMemberDTO
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

/**
 * Host-side service behind the embedded server (`share-contract.md` v2): pairing,
 * token lifecycle, offers, membership grants, share-scoped pull and validated
 * push. No HTTP here — `EmbeddedShareServer` is a thin shell over this.
 *
 * v2 hardening: **there is no self-service registration.** A device only gets a
 * credential by presenting a proof of the owner's displayed pairing code; tokens
 * are stored hashed, expire after 30 days of inactivity (sliding) and can be
 * revoked ("Reset pairing").
 */
class ShareHostService(
    private val dao: ShareDao,
    private val ingestService: ShareHostIngestService,
    private val prefs: ShareHostPrefs,
) {

    // ---------- identity ----------

    /**
     * Validates a bearer token → userId. Revoked → [ShareError.DeviceRevoked]
     * (guest detaches); expired → [ShareError.TokenExpired] (re-pair). Extends the
     * sliding expiry on every successful authentication.
     */
    suspend fun authenticate(token: String?): String {
        if (token.isNullOrBlank()) throw ShareError.Unauthenticated()
        val guest = dao.findGuestByTokenHash(PairingCode.tokenHash(token))
            ?: throw ShareError.Unauthenticated()
        if (guest.revoked) throw ShareError.DeviceRevoked()
        val now = System.currentTimeMillis()
        if (guest.expiresAt <= now) throw ShareError.TokenExpired()
        dao.touchGuest(guest.userId, now, now + TOKEN_TTL_MS)
        return guest.userId
    }

    /**
     * Owner-side identity creation — **in-process only** (the owner's loopback
     * device pairs implicitly at `share()` time). Not reachable via HTTP in v2;
     * the network-facing path is [pair].
     */
    suspend fun register(deviceName: String?): RegisterResponse {
        val rawToken = UuidGenerator.getRandomUuid() + UuidGenerator.getRandomUuid()
        val now = System.currentTimeMillis()
        val guest = ShareGuestDTO(
            userId = UuidGenerator.getRandomUuid(),
            tokenHash = PairingCode.tokenHash(rawToken),
            name = deviceName,
            approvedAt = now,
            lastSeenAt = now,
            expiresAt = now + TOKEN_TTL_MS,
        )
        dao.insertGuest(guest)
        return RegisterResponse(userId = guest.userId, token = rawToken)
    }

    // ---------- pairing (v2) ----------

    /** The code the owner screen displays right now (`XXXX-XXXX`). */
    fun currentPairingCode(): String =
        PairingCode.displayCode(prefs.getMasterPairingSecret(), PairingCode.windowIndex())

    /** Millis until the displayed code rotates — for the UI countdown. */
    fun msUntilCodeRotation(): Long = PairingCode.msUntilRotation()

    /**
     * Pairs a guest device: verifies the HMAC proof of the displayed code
     * (current or previous window), rate-limited. Wrong code →
     * [ShareError.PairingFailed]; flooder → [ShareError.PairingFailed(retryLater)].
     */
    suspend fun pair(request: PairRequest): PairResponse {
        if (!attemptLimiter.tryAcquire()) {
            throw ShareError.PairingFailed(retryLater = true)
        }
        // the guest proves knowledge of the CODE; the host recomputes the expected
        // proof per candidate window code instead of trusting any client-sent code
        val matchedCode = verifyPairingProof(request)
            ?: throw ShareError.PairingFailed(retryLater = false)
        val rawToken = UuidGenerator.getRandomUuid() + UuidGenerator.getRandomUuid()
        val now = System.currentTimeMillis()
        val guest = ShareGuestDTO(
            userId = UuidGenerator.getRandomUuid(),
            tokenHash = PairingCode.tokenHash(rawToken),
            name = request.deviceName,
            approvedAt = now,
            lastSeenAt = now,
            expiresAt = now + TOKEN_TTL_MS,
        )
        dao.insertGuest(guest)
        // the master secret travels wrapped under a key derived from the typed code:
        // a sniffer without the code can neither unwrap it nor forge proofs
        return PairResponse(
            userId = guest.userId,
            token = rawToken,
            expiresAt = guest.expiresAt,
            wrappedPairingSecret = ShareCrypto.wrap(prefs.getMasterPairingSecret(), matchedCode),
        )
    }

    /**
     * Owner "Reset pairing": every guest becomes revoked (they keep frozen local
     * copies — same semantics as share-end) and the master secret rotates, so old
     * codes die instantly. The owner's own loopback re-pairs on the next `share()`.
     */
    suspend fun resetPairing() {
        dao.revokeAllGuests()
        prefs.setMasterPairingSecret(ByteArray(MASTER_SECRET_BYTES).also { bytes ->
            java.security.SecureRandom().nextBytes(bytes)
        })
        prefs.setOwnerToken(null) // owner re-pairs via the next share() call
    }

    /** The code whose window matched [request.proof], or null (constant-time compares). */
    private fun verifyPairingProof(request: PairRequest): String? {
        val master = prefs.getMasterPairingSecret()
        val current = PairingCode.windowIndex()
        return listOf(current, current - 1)
            .map { PairingCode.displayCode(master, it) }
            .firstOrNull { candidate ->
                val expected = PairingCode.proofOf(candidate, request.deviceName, request.nonce)
                constantTimeEquals(expected, request.proof)
            }
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].code xor b[i].code)
        return result == 0
    }

    private val attemptLimiter = object {
        private var windowStart = 0L
        private var count = 0
        private val lock = Any()

        fun tryAcquire(): Boolean = synchronized(lock) {
            val now = System.currentTimeMillis()
            if (now - windowStart > ATTEMPT_WINDOW_MS) {
                windowStart = now
                count = 0
            }
            if (count >= MAX_ATTEMPTS_PER_WINDOW) return false
            count++
            true
        }
    }

    // ---------- offers / membership ----------

    /** Creates a share offer (the checklist keeps living in the owner's main Room). */
    suspend fun createShare(rootUuid: String, name: String): String {
        val existing = dao.getActiveShares().firstOrNull { it.rootUuid == rootUuid }
        if (existing != null) return existing.shareId
        val shareId = UuidGenerator.getRandomUuid()
        dao.insertShare(
            ShareDTO(shareId = shareId, rootUuid = rootUuid, name = name, createdAt = System.currentTimeMillis())
        )
        return shareId
    }

    /** Ends a share: the offer disappears; guests converge to detach on their next pull. */
    suspend fun endShare(shareId: String): ShareDTO {
        val share = dao.getShare(shareId) ?: throw ShareError.ShareNotFound()
        dao.deactivateShare(shareId)
        return share
    }

    suspend fun hasActiveShares(): Boolean = dao.getActiveShares().isNotEmpty()

    /** Owner self-membership on its own host (loopback). Idempotent. */
    suspend fun acceptSelf(token: String, shareId: String) {
        val userId = authenticate(token)
        accept(userId, shareId)
    }

    suspend fun offers(): List<ShareOfferSnapshot> {
        return dao.getActiveShares().map { share ->
            ShareOfferSnapshot(
                shareId = share.shareId,
                name = share.name,
                ownerDeviceName = prefs.getDeviceName(),
                updatedAt = maxOf(share.createdAt, dao.lastChangeAt(share.shareId)),
            )
        }
    }

    suspend fun accept(userId: String, shareId: String): ShareAcceptResponse {
        val share = dao.getShare(shareId) ?: throw ShareError.ShareNotFound()
        if (!share.active) throw ShareError.ShareNotFound()
        // idempotent for repeat accepts
        if (!dao.isMember(shareId, userId)) {
            dao.insertMember(ShareMemberDTO(shareId = shareId, userId = userId, acceptedAt = System.currentTimeMillis()))
        }
        return ShareAcceptResponse(shareId = shareId, cursor = dao.maxServerSeq(shareId))
    }

    suspend fun guestCount(): Int = dao.getActiveShares().sumOf { dao.countMembers(it.shareId) }

    // ---------- pull ----------

    suspend fun pull(token: String?, shareId: String?, since: Long, limit: Int): SyncPullResponse {
        val userId = authenticate(token)
        if (shareId.isNullOrBlank()) {
            throw ShareError.ValidationRejected("shareId is required on a device host")
        }
        val share = dao.getShare(shareId) ?: throw ShareError.ShareNotFound()
        if (!share.active) throw ShareError.ShareNotFound()
        if (!dao.isMember(shareId, userId)) throw ShareError.ShareNotAccepted()

        val page = dao.pullPage(shareId, since, limit)
        val cursor = if (page.isEmpty()) {
            maxOf(since, dao.maxServerSeq(shareId))
        } else {
            page.last().serverSeq
        }
        val hasMore = dao.maxServerSeq(shareId) > cursor
        return SyncPullResponse(
            cursor = cursor,
            hasMore = hasMore,
            changes = page.map { it.toEntryDto() },
        )
    }

    // ---------- push ----------

    suspend fun push(token: String?, changes: List<SyncChangeDto>): SyncPushResponse {
        val userId = authenticate(token)
        // v2 hardening caps (share-contract.md): a phone hosts on best effort
        if (changes.size > MAX_BATCH_CHANGES) {
            throw ShareError.ValidationRejected("batch too large (max $MAX_BATCH_CHANGES changes)")
        }
        if (!pushRateLimiter.tryAcquire(userId)) {
            throw ShareError.TooManyRequests()
        }
        val acks = ingestService.ingest(userId, changes)
        return SyncPushResponse(results = acks)
    }

    /** Purges guests revoked/expired more than RETENTION_DAYS ago — on host start. */
    suspend fun purgeStaleGuests() {
        dao.purgeStaleGuests(System.currentTimeMillis() - RETENTION_DAYS_MS)
    }

    private fun ShareChangeLogDTO.toEntryDto() = SyncChangeEntryDto(
        entity = entity,
        uuid = uuid,
        op = op,
        at = at,
        serverSeq = serverSeq,
        payload = ServerSyncJson.parseToJsonElement(payload),
    )

    /** Per-token sliding-minute push rate limiter (v2 hardening). */
    private val pushRateLimiter = object {
        private val counts = HashMap<String, Int>()
        private var windowStart = 0L
        private val lock = Any()

        fun tryAcquire(userId: String): Boolean = synchronized(lock) {
            val now = System.currentTimeMillis()
            if (now - windowStart > RATE_WINDOW_MS) {
                windowStart = now
                counts.clear()
            }
            val next = (counts[userId] ?: 0) + 1
            if (next > MAX_PUSHES_PER_WINDOW) return false
            counts[userId] = next
            true
        }
    }

    private companion object {
        const val TOKEN_TTL_MS = 30L * 24 * 60 * 60 * 1_000
        const val ATTEMPT_WINDOW_MS = 60_000L
        const val MAX_ATTEMPTS_PER_WINDOW = 5
        const val MASTER_SECRET_BYTES = 32
        const val MAX_BATCH_CHANGES = 200
        const val RATE_WINDOW_MS = 60_000L
        const val MAX_PUSHES_PER_WINDOW = 30
        const val RETENTION_DAYS_MS = 30L * 24 * 60 * 60 * 1_000
    }
}

/** Wire shape of `GET /v1/share/offers`. */
data class ShareOfferSnapshot(
    val shareId: String,
    val name: String,
    val ownerDeviceName: String,
    val updatedAt: Long,
)
