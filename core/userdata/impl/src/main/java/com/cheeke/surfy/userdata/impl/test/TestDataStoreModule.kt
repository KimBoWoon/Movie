package com.cheeke.surfy.userdata.impl.test

import androidx.datastore.core.DataStore
import com.cheeke.surfy.core.userdata.InternalDataPreferences
import com.cheeke.surfy.userdata.impl.DataStoreModule
import com.cheeke.surfy.userdata.impl.InternalDataPreferencesSerializer
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