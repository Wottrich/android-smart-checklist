package wottrich.github.io.smartchecklist.share.host

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Windowed pairing codes (v2 hardening): the owner screen displays an 8-char code
 * derived from the host's **master pairing secret** and the current 10-minute
 * window — it rotates itself and never travels the network. The guest proves
 * knowledge of the code with an HMAC proof (`pairWithCode`), and the host verifies
 * against the current + previous window (typing delay tolerance).
 *
 * The master secret lives in `ShareHostPrefs` (host device only); codes are
 * derivations, so a leaked old code dies with its window. Attempt limiting lives
 * in [ShareHostService].
 */
object PairingCode {

    private const val WINDOW_MS = 10 * 60 * 1_000L

    /** 32 chars, confusables I/O/0/1 excluded — exactly one symbol per 5 bits. */
    private const val CODE_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
    private const val CODE_LENGTH = 8

    /** Current window index — also used by the UI for the rotation countdown. */
    fun windowIndex(nowMs: Long = System.currentTimeMillis()): Long = nowMs / WINDOW_MS

    /** Millis until the displayed code rotates. */
    fun msUntilRotation(nowMs: Long = System.currentTimeMillis()): Long =
        WINDOW_MS - (nowMs % WINDOW_MS)

    /** The code the owner screen shows for [windowIndex]: `XXXX-XXXX`. */
    fun displayCode(masterSecret: ByteArray, windowIndex: Long, nowMs: Long = System.currentTimeMillis()): String {
        val raw = hmac(masterSecret, "code|$windowIndex")
        val sb = StringBuilder()
        var value = 0L
        var bits = 0
        for (byte in raw) {
            value = (value shl 8) or (byte.toLong() and 0xFF)
            bits += 8
            while (bits >= 5 && sb.length < CODE_LENGTH) {
                val index = ((value shr (bits - 5)) and 0x1F).toInt()
                sb.append(CODE_ALPHABET[index])
                bits -= 5
            }
            if (sb.length >= CODE_LENGTH) break
        }
        val chars = sb.toString()
        return "${chars.substring(0, 4)}-${chars.substring(4)}"
    }

    /**
     * The proof a guest sends: HMAC(code, "$deviceName|$nonce"), base64. The code
     * itself never crosses the network.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun proofOf(code: String, deviceName: String?, nonce: String): String {
        val normalized = normalize(code)
        return Base64.encode(hmac(normalized.toByteArray(), "${deviceName.orEmpty()}|$nonce"))
    }

    /** Uppercase, separators stripped (the alphabet itself excludes I/O/0/1). */
    fun normalize(code: String): String = code.uppercase()
        .replace("-", "")
        .replace(" ", "")

    fun tokenHash(rawToken: String): String =
        MessageDigest.getInstance("SHA-256").digest(rawToken.toByteArray())
            .joinToString("") { "%02x".format(it) }

    private fun hmac(key: ByteArray, data: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data.toByteArray())
    }
}
