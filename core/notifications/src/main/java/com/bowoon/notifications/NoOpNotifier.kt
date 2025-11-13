package com.bowoon.notifications

import com.bowoon.model.Movie
import javax.inject.Inject

internal class NoOpNotifier @Inject constructor() : Notifier {
    override suspend fun postMovieNotifications(movies: List<Movie>) = Unit
}
