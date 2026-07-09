package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.model.Media
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.Instant
import javax.inject.Inject

class PeopleDataBaseRepositoryImpl @Inject constructor(
    private val peopleDao: PeopleDao
) : PeopleDataBaseRepository {
    override fun isFavorite(id: Int): Observable<Boolean> = peopleDao.isFavoritePeople(id = id)

    override fun insert(media: Media): Single<Long> =
        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = media.id ?: -1,
                timestamp = Instant.now().toEpochMilli(),
                name = media.title ?: "",
                profilePath = media.posterPath ?: ""
            )
        ).subscribeOn(Schedulers.io())

    override fun delete(media: Media): Completable {
        return media.id?.let { id ->
            peopleDao.deletePeople(id = id).subscribeOn(Schedulers.io())
        } ?: Completable.complete()
    }

    override fun upsert(medias: List<Media>): Completable =
        peopleDao.upsertPeoples(
            entities = medias.map { media ->
                PeopleEntity(
                    id = media.id ?: -1,
                    timestamp = Instant.now().toEpochMilli(),
                    name = media.title ?: "",
                    profilePath = media.posterPath ?: ""
                )
            }
        ).subscribeOn(Schedulers.io())

    override fun getFavorite(): PagingSource<Int, PeopleEntity> =
        peopleDao.getFavoritePeople()
}