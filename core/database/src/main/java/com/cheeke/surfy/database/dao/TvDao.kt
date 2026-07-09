package com.cheeke.surfy.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.model.TvEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface TvDao {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM tvs WHERE id = :id)")
    fun isFavoriteTv(id: Int): Observable<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnoreTvs(tv: TvEntity): Single<Long>

    @Upsert
    fun upsertTvs(entities: List<TvEntity>): Completable

    @Query(value = "DELETE FROM tvs WHERE id = (:id)")
    fun deleteTv(id: Int): Completable

    @Query(value = "SELECT * FROM tvs WHERE firstAirDate BETWEEN DATE('now', 'localtime') AND DATE('now', '+7 day', 'localtime') ORDER BY firstAirDate ASC, name ASC")
    fun getNextWeekReleaseTvs(): Single<List<TvEntity>>

    @Query(value = "DELETE FROM tvs")
    fun deleteAllFavoriteTvs()

    @Query(value = "SELECT * FROM tvs ORDER BY timestamp DESC")
    fun getFavoriteTv(): PagingSource<Int, TvEntity>
}