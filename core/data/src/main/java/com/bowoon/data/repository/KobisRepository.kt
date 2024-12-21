package com.bowoon.data.repository

import com.bowoon.model.KOBISBoxOffice
import com.bowoon.model.KOBISMovieData
import kotlinx.coroutines.flow.Flow

interface KobisRepository {
    fun getDailyBoxOffice(
        key: String,
        targetDt: String
    ): Flow<KOBISBoxOffice>

    fun getMovieInfo(
        key: String,
        movieCd: String
    ): Flow<KOBISMovieData>
}