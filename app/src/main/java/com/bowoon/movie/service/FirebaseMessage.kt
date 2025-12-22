package com.bowoon.movie.service

import android.Manifest.permission
import android.app.Notification
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.bowoon.common.Log
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Movie
import com.bowoon.movie.core.notifications.R
import com.bowoon.notifications.createMovieNotification
import com.bowoon.notifications.moviePendingIntent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieFCMService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MovieFCMService"
        private const val MOVIE_RELEASE_NOTIFICATION = "MOVIE_RELEASE_NOTIFICATION"
        private const val MOVIE_NOTIFICATION_RELEASE_GROUP = "MOVIE_NOTIFICATION_RELEASE_GROUP"
    }

    @Inject
    lateinit var userdataRepository: UserDataRepository
    @Inject
    @ApplicationScope
    lateinit var scope: CoroutineScope

    override fun onNewToken(token: String) {
        super.onNewToken(token)

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
                applicationContext.imageLoader.enqueue(
                    request = ImageRequest.Builder(applicationContext)
                        .data(data = imageUri)
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

                                notify(movieNotification)
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

                notify(movieNotification)
            }
        }
    }

    private fun notify(movieNotification: Notification) {
        if (ActivityCompat.checkSelfPermission(this, permission.POST_NOTIFICATIONS) == PERMISSION_GRANTED) {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(
                movieNotification.hashCode(),
                movieNotification
            )
        }
    }
}