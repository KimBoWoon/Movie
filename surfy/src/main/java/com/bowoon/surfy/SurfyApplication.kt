package com.bowoon.surfy

import android.app.Application
import com.bowoon.common.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SurfyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("Application", "onCreate()")
        Firebase.crashlytics.log("Surfy Application start!")
    }
}