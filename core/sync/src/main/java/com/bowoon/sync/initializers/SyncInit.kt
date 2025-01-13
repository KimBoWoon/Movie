package com.bowoon.sync.initializers

import android.content.Context
import androidx.work.WorkManager
import com.bowoon.sync.workers.MyDataSyncWorker
import com.bowoon.sync.workers.MainMenuSyncWorker

object Sync {
    fun initialize(context: Context) {
        WorkManager.getInstance(context)
            .beginWith(MyDataSyncWorker.startUpSyncWork())
            .then(MainMenuSyncWorker.startUpSyncWork(false))
            .enqueue()
//            .enqueueUniqueWork(
//                UNIQUE_SYNC_WORKER,
//                ExistingWorkPolicy.KEEP,
//                SyncWorker.startUpSyncWork()
//            )
    }
}