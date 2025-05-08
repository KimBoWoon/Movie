package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchPeopleKnownFor
import com.bowoon.model.SearchType
import com.bowoon.model.Series

class TestPagingRepository : PagingRepository {
    @SuppressLint("VisibleForTests")
    private val testPagingSource = (0..100).map {
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

    override fun getUpComingMovies(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie> = testPagingSource

    override fun getNowPlaying(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, Movie> = testPagingSource

    @SuppressLint("VisibleForTests")
    override fun searchMovieSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, SearchGroup> {
        return (0..100).map {
            when (type) {
                SearchType.MOVIE -> {
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
                }
                SearchType.PEOPLE -> {
                    People(
                        gender = 1,
                        knownFor = listOf(SearchPeopleKnownFor()),
                        knownForDepartment = "knownForDepartment_$it",
                        profilePath = "profilePath_$it",
                        adult = true,
                        id = it,
                        name = "name_$it",
                        imagePath = "imagePath_$it",
                        originalName = "originalName_$it",
                        popularity = it.toDouble()
                    )
                }
                SearchType.SERIES -> {
                    Series(
                        backdropPath = "backdropPath_$it",
                        originalLanguage = "originalLanguage_$it",
                        overview = "overview_$it",
                        posterPath = "posterPath_$it",
                        adult = true,
                        id = it,
                        name = "name_$it",
                        imagePath = "imagePath_$it",
                        originalName = "originalName_$it",
                        popularity = it.toDouble()
                    )
                }
            }
        }.asPagingSourceFactory().invoke()
    }

    override fun getSimilarMovies(id: Int, language: String): PagingSource<Int, Movie> = testPagingSource

    @SuppressLint("VisibleForTests")
    override fun getSearchKeyword(query: String): PagingSource<Int, SearchKeyword> =
        (1..5).map {
            SearchKeyword(id = it, name = "mission$it")
        }.asPagingSourceFactory().invoke()
}