package com.bowoon.data.testdouble

import com.bowoon.database.dao.PeopleDao
import com.bowoon.database.model.PeopleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
}