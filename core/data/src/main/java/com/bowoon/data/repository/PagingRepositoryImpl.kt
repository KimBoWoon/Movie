package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.NowPlayingMoviePagingSource
import com.bowoon.data.paging.RecommendKeywordPagingSource
import com.bowoon.data.paging.SearchPagingSource
import com.bowoon.data.paging.SimilarMoviePagingSource
import com.bowoon.data.paging.UpComingMoviePagingSource
import com.bowoon.model.DisplayItem
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource
) : PagingRepository {
    override fun getNowPlayingMoviePagingSource(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, DisplayItem> = NowPlayingMoviePagingSource(
        apis = apis,
        language = language,
        region = region
    )

    override fun getUpComingMoviePagingSource(
        language: String,
        region: String,
        isAdult: Boolean,
        releaseDateGte: String,
        releaseDateLte: String
    ): PagingSource<Int, DisplayItem> = UpComingMoviePagingSource(
        apis = apis,
        language = language,
        region = region
    )

    override fun getSearchPagingSource(
        type: SearchType,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, DisplayItem> = SearchPagingSource(
        apis = apis,
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult
    )

    override fun getSimilarMoviePagingSource(
        id: Int,
        language: String
    ): PagingSource<Int, DisplayItem> = SimilarMoviePagingSource(
        apis = apis,
        id = id,
        language = language
    )

    override fun getRecommendKeywordPagingSource(query: String): PagingSource<Int, SearchKeyword> =
        RecommendKeywordPagingSource(
            apis = apis,
            query = query
        )
}