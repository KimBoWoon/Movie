package com.cheeke.surfy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.di.ApplicationScope
import com.cheeke.surfy.userdata.api.UserDataRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurfyFCMHelper @Inject constructor(
    @param:ApplicationScope private val scope: CoroutineScope,
    private val userDataRepository: UserDataRepository
) {
    companion object {
        private const val TAG = "FirebaseCloudMessage"
    }

    fun createFCMChannel(context: Context) {
        Log.d("create fcm notification channel")

        val channel = NotificationChannel(
            context.getString(R.string.release_movie_notification_channel_name),
            context.getString(R.string.release_movie_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.coming_soon_media)
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
                val savedToken = userDataRepository.getFCMToken()
                Log.d(TAG, "new token > $token")
                Log.d(TAG, "saved token > $savedToken")

                if (savedToken != token) {
                    userDataRepository.updateFCMToken(token)
                    // TODO 서버 저장 필요!
                }
            }
        })
    }
}