package com.cheeke.surfy.data.testdouble

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.People
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestPeopleDao : PeopleDao {
    private val entitiesStateFlow = MutableStateFlow(emptyList<PeopleEntity>())

    override fun getPeopleEntities(): Flow<List<PeopleEntity>> = entitiesStateFlow

    override suspend fun insertOrIgnorePeoples(people: PeopleEntity): Long {
        entitiesStateFlow.update { oldValues ->
            (oldValues + people).distinctBy(PeopleEntity::id)
        }
        return people.id.toLong()
    }

    override suspend fun upsertPeoples(entities: List<PeopleEntity>) {
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(PeopleEntity::id) }
    }

    override suspend fun deletePeople(id: Int) {
        entitiesStateFlow.update { entities -> entities.filterNot { it.id == id } }
    }

    override fun isFavoritePeople(id: Int): Flow<Boolean> = entitiesStateFlow.map {
        it.find { entity -> entity.id == id } != null
    }.distinctUntilChanged()

    override fun getFavoritePeople(): PagingSource<Int, PeopleEntity> =
        (0 until 50).map { People(id = it) }.map(transform = People::asExternalModel).asPagingSourceFactory().invoke()
}