package com.bowoon.data.testdouble

import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.PeopleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class TestPeopleDao : PeopleDao {
    private val entitiesStateFlow = MutableStateFlow(emptyList<PeopleEntity>())

    override fun getPeopleEntity(peopleId: Int): Flow<PeopleEntity> =
        entitiesStateFlow.mapNotNull { movieEntities ->
            movieEntities.find { people -> people.id == peopleId }
        }

    override fun getPeopleEntities(): Flow<List<PeopleEntity>> =
        entitiesStateFlow

    override fun getPeopleEntities(ids: Set<Int>): Flow<List<PeopleEntity>> =
        entitiesStateFlow.map { peopleEntities ->
            peopleEntities.filter { it.id in ids }
        }

    override suspend fun insertOrIgnorePeoples(people: PeopleEntity): Long {
        entitiesStateFlow.update { oldValues ->
            (oldValues + people).distinctBy(PeopleEntity::id)
        }
        return people.id.toLong()
    }

    override suspend fun upsertPeoples(entities: List<PeopleEntity>) {
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(PeopleEntity::id) }
    }

    override suspend fun deletePeoples(ids: List<Int>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities -> entities.filterNot { it.id in idSet } }
    }

    override suspend fun deletePeople(id: Int) {
        entitiesStateFlow.update { entities -> entities.filterNot { it.id == id } }
    }
}