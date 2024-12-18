package com.bowoon.data.repository

import com.bowoon.model.BoxOffice
import com.bowoon.model.KOBISMovieData
import kotlinx.coroutines.flow.Flow

interface KobisRepository {
    fun getDailyBoxOffice(
        key: String,
        targetDt: String
    ): Flow<BoxOffice>

    fun getMovieInfo(
        key: String,
        movieCd: String
    ): Flow<KOBISMovieData>
}