package com.bowoon.data.util

import com.bowoon.common.Log
import kotlin.coroutines.cancellation.CancellationException

interface Syncable {
    suspend fun syncWith(isForce: Boolean = false): Boolean
}

internal suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.printStackTrace(exception)
    Result.failure(exception)
}