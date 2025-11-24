package com.bowoon.movie

import android.app.Application
import androidx.startup.AppInitializer
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.bowoon.common.Log
import com.bowoon.movie.startup.ImageLoaderInitializer
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApplication : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")
        Firebase.crashlytics.log("Movie Application start!")
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        AppInitializer.getInstance(this)
            .initializeComponent(ImageLoaderInitializer::class.java)
}