package wottrich.github.io.smartchecklist.serversync.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.context.GlobalContext
import wottrich.github.io.smartchecklist.serversync.domain.usecase.SyncNowUseCase
import wottrich.github.io.smartchecklist.serversync.domain.SyncError

/**
 * Runs the sync engine on WorkManager's schedule. Failures are never user-facing:
 * unreachable → [Result.retry] (backoff), everything else → success (the error kind
 * is persisted for the settings status row). Not-configured exits successfully —
 * scheduling is always safe.
 */
class SyncWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val syncNowUseCase = GlobalContext.get().get<SyncNowUseCase>()
        val outcome = syncNowUseCase()
        return when (val error = outcome.exceptionOrNull()) {
            null -> Result.success()
            is SyncError.ServerUnreachable -> Result.retry()
            else -> Result.success()
        }
    }
}
