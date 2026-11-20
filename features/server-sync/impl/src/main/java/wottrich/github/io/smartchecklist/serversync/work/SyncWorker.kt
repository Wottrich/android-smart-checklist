package wottrich.github.io.smartchecklist.serversync.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.context.GlobalContext
import wottrich.github.io.smartchecklist.serversync.api.SyncParticipant
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.serversync.domain.usecase.SyncNowUseCase

/**
 * Runs the sync engine on WorkManager's schedule. Failures are never user-facing:
 * unreachable → [Result.retry] (backoff), everything else → success (the error kind
 * is persisted for the settings status row). Not-configured exits successfully —
 * scheduling is always safe. Registered [SyncParticipant]s (per-host share sessions)
 * run after the primary sync; their failures are swallowed into state too.
 */
class SyncWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.get()
        val outcome = koin.get<SyncNowUseCase>()()
        // per-host share sessions (Wi-Fi sharing) piggyback on the same schedule
        koin.getAll<SyncParticipant>().forEach { participant ->
            runCatching { participant.onSyncRequested() }
        }
        return when (val error = outcome.exceptionOrNull()) {
            null -> Result.success()
            is SyncError.ServerUnreachable -> Result.retry()
            else -> Result.success()
        }
    }
}
