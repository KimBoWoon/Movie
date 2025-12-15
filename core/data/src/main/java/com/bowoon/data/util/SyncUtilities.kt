package com.bowoon.data.util

import com.bowoon.common.Log
import com.bowoon.model.Movie
import kotlin.coroutines.cancellation.CancellationException

interface Synchronizer {
    suspend fun getVersion(): String
    suspend fun updateVersion(update: () -> String)
    suspend fun Syncable.sync(): Boolean = this@sync.syncWith(this@Synchronizer)
    fun getIsForce(): Boolean
    suspend fun afterSync() {}
}

interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

internal suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(value = block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.printStackTrace(tr = exception)
    Result.failure(exception = exception)
}

suspend fun Synchronizer.changeListSync(
    updateChecker: suspend Synchronizer.() -> Boolean,
    getList: suspend () -> List<Movie>,
    versionUpdater: () -> String,
    modelDeleter: suspend () -> Unit,
    modelUpdater: suspend (List<Movie>) -> Unit,
): Boolean = suspendRunCatching {
    if (updateChecker()) {
        val updateList = getList()
        Log.d("changeListSync -> $updateList")
        modelDeleter()
        modelUpdater(updateList)
        if (!getIsForce()) afterSync()
        updateVersion(update = { versionUpdater() })
    } else {
        Log.d("changeListSync -> update not necessary")
    }
}.isSuccess