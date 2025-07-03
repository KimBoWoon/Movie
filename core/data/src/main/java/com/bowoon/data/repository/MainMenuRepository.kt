package com.bowoon.data.repository

import com.bowoon.model.DisplayItem

interface MainMenuRepository {
    suspend fun syncWith(isForce: Boolean): Boolean
    suspend fun getNowPlaying(): List<DisplayItem>
    suspend fun getUpcomingMovies(): List<DisplayItem>
}