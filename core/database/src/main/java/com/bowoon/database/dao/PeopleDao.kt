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
    @Query(value = "SELECT * FROM peoples")
    fun getPeopleEntities(): Flow<List<PeopleEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePeoples(people: PeopleEntity): Long

    @Upsert
    suspend fun upsertPeoples(entities: List<PeopleEntity>)

    @Query(value = "DELETE FROM peoples WHERE id = (:id)")
    suspend fun deletePeople(id: Int)
}