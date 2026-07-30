package com.cheeke.surfy.firebase.impl

import com.cheeke.surfy.firebase.api.FIREBASE_LOG_MESSAGE
import com.cheeke.surfy.firebase.api.LogHelper
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class SurfyFirebaseLogHelper @Inject constructor() : LogHelper {
    companion object {
        private const val TAG = "FirebaseCloudMessage"
    }

    override fun sendLog(name: String?, message: String) {
        Firebase.crashlytics.log(
            FIREBASE_LOG_MESSAGE.replace(
                oldValue = "{name}", newValue = if (name.isNullOrEmpty()) "" else "$name -> "
            ).replace(oldValue = "{message}", newValue = message)
        )
    }
}