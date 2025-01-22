package com.bowoon.movie.startup

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.bowoon.common.Log
import com.bowoon.sync.workers.MyDataSyncWorker
import javax.inject.Inject

class DependencyGraphInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        //this will lazily initialize ApplicationComponent before Application's `onCreate`
        InitializerEntryPoint.resolve(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}

class WorkManagerInitializer : Initializer<WorkManager>, Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun create(context: Context): WorkManager {
        InitializerEntryPoint.resolve(context).inject(this)
        WorkManager.initialize(context, workManagerConfiguration)
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(DependencyGraphInitializer::class.java)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

class InitDataInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(this)

        Log.d("InitDataInitializer")

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                MyDataSyncWorker.WORKER_NAME,
                ExistingWorkPolicy.KEEP,
                MyDataSyncWorker.startUpSyncWork()
            )
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(WorkManagerInitializer::class.java)
}