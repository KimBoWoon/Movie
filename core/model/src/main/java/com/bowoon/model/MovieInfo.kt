package com.bowoon.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

data class MovieInfo(
    val detail: Movie,
    val series: Series?,
    val similarMovies: Flow<PagingData<DisplayItem>>
)