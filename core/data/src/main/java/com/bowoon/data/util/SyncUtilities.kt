package com.bowoon.data.util

import com.bowoon.common.Log
import kotlin.coroutines.cancellation.CancellationException

interface Synchronizer {
    suspend fun getChangeListVersions(): String
    suspend fun updateChangeListVersions(update: () -> String)
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
    fun getIsForce(): Boolean
}

interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

internal suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.printStackTrace(exception)
    Result.failure(exception)
}