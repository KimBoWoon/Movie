package com.bowoon.database.di

import com.bowoon.database.MovieDatabase
import com.bowoon.database.dao.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesMovieDao(
        database: MovieDatabase,
    ): MovieDao = database.movieDao()
}
