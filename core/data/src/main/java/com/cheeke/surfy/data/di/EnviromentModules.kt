package com.cheeke.surfy.data.di

import com.cheeke.surfy.data.util.ConnectivityManagerNetworkMonitor
import com.cheeke.surfy.data.util.DataManager
import com.cheeke.surfy.data.util.NetworkMonitor
import com.cheeke.surfy.data.util.SurfyDataManager
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

    @Binds
    @Singleton
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}