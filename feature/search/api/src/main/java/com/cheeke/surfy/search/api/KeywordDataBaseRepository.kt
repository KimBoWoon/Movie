package com.cheeke.surfy.search.api

import androidx.paging.PagingSource
import com.cheeke.surfy.database.impl.model.KeywordEntity

interface KeywordDataBaseRepository {
    fun getKeywords(): PagingSource<Int, KeywordEntity>
    suspend fun insert(keyword: String)
    suspend fun deleteAll()
    suspend fun delete(entity: KeywordEntity)
}