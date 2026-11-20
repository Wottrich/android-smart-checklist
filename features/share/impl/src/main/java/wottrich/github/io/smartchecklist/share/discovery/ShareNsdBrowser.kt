package wottrich.github.io.smartchecklist.share.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import wottrich.github.io.smartchecklist.share.api.NearbyHost
import java.util.Collections
import kotlin.coroutines.resume

/**
 * One NSD discovery round (`_smartchecklist._tcp`). No runtime permission needed;
 * some APs block multicast (client isolation) — the manual `ip:port` fallback
 * covers those. A round ends at the first successful resolution (single-host
 * LANs are the target) or at [timeoutMs], returning whatever resolved in time —
 * empty is a normal result, not an error.
 */
class ShareNsdBrowser(context: Context) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    suspend fun scan(timeoutMs: Long = DEFAULT_TIMEOUT_MS): List<NearbyHost> {
        return suspendCancellableCoroutine { continuation ->
            val resolved = Collections.synchronizedMap(LinkedHashMap<String, NearbyHost>())
            var discoveryListener: NsdManager.DiscoveryListener? = null
            var finished = false

            fun finish() {
                if (finished) return
                finished = true
                discoveryListener?.let { listener ->
                    runCatching { nsdManager.stopServiceDiscovery(listener) }
                }
                if (continuation.isActive) {
                    continuation.resume(resolved.values.toList())
                }
            }

            val resolveListener = object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) = Unit
                override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                    val host = serviceInfo?.host
                    val deviceId = serviceInfo?.attributes?.get(TXT_DEVICE_ID)
                        ?.let { bytes -> String(bytes, Charsets.UTF_8) }
                        ?: serviceInfo?.serviceName
                        ?: return finish()
                    if (host != null && serviceInfo.port > 0) {
                        resolved[deviceId] = NearbyHost(
                            deviceId = deviceId,
                            address = "${host.hostAddress}:${serviceInfo.port}",
                        )
                    }
                    finish()
                }
            }

            discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onDiscoveryStarted(regType: String?) = Unit
                override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                    finished = true
                    if (continuation.isActive) continuation.resume(emptyList())
                }
                override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                    if (serviceInfo != null && !finished) {
                        runCatching { nsdManager.resolveService(serviceInfo, resolveListener) }
                    }
                }
                override fun onServiceLost(serviceInfo: NsdServiceInfo?) = Unit
                override fun onDiscoveryStopped(serviceType: String?) = finish()
                override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) = finish()
            }

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                { finish() },
                timeoutMs,
            )
            runCatching {
                nsdManager.discoverServices(
                    SERVICE_TYPE,
                    NsdManager.PROTOCOL_DNS_SD,
                    discoveryListener,
                )
            }.onFailure {
                finished = true
                if (continuation.isActive) continuation.resume(emptyList())
            }
        }
    }

    private companion object {
        const val SERVICE_TYPE = "_smartchecklist._tcp."
        const val TXT_DEVICE_ID = "deviceId"
        const val DEFAULT_TIMEOUT_MS = 5_000L
    }
}
