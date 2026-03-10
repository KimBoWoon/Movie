package com.cheeke.surfy.network.di

import com.cheeke.surfy.network.MovieNetworkDataSource
import com.cheeke.surfy.network.retrofit.RetrofitMovieNetwork
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