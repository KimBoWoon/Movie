package com.cheeke.surfy.userdata.api

/**
 * Reports on if synchronization is in progress
 */
interface SyncManager {
    fun syncMain()
    fun requestSync()
    fun updateWorker()
}