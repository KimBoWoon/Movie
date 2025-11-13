package com.bowoon.notifications

import com.bowoon.model.Movie

interface Notifier {
    suspend fun postMovieNotifications(movies: List<Movie>)
}
