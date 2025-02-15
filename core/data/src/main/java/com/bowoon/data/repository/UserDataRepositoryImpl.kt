package com.bowoon.data.repository

import com.bowoon.data.util.SyncManager
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource,
    private val syncManager: SyncManager
) : UserDataRepository {
    override val internalData: Flow<InternalData> = datastore.userData

    override suspend fun updateUserData(
        userData: InternalData,
        isSync: Boolean
    ) {
        datastore.updateUserData(userData)
        if (isSync) syncManager.requestSync()
    }

    override suspend fun updateFCMToken(token: String) {
        datastore.updateFCMToken(token)
    }

    override suspend fun getFCMToken(): String = datastore.getFCMToken()
}