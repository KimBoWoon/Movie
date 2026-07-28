package com.cheeke.surfy.sync.impl.utils

import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.sync.api.Synchronizer
import kotlin.coroutines.cancellation.CancellationException

internal suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(value = block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.printStackTrace(tr = exception)
    Result.failure(exception = exception)
}

suspend fun Synchronizer.updateMovieSync(
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
        (getSyncInputData().firstOrNull { it.first == "IS_FORCE" }?.second as Boolean).let { forceUpdate ->
            if (!forceUpdate) afterSync()
        }
        updateVersion(update = { versionUpdater() })
    } else {
        Log.d("changeListSync -> update not necessary")
    }
}.isSuccess

suspend fun Synchronizer.changeListSync(
    getList: suspend () -> List<Movie>,
    versionUpdater: () -> String,
    modelDeleter: suspend () -> Unit,
    modelUpdater: suspend (List<Movie>) -> Unit,
): Boolean = suspendRunCatching {
    val updateList = getList()
    modelDeleter()
    modelUpdater(updateList)
    afterSync()
    updateVersion(update = { versionUpdater() })
}.isSuccess