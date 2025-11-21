package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log
import com.bowoon.data.util.SyncManager
import javax.inject.Inject

class SyncInitializer : Initializer<SyncManager> {
    @Inject
    lateinit var syncManager: SyncManager

    override fun create(context: Context): SyncManager {
        InitializerEntryPoint.resolve(context).inject(syncInitializer = this)
        Log.d("SyncInitializer end")
        return syncManager
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        listOf(DependencyGraphInitializer::class.java)
}