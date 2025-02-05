package com.bowoon.movie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationManagerCompat
import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.repository.UserDataRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieFirebase @Inject constructor(
    private val userdataRepository: UserDataRepository,
    @ApplicationScope private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "FirebaseCloudMessage"
    }

    fun createFCMChannel(context: Context) {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

        Log.d("create fcm notification channel")

        val channel = NotificationChannel(
            "Movie release notification",
            "Movie release notification",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "곧 개봉하는 영화가 있습니다."
        }

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    fun checkToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, task.exception?.message ?: "Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            scope.launch {
                userdataRepository.getFCMToken().let { savedToken ->
                    Log.d(TAG, "new token > $token")
                    Log.d(TAG, "saved token > $savedToken")

                    if (savedToken != token) {
                        userdataRepository.updateFCMToken(token)
                        // TODO 서버 저장 필요!
                    }
                }
            }
        })
    }
}