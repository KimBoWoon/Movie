package com.bowoon.data.repository

import com.bowoon.model.UpComingResult
import com.bowoon.model.NowPlaying

interface MainMenuRepository {
    suspend fun syncWith(isForce: Boolean): Boolean
    suspend fun getNowPlaying(): List<NowPlaying>
    suspend fun getUpcomingMovies(): List<UpComingResult>
}