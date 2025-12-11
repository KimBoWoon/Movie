package com.bowoon.sync.status

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.bowoon.data.util.SyncManager
import com.bowoon.sync.workers.MainMenuSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class WorkManagerSyncManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) : SyncManager {
    override fun syncMain() {
//        WorkManager.getInstance(context = appContext)
//            .enqueueUniquePeriodicWork(
//                uniqueWorkName = "TEST_PERIODIC_WORK",
//                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
//                request = MainMenuSyncWorker.test(isForce = true)
//            )
//        WorkManager.getInstance(context = appContext)
//            .cancelUniqueWork(uniqueWorkName = "TEST_PERIODIC_WORK")
        WorkManager.getInstance(context = appContext)
            .enqueueUniqueWork(
                uniqueWorkName = MainMenuSyncWorker.WORKER_NAME,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = MainMenuSyncWorker.startUpSyncWork(isForce = false)
            )
    }

    override fun requestSync() {
        WorkManager.getInstance(context = appContext)
            .beginUniqueWork(
                uniqueWorkName = MainMenuSyncWorker.WORKER_NAME,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = MainMenuSyncWorker.startUpExpeditedSyncWork(isForce = true)
            ).enqueue()
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