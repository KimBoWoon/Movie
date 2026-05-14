package com.cheeke.surfy.data.di

import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.MovieDataBaseRepositoryImpl
import com.cheeke.surfy.data.repository.MovieDetailRepository
import com.cheeke.surfy.data.repository.MovieDetailRepositoryImpl
import com.cheeke.surfy.data.repository.PagingRepository
import com.cheeke.surfy.data.repository.PagingRepositoryImpl
import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.data.repository.PeopleDataBaseRepositoryImpl
import com.cheeke.surfy.data.repository.PeopleDetailRepository
import com.cheeke.surfy.data.repository.PeopleDetailRepositoryImpl
import com.cheeke.surfy.data.repository.SeriesDetailRepository
import com.cheeke.surfy.data.repository.SeriesDetailRepositoryImpl
import com.cheeke.surfy.data.repository.SyncRepository
import com.cheeke.surfy.data.repository.SyncRepositoryImpl
import com.cheeke.surfy.data.repository.TvDataBaseRepository
import com.cheeke.surfy.data.repository.TvDataBaseRepositoryImpl
import com.cheeke.surfy.data.repository.TvDetailRepository
import com.cheeke.surfy.data.repository.TvDetailRepositoryImpl
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModules {
    @Binds
    abstract fun bindUserRepository(
        userDataRepository: UserDataRepositoryImpl
    ): UserDataRepository

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
    ): SeriesDetailRepository

    @Binds
    abstract fun bindMovieDatabaseRepository(
        databaseRepository: MovieDataBaseRepositoryImpl
    ): MovieDataBaseRepository

    @Binds
    abstract fun bindPeopleDatabaseRepository(
        databaseRepository: PeopleDataBaseRepositoryImpl
    ): PeopleDataBaseRepository

    @Binds
    abstract fun bindTvDatabaseRepository(
        databaseRepository: TvDataBaseRepositoryImpl
    ): TvDataBaseRepository

    @Binds
    abstract fun bindMainMenuRepository(
        mainMenuRepository: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    abstract fun bindPagingRepository(
        pagingRepository: PagingRepositoryImpl
    ): PagingRepository
}