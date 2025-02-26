package com.bowoon.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.bowoon.model.Movie
import com.bowoon.movie.core.notifications.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MOVIE_NOTIFICATION_CHANNEL_ID = ""
private const val MOVIE_NOTIFICATION_GROUP = "MOVIE_NOTIFICATIONS"
private const val MOVIE_NOTIFICATION_REQUEST_CODE = 0
private const val TARGET_ACTIVITY_NAME = "com.bowoon.movie.ui.activities.MainActivity"
private const val DEEP_LINK_SCHEME_AND_HOST = "movieinfo://movie"
private const val DEEP_LINK_MOVIE_ID_PATH = "detail"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_MOVIE_ID_PATH"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{id}"

@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {
    override fun postMovieNotifications(movies: List<Movie>) {
        with(context) {
            if (checkSelfPermission(this, permission.POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
                return
            }

            val movieNotifications = movies.map { movie ->
                createMovieNotification {
                    setSmallIcon(R.drawable.ic_launcher_round)
                        .setContentTitle("곧 영화가 개봉합니다!")
                        .setContentText(movie.title)
                        .setContentIntent(moviePendingIntent(movie))
                        .setGroup(MOVIE_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }

            val notificationManager = NotificationManagerCompat.from(this)
            movieNotifications.forEachIndexed { index, notification ->
                notificationManager.notify(
                    movies[index].id.hashCode(),
                    notification,
                )
            }
        }
    }
}

fun Context.createMovieNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        MOVIE_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

fun Context.ensureNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        MOVIE_NOTIFICATION_CHANNEL_ID,
        getString(R.string.system_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = "곧 개봉하는 영화가 있습니다."
    }

    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

fun Context.moviePendingIntent(
    movie: Movie,
): PendingIntent? = PendingIntent.getActivity(
    this,
    MOVIE_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = movie.movieDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)

private fun Movie.movieDeepLinkUri() = "$DEEP_LINK_BASE_PATH/$id".toUri()