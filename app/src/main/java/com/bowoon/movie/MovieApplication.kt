package com.bowoon.movie

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.bowoon.common.Log
import com.bowoon.sync.initializers.Sync
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")
        AndroidThreeTen.init(this)
        Sync.initialize(context = this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()
}