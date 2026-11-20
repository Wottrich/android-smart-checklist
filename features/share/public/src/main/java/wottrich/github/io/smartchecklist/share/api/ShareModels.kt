package wottrich.github.io.smartchecklist.share.api

import kotlinx.serialization.Serializable

/**
 * Wire DTOs for the device-hosted share protocol — the delta over
 * `smart-checklist-backend/docs/api/sync-contract.md` (v1), documented in
 * `docs/api/share-contract.md`. Dates are epoch millis. Never rename a field
 * without checking the contract.
 */

@Serializable
data class HealthResponse(
    val status: String,
    val role: String,
    /** Wire-protocol version — 2 = pairing + encrypted envelopes on push/pull. */
    val proto: Int = 1,
)

@Serializable
data class ShareOfferDto(
    val shareId: String,
    val name: String,
    val ownerDeviceName: String,
    val updatedAt: Long,
)

@Serializable
data class ShareOffersResponse(val offers: List<ShareOfferDto> = emptyList())

@Serializable
data class ShareAcceptResponse(val shareId: String, val cursor: Long)

/**
 * Pairing (v2 security hardening): the guest proves knowledge of the owner's
 * displayed pairing code with an HMAC proof — the code itself never travels
 * the network.
 */
@Serializable
data class PairRequest(
    val deviceName: String? = null,
    /** HMAC-SHA256(code, "$deviceName|$nonce"), base64. */
    val proof: String,
    val nonce: String,
)

@Serializable
data class PairResponse(
    val userId: String,
    val token: String,
    /** Epoch millis — tokens expire (30-day sliding window of activity). */
    val expiresAt: Long,
    /**
     * The host's master pairing secret, AES-GCM-wrapped under a key derived from
     * the typed code — only the device that knows the code can unwrap it. All
     * share push/pull bodies are then encrypted under keys derived from it.
     */
    val wrappedPairingSecret: String,
)
