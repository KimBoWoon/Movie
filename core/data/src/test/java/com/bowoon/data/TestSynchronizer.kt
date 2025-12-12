package com.bowoon.data

import com.bowoon.data.util.Synchronizer
import com.bowoon.datastore.InternalDataSource
import com.bowoon.model.InternalData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Test synchronizer that delegates to DataStore
 */
class TestSynchronizer(
    private val datastore: InternalDataSource
) : Synchronizer {
    override suspend fun getChangeListVersions(): String =
        datastore.userData.map { it.updateDate }.firstOrNull() ?: ""

    override suspend fun updateChangeListVersions(update: () -> String) {
        datastore.updateUserData(userData = datastore.userData.map { it.copy(updateDate = update()) }.firstOrNull() ?: InternalData())
    }

    override fun getIsForce(): Boolean = true
}