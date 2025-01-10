package com.bowoon.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.bowoon.sync.workers.SyncWorker
import com.bowoon.sync.workers.SyncWorker.Companion.WORKER_NAME

object Sync {
    fun initialize(context: Context) {
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork()
            )
    }
}