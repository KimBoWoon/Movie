package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log
import java.io.File

class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        File(context.filesDir, "datastore").deleteRecursively()
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