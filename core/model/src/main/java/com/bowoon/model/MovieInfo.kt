package com.bowoon.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class MovieInfo(
    val detail: MovieDetail,
    val series: MovieSeries?,
    val similarMovies: Flow<PagingData<Movie>>
)