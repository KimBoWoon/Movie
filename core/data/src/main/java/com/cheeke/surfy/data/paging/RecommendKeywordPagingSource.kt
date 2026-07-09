package com.cheeke.surfy.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SearchKeyword
import com.cheeke.surfy.network.SearchRemoteDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RecommendKeywordPagingSource(
    private val apis: SearchRemoteDataSource,
    private val query: String
): RxPagingSource<Int, SearchKeyword>() {
    override fun getRefreshKey(state: PagingState<Int, SearchKeyword>): Int? = 1

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, SearchKeyword>> {
        return if (query.isNotEmpty()) {
            val page = params.key ?: 1
            apis.getSearchKeyword(query = query, page = page)
                .subscribeOn(Schedulers.io())
                .map<LoadResult<Int, SearchKeyword>> { response ->
                    LoadResult.Page(
                        data = response.results.orEmpty(),
                        prevKey = null,
                        nextKey = if ((response.totalPages ?: 1) > page) page + 1 else null
                    )
                }.onErrorReturn { throwable ->
                    Log.printStackTrace(throwable)
                    LoadResult.Error(throwable)
                }
        } else {
            Single.just(
                LoadResult.Page<Int, SearchKeyword>(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            )
        }
    }
}