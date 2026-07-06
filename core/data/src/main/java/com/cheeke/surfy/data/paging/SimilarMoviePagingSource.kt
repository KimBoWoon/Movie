package com.cheeke.surfy.data.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SimilarMedia
import com.cheeke.surfy.network.MovieRemoteDataSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class SimilarMoviePagingSource(
    private val apis: MovieRemoteDataSource,
    private val id: Int,
    private val language: String,
    private val region: String
) : RxPagingSource<Int, SimilarMedia>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, SimilarMedia>> {
        val page = params.key ?: 1

        return apis.getSimilarMovies(id = id, language = "$language-$region", page = page)
            .subscribeOn(Schedulers.io())
            .map<LoadResult<Int, SimilarMedia>> { response ->
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

    override fun getRefreshKey(state: PagingState<Int, SimilarMedia>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
}