package com.bowoon.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bowoon.database.model.TvEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {
    @Query(value = "SELECT * FROM tvs")
    fun getTvEntities(): Flow<List<TvEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM tvs WHERE id = :id)")
    fun isFavoriteTv(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTvs(tv: TvEntity): Long

    @Upsert
    suspend fun upsertTvs(entities: List<TvEntity>)

    @Query(value = "DELETE FROM tvs WHERE id = (:id)")
    suspend fun deleteTv(id: Int)

    @Query(value = "SELECT * FROM tvs WHERE firstAirDate BETWEEN DATE('now', 'localtime') AND DATE('now', '+7 day', 'localtime') ORDER BY firstAirDate ASC, name ASC")
    fun getNextWeekReleaseTvs(): Flow<List<TvEntity>>

    @Query(value = "DELETE FROM tvs")
    fun deleteAllFavoriteTvs()
}