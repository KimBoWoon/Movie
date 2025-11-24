package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log
import com.bowoon.data.util.SyncManager
import javax.inject.Inject

class SyncInitializer : Initializer<Unit> {
    @Inject
    lateinit var syncManager: SyncManager

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(syncInitializer = this)
        syncManager.syncMain()
        Log.d("SyncInitializer end")
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        listOf(ThreeTenABPInitializer::class.java)
}