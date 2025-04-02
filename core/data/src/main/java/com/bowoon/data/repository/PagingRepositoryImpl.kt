package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.TMDBNowPlayingPagingSource
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.data.paging.TMDBUpComingPagingSource
import com.bowoon.model.Movie
import com.bowoon.model.SearchResult
import com.bowoon.model.SearchType
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource
) : PagingRepository {
    override fun getNowPlaying(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie> = TMDBNowPlayingPagingSource(
        apis = apis,
        language = language,
        region = region,
        isAdult = isAdult,
        releaseDateGte = releaseDateGte,
        releaseDateLte = releaseDateLte
    )

    override fun getUpComingMovies(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie> = TMDBUpComingPagingSource(
        apis = apis,
        language = language,
        region = region,
        isAdult = isAdult,
        releaseDateGte = releaseDateGte,
        releaseDateLte = releaseDateLte
    )

    override fun searchMovieSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, SearchResult> = TMDBSearchPagingSource(
        apis = apis,
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult
    )

    override fun getSimilarMovies(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, Movie> = TMDBSimilarMoviePagingSource(
        apis = apis,
        id = id,
        language = language,
        region = region
    )
}