package com.cheeke.surfy.network.di

import com.cheeke.surfy.network.MovieRemoteDataSource
import com.cheeke.surfy.network.PeopleRemoteDataSource
import com.cheeke.surfy.network.SearchRemoteDataSource
import com.cheeke.surfy.network.SeriesRemoteDataSource
import com.cheeke.surfy.network.SettingRemoteDataSource
import com.cheeke.surfy.network.SyncRemoteDataSource
import com.cheeke.surfy.network.TrendingRemoteDataSource
import com.cheeke.surfy.network.TvRemoteDataSource
import com.cheeke.surfy.network.retrofit.MovieRemoteDataSourceImpl
import com.cheeke.surfy.network.retrofit.PeopleRemoteDataSourceImpl
import com.cheeke.surfy.network.retrofit.SearchRemoteDataSourceImpl
import com.cheeke.surfy.network.retrofit.SeriesRemoteDataSourceImpl
import com.cheeke.surfy.network.retrofit.SettingNetworkDataSourceImpl
import com.cheeke.surfy.network.retrofit.SyncRemoteDataSourceImpl
import com.cheeke.surfy.network.retrofit.TrendingRemoteDataSourceImpl
import com.cheeke.surfy.network.retrofit.TvRemoteDataSourceImpl
import com.cheeke.surfy.network.utils.ConnectivityManagerNetworkMonitor
import com.cheeke.surfy.network.utils.NetworkMonitor
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