package wottrich.github.io.smartchecklist.share.host

import wottrich.github.io.smartchecklist.share.discovery.ShareNsdAdvertiser
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Hosting lifecycle (foreground-only v1): the embedded server runs while ≥1 share
 * is active and the app process is alive; stopped at 0 active shares. Port binding
 * walks the 8090→8100 range (step 2); the actual port is advertised in NSD and kept
 * in [ShareHostPrefs] so the owner's own client follows the host.
 *
 * v2 hardening: the server binds to the **Wi-Fi interface address** (site-local
 * IPv4), not `0.0.0.0` — a phone can sit on several networks (hotspot, VPN, work
 * Wi-Fi) and the share should only be reachable on the one the user shares over.
 * Without a site-local address (rare), it falls back to loopback (no guests).
 */
class ShareHostManager(
    private val server: EmbeddedShareServer,
    private val prefs: ShareHostPrefs,
    private val advertiser: ShareNsdAdvertiser,
    private val service: ShareHostService,
) {

    private var activePort: Int? = null
    private var boundAddress: String? = null

    val isHosting: Boolean get() = server.isRunning

    /** `address:port` clients dial (Wi-Fi interface preferred), or null when down. */
    fun hostAddress(): String? {
        val port = activePort ?: prefs.getPort() ?: return null
        return "${boundAddress ?: LOOPBACK}:$port"
    }

    suspend fun ensureStarted(): Int? {
        if (server.isRunning) return activePort
        val host = wifiAddress() ?: LOOPBACK
        var port = PORT_DEFAULT
        while (port <= PORT_MAX) {
            if (server.isPortAvailable(port)) {
                val bound = server.start(port, host)
                activePort = bound
                boundAddress = host
                prefs.setPort(bound)
                advertiser.advertise(
                    serviceName = SERVICE_NAME,
                    deviceId = prefs.getDeviceId(),
                    port = bound,
                )
                // v2 hardening: drop revoked/expired guests older than the retention window
                purgeIgnored()
                return bound
            }
            // port taken — walk the range
            port += PORT_STEP
        }
        return null
    }

    fun ensureStopped() {
        advertiser.stop()
        server.stop()
        activePort = null
        boundAddress = null
        prefs.setPort(null)
    }

    private suspend fun purgeIgnored() {
        // best-effort, non-fatal: hosting must not fail because of housekeeping
        runCatching { service.purgeStaleGuests() }
    }

    /** First site-local IPv4 on an up interface (excluding loopback). */
    private fun wifiAddress(): String? =
        NetworkInterface.getNetworkInterfaces().asSequence()
            .filter { it.isUp && !it.isLoopback }
            .flatMap { it.inetAddresses.asSequence() }
            .firstOrNull { it is Inet4Address && it.isSiteLocalAddress }
            ?.hostAddress

    private companion object {
        const val PORT_DEFAULT = 8090
        const val PORT_MAX = 8100
        const val PORT_STEP = 2
        const val LOOPBACK = "127.0.0.1"

        /** v2 hardening: no device model in the service name (discovery leak). */
        const val SERVICE_NAME = "Smart Checklist"
    }
}
