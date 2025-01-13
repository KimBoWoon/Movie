package com.bowoon.data.util

import kotlinx.coroutines.flow.Flow

/**
 * Reports on if synchronization is in progress
 */
interface SyncManager {
    val isSyncing: Flow<Boolean>
    fun requestSync()
}
