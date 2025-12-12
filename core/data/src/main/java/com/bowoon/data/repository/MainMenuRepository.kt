package com.bowoon.data.repository

import com.bowoon.data.util.Syncable
import com.bowoon.model.Movie

interface MainMenuRepository : Syncable {
    suspend fun getNowPlaying(): List<Movie>
    suspend fun getUpcomingMovies(): List<Movie>
}