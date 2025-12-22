package com.bowoon.datastore_test

import androidx.datastore.core.DataStore
import com.bowoon.datastore.di.DataStoreModule
import com.bowoon.datastore.protobuf.InternalDataPreferencesSerializer
import com.bowoon.movie.core.datastore.InternalDataPreferences
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