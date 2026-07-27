package com.cheeke.surfy.sync.di

import com.cheeke.surfy.sync.repository.SyncRepository
import com.cheeke.surfy.sync.repository.SyncRepositoryImpl
import com.cheeke.surfy.sync.status.WorkSyncManager
import com.cheeke.surfy.userdata.api.SyncManager
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

    @Binds
    abstract fun bindSyncRepository(
        mainMenuRepository: SyncRepositoryImpl
    ): SyncRepository
}