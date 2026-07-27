package com.cheeke.surfy.detail.api

import androidx.paging.PagingData
import com.cheeke.surfy.model.Media
import kotlinx.coroutines.flow.Flow

interface DataBaseRepository<T : Media> {
    fun getFavorite(): Flow<PagingData<T>>
    fun isFavorite(id: Int): Flow<Boolean>
    suspend fun insert(media: T): Long
    suspend fun delete(media: T)
    suspend fun upsert(medias: List<T>)
}