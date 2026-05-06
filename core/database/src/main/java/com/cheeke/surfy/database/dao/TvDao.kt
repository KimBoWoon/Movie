package com.cheeke.surfy.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.model.TvEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM tvs WHERE id = :id)")
    fun isFavoriteTv(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTvs(tv: TvEntity): Long

    @Upsert
    suspend fun upsertTvs(entities: List<TvEntity>)

    @Query(value = "DELETE FROM tvs WHERE id = (:id)")
    suspend fun deleteTv(id: Int)

    @Query(value = "SELECT * FROM tvs WHERE firstAirDate BETWEEN DATE('now', 'localtime') AND DATE('now', '+7 day', 'localtime') ORDER BY firstAirDate ASC, name ASC")
    suspend fun getNextWeekReleaseTvs(): List<TvEntity>

    @Query(value = "DELETE FROM tvs")
    fun deleteAllFavoriteTvs()

    @Query(value = "SELECT * FROM tvs ORDER BY timestamp DESC")
    fun getFavoriteTv(): PagingSource<Int, TvEntity>
}