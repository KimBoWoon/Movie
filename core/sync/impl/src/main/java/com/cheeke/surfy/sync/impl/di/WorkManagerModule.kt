package com.cheeke.surfy.sync.impl.di

import com.cheeke.surfy.sync.api.SyncRepository
import com.cheeke.surfy.sync.impl.repository.SyncRepositoryImpl
import com.cheeke.surfy.sync.impl.status.WorkSyncManager
import com.cheeke.surfy.userdata.api.SyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(value = [SingletonComponent::class])
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