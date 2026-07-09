package com.cheeke.surfy.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.model.PeopleEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface PeopleDao {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM peoples WHERE id = :id)")
    fun isFavoritePeople(id: Int): Observable<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOrIgnorePeoples(people: PeopleEntity): Single<Long>

    @Upsert
    fun upsertPeoples(entities: List<PeopleEntity>): Completable

    @Query(value = "DELETE FROM peoples WHERE id = (:id)")
    fun deletePeople(id: Int): Completable

    @Query(value = "SELECT * FROM peoples ORDER BY timestamp DESC")
    fun getFavoritePeople(): PagingSource<Int, PeopleEntity>
}