package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.Movie

interface PagingRepository {
    fun getUpComingMovies(
        language: String,
        region: String,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie>
    fun getNowPlaying(
        language: String,
        region: String,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie>
    fun searchMovieSource(
        type: String,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Movie>
    fun getSimilarMovies(id: Int, language: String, region: String): PagingSource<Int, Movie>
}