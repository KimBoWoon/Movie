package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.testing.model.testRecommendedKeyword

class TestPagingRepository : PagingRepository {
    @SuppressLint("VisibleForTests")
    private val testPagingSource = (0..100).map {
        Movie(
            genres = listOf(Genre(id = it)),
            releaseDate = "releaseDate_$it",
            title = "title_$it",
            adult = true,
            id = it,
            posterPath = "/imagePath_$it.png"
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Movie> {
        return (0..100).map {
            Movie(
                genres = listOf(Genre(id = it)),
                releaseDate = "releaseDate_$it",
                title = "title_$it",
                adult = true,
                id = it,
                posterPath = "/imagePath_$it.png"
            )
        }.asPagingSourceFactory().invoke()
    }

    override fun getSimilarMoviePagingSource(id: Int, language: String): PagingSource<Int, Movie> = testPagingSource

    @SuppressLint("VisibleForTests")
    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> = testRecommendedKeyword.asPagingSourceFactory().invoke()
}