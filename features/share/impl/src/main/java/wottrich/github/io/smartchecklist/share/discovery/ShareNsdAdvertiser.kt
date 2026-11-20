package wottrich.github.io.smartchecklist.share.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

/**
 * Advertises the share host over NSD (`_smartchecklist._tcp`) so guests on the same
 * Wi-Fi can discover it without typing anything. Needs **no runtime permission**.
 * TXT records carry the host device id (the outbox routing identity); the port is
 * the NSD service port itself. APs that block multicast fall back to the manual
 * `ip:port` entry — the advertiser is best-effort, never a hard dependency.
 */
class ShareNsdAdvertiser(context: Context) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    private var registrationListener: NsdManager.RegistrationListener? = null

    fun advertise(serviceName: String, deviceId: String, port: Int, onResult: (Boolean) -> Unit = {}) {
        stop()
        val info = NsdServiceInfo().apply {
            setServiceName(serviceName)
            setServiceType(SERVICE_TYPE)
            setPort(port)
            setAttribute(TXT_DEVICE_ID, deviceId)
        }
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) = onResult(true)
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) = onResult(false)
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) = Unit
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) = Unit
        }
        registrationListener = listener
        nsdManager.registerService(info, NsdManager.PROTOCOL_DNS_SD, listener)
    }

    fun stop() {
        registrationListener?.let { listener ->
            runCatching { nsdManager.unregisterService(listener) }
            registrationListener = null
        }
    }

    private companion object {
        const val SERVICE_TYPE = "_smartchecklist._tcp."
        const val TXT_DEVICE_ID = "deviceId"
    }
}
