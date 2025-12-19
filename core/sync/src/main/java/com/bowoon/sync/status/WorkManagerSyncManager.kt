package com.bowoon.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.bowoon.common.Log
import com.bowoon.data.util.SyncManager
import com.bowoon.sync.workers.MainMenuSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.filterNotNull
import java.util.UUID
import javax.inject.Inject

internal class WorkManagerSyncManager @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) : SyncManager {
    override fun syncMain() {
//        WorkManager.getInstance(context = appContext).cancelAllWork()
        WorkManager.getInstance(context = appContext)
            .enqueueUniqueWork(
                uniqueWorkName = MainMenuSyncWorker.WORKER_NAME,
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = MainMenuSyncWorker.startUpSyncWork()
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

    suspend fun checkWorkState(id: UUID) {
        WorkManager.getInstance(context = appContext)
            .getWorkInfoByIdFlow(id = id)
            .filterNotNull()
            .collect { workInfo ->
                Log.d(workInfo.state.toString())

                if (workInfo.state == State.FAILED) {
                    when (workInfo.stopReason) {
                        WorkInfo.STOP_REASON_USER -> Log.e("STOP_REASON_USER")
                        WorkInfo.STOP_REASON_QUOTA -> Log.e("STOP_REASON_QUOTA")
                        WorkInfo.STOP_REASON_PREEMPT -> Log.e("STOP_REASON_PREEMPT")
                        WorkInfo.STOP_REASON_TIMEOUT -> Log.e("STOP_REASON_TIMEOUT")
                        WorkInfo.STOP_REASON_UNKNOWN -> Log.e("STOP_REASON_UNKNOWN")
                        WorkInfo.STOP_REASON_APP_STANDBY -> Log.e("STOP_REASON_APP_STANDBY")
                        WorkInfo.STOP_REASON_BACKGROUND_RESTRICTION -> Log.e("STOP_REASON_BACKGROUND_RESTRICTION")
                        WorkInfo.STOP_REASON_CANCELLED_BY_APP -> Log.e("STOP_REASON_CANCELLED_BY_APP")
                        WorkInfo.STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW -> Log.e("STOP_REASON_CONSTRAINT_BATTERY_NOT_LOW")
                        WorkInfo.STOP_REASON_SYSTEM_PROCESSING -> Log.e("STOP_REASON_SYSTEM_PROCESSING")
                        WorkInfo.STOP_REASON_NOT_STOPPED -> Log.e("STOP_REASON_NOT_STOPPED")
                        WorkInfo.STOP_REASON_FOREGROUND_SERVICE_TIMEOUT -> Log.e("STOP_REASON_FOREGROUND_SERVICE_TIMEOUT")
                        WorkInfo.STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED -> Log.e("STOP_REASON_ESTIMATED_APP_LAUNCH_TIME_CHANGED")
                        WorkInfo.STOP_REASON_DEVICE_STATE -> Log.e("STOP_REASON_DEVICE_STATE")
                        WorkInfo.STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW -> Log.e("STOP_REASON_CONSTRAINT_STORAGE_NOT_LOW")
                        WorkInfo.STOP_REASON_CONSTRAINT_DEVICE_IDLE -> Log.e("STOP_REASON_CONSTRAINT_DEVICE_IDLE")
                        WorkInfo.STOP_REASON_CONSTRAINT_CONNECTIVITY -> Log.e("STOP_REASON_CONSTRAINT_CONNECTIVITY")
                        WorkInfo.STOP_REASON_CONSTRAINT_CHARGING -> Log.e("STOP_REASON_CONSTRAINT_CHARGING")
                    }
                }
            }
    }
}

private fun List<WorkInfo>.anyRunning() = any { it.state == State.RUNNING }