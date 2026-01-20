package com.bowoon.notifications

import com.bowoon.model.Movie
import javax.inject.Inject

internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postNotification(id: Int, message: String) = Unit
    override fun postMovieNotifications(movies: List<Movie>) = Unit
}