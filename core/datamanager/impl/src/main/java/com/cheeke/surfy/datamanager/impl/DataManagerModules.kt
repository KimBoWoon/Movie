package com.cheeke.surfy.datamanager.impl

import com.cheeke.surfy.datamanager.api.DataManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EnvironmentModules {
    @Binds
    @Singleton
    internal abstract fun bindsMovieAppData(
        movieAppData: SurfyDataManager
    ): DataManager
}