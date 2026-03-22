package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.network.MovieNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MovieDetailRepository : DetailRepository<Movie> {
    override fun getData(id: Int): Flow<Movie>
    fun getMovieSeries(collectionId: Int): Flow<Series>
    fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList>
    fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider>
}

class MovieDetailRepositoryImpl @Inject constructor(
    private val apis: MovieNetworkDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : MovieDetailRepository {
    override fun getData(id: Int): Flow<Movie> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getMovie(id = id, language = "${internalData.language}-${internalData.region}", region = internalData.region, includeImageLanguage = "${internalData.language},null"))
    }

    override fun getMovieSeries(collectionId: Int): Flow<Series> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = apis.getMovieSeries(collectionId = collectionId, language = "${internalData.language}-${internalData.region}"))
    }

    override fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList> = flow {
        val internalData = requestOptionsProvider.current()
        val language = "${internalData.language}-${internalData.region}"

        emit(value = apis.getSeriesImages(collectionId = collectionId, includeImageLanguage = "$language,null", language = language))
    }

    override fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider> = flow {
        emit(value = apis.getMovieWatchProvider(movieId = movieId))
    }
}