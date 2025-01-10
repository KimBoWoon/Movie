package com.bowoon.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.bowoon.data.util.SyncManager
import com.bowoon.sync.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkFlow(SyncWorker.WORKER_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    override fun requestSync() {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                SyncWorker.WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(true)
            )
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == State.RUNNING }