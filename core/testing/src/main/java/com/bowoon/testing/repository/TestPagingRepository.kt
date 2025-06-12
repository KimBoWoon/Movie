package com.bowoon.testing.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.TMDBNowPlayingPagingSource
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.data.paging.TMDBUpComingPagingSource
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Movie
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchType
import com.bowoon.testing.TestMovieDataSource

class TestPagingRepository : PagingRepository {
    override fun getNowPlaying(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie> = TMDBUpComingPagingSource(
        apis = TestMovieDataSource(),
        language = language,
        region = region
    )

    override fun getUpComingMovies(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie> = TMDBNowPlayingPagingSource(
        apis = TestMovieDataSource(),
        language = language,
        region = region
    )

    override fun searchMovieSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, SearchGroup> = TMDBSearchPagingSource(
        apis = TestMovieDataSource(),
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult
    )

    override fun getSimilarMovies(
        id: Int,
        language: String,
        imageUrl: String?
    ): PagingSource<Int, Movie> = TMDBSimilarMoviePagingSource(
        apis = TestMovieDataSource(),
        id = id,
        language = language,
        imageUrl = imageUrl
    )
}