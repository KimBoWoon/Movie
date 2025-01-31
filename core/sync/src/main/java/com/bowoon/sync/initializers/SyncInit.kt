package com.bowoon.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.bowoon.sync.workers.MainMenuSyncWorker
import com.bowoon.sync.workers.MyDataSyncWorker

object Sync {
    fun initialize(context: Context) {
        WorkManager.getInstance(context)
            .beginUniqueWork(
                UNIQUE_SYNC_WORKER,
                ExistingWorkPolicy.KEEP,
                MyDataSyncWorker.startUpSyncWork()
            )
            .then(MainMenuSyncWorker.startUpSyncWork(false))
            .enqueue()
    }
}