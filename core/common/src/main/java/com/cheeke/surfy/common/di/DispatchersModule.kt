package com.cheeke.surfy.common.di

import com.cheeke.surfy.common.Dispatcher
import com.cheeke.surfy.common.Dispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(Dispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO

    @Provides
    @Dispatcher(Dispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default

    @Provides
    @Dispatcher(Dispatchers.Main)
    fun providesMainDispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main
}