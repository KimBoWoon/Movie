package com.bowoon.notifications

import com.bowoon.model.Movie

interface Notifier {
    fun postNotification(message: String)
    fun postMovieNotifications(movies: List<Movie>)
}