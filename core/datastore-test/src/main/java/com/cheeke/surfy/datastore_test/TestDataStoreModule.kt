package com.cheeke.surfy.datastore_test

import androidx.datastore.core.DataStore
import com.cheeke.surfy.core.datastore.InternalDataPreferences
import com.cheeke.surfy.datastore.di.DataStoreModule
import com.cheeke.surfy.datastore.protobuf.InternalDataPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
internal object TestDataStoreModule {
    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        serializer: InternalDataPreferencesSerializer
    ): DataStore<InternalDataPreferences> =
        InMemoryDataStore(initialValue = serializer.defaultValue)
}