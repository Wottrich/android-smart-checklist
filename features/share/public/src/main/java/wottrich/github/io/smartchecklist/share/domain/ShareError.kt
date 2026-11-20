package wottrich.github.io.smartchecklist.share.domain

/**
 * Share-domain failures. `kind` strings match the wire codes of
 * `docs/api/share-contract.md` (`share_not_found`, `share_not_accepted`, …).
 */
sealed class ShareError(message: String, val kind: String) : Exception(message) {

    /** The shareId does not exist (or sharing ended — same tombstone effect). */
    class ShareNotFound : ShareError("share not found", "share_not_found")

    /** Caller is not an accepted member of the share subtree. */
    class ShareNotAccepted : ShareError("share not accepted", "share_not_accepted")

    /** Missing/unknown bearer token. */
    class Unauthenticated : ShareError("unauthenticated", "unauthenticated")

    /**
     * The host requires a pairing code before anything is revealed or synced
     * (v2 security hardening — offers and sync are gated behind pairing).
     */
    class PairingRequired : ShareError("pairing required", "pairing_required")

    /** Wrong pairing code — or too many attempts in a row (`retryLater`). */
    class PairingFailed(val retryLater: Boolean) :
        ShareError("pairing failed", if (retryLater) "too_many_attempts" else "pairing_failed")

    /**
     * The owner revoked this device ("Reset pairing" / explicit revoke): the guest
     * detaches and keeps a frozen copy — same semantics as [ShareNotFound].
     */
    class DeviceRevoked : ShareError("device revoked", "device_revoked")

    /** The device token expired (30-day sliding window) — pair again. */
    class TokenExpired : ShareError("token expired", "token_expired")

    /** Rate limit exceeded (pairing attempts or push batches) — retry later. */
    class TooManyRequests : ShareError("too many requests", "too_many_requests")

    /** Push rejected — whole batch is dropped (parents-first, membership, payload). */
    class ValidationRejected(message: String) : ShareError(message, "validation")

    /** Host unreachable on the LAN (or the app is closed — foreground-only v1). */
    class HostUnreachable(cause: Throwable?) :
        ShareError(cause?.message ?: "host unreachable", "host_unreachable")
}
