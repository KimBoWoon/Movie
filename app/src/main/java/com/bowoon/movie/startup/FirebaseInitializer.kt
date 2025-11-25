package com.bowoon.movie.startup

import android.content.Context
import androidx.startup.Initializer
import com.bowoon.common.Log
import com.bowoon.movie.BuildConfig
import com.bowoon.movie.MovieFirebase
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirebaseInitializer : Initializer<Unit> {
    @Inject
    lateinit var firebase: MovieFirebase

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(firebaseInitializer = this)
        Firebase.crashlytics.setCustomKey("git_hash_code", BuildConfig.GIT_HASH)
        Firebase.crashlytics.setCustomKey("versionCode", BuildConfig.VERSION_CODE)
        Firebase.crashlytics.setCustomKey("versionName", BuildConfig.VERSION_NAME)
        Firebase.crashlytics.setCustomKey("isDebug", BuildConfig.DEBUG)
        Firebase.crashlytics.setCustomKey("appFlavor", BuildConfig.FLAVOR)
        firebase.createFCMChannel(context = context)
        firebase.checkToken()

        firebase.sendLog(javaClass.simpleName, "firebase app initializer end")

        Log.d("FirebaseInitializer end")
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> =
        emptyList()
}