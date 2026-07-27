package com.cheeke.surfy.detail.impl.tv

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.cheeke.surfy.database.dao.TvDao
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.detail.api.tv.TvRepository
import com.cheeke.surfy.model.Tv
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class TvRepositoryImpl @Inject constructor(
    private val tvDao: TvDao
) : TvRepository {
    override suspend fun getNextWeekReleaseTvs(): List<Tv> =
        tvDao.getNextWeekReleaseTvs().map { it.asExternalModel() }

    override fun getFavorite(): Flow<PagingData<Tv>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            tvDao.getFavoriteTv()
        }.flow.map { pagingData -> pagingData.map(transform = TvEntity::asExternalModel) }

    override fun isFavorite(id: Int): Flow<Boolean> =
        tvDao.isFavoriteTv(id = id)

    override suspend fun insert(media: Tv): Long =
        tvDao.insertOrIgnoreTvs(
            tv = TvEntity(
                id = media.id ?: -1,
                name = media.title,
                posterPath = media.posterPath ?: "",
                timestamp = Instant.now().toEpochMilli(),
                firstAirDate = media.firstAirDate,
                lastAirDate = media.lastAirDate
            )
        )

    override suspend fun delete(media: Tv) {
        media.id?.let { id ->
            tvDao.deleteTv(id = id)
        }
    }

    override suspend fun upsert(medias: List<Tv>) {
        tvDao.upsertTvs(entities = medias.map(transform = Tv::asExternalModel))
    }
}