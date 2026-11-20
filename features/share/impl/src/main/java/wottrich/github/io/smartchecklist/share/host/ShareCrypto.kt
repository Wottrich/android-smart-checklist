package wottrich.github.io.smartchecklist.share.host

import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import wottrich.github.io.smartchecklist.share.host.ShareCrypto.wrap
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * App-layer wire crypto for device hosts (v2 hardening, Phase S2). All share
 * push/pull bodies travel as an authenticated envelope:
 *
 * ```
 * { "v": 1, "n": "<base64 nonce>", "ct": "<base64 AES-256-GCM ciphertext>" }
 * ```
 *
 * `ct` decrypts to the exact v1 JSON body, so DTO shapes and the sync engine are
 * untouched — the envelope lives only at the `ShareHostClient`/`EmbeddedShareServer`
 * boundary. Keys derive from the master pairing secret (HKDF-SHA256); the secret
 * itself is distributed at pair time wrapped under a key derived from the typed
 * code, so a passive sniffer never sees either.
 */
@OptIn(ExperimentalEncodingApi::class)
object ShareCrypto {

    private const val ENVELOPE_VERSION = 1
    private const val GCM_TAG_BITS = 128
    private const val GCM_NONCE_BYTES = 12
    private const val HKDF_SALT_BYTES = 32
    private const val HKDF_CONTENT_KEY_INFO = "content-key"
    private const val HKDF_CODE_KEY_INFO = "code-key"

    /** Deterministic 32-byte content key for one host: HKDF(masterSecret, info). */
    fun contentKey(masterSecret: ByteArray): ByteArray = hkdf(masterSecret, HKDF_CONTENT_KEY_INFO)

    /**
     * Key used to wrap the master pairing secret during pairing: derived from the
     * code the guest typed (the code never travels, so neither does this key).
     */
    fun codeKey(code: String): ByteArray = hkdf(PairingCode.normalize(code).toByteArray(), HKDF_CODE_KEY_INFO)

    /**
     * Hides the master pairing secret for transport at pair time: only a device
     * that knows the typed code can unwrap it.
     */
    fun wrap(masterSecret: ByteArray, code: String): String {
        val nonce = ByteArray(GCM_NONCE_BYTES).also { java.security.SecureRandom().nextBytes(it) }
        val ct = encrypt(masterSecret, codeKey(code), "pair".toByteArray(), nonce)
        return Base64.encode(nonce + ct) // nonce ‖ ciphertext+tag
    }

    /** Inverse of [wrap] — returns null on wrong code / tampering. */
    fun unwrap(wrapped: String, code: String): ByteArray? = runCatching {
        val data = Base64.decode(wrapped)
        val nonce = data.copyOfRange(0, GCM_NONCE_BYTES)
        val ct = data.copyOfRange(GCM_NONCE_BYTES, data.size)
        decrypt(ct, codeKey(code), "pair".toByteArray(), nonce)
    }.getOrNull()

    /** Encrypts [content] into an envelope JSON string bound to [aad]. */
    fun seal(content: ByteArray, key: ByteArray, aad: String): String {
        val nonce = ByteArray(GCM_NONCE_BYTES).also { java.security.SecureRandom().nextBytes(it) }
        val ct = encrypt(content, key, aad.toByteArray(), nonce)
        return """{"v":$ENVELOPE_VERSION,"n":"${Base64.encode(nonce)}","ct":"${Base64.encode(ct)}"}"""
    }

    /** Opens a [sealed] envelope; returns null on tampering/version/key mismatch. */
    fun open(sealed: String, key: ByteArray, aad: String): ByteArray? = runCatching {
        val parsed = kotlinx.serialization.json.Json.parseToJsonElement(sealed).jsonObject
        if (parsed["v"]?.jsonPrimitive?.intOrNull != ENVELOPE_VERSION) return null
        val nonce = Base64.decode(parsed["n"]?.jsonPrimitive?.content ?: return null)
        val ct = Base64.decode(parsed["ct"]?.jsonPrimitive?.content ?: return null)
        decrypt(ct, key, aad.toByteArray(), nonce)
    }.getOrNull()

    private fun encrypt(content: ByteArray, key: ByteArray, aad: ByteArray, nonce: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_BITS, nonce))
        cipher.updateAAD(aad)
        return cipher.doFinal(content) // ciphertext ‖ 128-bit tag
    }

    private fun decrypt(data: ByteArray, key: ByteArray, aad: ByteArray, nonce: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(GCM_TAG_BITS, nonce))
        cipher.updateAAD(aad)
        return cipher.doFinal(data)
    }

    /** RFC 5869 HKDF-SHA256, one block (≤ 32 bytes out) — enough for AES-256. */
    fun hkdf(ikm: ByteArray, info: String, length: Int = 32): ByteArray {
        val prk = hmac(ByteArray(HKDF_SALT_BYTES), ikm) // zero salt per spec
        val t1 = hmac(prk, info.toByteArray() + 0x01)
        return t1.copyOf(length.coerceAtMost(t1.size))
    }

    private fun hmac(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    private val kotlinx.serialization.json.JsonPrimitive.intOrNull: Int?
        get() = runCatching { content.toIntOrNull() }.getOrNull()
}
