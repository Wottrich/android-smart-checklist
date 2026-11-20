package wottrich.github.io.smartchecklist.share.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.serversync.api.HostSyncEngine
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.SyncParticipant
import wottrich.github.io.smartchecklist.serversync.data.repository.HostSyncSession
import wottrich.github.io.smartchecklist.share.api.ShareRepository
import wottrich.github.io.smartchecklist.share.data.ShareEnvelopeServerApi
import wottrich.github.io.smartchecklist.share.data.ShareHostClient
import wottrich.github.io.smartchecklist.share.data.ShareRepositoryImpl
import wottrich.github.io.smartchecklist.share.discovery.ShareNsdAdvertiser
import wottrich.github.io.smartchecklist.share.discovery.ShareNsdBrowser
import wottrich.github.io.smartchecklist.share.host.EmbeddedShareServer
import wottrich.github.io.smartchecklist.share.host.ShareHostIngestService
import wottrich.github.io.smartchecklist.share.host.ShareHostManager
import wottrich.github.io.smartchecklist.share.host.ShareHostPrefs
import wottrich.github.io.smartchecklist.share.host.ShareHostPrefsImpl
import wottrich.github.io.smartchecklist.share.host.ShareHostService
import wottrich.github.io.smartchecklist.share.host.db.ShareDao
import wottrich.github.io.smartchecklist.share.host.db.ShareHostDatabase
import wottrich.github.io.smartchecklist.share.navigation.ShareContextNavigator
import wottrich.github.io.smartchecklist.share.presentation.viewmodel.NearbyViewModel
import wottrich.github.io.smartchecklist.share.presentation.viewmodel.ShareViewModel
import wottrich.github.io.smartchecklist.share.sync.ShareSyncDriver

/**
 * Wi-Fi share feature (device-hosted checklist sharing). Host server, ingest,
 * loopback routing and repository (Phases A–C); owner/guest UI lands in D/E —
 * see docs/plans/wifi-checklist-sharing.md.
 */
val shareModule: Module = module {

    single<ShareHostPrefs> { ShareHostPrefsImpl(androidContext()) }
    single<ShareDao> { ShareHostDatabase.getInstance(androidContext()).shareDao() }
    single {
        ShareHostIngestService(
            database = ShareHostDatabase.getInstance(androidContext()),
            dao = get(),
        )
    }
    single {
        ShareHostService(
            dao = get(),
            ingestService = get(),
            prefs = get(),
        )
    }
    single { EmbeddedShareServer(service = get(), prefs = get()) }
    // v2 hardening: share pull/push travels as encrypted envelopes — the share
    // feature binds a qualified ServerApi/engine pair, the PC backend keeps its
    // unqualified v1 pair untouched
    single<ServerApi>(named("shareHost")) { ShareEnvelopeServerApi(client = get(), prefs = get()) }
    single<HostSyncEngine>(named("shareHost")) { HostSyncSession(get(), get(), get(named("shareHost"))) }
    single { ShareNsdAdvertiser(androidContext()) }
    single {
        ShareHostManager(
            server = get(),
            prefs = get(),
            advertiser = get(),
            service = get(),
        )
    }
    single {
        ShareSyncDriver(
            dispatchersProviders = get(),
            metaDatasource = get(),
            hostSyncEngine = get(named("shareHost")),
            hostManager = get(),
            prefs = get(),
        )
    } bind SyncParticipant::class
    single<ShareRepository> {
        ShareRepositoryImpl(
            dispatchersProviders = get(),
            hostService = get(),
            hostManager = get(),
            prefs = get(),
            metaDatasource = get(),
            checklistDatasource = get(),
            hostClient = get(),
            serverApi = get(named("shareHost")),
            hostSyncEngine = get(named("shareHost")),
            nsdBrowser = get(),
        )
    }
    single { ShareContextNavigator() } bind SmartChecklistNavigation::class
    single { ShareNsdBrowser(androidContext()) }
    single { ShareHostClient(client = get()) }
    viewModelOf(::ShareViewModel)
    viewModelOf(::NearbyViewModel)
}
