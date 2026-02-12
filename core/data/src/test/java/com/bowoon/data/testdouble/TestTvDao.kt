package com.bowoon.data.testdouble

import com.bowoon.database.dao.TvDao
import com.bowoon.database.model.TvEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class TestTvDao : TvDao {
    private val entitiesStateFlow = MutableStateFlow(value = emptyList<TvEntity>())

    override fun getTvEntities(): Flow<List<TvEntity>> = entitiesStateFlow

    override fun isFavoriteTv(id: Int): Flow<Boolean> = entitiesStateFlow.map {
        it.find { entity -> entity.id == id } != null
    }.distinctUntilChanged()

    override suspend fun insertOrIgnoreTvs(tv: TvEntity): Long {
        entitiesStateFlow.update { oldValues ->
            (oldValues + tv).distinctBy(TvEntity::id)
        }
        return tv.id.toLong()
    }

    override suspend fun upsertTvs(entities: List<TvEntity>) {
        entitiesStateFlow.update { oldValues -> (entities + oldValues).distinctBy(TvEntity::id) }
    }

    override suspend fun deleteTv(id: Int) {
        entitiesStateFlow.update { entities -> entities.filterNot { it.id == id } }
    }

    override fun getNextWeekReleaseTvs(): Flow<List<TvEntity>> = entitiesStateFlow.map { favoriteTvList ->
        favoriteTvList.filter {
            LocalDate.parse(it.firstAirDate) in LocalDate.now()..LocalDate.now().plusDays(7)
        }.sortedWith(compareBy({ it.firstAirDate }, { it.name }))
    }

    override fun deleteAllFavoriteTvs() {
        entitiesStateFlow.tryEmit(value = emptyList())
    }
}