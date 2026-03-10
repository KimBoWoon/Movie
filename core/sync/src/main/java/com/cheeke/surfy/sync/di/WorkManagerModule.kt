package com.cheeke.surfy.sync.di

import com.cheeke.surfy.data.util.SyncManager
import com.cheeke.surfy.sync.status.WorkSyncManager
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