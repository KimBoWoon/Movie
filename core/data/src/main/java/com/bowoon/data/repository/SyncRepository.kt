package com.bowoon.data.repository

interface SyncRepository {
    suspend fun syncWith(isForce: Boolean): Boolean
}