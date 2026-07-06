package com.cheeke.surfy.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.model.PeopleEntity
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PeopleDao {
    @Query(value = "SELECT EXISTS(SELECT 1 FROM peoples WHERE id = :id)")
    fun isFavoritePeople(id: Int): Flowable<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePeoples(people: PeopleEntity): Long

    @Upsert
    suspend fun upsertPeoples(entities: List<PeopleEntity>)

    @Query(value = "DELETE FROM peoples WHERE id = (:id)")
    suspend fun deletePeople(id: Int)

    @Query(value = "SELECT * FROM peoples ORDER BY timestamp DESC")
    fun getFavoritePeople(): PagingSource<Int, PeopleEntity>
}