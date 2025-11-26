package com.bowoon.movie

import android.app.Application
import com.bowoon.common.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")
        Firebase.crashlytics.log("Movie Application start!")
    }
}