package com.cheeke.surfy.data.repository

import com.cheeke.surfy.model.ImageList
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.MovieWatchProvider
import com.cheeke.surfy.model.Series
import com.cheeke.surfy.network.MovieRemoteDataSource
import com.cheeke.surfy.network.SeriesRemoteDataSource
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

interface MovieDetailRepository : DetailRepository<Movie> {
    override fun getData(id: Int): Single<Movie>
    fun getMovieSeries(collectionId: Int): Single<Series>
    fun getMovieSeriesImageList(collectionId: Int): Single<ImageList>
    fun getMovieWatchProviders(movieId: Int): Single<MovieWatchProvider>
}

class MovieDetailRepositoryImpl @Inject constructor(
    private val movieApis: MovieRemoteDataSource,
    private val seriesApis: SeriesRemoteDataSource,
    private val requestOptionsProvider: DetailRequestOptionsProvider
) : MovieDetailRepository {
    override fun getData(id: Int): Single<Movie> = requestOptionsProvider.current()
        .flatMap {
            movieApis.getMovie(
                id = id,
                language = it.languageTag,
                region = it.region,
                includeImageLanguage = it.includeImageLanguage
            )
        }

    override fun getMovieSeries(collectionId: Int): Single<Series> = requestOptionsProvider.current()
        .flatMap {
            seriesApis.getMovieSeries(
                collectionId = collectionId,
                language = it.languageTag
            )
        }

    override fun getMovieSeriesImageList(collectionId: Int): Single<ImageList> = requestOptionsProvider.current()
        .flatMap {
            seriesApis.getSeriesImages(
                collectionId = collectionId,
                includeImageLanguage = it.localizedImageLanguage,
                language = it.languageTag
            )
        }

    override fun getMovieWatchProviders(movieId: Int): Single<MovieWatchProvider> =
        movieApis.getMovieWatchProvider(movieId = movieId)
}