package com.cheeke.surfy.sync.impl

import com.cheeke.surfy.datastore.InternalDataSource
import com.cheeke.surfy.sync.repository.Synchronizer
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Test synchronizer that delegates to DataStore
 */
class TestSynchronizer(
    private val datastore: InternalDataSource
) : Synchronizer {
    override suspend fun getVersion(): String =
        datastore.userData.map { it.updateDate }.firstOrNull().orEmpty()

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