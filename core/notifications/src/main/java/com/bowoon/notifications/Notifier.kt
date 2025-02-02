package com.bowoon.notifications

import com.bowoon.model.Movie

interface Notifier {
    fun postMovieNotifications(movies: List<Movie>)
}
