package com.bowoon.data.repository

import com.bowoon.model.KMDBMovieDetail
import kotlinx.coroutines.flow.Flow

interface KmdbRepository {
    fun getMovieInfo(url: String): Flow<KMDBMovieDetail>
}