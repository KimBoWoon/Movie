package com.bowoon.data.util

/**
 * Reports on if synchronization is in progress
 */
interface SyncManager {
    fun syncMain()
    fun requestSync()
}
