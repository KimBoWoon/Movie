package com.bowoon.movie.service

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.bowoon.common.Log
import com.bowoon.model.Movie
import com.bowoon.movie.core.notifications.R
import com.bowoon.notifications.createMovieNotification
import com.bowoon.notifications.moviePendingIntent
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessage {
    companion object {
        private const val TAG = "FirebaseCloudMessage"
    }

    fun createChannel(context: Context) {
        if (VERSION.SDK_INT < VERSION_CODES.O) return

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

            // Log and toast
            Log.d(TAG, token)
        })
    }
}

class MovieFCMService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MovieFCMService"
        private const val MOVIE_RELEASE_NOTIFICATION = "MOVIE_RELEASE_NOTIFICATION"
        private const val MOVIE_NOTIFICATION_RELEASE_GROUP = "MOVIE_NOTIFICATION_RELEASE_GROUP"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (ActivityCompat.checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            return
        }

        message.notification?.let { notiData ->
            Log.d(TAG, notiData.title ?: "")
            Log.d(TAG, notiData.body ?: "")
            Log.d(TAG, notiData.imageUrl.toString())
            Log.d(TAG, message.data.toString())

            val pendingIntent = message.data["movieId"]?.let { id ->
                moviePendingIntent(Movie(id = id.toInt()))
            }

            notiData.imageUrl?.let { imageUri ->
                ImageLoader(applicationContext).enqueue(
                    ImageRequest.Builder(applicationContext)
                        .data(imageUri)
                        .listener(
                            onSuccess = { request, result ->
                                val movieNotification = createMovieNotification {
                                    setSmallIcon(R.drawable.ic_launcher_round)
                                        .setContentTitle(notiData.title)
                                        .setContentText(notiData.body)
                                        .setLargeIcon(result.image.toBitmap())
                                        .setStyle(
                                            NotificationCompat.BigPictureStyle()
                                                .bigPicture(result.image.toBitmap())
//                                            .bigLargeIcon(null)
                                        )
                                        .setContentIntent(pendingIntent)
                                        .setGroup(MOVIE_NOTIFICATION_RELEASE_GROUP)
                                        .setAutoCancel(true)
                                }

                                val notificationManager = NotificationManagerCompat.from(this)
                                notificationManager.notify(
                                    movieNotification.hashCode(),
                                    movieNotification
                                )
                            }
                        )
                        .build()
                )
            } ?: run {
                val movieNotification = createMovieNotification {
                    setSmallIcon(R.drawable.ic_launcher_round)
                        .setContentTitle(notiData.title)
                        .setContentText(notiData.body)
                        .setContentIntent(pendingIntent)
                        .setGroup(MOVIE_NOTIFICATION_RELEASE_GROUP)
                        .setAutoCancel(true)
                }

                val notificationManager = NotificationManagerCompat.from(this)
                notificationManager.notify(
                    movieNotification.hashCode(),
                    movieNotification
                )
            }
        }
    }
}