package com.cheeke.surfy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.di.ApplicationScope
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.firebase.FIREBASE_LOG_MESSAGE
import com.cheeke.surfy.firebase.LogHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurfyFirebase @Inject constructor(
    @param:ApplicationScope private val scope: CoroutineScope,
    private val userdataRepository: UserDataRepository
) : LogHelper {
    companion object {
        private const val TAG = "FirebaseCloudMessage"
    }

    override fun sendLog(name: String?, message: String) {
        Firebase.crashlytics.log(
            FIREBASE_LOG_MESSAGE.replace(
                "{name}", if (name.isNullOrEmpty()) "" else "$name -> "
            ).replace("{message}", message)
        )
    }

    fun createFCMChannel(context: Context) {
        Log.d("create fcm notification channel")

        val channel = NotificationChannel(
            context.getString(R.string.release_movie_notification_channel_name),
            context.getString(R.string.release_movie_notification_channel_name),
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