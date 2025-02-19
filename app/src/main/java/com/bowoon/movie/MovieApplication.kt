package com.bowoon.movie

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.bowoon.common.Log
import com.bowoon.sync.initializers.Sync
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieApplication : Application(), SingletonImageLoader.Factory {
    @Inject
    lateinit var imageLoader: ImageLoader
    @Inject
    lateinit var firebase: MovieFirebase
    @Inject
    lateinit var sync: Sync

    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")
        firebase.sendLog(javaClass.simpleName, "Movie Application start!")
        sync.initialize()
        AndroidThreeTen.init(this)
        initFirebase()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader

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