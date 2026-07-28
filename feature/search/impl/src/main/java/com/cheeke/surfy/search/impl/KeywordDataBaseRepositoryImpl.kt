package com.cheeke.surfy.search.impl

import androidx.paging.PagingSource
import com.cheeke.surfy.database.impl.dao.KeywordDao
import com.cheeke.surfy.database.impl.model.KeywordEntity
import com.cheeke.surfy.search.api.KeywordDataBaseRepository
import jakarta.inject.Inject
import kotlin.time.Clock

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