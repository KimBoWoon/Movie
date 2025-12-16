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
    override suspend fun getVersion(): String =
        datastore.userData.map { it.updateDate }.firstOrNull() ?: ""

    override suspend fun updateVersion(update: () -> String) {
        datastore.updateMainDate(value = update())
    }

    override fun getSyncInputData(): List<Pair<String, Any?>> = mutableListOf(
        Pair(
            first = "IS_FORCE",
            second = true
        )
    )
}