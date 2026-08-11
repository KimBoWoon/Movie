package com.cheeke.surfy.detail.impl

import com.cheeke.surfy.detail.api.movie.MovieRepository
import com.cheeke.surfy.detail.api.people.PeopleRepository
import com.cheeke.surfy.detail.api.tv.TvRepository
import com.cheeke.surfy.detail.impl.movie.MovieDetailRepository
import com.cheeke.surfy.detail.impl.movie.MovieDetailRepositoryImpl
import com.cheeke.surfy.detail.impl.movie.MovieRepositoryImpl
import com.cheeke.surfy.detail.impl.people.PeopleDetailRepository
import com.cheeke.surfy.detail.impl.people.PeopleDetailRepositoryImpl
import com.cheeke.surfy.detail.impl.people.PeopleRepositoryImpl
import com.cheeke.surfy.detail.impl.series.SeriesDetailRepositoryImpl
import com.cheeke.surfy.detail.impl.series.SeriesRepository
import com.cheeke.surfy.detail.impl.tv.TvDetailRepository
import com.cheeke.surfy.detail.impl.tv.TvDetailRepositoryImpl
import com.cheeke.surfy.detail.impl.tv.TvRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DetailRepositoryModules {
    @Binds
    abstract fun bindMovieDetailRepository(
        movieDetailRepository: MovieDetailRepositoryImpl
    ): MovieDetailRepository

    @Binds
    abstract fun bindPeopleDetailRepository(
        peopleDetailRepository: PeopleDetailRepositoryImpl
    ): PeopleDetailRepository

    @Binds
    abstract fun bindTvDetailRepository(
        tvDetailRepository: TvDetailRepositoryImpl
    ): TvDetailRepository

    @Binds
    abstract fun bindSeriesDetailRepository(
        seriesDetailRepository: SeriesDetailRepositoryImpl
    ): SeriesRepository

    @Binds
    abstract fun bindMovieRepository(
        movieRepository: MovieRepositoryImpl
    ): MovieRepository

    @Binds
    abstract fun bindPeopleRepository(
        movieRepository: PeopleRepositoryImpl
    ): PeopleRepository

    @Binds
    abstract fun bindTvRepository(
        movieRepository: TvRepositoryImpl
    ): TvRepository
}