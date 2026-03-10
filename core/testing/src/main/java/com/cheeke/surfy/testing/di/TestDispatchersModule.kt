package com.cheeke.surfy.testing.di

import com.cheeke.surfy.common.Dispatcher
import com.cheeke.surfy.common.Dispatchers
import com.cheeke.surfy.common.di.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatchersModule::class],
)
internal object TestDispatchersModule {
    @Provides
    @Singleton
    fun providesTestDispatcher(): TestDispatcher = UnconfinedTestDispatcher()

    @Provides
    @Dispatcher(Dispatchers.IO)
    fun providesIODispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @Dispatcher(Dispatchers.Default)
    fun providesDefaultDispatcher(
        testDispatcher: TestDispatcher,
    ): CoroutineDispatcher = testDispatcher
}
