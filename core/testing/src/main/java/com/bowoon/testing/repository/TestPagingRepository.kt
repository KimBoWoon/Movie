package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.paging.TMDBNowPlayingPagingSource
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBUpComingPagingSource
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Movie
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchType
import com.bowoon.testing.TestMovieDataSource
import kotlin.random.Random

class TestPagingRepository : PagingRepository {
    @SuppressLint("VisibleForTests")
    private val testPagingSource = (0..50).map {
        Movie(
            backdropPath = "backdropPath_$it",
            genreIds = listOf(it),
            originalLanguage = "originalLanguage_$it",
            originalTitle = "originalTitle_$it",
            overview = "overview_$it",
            posterPath = "/posterPath_$it.png",
            releaseDate = "releaseDate_$it",
            title = "title_$it",
            video = true,
            voteAverage = it.toDouble(),
            voteCount = it,
            adult = true,
            id = it,
            name = "name_$it",
            imagePath = "/imagePath_$it.png",
            originalName = "originalName_$it",
            popularity = it.toDouble()
        )
    }.asPagingSourceFactory().invoke()

    override fun getSimilarMovies(id: Int, language: String): PagingSource<Int, Movie> = testPagingSource

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

//    override fun getSimilarMovies(
//        id: Int,
//        language: String
//    ): PagingSource<Int, Movie> = TMDBSimilarMoviePagingSource(
//        apis = TestMovieDataSource(),
//        id = id,
//        language = language
//    )
}