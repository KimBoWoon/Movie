package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import com.bowoon.common.Log

class WorkManagerInitializer : Initializer<WorkManager>, Configuration.Provider {
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()

    override fun create(context: Context): WorkManager {
        WorkManager.initialize(context, workManagerConfiguration)
        Log.d("WorkManagerInitializer end")
        return WorkManager.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}