package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log
import com.jakewharton.threetenabp.AndroidThreeTen

class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context)

        AndroidThreeTen.init(context)

        Log.d("AppInitializer end")
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        listOf(FirebaseInitializer::class.java)
}