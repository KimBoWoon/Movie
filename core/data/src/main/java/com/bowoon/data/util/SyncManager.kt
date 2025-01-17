package com.bowoon.data.util

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Reports on if synchronization is in progress
 */
interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun initialize()
    fun requestSync()
    suspend fun checkWork(
        context: Context,
        onSuccess: suspend () -> Unit,
        onFailure: suspend () -> Unit
    )
}
