package com.cheeke.surfy.home.impl

import androidx.paging.PagingSource
import com.cheeke.surfy.home.impl.paging.TrendingMoviePagingSource
import com.cheeke.surfy.home.impl.paging.TrendingPeoplePagingSource
import com.cheeke.surfy.home.impl.paging.TrendingTvPagingSource
import com.cheeke.surfy.model.TrendingMediaResult
import com.cheeke.surfy.network.api.TrendingRemoteDataSource
import javax.inject.Inject

interface HomeRepository {
    fun getTrendingMovie(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult>

    fun getTrendingPeople(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult>

    fun getTrendingTv(timeWindow: String, language: String): PagingSource<Int, TrendingMediaResult>
}

class HomeRepositoryImpl @Inject constructor(
    private val trendingApis: TrendingRemoteDataSource
) : HomeRepository {
    override fun getTrendingMovie(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult> = TrendingMoviePagingSource(
        apis = trendingApis,
        timeWindow = timeWindow,
        language = language
    )

    override fun getTrendingPeople(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult> = TrendingPeoplePagingSource(
        apis = trendingApis,
        timeWindow = timeWindow,
        language = language
    )

    override fun getTrendingTv(
        timeWindow: String,
        language: String
    ): PagingSource<Int, TrendingMediaResult> = TrendingTvPagingSource(
        apis = trendingApis,
        timeWindow = timeWindow,
        language = language
    )
}