package com.bowoon.surfy.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log

class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Log.d("AppInitializer end")
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        listOf(
            WorkManagerInitializer::class.java,
            ImageLoaderInitializer::class.java,
            SyncInitializer::class.java,
            FirebaseInitializer::class.java
        )
}