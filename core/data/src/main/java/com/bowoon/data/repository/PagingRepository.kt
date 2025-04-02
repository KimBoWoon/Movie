package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.model.Movie
import com.bowoon.model.SearchResult
import com.bowoon.model.SearchType

interface PagingRepository {
    fun getUpComingMovies(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie>
    fun getNowPlaying(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie>
    fun searchMovieSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, SearchResult>
    fun getSimilarMovies(id: Int, language: String, region: String): PagingSource<Int, Movie>
}