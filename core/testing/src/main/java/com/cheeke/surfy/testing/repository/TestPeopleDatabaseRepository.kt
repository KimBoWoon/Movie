package com.cheeke.surfy.testing.repository

import androidx.paging.PagingData
import com.cheeke.surfy.detail.api.people.PeopleRepository
import com.cheeke.surfy.model.People
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TestPeopleDatabaseRepository : PeopleRepository {
    val peopleDatabase = MutableSharedFlow<List<People>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentPeopleDatabase get() = peopleDatabase.replayCache.firstOrNull().orEmpty()

    override fun getFavorite(): Flow<PagingData<People>> =
        flow { emit(value = PagingData.from(data = currentPeopleDatabase)) }

    override fun isFavorite(id: Int): Flow<Boolean> =
        peopleDatabase
            .map { peoples ->
                peoples.firstOrNull { it.id == id } != null
            }.distinctUntilChanged()

    override suspend fun insert(media: People): Long {
        peopleDatabase.emit(value = currentPeopleDatabase + media)
        return media.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun delete(media: People) {
        peopleDatabase.emit(value = currentPeopleDatabase.filter { it.id != media.id })
    }

    override suspend fun upsert(medias: List<People>) {
        peopleDatabase.emit(
            value = (currentPeopleDatabase + medias).map { people ->
                medias.find { it.id == people.id } ?: people
            }
        )
    }
}