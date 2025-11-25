package com.bowoon.testing

import com.bowoon.data.util.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow

class TestSyncManager : SyncManager {
    private val syncStatusFlow = MutableStateFlow(false)

    override fun syncMain() {
        TODO("Not yet implemented")
    }

    override fun requestSync() {
        TODO("Not yet implemented")
    }

    fun testSync(isSync: Boolean) {
        syncStatusFlow.tryEmit(isSync)
    }
}