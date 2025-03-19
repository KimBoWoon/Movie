package com.bowoon.data.repository

import androidx.paging.PagingSource
import com.bowoon.data.paging.TMDBSearchPagingSource
import com.bowoon.data.paging.TMDBSimilarMoviePagingSource
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.Movie
import com.bowoon.network.MovieNetworkDataSource
import javax.inject.Inject

class PagingRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val datastore: InternalDataSource
) : PagingRepository {
//    override suspend fun getNowPlaying(): Flow<PagingData<NowPlaying>> {
//        val language = datastore.getUserData().language
//        val region = datastore.getUserData().region
//
//        return Pager(
//            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//            pagingSourceFactory = {
//                NowPlayingPagingSource(
//                    apis = apis,
//                    language = language,
//                    region = region
//                )
//            }
//        ).flow
//    }
//
//    override suspend fun getUpcomingMovies(): Flow<PagingData<UpComingResult>> {
//        val language = datastore.getUserData().language
//        val region = datastore.getUserData().region
//
//        return Pager(
//            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//            pagingSourceFactory = {
//                UpComingMoviePagingSource(
//                    apis = apis,
//                    language = language,
//                    region = region
//                )
//            }
//        ).flow
//    }

//    override suspend fun searchMovies(
//        type: String,
//        query: String
//    ): Flow<PagingData<Movie>> {
//        val language = datastore.getUserData().language
//        val region = datastore.getUserData().region
//        val isAdult = datastore.getUserData().isAdult
//
//        return Pager(
//            config = PagingConfig(pageSize = 20, initialLoadSize = 20, prefetchDistance = 5),
//            pagingSourceFactory = {
//                TMDBSearchPagingSource(
//                    apis = apis,
//                    type = type,
//                    query = query,
//                    language = language,
//                    region = region,
//                    isAdult = isAdult
//                )
//            }
//        ).flow
//    }

    override fun searchMovieSource(
        type: String,
        query: String,
        language: String,
        region: String,
        isAdult: Boolean
    ): PagingSource<Int, Movie> = TMDBSearchPagingSource(
        apis = apis,
        type = type,
        query = query,
        language = language,
        region = region,
        isAdult = isAdult
    )

    override fun getSimilarMovies(
        id: Int,
        language: String,
        region: String
    ): PagingSource<Int, Movie> = TMDBSimilarMoviePagingSource(
        apis = apis,
        id = id,
        language = language,
        region = region
    )
}