package com.bowoon.notifications

import com.bowoon.model.DisplayItem

interface Notifier {
    fun postMovieNotifications(movies: List<DisplayItem>)
}
