package com.cheeke.surfy.data.util

/**
 * Reports on if synchronization is in progress
 */
interface SyncManager {
    fun syncMain()
    fun requestSync()
    fun updateWorker()
}