package com.cheeke.surfy.startup

import android.content.Context
import androidx.startup.Initializer
import com.cheeke.surfy.BuildConfig
import com.cheeke.surfy.SurfyFirebase
import com.cheeke.surfy.common.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import javax.inject.Inject

class FirebaseInitializer : Initializer<Unit> {
    @Inject
    lateinit var firebase: SurfyFirebase

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