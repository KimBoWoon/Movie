package com.cheeke.surfy.notifications

import com.cheeke.surfy.model.Movie

interface Notifier {
    fun postNotification(id: Int, message: String)
    fun postMovieNotifications(movies: List<Movie>)
}