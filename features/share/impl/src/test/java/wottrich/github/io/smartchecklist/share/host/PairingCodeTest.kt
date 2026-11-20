package wottrich.github.io.smartchecklist.share.host

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.SecureRandom

/** Windowed pairing codes: derivation, rotation, proof round-trip, normalization. */
class PairingCodeTest {

    private val masterSecret = ByteArray(32).also { SecureRandom().nextBytes(it) }

    @Test
    fun `GIVEN a window index WHEN the code is derived THEN it is formatted XXXX-XXXX`() {
        val code = PairingCode.displayCode(masterSecret, windowIndex = 100)

        assertEquals(9, code.length)
        assertEquals('-', code[4])
        assertTrue(code.all { it == '-' || it in PairingCode.normalize(PairingCodeAbc.CODE_ALPHABET_ALL) })
    }

    @Test
    fun `GIVEN consecutive windows WHEN codes are derived THEN they rotate`() {
        val current = PairingCode.displayCode(masterSecret, windowIndex = 1_000)
        val next = PairingCode.displayCode(masterSecret, windowIndex = 1_001)

        assertNotEquals(current, next)
    }

    @Test
    fun `GIVEN the same secret and window WHEN a code is derived twice THEN it is stable`() {
        assertEquals(
            PairingCode.displayCode(masterSecret, windowIndex = 42),
            PairingCode.displayCode(masterSecret, windowIndex = 42),
        )
    }

    @Test
    fun `GIVEN a different master secret WHEN codes are derived THEN they differ`() {
        val otherSecret = ByteArray(32).also { SecureRandom().nextBytes(it) }

        assertNotEquals(
            PairingCode.displayCode(masterSecret, windowIndex = 42),
            PairingCode.displayCode(otherSecret, windowIndex = 42),
        )
    }

    @Test
    fun `GIVEN a proof of the displayed code WHEN verified against the same inputs THEN it matches`() {
        val code = PairingCode.displayCode(masterSecret, windowIndex = 7)
        val proof = PairingCode.proofOf(code, deviceName = "Guest phone", nonce = "nonce-1")

        val expected = PairingCode.proofOf(code, deviceName = "Guest phone", nonce = "nonce-1")
        assertEquals(expected, proof)
    }

    @Test
    fun `GIVEN proof computed from a lowercase spaced code WHEN compared THEN normalization makes it equal`() {
        val code = PairingCode.displayCode(masterSecret, windowIndex = 7)

        assertEquals(
            PairingCode.proofOf(code, "d", "n"),
            PairingCode.proofOf(code.lowercase().replace("-", " "), "d", "n"),
        )
    }

    @Test
    fun `GIVEN token hashing WHEN the same token is hashed twice THEN hashes are equal and never the raw token`() {
        val hash = PairingCode.tokenHash("raw-token")

        assertEquals(hash, PairingCode.tokenHash("raw-token"))
        assertNotEquals("raw-token", hash)
        assertEquals(64, hash.length) // hex sha-256
    }

    @Test
    fun `GIVEN window math WHEN now is mid-window THEN rotation happens before the next window starts`() {
        val windowMs = 10 * 60 * 1_000L
        val now = 1_234_567_890_123L

        assertTrue(PairingCode.msUntilRotation(now) in 1 until windowMs)
        assertEquals(now / windowMs, PairingCode.windowIndex(now))
    }
}

/** Compile-time accessor for the alphabet used in the format assertion above. */
private object PairingCodeAbc {
    const val CODE_ALPHABET_ALL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
}
