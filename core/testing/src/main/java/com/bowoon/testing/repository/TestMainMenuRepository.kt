package com.bowoon.testing.repository

import androidx.annotation.VisibleForTesting
import com.bowoon.data.repository.MainMenuRepository
import com.bowoon.model.DisplayItem
import com.bowoon.testing.model.nowPlayingMoviesTestData
import com.bowoon.testing.model.upcomingMoviesTestData
import java.time.LocalDate

class TestMainMenuRepository : MainMenuRepository {
    private var date = LocalDate.now()

    override suspend fun syncWith(isForce: Boolean): Boolean =
        LocalDate.now().minusDays(1).isAfter(date) || isForce

    override suspend fun getNowPlaying(): List<DisplayItem> = nowPlayingMoviesTestData

    override suspend fun getUpcomingMovies(): List<DisplayItem> = upcomingMoviesTestData

    @VisibleForTesting
    fun setDate(date: LocalDate) {
        this.date = date
    }
}