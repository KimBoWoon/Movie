package com.bowoon.testing.repository

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.DisplayItem
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.testing.model.testRecommendedKeyword

class TestPagingRepository : PagingRepository {
    @SuppressLint("VisibleForTests")
    private val testPagingSource = (0..100).map {
        DisplayItem(
            genreIds = listOf(it),
            releaseDate = "releaseDate_$it",
            title = "title_$it",
            adult = true,
            id = it,
            imagePath = "/imagePath_$it.png"
        )
    }.asPagingSourceFactory().invoke()

    @SuppressLint("VisibleForTests")
    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, DisplayItem> {
        return (0..100).map {
            DisplayItem(
                genreIds = listOf(it),
                releaseDate = "releaseDate_$it",
                title = "title_$it",
                adult = true,
                id = it,
                imagePath = "/imagePath_$it.png"
            )
        }.asPagingSourceFactory().invoke()
    }

    override fun getSimilarMoviePagingSource(id: Int, language: String): PagingSource<Int, DisplayItem> = testPagingSource

    @SuppressLint("VisibleForTests")
    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> = testRecommendedKeyword.asPagingSourceFactory().invoke()
}