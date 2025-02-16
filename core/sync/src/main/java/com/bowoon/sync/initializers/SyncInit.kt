package com.bowoon.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.bowoon.sync.workers.MainMenuSyncWorker
import com.bowoon.sync.workers.MyDataSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Sync @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    fun initialize() {
        WorkManager.getInstance(appContext)
            .beginUniqueWork(
                UNIQUE_SYNC_WORKER,
                ExistingWorkPolicy.KEEP,
                MyDataSyncWorker.startUpSyncWork()
            )
            .then(MainMenuSyncWorker.startUpSyncWork(false))
            .enqueue()
    }
}