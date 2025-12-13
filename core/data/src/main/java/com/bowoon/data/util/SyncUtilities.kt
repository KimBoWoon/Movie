package com.bowoon.data.util

import com.bowoon.common.Log
import com.bowoon.model.Movie
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.cancellation.CancellationException

interface Synchronizer {
    suspend fun getChangeListVersions(): String
    suspend fun updateChangeListVersions(update: () -> String)
    suspend fun Syncable.sync(): Boolean = this@sync.syncWith(this@Synchronizer)
    fun getIsForce(): Boolean
    suspend fun afterUpdate() {}
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

suspend fun Synchronizer.changeMainSync(
    updateChecker: suspend Synchronizer.() -> Boolean,
    nowPlayingMovies: suspend () -> List<Movie>,
    upComingMovies: suspend () -> List<Movie>,
    versionUpdater: () -> String,
    modelDeleter: suspend () -> Unit,
    modelUpdater: suspend (List<List<Movie>>) -> Unit,
): Boolean = suspendRunCatching {
    if (updateChecker()) {
        val updateMovies = coroutineScope {
            listOf(
                async { nowPlayingMovies() },
                async { upComingMovies() }
            ).awaitAll()
        }
        Log.d("changeListSync -> $updateMovies")
        modelDeleter()
        modelUpdater(updateMovies)
        if (!getIsForce()) afterUpdate()
        updateChangeListVersions(update = { versionUpdater() })
    } else {
        Log.d("changeListSync -> else")
    }
}.isSuccess

suspend fun Synchronizer.changeListSync(
    updateChecker: suspend Synchronizer.() -> Boolean,
    getList: suspend () -> List<Movie>,
    versionUpdater: () -> String,
    modelDeleter: suspend () -> Unit,
    modelUpdater: suspend (List<Movie>) -> Unit,
): Boolean = suspendRunCatching {
    if (updateChecker()) {
        val updateMovies = getList()
        Log.d("changeListSync -> $updateMovies")
        modelDeleter()
        modelUpdater(updateMovies)
        updateChangeListVersions(update = { versionUpdater() })
    } else {
        Log.d("changeListSync -> else")
    }
}.isSuccess