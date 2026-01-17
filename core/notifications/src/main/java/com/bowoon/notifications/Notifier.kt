package com.bowoon.notifications

import com.bowoon.model.Movie

interface Notifier {
    fun postTestNotification(id: Int, message: String)
    fun postMovieNotifications(movies: List<Movie>)
}