package com.bowoon.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.RoundedCornersTransformation
import com.bowoon.common.Dispatcher
import com.bowoon.common.Dispatchers.IO
import com.bowoon.data.util.ApplicationData
import com.bowoon.model.Movie
import com.bowoon.movie.core.notifications.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

private const val MOVIE_NOTIFICATION_CHANNEL_ID = "MOVIE_NOTIFICATION_CHANNEL"
private const val MOVIE_NOTIFICATION_GROUP = "MOVIE_NOTIFICATIONS"
val SUMMARY_ID = 0
private const val MOVIE_NOTIFICATION_REQUEST_CODE = 0
private const val TARGET_ACTIVITY_NAME = "com.bowoon.movie.ui.activities.MainActivity"
private const val DEEP_LINK_SCHEME_AND_HOST = "movieinfo://movie"
private const val DEEP_LINK_MOVIE_ID_PATH = "detail"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_MOVIE_ID_PATH"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{id}"

@Singleton
class SystemTrayNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(dispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val movieAppData: ApplicationData
) : Notifier {
    override suspend fun postMovieNotifications(movies: List<Movie>) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) return

        val comingSoonMovie = context.getString(R.string.coming_soon_movie)
        val imageUrl = movieAppData.movieAppData.value.getImageUrl()

        val notifications = movies.map { movie ->
            val bigPictureBitmap = loadNotificationImage(context = context, imageUrl = "$imageUrl${movie.posterPath}")
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
                .bigPicture(bigPictureBitmap)

            context.createMovieNotification {
                setSmallIcon(R.drawable.ic_launcher_round)
                    .setLargeIcon(bigPictureBitmap)
                    .setContentTitle(comingSoonMovie)
                    .setContentText(movie.title)
                    .setContentIntent(context.moviePendingIntent(movie = movie))
                    .setStyle(bigPictureStyle)
                    .setGroup(MOVIE_NOTIFICATION_GROUP)
                    .setAutoCancel(true)
            }
        }

        val summaryMovieNotification = context.createMovieNotification {
            setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(comingSoonMovie)
                .setGroup(MOVIE_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
        }

        notifications.forEachIndexed { index, notification ->
            NotificationManagerCompat.from(context).apply {
                notify(movies[index].id ?: 0, notification)
            }
        }
        if (notifications.isNotEmpty()) {
            NotificationManagerCompat.from(context).notify(SUMMARY_ID, summaryMovieNotification)
        }
    }

    suspend fun loadNotificationImage(context: Context, imageUrl: String): Bitmap? = coroutineScope {
        async(context = ioDispatcher) {
            context.imageLoader.execute(
                request = ImageRequest.Builder(context = context)
                    .data(data = imageUrl)
                    .transformations(RoundedCornersTransformation(radius = 50f))
                    .build()
            ).image?.toBitmap()
        }
    }.await()
}

fun Context.createMovieNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        MOVIE_NOTIFICATION_CHANNEL_ID,
    ).setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

fun Context.ensureNotificationChannelExists() {
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