package com.bowoon.notifications

import com.bowoon.model.Movie

interface Notifier {
    fun postNotification(id: Int, message: String)
    fun postMovieNotifications(movies: List<Movie>)
}