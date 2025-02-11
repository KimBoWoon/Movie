package com.bowoon.data.repository

import com.bowoon.data.util.SyncManager
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val datastore: InternalDataSource,
    private val syncManager: SyncManager
) : UserDataRepository {
    override val userData: Flow<UserData> = datastore.userData

    override suspend fun updateUserData(
        userData: UserData,
        isSync: Boolean
    ) {
        datastore.updateUserData(userData)
        if (isSync) syncManager.requestSync()
    }

    override suspend fun updateFCMToken(token: String) {
        datastore.updateFCMToken(token)
    }

    override suspend fun isAdult(): Boolean = datastore.getUserData().isAdult

    override suspend fun isAutoPlayTrailer(): Boolean = datastore.getUserData().autoPlayTrailer

    override suspend fun getMainOfDate(): String = datastore.getUserData().updateDate

    override suspend fun getRegion(): String = datastore.getUserData().region

    override suspend fun getLanguage(): String = datastore.getUserData().language

    override suspend fun getImageQuality(): String = datastore.getUserData().imageQuality

    override suspend fun getFCMToken(): String = datastore.getFCMToken()
}