package com.cheeke.surfy.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.RoundedCornersTransformation
import com.cheeke.surfy.common.Dispatcher
import com.cheeke.surfy.common.Dispatchers.IO
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.common.di.ApplicationScope
import com.cheeke.surfy.core.notifications.R
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.MediaVisitor
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.model.Tv
import com.cheeke.surfy.userdata.api.UserDataRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val MOVIE_NOTIFICATION_CHANNEL_ID = "MOVIE_NOTIFICATION_CHANNEL"
private const val MOVIE_NOTIFICATION_GROUP = "MOVIE_NOTIFICATIONS"
private const val SUMMARY_NOTIFICATION_ID = 0
private const val TARGET_ACTIVITY_NAME = "com.cheeke.surfy.ui.activities.MainActivity"
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.cheeke.surfy.com"
private const val DEEP_LINK_MOVIE_ID_PATH = "surfy"
private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$DEEP_LINK_MOVIE_ID_PATH"
const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_BASE_PATH/{id}"

private object ComingSoonTitleResolver : MediaVisitor<Int?> {
    override fun visitMovie(movie: Movie): Int = R.string.coming_soon_movie
    override fun visitTv(tv: Tv): Int = R.string.coming_soon_tv
    override fun visitPeople(people: People): Int? = null
    override fun visitSeries(series: Series): Int? = null
}

@Singleton
class SystemTrayNotifier @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    @param:Dispatcher(dispatcher = IO) private val ioDispatcher: CoroutineDispatcher,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    private val userDataRepository: UserDataRepository
) : Notifier {
    init {
        appContext.ensureNotificationChannelExists()
    }

    override fun postNotification(notificationId: Int, contentTitle: String, deepLinkUri: String) {
        if (!appContext.hasPostNotificationPermission()) return

        val notification = appContext.buildMovieNotification { builder ->
            builder.setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(contentTitle)
                .setContentIntent(
                    appContext.deepLinkPendingIntent(requestCode = notificationId, deepLinkUri = deepLinkUri.toUri())
                )
        }

        NotificationManagerCompat.from(appContext).notify(notificationId, notification)
    }

    override fun postMovieNotifications(movies: List<Media>) {
        if (!appContext.hasPostNotificationPermission()) return
        if (movies.isEmpty()) return

        applicationScope.launch(context = ioDispatcher) {
            val baseImageUrl = userDataRepository.getSecureBaseUrl()
            Log.d("baseImageUrl -> $baseImageUrl")

            val posterBitmaps = movies.map { media ->
                async(context = ioDispatcher) {
                    runCatching {
                        loadNotificationImage(context = appContext, imageUrl = "$baseImageUrl${media.posterPath}")
                    }.getOrNull()
                }
            }.awaitAll()

            movies.forEachIndexed { index, media ->
                val notificationId = media.id
                if (notificationId == null) {
                    Log.i("id가 없는 미디어는 알림을 건너뜁니다 -> $media")
                    return@forEachIndexed
                }

                val titleRes = media.accept(visitor = ComingSoonTitleResolver)
                if (titleRes == null) {
                    Log.i("알림 대상이 아닌 타입 -> $media")
                    return@forEachIndexed
                }
                val notificationTitle = appContext.getString(titleRes)

                val posterBitmap = posterBitmaps[index]
                val notification = appContext.buildMovieNotification { builder ->
                    builder.setSmallIcon(R.drawable.ic_launcher_round)
                        .setContentTitle(notificationTitle)
                        .setContentText(media.title)
                        .setContentIntent(appContext.deepLinkPendingIntent(requestCode = notificationId, deepLinkUri = media.movieDeepLinkUri()))
                        .setGroup(MOVIE_NOTIFICATION_GROUP)
                        .setAutoCancel(true)

                    if (posterBitmap != null) {
                        builder.setLargeIcon(posterBitmap)
                            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(posterBitmap))
                    }
                }

                NotificationManagerCompat.from(appContext).notify(notificationId, notification)
            }

            val summaryNotification = appContext.buildMovieNotification { builder ->
                builder.setSmallIcon(R.drawable.ic_launcher_round)
                    .setGroup(MOVIE_NOTIFICATION_GROUP)
                    .setGroupSummary(true)
                    .setAutoCancel(true)
            }

            NotificationManagerCompat.from(appContext).notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
        }
    }

    private suspend fun loadNotificationImage(context: Context, imageUrl: String): Bitmap? {
        return context.imageLoader.execute(
            request = ImageRequest.Builder(context = context)
                .data(data = imageUrl)
                .size(width = context.resources.displayMetrics.widthPixels, height = context.resources.displayMetrics.widthPixels / 2)
                .transformations(RoundedCornersTransformation(radius = 20f))
                .build()
        ).image?.toBitmap()
    }
}

private fun Context.hasPostNotificationPermission(): Boolean {
    return NotificationManagerCompat.from(this).areNotificationsEnabled()
}

fun Context.buildMovieNotification(
    block: (NotificationCompat.Builder) -> Unit,
): Notification {
    val builder = NotificationCompat.Builder(this, MOVIE_NOTIFICATION_CHANNEL_ID)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    block(builder)
    return builder.build()
}

private fun Context.ensureNotificationChannelExists() {
    val channel = NotificationChannel(
        MOVIE_NOTIFICATION_CHANNEL_ID,
        getString(R.string.system_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.coming_soon_movie)
    }

    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

fun Context.deepLinkPendingIntent(requestCode: Int, deepLinkUri: Uri): PendingIntent {
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = deepLinkUri
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        component = ComponentName(packageName, TARGET_ACTIVITY_NAME)
    }
    return PendingIntent.getActivity(
        this,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun Media.movieDeepLinkUri(): Uri =
    "$DEEP_LINK_SCHEME_AND_HOST/$deepLinkPath?id=$id".toUri()