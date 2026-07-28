package com.cheeke.surfy.testing.repository

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.database.model.KeywordEntity
import com.cheeke.surfy.search.api.KeywordDataBaseRepository
import com.cheeke.surfy.testing.model.keywordList
import kotlinx.coroutines.flow.MutableStateFlow

class TestKeywordDataBaseRepository : KeywordDataBaseRepository {
    val keywordDatabase = MutableStateFlow<List<KeywordEntity>>(value = emptyList())

    override fun getKeywords(): PagingSource<Int, KeywordEntity> =
        keywordList.asPagingSourceFactory().invoke()

    override suspend fun insert(keyword: String) {
        keywordDatabase.emit(value = keywordDatabase.value + KeywordEntity(id = 0, keyword = keyword, timestamp = 0))
    }

    override suspend fun deleteAll() {
        keywordDatabase.emit(value = emptyList())
    }

    override suspend fun delete(entity: KeywordEntity) {
        keywordDatabase.emit(value = keywordDatabase.value.filterNot { it.id == entity.id })
    }
}