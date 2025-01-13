package com.bowoon.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.bowoon.database.model.PeopleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeopleDao {
    @Query(value = " SELECT * FROM peoples WHERE id = (:peopleId)")
    fun getPeopleEntity(peopleId: Int): Flow<PeopleEntity>

    @Query(value = "SELECT * FROM peoples")
    fun getPeopleEntities(): Flow<List<PeopleEntity>>

    @Query(value = " SELECT * FROM peoples WHERE id IN (:ids)")
    fun getPeopleEntities(ids: Set<Int>): Flow<List<PeopleEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePeoples(people: PeopleEntity): Long

    @Upsert
    suspend fun upsertPeoples(entities: List<PeopleEntity>)

    @Query(value = "DELETE FROM peoples WHERE id in (:ids)")
    suspend fun deletePeoples(ids: List<Int>)

    @Query(value = "DELETE FROM peoples WHERE id = (:id)")
    suspend fun deletePeople(id: Int)
}