package com.cheeke.surfy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.cheeke.surfy.database.model.PeopleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeopleDao {
    @Query(value = "SELECT * FROM peoples")
    fun getPeopleEntities(): Flow<List<PeopleEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM peoples WHERE id = :id)")
    fun isFavoritePeople(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnorePeoples(people: PeopleEntity): Long

    @Upsert
    suspend fun upsertPeoples(entities: List<PeopleEntity>)

    @Query(value = "DELETE FROM peoples WHERE id = (:id)")
    suspend fun deletePeople(id: Int)
}