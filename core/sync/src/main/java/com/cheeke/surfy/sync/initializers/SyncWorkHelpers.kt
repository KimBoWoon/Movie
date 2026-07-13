package com.cheeke.surfy.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.cheeke.surfy.core.sync.R

const val SYNC_TOPIC = "sync"
const val UNIQUE_SYNC_WORKER = "UNIQUE_SYNC_WORKER"
private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(networkType = NetworkType.CONNECTED)
        .build()

fun Context.syncForegroundInfo() = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification(),
)

private fun Context.syncWorkNotification(): Notification {
    val channel = NotificationChannel(
        SYNC_NOTIFICATION_CHANNEL_ID,
        getString(R.string.mid_night_sync_worker_noti_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.syncing)
    }

    val notificationManager: NotificationManager? =
        getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    notificationManager?.createNotificationChannel(channel)

    return NotificationCompat.Builder(
        this,
        SYNC_NOTIFICATION_CHANNEL_ID,
    )
        .setSmallIcon(com.cheeke.surfy.core.notifications.R.drawable.ic_launcher_round)
        .setContentTitle(getString(R.string.mid_night_sync_worker_noti_description))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
