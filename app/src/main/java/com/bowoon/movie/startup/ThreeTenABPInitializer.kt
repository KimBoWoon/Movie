package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log
import com.jakewharton.threetenabp.AndroidThreeTen

class ThreeTenABPInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AndroidThreeTen.init(context)
        Log.d("ThreeTenABPInitializer end")
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        emptyList()
}