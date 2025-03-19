package com.bowoon.testing.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Movie
import com.bowoon.testing.TestMovieDataSource

class TestPagingRepository : PagingRepository {
    override fun searchMovieSource(
        type: String,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Movie> = TMDBSearchPagingSource(
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
        region: String
    ): PagingSource<Int, Movie> = TMDBSimilarMoviePagingSource(
        apis = TestMovieDataSource(),
        id = id,
        language = language,
        region = region
    )
}

val testPagingData = listOf(
    Movie(id = 0, title = "title_1"),
    Movie(id = 1, title = "title_2"),
    Movie(id = 2, title = "title_3"),
    Movie(id = 3, title = "title_4"),
    Movie(id = 4, title = "title_5")
)