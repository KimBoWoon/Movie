package com.bowoon.sync.di

import com.bowoon.data.util.SyncManager
import com.bowoon.sync.status.WorkSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkSyncManager,
    ): SyncManager
}