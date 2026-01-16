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
import com.bowoon.common.Log
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Movie
import com.bowoon.movie.core.notifications.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val MOVIE_NOTIFICATION_CHANNEL_ID = "MOVIE_NOTIFICATION_CHANNEL"
private const val MOVIE_NOTIFICATION_GROUP = "MOVIE_NOTIFICATIONS"
val SUMMARY_ID = 0
private const val MOVIE_NOTIFICATION_REQUEST_CODE = 0
private const val TARGET_ACTIVITY_NAME = "com.bowoon.movie.ui.activities.MainActivity"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.bowoon.movie.com"
private const val DEEP_LINK_MOVIE_ID_PATH = "movie"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_MOVIE_ID_PATH"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{id}"

@Singleton
class SystemTrayNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Dispatcher(dispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    private val userDataRepository: UserDataRepository
) : Notifier {
    override fun postNotification(id: Int, message: String) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) return

        val notification = context.createMovieNotification {
            setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(message)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        MOVIE_NOTIFICATION_REQUEST_CODE,
                        Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = message.toUri()
                            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            component = ComponentName(
                                context.packageName,
                                TARGET_ACTIVITY_NAME,
                            )
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
        }

        NotificationManagerCompat.from(context).apply {
            notify(id, notification)
        }
    }

    override fun postMovieNotifications(movies: List<Movie>) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) return
        if (movies.isEmpty()) return

        val comingSoonMovie = context.getString(R.string.coming_soon_movie)

        CoroutineScope(context = ioDispatcher).launch {
            val imageUrl = userDataRepository.getSecureBaseUrl()
            Log.d("imageUrl -> $imageUrl")
            val notifications = movies.map { movie ->
                async(context = ioDispatcher) { loadNotificationImage(context = context, imageUrl = "$imageUrl${movie.posterPath}") }
            }.awaitAll().let { bitmapList ->
                movies.mapIndexed { index, movie ->
                    context.createMovieNotification {
                        if (bitmapList[index] == null) {
                            setSmallIcon(R.drawable.ic_launcher_round)
                                .setContentTitle(comingSoonMovie)
                                .setContentText(movie.title)
                                .setContentIntent(context.moviePendingIntent(movie = movie))
                                .setGroup(MOVIE_NOTIFICATION_GROUP)
                                .setAutoCancel(true)
                        } else {
                            setSmallIcon(R.drawable.ic_launcher_round)
                                .setLargeIcon(bitmapList[index])
                                .setContentTitle(comingSoonMovie)
                                .setContentText(movie.title)
                                .setContentIntent(context.moviePendingIntent(movie = movie))
                                .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmapList[index]))
                                .setGroup(MOVIE_NOTIFICATION_GROUP)
                                .setAutoCancel(true)
                        }
                    }
                }
            }

            notifications.forEachIndexed { index, notification ->
                NotificationManagerCompat.from(context).apply {
                    notify(movies[index].id ?: 0, notification)
                }
            }

            val summaryMovieNotification = context.createMovieNotification {
                setSmallIcon(R.drawable.ic_launcher_round)
                    .setContentTitle(comingSoonMovie)
                    .setGroup(MOVIE_NOTIFICATION_GROUP)
                    .setGroupSummary(true)
                    .setAutoCancel(true)
            }

            if (movies.isNotEmpty()) {
                NotificationManagerCompat.from(context).notify(SUMMARY_ID, summaryMovieNotification)
            }
        }
    }

    private suspend fun loadNotificationImage(context: Context, imageUrl: String): Bitmap? = coroutineScope {
        async(context = ioDispatcher) {
            context.imageLoader.execute(
                request = ImageRequest.Builder(context = context)
                    .data(data = imageUrl)
                    .size(width = context.resources.displayMetrics.widthPixels, height = context.resources.displayMetrics.widthPixels / 2)
                    .transformations(RoundedCornersTransformation(radius = 20f))
                    .build()
            ).image?.toBitmap()
        }.await()
    }
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
        description = getString(R.string.coming_soon_movie)
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

private fun Movie.movieDeepLinkUri() = "$DEEP_LINK_BASE_PATH?id=$id".toUri()