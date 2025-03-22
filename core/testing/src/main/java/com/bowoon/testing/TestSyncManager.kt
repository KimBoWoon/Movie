package com.bowoon.testing

import android.content.Context
import com.bowoon.data.util.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestSyncManager : SyncManager {
    private val syncStatusFlow = MutableStateFlow(false)
    override val isSyncing: Flow<Boolean> = syncStatusFlow

    override fun myDataSync() {
        TODO("Not yet implemented")
    }

    override fun syncMain() {
        TODO("Not yet implemented")
    }

    override fun requestSync() {
        TODO("Not yet implemented")
    }

    override suspend fun checkWork(
        context: Context,
        onSuccess: suspend () -> Unit,
        onFailure: suspend () -> Unit
    ) {
        TODO("Not yet implemented")
    }

    fun testSync(isSync: Boolean) {
        syncStatusFlow.tryEmit(isSync)
    }
}