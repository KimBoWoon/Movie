package com.cheeke.surfy.testing.repository

import androidx.paging.PagingSource
import androidx.paging.testing.asPagingSourceFactory
import com.cheeke.surfy.data.repository.TvDataBaseRepository
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TestTvDatabaseRepository : TvDataBaseRepository {
    val tvDatabase = MutableSharedFlow<List<Tv>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val currentTvDatabase get() = tvDatabase.replayCache.firstOrNull() ?: emptyList()

    override fun getFavorite(): PagingSource<Int, TvEntity> =
        currentTvDatabase
            .map(transform = Tv::asExternalModel)
            .asPagingSourceFactory()
            .invoke()

    override fun isFavorite(id: Int): Flow<Boolean> =
        tvDatabase
            .map { tvs ->
                tvs.firstOrNull { it.id == id } != null
            }.distinctUntilChanged()

    override suspend fun insert(media: Media): Long {
        tvDatabase.emit(value = currentTvDatabase + (media as Tv))
        return media.id?.toLong() ?: throw RuntimeException("room database insert failed...")
    }

    override suspend fun delete(media: Media) {
        tvDatabase.emit(value = currentTvDatabase.filter { it.id != media.id })
    }

    override suspend fun upsert(medias: List<Media>) {
        tvDatabase.emit(
            value = (currentTvDatabase + (medias as List<Tv>)).map { tv ->
                medias.find { it.id == tv.id } ?: tv
            }
        )
    }

    override suspend fun getNextWeekReleaseTvs(): List<Tv> {
        val now = LocalDate.now()
        val nextWeekReleaseMovies = currentTvDatabase.filter { movie ->
            !movie.releaseDate?.trim().isNullOrEmpty() && LocalDate.parse(movie.releaseDate ?: "") in (now..now.plusDays(7))
        }
        tvDatabase.tryEmit(value = nextWeekReleaseMovies)
        return tvDatabase.first()
    }
}