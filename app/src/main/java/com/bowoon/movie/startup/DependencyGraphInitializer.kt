package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log

class DependencyGraphInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context)

        Log.d("DependencyGraphInitializer end")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}