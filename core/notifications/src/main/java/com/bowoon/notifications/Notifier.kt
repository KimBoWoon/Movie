package com.bowoon.notifications

import com.bowoon.model.Movie

interface Notifier {
    fun postNewsNotifications(movies: List<Movie>)
}
