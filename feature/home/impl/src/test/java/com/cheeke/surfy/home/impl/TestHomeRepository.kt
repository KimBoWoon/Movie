package com.cheeke.surfy.home.impl

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.model.TrendingMediaResult
import com.cheeke.surfy.testing.model.testTrendingMovie
import com.cheeke.surfy.testing.model.testTrendingPeople
import com.cheeke.surfy.testing.model.testTrendingTv

class TestHomeRepository : HomeRepository {
    override fun getTrendingMovie(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult> = (testTrendingMovie.results ?: emptyList()).asPagingSourceFactory().invoke()

    override fun getTrendingPeople(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult> = (testTrendingPeople.results ?: emptyList()).asPagingSourceFactory().invoke()

    override fun getTrendingTv(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult> = (testTrendingTv.results ?: emptyList()).asPagingSourceFactory().invoke()
}