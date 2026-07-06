package com.cheeke.surfy.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.network.TvRemoteDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class SimilarTvPagingSource(
    private val apis: TvRemoteDataSource,
    private val id: Int,
    private val language: String,
    private val region: String
) : RxPagingSource<Int, SimilarMedia>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, SimilarMedia>> {
        val currentPage = params.key ?: 1

        return apis.getSimilarTv(id = id, language = "$language-$region", page = currentPage)
            .subscribeOn(Schedulers.io())
            .map<LoadResult<Int, SimilarMedia>> { response ->
                LoadResult.Page(
                    data = response.results.orEmpty(),
                    prevKey = null,
                    nextKey = if ((response.totalPages ?: 1) > currentPage) currentPage + 1 else null
                )
            }.onErrorReturn { throwable ->
                Log.printStackTrace(throwable)
                LoadResult.Error(throwable)
            }
    }

    override fun getRefreshKey(state: PagingState<Int, SimilarMedia>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}

//class SimilarTvPagingSource(
//    private val apis: TvRemoteDataSource,
//    private val id: Int,
//    private val language: String,
//    private val region: String
//) : PagingSource<Int, SimilarMedia>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SimilarMedia> =
//        runCatching {
//            val response = apis.getSimilarTv(id = id, language = "$language-$region", page = params.key ?: 1).await()
//
//            LoadResult.Page(
//                data = response.results.orEmpty(),
//                prevKey = null,
//                nextKey = if ((response.totalPages ?: 1) > (params.key ?: 1)) (params.key ?: 1) + 1 else null
//            )
//        }.getOrElse { e ->
//            Log.printStackTrace(e)
//            LoadResult.Error(e)
//        }
//
//    override fun getRefreshKey(state: PagingState<Int, SimilarMedia>): Int? =
//        state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey ?: anchorPage?.nextKey
//        }
//}