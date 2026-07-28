package com.cheeke.surfy.sync.api

interface Synchronizer {
    suspend fun getVersion(): String
    suspend fun updateVersion(update: () -> String)
    suspend fun Syncable.sync(): Boolean = this@sync.syncWith(this@Synchronizer)
    fun getSyncInputData(): List<Pair<String, Any?>>
    suspend fun afterSync() {}
}

interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

interface SyncRepository : Syncable