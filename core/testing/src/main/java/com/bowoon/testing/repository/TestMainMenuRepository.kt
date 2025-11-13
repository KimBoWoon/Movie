package com.bowoon.testing.repository

import androidx.annotation.VisibleForTesting
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.model.Movie
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.upcomingMoviesTestData
import java.time.LocalDate

class TestMainMenuRepository : MainMenuRepository {
    private var date = LocalDate.now()

    override suspend fun syncWith(isForce: Boolean, notification: suspend () -> Unit): Boolean =
        LocalDate.now().minusDays(1).isAfter(date) || isForce

    override suspend fun getNowPlaying(): List<Movie> = nowPlayingMoviesTestData

    override suspend fun getUpcomingMovies(): List<Movie> = upcomingMoviesTestData

    @VisibleForTesting
    fun setDate(date: LocalDate) {
        this.date = date
    }
}