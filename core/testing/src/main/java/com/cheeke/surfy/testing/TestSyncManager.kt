package com.cheeke.surfy.testing

import com.cheeke.surfy.data.util.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow

class TestSyncManager : SyncManager {
    private val syncStatusFlow = MutableStateFlow(value = false)

    override fun syncMain() {
        TODO("Not yet implemented")
    }

    override fun requestSync() {
        TODO("Not yet implemented")
    }

    override fun updateWorker() {
        TODO("Not yet implemented")
    }

    fun testSync(isSync: Boolean) {
        syncStatusFlow.tryEmit(isSync)
    }
}