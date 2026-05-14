package com.cheeke.surfy.testing.repository

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.People
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class TestPeopleDatabaseRepository : PeopleDataBaseRepository {
    val peopleDatabase = MutableSharedFlow<List<People>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentPeopleDatabase get() = peopleDatabase.replayCache.firstOrNull() ?: emptyList()

    override fun getFavorite(): PagingSource<Int, PeopleEntity> =
        currentPeopleDatabase
            .map(transform = People::asExternalModel)
            .asPagingSourceFactory()
            .invoke()

    override fun isFavorite(id: Int): Flow<Boolean> =
        peopleDatabase
            .map { peoples ->
                peoples.firstOrNull { it.id == id } != null
            }.distinctUntilChanged()

    override suspend fun insert(media: Media): Long {
        peopleDatabase.emit(value = currentPeopleDatabase + (media as People))
        return media.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun delete(media: Media) {
        peopleDatabase.emit(value = currentPeopleDatabase.filter { it.id != media.id })
    }

    override suspend fun upsert(medias: List<Media>) {
        peopleDatabase.emit(
            value = (currentPeopleDatabase + (medias as List<People>)).map { people ->
                medias.find { it.id == people.id } ?: people
            }
        )
    }
}