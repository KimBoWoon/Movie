package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.dao.TvDao
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class TvDataBaseRepositoryImpl @Inject constructor(
    private val tvDao: TvDao
) : TvDataBaseRepository {
    override fun isFavorite(id: Int): Flow<Boolean> = tvDao.isFavoriteTv(id = id)

    override suspend fun insert(media: Media): Long =
        tvDao.insertOrIgnoreTvs(
            tv = TvEntity(
                id = media.id ?: -1,
                posterPath = media.posterPath ?: "",
                name = media.title ?: "",
                firstAirDate = media.firstAirDate ?: "",
                lastAirDate = media.lastAirDate,
                timestamp = Instant.now().toEpochMilli()
            )
        )

    override suspend fun delete(media: Media) {
        media.id?.let { id ->
            tvDao.deleteTv(id = id)
        }
    }

    override suspend fun upsert(medias: List<Media>) {
        tvDao.upsertTvs(
            entities = medias.map { media ->
                TvEntity(
                    id = media.id ?: -1,
                    posterPath = media.posterPath ?: "",
                    name = media.title ?: "",
                    firstAirDate = media.firstAirDate ?: "",
                    lastAirDate = media.lastAirDate ?: "",
                    timestamp = Instant.now().toEpochMilli()
                )
            }
        )
    }

    override suspend fun getNextWeekReleaseTvs(): List<Tv> =
        tvDao.getNextWeekReleaseTvs().map { tvEntity ->
            tvEntity.asExternalModel()
        }

    override fun getFavorite(): PagingSource<Int, TvEntity> =
        tvDao.getFavoriteTv()
}