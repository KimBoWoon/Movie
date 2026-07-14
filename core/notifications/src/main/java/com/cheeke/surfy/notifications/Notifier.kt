package com.cheeke.surfy.notifications

import com.cheeke.surfy.model.Media

interface Notifier {
//    fun postNotification(id: Int, message: String)
    fun postMovieNotifications(movies: List<Media>)
    fun postNotification(notificationId: Int, contentTitle: String, deepLinkUri: String)
}