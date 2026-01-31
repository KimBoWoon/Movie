package com.bowoon.network.di

import com.bowoon.network.MovieNetworkDataSource
import com.bowoon.network.MovieRxNetworkDataSource
import com.bowoon.network.retrofit.RetrofitMovieNetwork
import com.bowoon.network.retrofit.RxMovieNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CoroutineNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RxNetwork

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModules {
    @CoroutineNetwork
    @Binds
    abstract fun bindApis(apis: RetrofitMovieNetwork): MovieNetworkDataSource

    @RxNetwork
    @Binds
    abstract fun bindRxApis(apis: RxMovieNetwork): MovieRxNetworkDataSource
}