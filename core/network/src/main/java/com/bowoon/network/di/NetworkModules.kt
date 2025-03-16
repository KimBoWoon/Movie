package com.bowoon.network.di

import com.bowoon.network.MovieNetworkDataSource
import com.bowoon.network.retrofit.RetrofitMovieNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModules {
    @Binds
    abstract fun bindApis(apis: RetrofitMovieNetwork): MovieNetworkDataSource
}