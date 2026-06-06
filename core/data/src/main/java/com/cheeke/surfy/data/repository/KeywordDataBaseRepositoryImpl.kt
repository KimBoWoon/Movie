package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.dao.KeywordDao
import com.cheeke.surfy.database.model.KeywordEntity
import jakarta.inject.Inject
import kotlin.time.Clock

interface KeywordDataBaseRepository {
    fun getKeywords(): PagingSource<Int, KeywordEntity>
    suspend fun insert(keyword: String)
    suspend fun deleteAll()
    suspend fun delete(entity: KeywordEntity)
}

class KeywordDataBaseRepositoryImpl @Inject constructor(
    private val keywordDao: KeywordDao
) : KeywordDataBaseRepository {
    override fun getKeywords(): PagingSource<Int, KeywordEntity> = keywordDao.getKeywords()
    override suspend fun insert(keyword: String) {
        keywordDao.insertOrReplaceKeyword(
            entity = KeywordEntity(
                keyword = keyword,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    override suspend fun deleteAll() {
        keywordDao.deleteAll()
    }

    override suspend fun delete(entity: KeywordEntity) {
        keywordDao.delete(id = entity.id)
    }
}