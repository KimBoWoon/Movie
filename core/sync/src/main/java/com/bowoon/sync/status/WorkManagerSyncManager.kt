package com.bowoon.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.bowoon.data.util.SyncManager
import com.bowoon.sync.workers.MainMenuSyncWorker
import com.bowoon.sync.workers.MyDataSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : SyncManager {
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(appContext)
            .getWorkInfosForUniqueWorkFlow(MainMenuSyncWorker.WORKER_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    override fun syncMain() {
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(
                MainMenuSyncWorker.WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                MainMenuSyncWorker.startUpSyncWork(false)
            )
    }

    override fun myDataSync() {
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork(
                MyDataSyncWorker.WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                MyDataSyncWorker.startUpSyncWork()
            )
    }

    override fun requestSync() {
        WorkManager.getInstance(appContext)
            .beginUniqueWork(
                MyDataSyncWorker.WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                MyDataSyncWorker.startUpSyncWork()
            ).then(MainMenuSyncWorker.startUpSyncWork(true))
            .enqueue()
    }

    override suspend fun checkWork(
        context: Context,
        onSuccess: suspend () -> Unit,
        onFailure: suspend () -> Unit
    ) {
        WorkManager.getInstance(context)
            .getWorkInfosByTagFlow(MyDataSyncWorker.WORKER_NAME)
            .map { works ->
                works.find { it.id == MyDataSyncWorker.workerId }
            }.collect { work ->
                work?.getWorkResult(
                    { onSuccess() },
                    { onFailure() }
                )
            }
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == State.RUNNING }
private suspend fun WorkInfo.getWorkResult(
    onSuccess: suspend () -> Unit,
    onFailure: suspend () -> Unit
) {
    if (state != State.ENQUEUED && state != State.RUNNING) {
        if (state == State.SUCCEEDED) {
            onSuccess()
        } else {
            onFailure()
        }
    }
}