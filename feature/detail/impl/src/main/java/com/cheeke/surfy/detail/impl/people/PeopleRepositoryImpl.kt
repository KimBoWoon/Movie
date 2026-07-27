package com.cheeke.surfy.detail.impl.people

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.cheeke.surfy.database.dao.PeopleDao
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.detail.api.people.PeopleRepository
import com.cheeke.surfy.model.People
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class PeopleRepositoryImpl @Inject constructor(
    private val peopleDao: PeopleDao
) : PeopleRepository {
    override fun getFavorite(): Flow<PagingData<People>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            peopleDao.getFavoritePeople()
        }.flow.map { pagingData -> pagingData.map(transform = PeopleEntity::asExternalModel) }

    override fun isFavorite(id: Int): Flow<Boolean> =
        peopleDao.isFavoritePeople(id = id)

    override suspend fun insert(media: People): Long =
        peopleDao.insertOrIgnorePeoples(
            people = PeopleEntity(
                id = media.id ?: -1,
                name = media.title,
                profilePath = media.posterPath ?: "",
                timestamp = Instant.now().toEpochMilli()
            )
        )

    override suspend fun delete(media: People) {
        media.id?.let { id ->
            peopleDao.deletePeople(id = id)
        }
    }

    override suspend fun upsert(medias: List<People>) {
        peopleDao.upsertPeoples(entities = medias.map(transform = People::asExternalModel))
    }
}