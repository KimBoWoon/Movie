package com.bowoon.movie

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.bowoon.common.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>
    @Inject
    lateinit var firebase: MovieFirebase

    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")
        firebase.sendLog(javaClass.simpleName, "Movie Application start!")
        AndroidThreeTen.init(this)
        initFirebase()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()

    private fun initFirebase() {
        Firebase.crashlytics.setCustomKey("git_hash_code", BuildConfig.GIT_HASH)
        Firebase.crashlytics.setCustomKey("versionCode", BuildConfig.VERSION_CODE)
        Firebase.crashlytics.setCustomKey("versionName", BuildConfig.VERSION_NAME)
        Firebase.crashlytics.setCustomKey("isDebug", BuildConfig.DEBUG)
        Firebase.crashlytics.setCustomKey("appFlavor", BuildConfig.FLAVOR)
        firebase.createFCMChannel(context = this)
        firebase.checkToken()
    }
}