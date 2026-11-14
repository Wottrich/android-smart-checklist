package wottrich.github.io.smartchecklist.serversync.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import wottrich.github.io.smartchecklist.serversync.api.SyncScheduler
import java.util.concurrent.TimeUnit

/**
 * Periodic sync (15 min, UNMETERED) as the safety net plus a deduplicated expedited
 * one-shot fired after every local mutation. Unique work names keep both from piling up.
 */
class WorkManagerSyncScheduler(
    private val context: Context
) : SyncScheduler {

    override fun schedulePeriodicSync() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<SyncWorker>(PERIODIC_INTERVAL_MINUTES, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                )
                .build()
        )
    }

    override fun scheduleOneShotSync() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_SHOT_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_SECONDS, TimeUnit.SECONDS)
                .build()
        )
    }

    private companion object {
        const val PERIODIC_WORK_NAME = "server_sync_periodic"
        const val ONE_SHOT_WORK_NAME = "server_sync_one_shot"
        const val PERIODIC_INTERVAL_MINUTES = 15L
        const val BACKOFF_SECONDS = 30L
    }
}
