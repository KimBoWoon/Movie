package com.bowoon.data.util

import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

interface Syncable {
    suspend fun syncWith(isForce: Boolean = false): Boolean
}

internal suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}