package com.cheeke.surfy.detail.impl.movie

import com.cheeke.surfy.detail.api.DetailRequestOptionsProvider
import com.cheeke.surfy.detail.api.movie.MovieDetailRepository
import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.network.MovieRemoteDataSource
import com.cheeke.surfy.network.SeriesRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MovieDetailRepositoryImpl @Inject constructor(
    private val movieApis: MovieRemoteDataSource,
    private val seriesApis: SeriesRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : MovieDetailRepository {
    override fun getData(id: Int): Flow<Movie> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = movieApis.getMovie(id = id, language = internalData.languageTag, region = internalData.region, includeImageLanguage = internalData.includeImageLanguage))
    }

    override fun getMovieSeries(collectionId: Int): Flow<Series> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = seriesApis.getMovieSeries(collectionId = collectionId, language = internalData.languageTag))
    }

    override fun getMovieSeriesImageList(collectionId: Int): Flow<ImageList> = flow {
        val internalData = requestOptionsProvider.current()

        emit(value = seriesApis.getSeriesImages(collectionId = collectionId, includeImageLanguage = "${internalData.languageTag},null", language = internalData.languageTag))
    }

    override fun getMovieWatchProviders(movieId: Int): Flow<MovieWatchProvider> = flow {
        emit(value = movieApis.getMovieWatchProvider(movieId = movieId))
    }
}