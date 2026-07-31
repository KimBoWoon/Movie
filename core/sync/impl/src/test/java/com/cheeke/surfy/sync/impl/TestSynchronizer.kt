package com.cheeke.surfy.sync.impl

import com.cheeke.surfy.sync.api.Synchronizer
import com.cheeke.surfy.userdata.api.TestUserDataRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Test synchronizer that delegates to DataStore
 */
class TestSynchronizer : Synchronizer {
    private val userData = TestUserDataRepository()

    override suspend fun getVersion(): String =
        userData.internalData.map { it.updateDate }.firstOrNull().orEmpty()

    override suspend fun updateVersion(update: () -> String) {
        userData.updateMainDate(value = update())
    }

    override fun getSyncInputData(): List<Pair<String, Any?>> = mutableListOf(
        Pair(
            first = "IS_FORCE",
            second = true
        )
    )
}