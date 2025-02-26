package com.bowoon.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Movie
import com.bowoon.network.retrofit.Apis
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: Apis,
    private val datastore: InternalDataSource,
    private val myDataRepository: MyDataRepository
) : PagingRepository {
    override suspend fun searchMovies(
        type: String,
        query: String
    ): Flow<PagingData<Movie>> {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region
        val isAdult = datastore.getUserData().isAdult

        return Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
            pagingSourceFactory = {
                TMDBSearchPagingSource(
                    apis = apis,
                    type = type,
                    query = query,
                    language = language,
                    region = region,
                    isAdult = isAdult
                )
            }
        ).flow
    }

    override suspend fun getSimilarMovies(id: Int): Flow<PagingData<Movie>> {
        val language = datastore.getUserData().language
        val region = datastore.getUserData().region

        return Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
            pagingSourceFactory = {
                TMDBSimilarMoviePagingSource(
                    apis = apis,
                    id = id,
                    language = language,
                    region = region
                )
            }
        ).flow
    }
}