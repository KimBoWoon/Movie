package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.dao.TvDao
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Tv
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.Instant
import javax.inject.Inject

class TvDataBaseRepositoryImpl @Inject constructor(
    private val tvDao: TvDao
) : TvDataBaseRepository {
    override fun isFavorite(id: Int): Observable<Boolean> = tvDao.isFavoriteTv(id = id)

    override fun insert(media: Media): Single<Long> =
        tvDao.insertOrIgnoreTvs(
            tv = TvEntity(
                id = media.id ?: -1,
                posterPath = media.posterPath ?: "",
                name = media.title ?: "",
                firstAirDate = media.firstAirDate ?: "",
                lastAirDate = media.lastAirDate,
                timestamp = Instant.now().toEpochMilli()
            )
        ).subscribeOn(Schedulers.io())

    override fun delete(media: Media): Completable {
        return media.id?.let { id ->
            tvDao.deleteTv(id = id).subscribeOn(Schedulers.io())
        } ?: Completable.complete()
    }

    override fun upsert(medias: List<Media>): Completable =
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
        ).subscribeOn(Schedulers.io())

    override fun getNextWeekReleaseTvs(): Single<List<Tv>> =
        tvDao.getNextWeekReleaseTvs().map { tvEntity ->
            tvEntity.map(transform = TvEntity::asExternalModel)
        }.subscribeOn(Schedulers.io())

    override fun getFavorite(): PagingSource<Int, TvEntity> =
        tvDao.getFavoriteTv()
}