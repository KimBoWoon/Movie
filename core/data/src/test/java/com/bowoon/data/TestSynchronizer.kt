package com.bowoon.data

import com.bowoon.data.util.Synchronizer
import com.bowoon.datastore.InternalDataSource
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
        datastore.updateMainDate(value = update())
    }

    override fun getIsForce(): Boolean = true
}