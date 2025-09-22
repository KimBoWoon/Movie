package com.bowoon.data.repository

import com.bowoon.model.Movie

interface MainMenuRepository {
    suspend fun syncWith(isForce: Boolean): Boolean
    suspend fun getNowPlaying(): List<Movie>
    suspend fun getUpcomingMovies(): List<Movie>
}