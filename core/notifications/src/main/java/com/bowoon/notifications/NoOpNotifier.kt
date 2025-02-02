package com.bowoon.notifications

import com.bowoon.model.Movie
import javax.inject.Inject

internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postNewsNotifications(movies: List<Movie>) = Unit
}
