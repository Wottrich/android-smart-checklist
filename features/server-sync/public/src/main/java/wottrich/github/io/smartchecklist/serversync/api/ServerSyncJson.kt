package wottrich.github.io.smartchecklist.serversync.api

import kotlinx.serialization.json.Json

/**
 * The one JSON config for the sync + share wire protocol (PC backend, device
 * hosts and the loopback client). `ignoreUnknownKeys` keeps older apps
 * forward-compatible when a server adds fields; `encodeDefaults` keeps payloads
 * explicit (matching the contract examples, e.g. `parentUuid: null`).
 */
val ServerSyncJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
