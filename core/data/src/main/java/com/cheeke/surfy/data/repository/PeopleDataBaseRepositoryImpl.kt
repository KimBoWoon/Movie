package com.cheeke.surfy.data.repository

import androidx.paging.PagingSource
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.model.Media
import io.reactivex.rxjava3.core.Flowable
import java.time.Instant
import javax.inject.Inject

class PeopleDataBaseRepositoryImpl @Inject constructor(
    private val peopleDao: PeopleDao
) : PeopleDataBaseRepository {
    override fun isFavorite(id: Int): Flowable<Boolean> = peopleDao.isFavoritePeople(id = id)

    override suspend fun insert(media: Media): Long =
        peopleDao.insertOrIgnorePeoples(
            PeopleEntity(
                id = media.id ?: -1,
                timestamp = Instant.now().toEpochMilli(),
                name = media.title ?: "",
                profilePath = media.posterPath ?: ""
            )
        )

    override suspend fun delete(media: Media) {
        media.id?.let { id ->
            peopleDao.deletePeople(id = id)
        }
    }

    override suspend fun upsert(medias: List<Media>) =
        peopleDao.upsertPeoples(
            entities = medias.map { media ->
                PeopleEntity(
                    id = media.id ?: -1,
                    timestamp = Instant.now().toEpochMilli(),
                    name = media.title ?: "",
                    profilePath = media.posterPath ?: ""
                )
            }
        )

    override fun getFavorite(): PagingSource<Int, PeopleEntity> =
        peopleDao.getFavoritePeople()
}