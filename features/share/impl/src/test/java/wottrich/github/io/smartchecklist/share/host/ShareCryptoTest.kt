package wottrich.github.io.smartchecklist.share.host

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.security.SecureRandom

/** Envelope crypto (Phase S2): round-trip, AAD binding, wrong-key rejection. */
class ShareCryptoTest {

    private val masterSecret = ByteArray(32).also { SecureRandom().nextBytes(it) }

    @Test
    fun `GIVEN content sealed under a key WHEN opened with the same key THEN it round-trips`() {
        val key = ShareCrypto.contentKey(masterSecret)
        val content = """{"cursor":42,"changes":[]}"""

        val plain = ShareCrypto.open(ShareCrypto.seal(content.toByteArray(), key, "pull"), key, "pull")

        assertEquals(content, plain?.decodeToString())
    }

    @Test
    fun `GIVEN an envelope bound to one aad WHEN opened with a different aad THEN it fails (splicing blocked)`() {
        val key = ShareCrypto.contentKey(masterSecret)
        val sealed = ShareCrypto.seal("secret".toByteArray(), key, "push")

        assertNull(ShareCrypto.open(sealed, key, "pull"))
    }

    @Test
    fun `GIVEN an envelope WHEN opened with the wrong key THEN it fails`() {
        val otherSecret = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val sealed = ShareCrypto.seal("secret".toByteArray(), ShareCrypto.contentKey(masterSecret), "pull")

        assertNull(ShareCrypto.open(sealed, ShareCrypto.contentKey(otherSecret), "pull"))
    }

    @Test
    fun `GIVEN a tampered ciphertext WHEN opened THEN it fails`() {
        val key = ShareCrypto.contentKey(masterSecret)
        val sealed = ShareCrypto.seal("secret".toByteArray(), key, "pull")
        val tampered = sealed.dropLast(4) + "AAAA"

        assertNull(ShareCrypto.open(tampered, key, "pull"))
    }

    @Test
    fun `GIVEN the master secret wrapped under a code WHEN unwrapped with the same code THEN it round-trips`() {
        val wrapped = ShareCrypto.wrap(masterSecret, code = "K7RM2QP4X")

        assertEquals(masterSecret.toList(), ShareCrypto.unwrap(wrapped, code = "K7RM2QP4X")?.toList())
    }

    @Test
    fun `GIVEN wrapped secret WHEN unwrapped with the wrong code THEN it fails`() {
        val wrapped = ShareCrypto.wrap(masterSecret, code = "K7RM2QP4X")

        assertNull(ShareCrypto.unwrap(wrapped, code = "ZZZZZZZZ"))
    }

    @Test
    fun `GIVEN content keys derived from different secrets WHEN sealed THEN envelopes differ`() {
        val otherSecret = ByteArray(32).also { SecureRandom().nextBytes(it) }

        assertNotEquals(
            ShareCrypto.seal("x".toByteArray(), ShareCrypto.contentKey(masterSecret), "pull"),
            ShareCrypto.seal("x".toByteArray(), ShareCrypto.contentKey(otherSecret), "pull"),
        )
    }

    @Test
    fun `GIVEN hkdf WHEN the same inputs are used THEN keys are deterministic and 32 bytes`() {
        assertEquals(
            ShareCrypto.contentKey(masterSecret).toList(),
            ShareCrypto.contentKey(masterSecret).toList(),
        )
        assertEquals(32, ShareCrypto.contentKey(masterSecret).size)
    }
}
