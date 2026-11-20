package wottrich.github.io.smartchecklist.share.host

import android.content.Context
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

/**
 * Device identity + host runtime state (`share_host_prefs`). The device id is the
 * outbox routing key for owner-side entries and the NSD service identity; the
 * owner token is this device's own guest credential on its own host (loopback).
 *
 * v2 hardening: the **master pairing secret** — the root of the pairing-code
 * scheme — lives here (host device only); codes are windowed derivations of it
 * (see [PairingCode]). Guest devices store their per-host device token in the
 * same prefs (`guest_token_<address>`).
 */
interface ShareHostPrefs {

    fun getDeviceId(): String

    fun getDeviceName(): String

    fun getOwnerToken(): String?

    /** Null clears (used by "Reset pairing" — the owner re-pairs on next share). */
    fun setOwnerToken(token: String?)

    fun getPort(): Int?

    fun setPort(port: Int?)

    /** Manually added `ip:port` host addresses (NSD fallback). */
    fun getManualHosts(): List<String>

    fun addManualHost(address: String)

    /** 32-byte master pairing secret — created on first access, rotated on reset. */
    fun getMasterPairingSecret(): ByteArray

    fun setMasterPairingSecret(secret: ByteArray)

    /** Guest-side: this device's paired token on a given host address. */
    fun getGuestToken(address: String): String?

    fun setGuestToken(address: String, token: String)

    /**
     * Guest-side: the host's master pairing secret, unwrapped with the typed code
     * at pair time — root of the share push/pull payload keys (Phase S2).
     */
    fun getGuestPairingSecret(address: String): ByteArray?

    fun setGuestPairingSecret(address: String, secret: ByteArray)
}

class ShareHostPrefsImpl(context: Context) : ShareHostPrefs {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getDeviceId(): String =
        preferences.getString(KEY_DEVICE_ID, null) ?: UuidGenerator.getRandomUuid().also {
            preferences.edit().putString(KEY_DEVICE_ID, it).apply()
        }

    override fun getDeviceName(): String = android.os.Build.MODEL

    override fun getOwnerToken(): String? = preferences.getString(KEY_OWNER_TOKEN, null)

    override fun setOwnerToken(token: String?) {
        preferences.edit().apply {
            if (token == null) remove(KEY_OWNER_TOKEN) else putString(KEY_OWNER_TOKEN, token)
        }.apply()
    }

    override fun getPort(): Int? =
        if (preferences.contains(KEY_PORT)) preferences.getInt(KEY_PORT, 0) else null

    override fun setPort(port: Int?) {
        preferences.edit().apply {
            if (port == null) remove(KEY_PORT) else putInt(KEY_PORT, port)
        }.apply()
    }

    override fun getManualHosts(): List<String> =
        preferences.getStringSet(KEY_MANUAL_HOSTS, emptySet()).orEmpty().sorted()

    override fun addManualHost(address: String) {
        val normalized = address.trim().removePrefix("http://").trimEnd('/')
        if (normalized.isEmpty()) return
        preferences.edit()
            .putStringSet(KEY_MANUAL_HOSTS, getManualHosts().toSet() + normalized)
            .apply()
    }

    override fun getMasterPairingSecret(): ByteArray {
        val stored = preferences.getString(KEY_MASTER_SECRET, null) ?: return newMasterSecret()
        return runCatching { android.util.Base64.decode(stored, android.util.Base64.NO_WRAP) }
            .getOrNull() ?: newMasterSecret()
    }

    override fun setMasterPairingSecret(secret: ByteArray) {
        preferences.edit()
            .putString(KEY_MASTER_SECRET, android.util.Base64.encodeToString(secret, android.util.Base64.NO_WRAP))
            .apply()
    }

    private fun newMasterSecret(): ByteArray =
        ByteArray(MASTER_SECRET_BYTES).also { bytes ->
            java.security.SecureRandom().nextBytes(bytes)
        }.also { secret -> setMasterPairingSecret(secret) }

    override fun getGuestToken(address: String): String? =
        preferences.getString("$KEY_GUEST_TOKEN_PREFIX${address.trim()}", null)

    override fun setGuestToken(address: String, token: String) {
        preferences.edit().putString("$KEY_GUEST_TOKEN_PREFIX${address.trim()}", token).apply()
    }

    override fun getGuestPairingSecret(address: String): ByteArray? =
        preferences.getString("$KEY_GUEST_SECRET_PREFIX${address.trim()}", null)
            ?.let { stored -> runCatching { android.util.Base64.decode(stored, android.util.Base64.NO_WRAP) }.getOrNull() }

    override fun setGuestPairingSecret(address: String, secret: ByteArray) {
        preferences.edit()
            .putString(
                "$KEY_GUEST_SECRET_PREFIX${address.trim()}",
                android.util.Base64.encodeToString(secret, android.util.Base64.NO_WRAP),
            )
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "share_host_prefs"
        const val KEY_DEVICE_ID = "deviceId"
        const val KEY_OWNER_TOKEN = "ownerToken"
        const val KEY_PORT = "port"
        const val KEY_MANUAL_HOSTS = "manualHosts"
        const val KEY_MASTER_SECRET = "masterPairingSecret"
        const val KEY_GUEST_TOKEN_PREFIX = "guest_token_"
        const val KEY_GUEST_SECRET_PREFIX = "guest_secret_"
        const val MASTER_SECRET_BYTES = 32
    }
}
