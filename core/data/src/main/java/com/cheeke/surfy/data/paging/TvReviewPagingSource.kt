package com.cheeke.surfy.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.Review
import com.cheeke.surfy.network.TvRemoteDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class TvReviewPagingSource(
    private val apis: TvRemoteDataSource,
    private val id: Int,
    private val language: String,
    private val region: String
) : RxPagingSource<Int, Review>() {
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? = 1

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Review>> {
        val page = params.key ?: 1

        return apis.getTvReviews(seriesId = id, language = "$language-$region", page = page)
            .subscribeOn(Schedulers.io())
            .map<LoadResult<Int, Review>> { response ->
                LoadResult.Page(
                    data = response.results.orEmpty(),
                    prevKey = null,
                    nextKey = if ((response.totalPages ?: 1) > page) page + 1 else null
                )
            }.onErrorReturn { e ->
                Log.printStackTrace(e)
                LoadResult.Error(e)
            }
    }
}