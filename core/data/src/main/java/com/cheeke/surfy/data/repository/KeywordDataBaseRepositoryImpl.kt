package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.dao.KeywordDao
import com.cheeke.surfy.database.model.KeywordEntity
import io.reactivex.rxjava3.core.Completable
import jakarta.inject.Inject
import kotlin.time.Clock

interface KeywordDataBaseRepository {
    fun getKeywords(): PagingSource<Int, KeywordEntity>
    fun insert(keyword: String): Completable
    fun deleteAll(): Completable
    fun delete(entity: KeywordEntity): Completable
}

class KeywordDataBaseRepositoryImpl @Inject constructor(
    private val keywordDao: KeywordDao
) : KeywordDataBaseRepository {
    override fun getKeywords(): PagingSource<Int, KeywordEntity> = keywordDao.getKeywords()
    override fun insert(keyword: String): Completable =
        keywordDao.insertOrReplaceKeyword(
            entity = KeywordEntity(
                keyword = keyword,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )

    override fun deleteAll(): Completable = keywordDao.deleteAll()

    override fun delete(entity: KeywordEntity): Completable = keywordDao.delete(id = entity.id)
}