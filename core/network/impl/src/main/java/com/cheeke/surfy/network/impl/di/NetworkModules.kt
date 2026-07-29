package com.cheeke.surfy.network.impl.di

import com.cheeke.surfy.network.api.MovieRemoteDataSource
import com.cheeke.surfy.network.api.NetworkMonitor
import com.cheeke.surfy.network.api.PeopleRemoteDataSource
import com.cheeke.surfy.network.api.SearchRemoteDataSource
import com.cheeke.surfy.network.api.SeriesRemoteDataSource
import com.cheeke.surfy.network.api.SettingRemoteDataSource
import com.cheeke.surfy.network.api.SyncRemoteDataSource
import com.cheeke.surfy.network.api.TrendingRemoteDataSource
import com.cheeke.surfy.network.api.TvRemoteDataSource
import com.cheeke.surfy.network.impl.retrofit.MovieRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.PeopleRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.SearchRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.SeriesRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.SettingNetworkDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.SyncRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.TrendingRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.retrofit.TvRemoteDataSourceImpl
import com.cheeke.surfy.network.impl.utils.ConnectivityManagerNetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModules {
    @Binds
    abstract fun bindSettingApis(apis: SettingNetworkDataSourceImpl): SettingRemoteDataSource

    @Binds
    abstract fun bindSearchApis(apis: SearchRemoteDataSourceImpl): SearchRemoteDataSource

    @Binds
    abstract fun bindMovieApis(apis: MovieRemoteDataSourceImpl): MovieRemoteDataSource

    @Binds
    abstract fun bindPeopleApis(apis: PeopleRemoteDataSourceImpl): PeopleRemoteDataSource

    @Binds
    abstract fun bindTvApis(apis: TvRemoteDataSourceImpl): TvRemoteDataSource

    @Binds
    abstract fun bindSeriesApis(apis: SeriesRemoteDataSourceImpl): SeriesRemoteDataSource

    @Binds
    abstract fun bindSyncApis(apis: SyncRemoteDataSourceImpl): SyncRemoteDataSource

    @Binds
    abstract fun bindTrendingApis(apis: TrendingRemoteDataSourceImpl): TrendingRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class EnvironmentModules {
    @Binds
    @Singleton
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}