package com.bowoon.data.di

import com.bowoon.data.util.ApplicationData
import com.bowoon.data.util.MovieAppDataManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MovieAppDataModules {
    @Binds
    @Singleton
    internal abstract fun bindsMovieAppData(
        networkMonitor: MovieAppDataManager,
    ): ApplicationData
}