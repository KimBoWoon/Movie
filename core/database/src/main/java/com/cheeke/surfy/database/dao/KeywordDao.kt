package com.cheeke.surfy.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cheeke.surfy.database.model.KeywordEntity
import io.reactivex.rxjava3.core.Completable

@Dao
interface KeywordDao {
    @Query(value = "SELECT * FROM recentlyKeyword ORDER BY timestamp DESC LIMIT 10")
    fun getKeywords(): PagingSource<Int, KeywordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceKeyword(entity: KeywordEntity): Completable

    @Query(value = "DELETE FROM recentlyKeyword")
    fun deleteAll(): Completable

    @Query(value = "DELETE FROM recentlyKeyword WHERE id = :id")
    fun delete(id: Int): Completable
}