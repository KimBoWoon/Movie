package com.cheeke.surfy.favorite

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.cheeke.surfy.data.repository.MovieDataBaseRepository
import com.cheeke.surfy.data.repository.PeopleDataBaseRepository
import com.cheeke.surfy.data.repository.TvDataBaseRepository
import com.cheeke.surfy.database.model.MovieEntity
import com.cheeke.surfy.database.model.PeopleEntity
import com.cheeke.surfy.database.model.TvEntity
import com.cheeke.surfy.database.model.asExternalModel
import com.cheeke.surfy.feature.favorite.R
import com.cheeke.surfy.model.Media
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface FavoriteTab {
    val key: String
    val stringId: Int
    val pagingSource: Flow<PagingData<Media>>

    suspend fun insert(media: Media)
    suspend fun delete(media: Media)
}

class MovieTab @Inject constructor(
    private val movieDataBaseRepository: MovieDataBaseRepository
) : FavoriteTab {
    override val key: String = FavoriteKeys.MOVIE
    override val stringId = R.string.movie
    override val pagingSource: Flow<PagingData<Media>> =
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { movieDataBaseRepository.getFavorite() }
        ).flow.map { pagingData ->
            pagingData.map(transform = MovieEntity::asExternalModel)
        }

    override suspend fun insert(media: Media) {
        movieDataBaseRepository.insert(media = media)
    }

    override suspend fun delete(media: Media) {
        movieDataBaseRepository.delete(media = media)
    }
}

class PeopleTab @Inject constructor(
    private val peopleDataBaseRepository: PeopleDataBaseRepository
) : FavoriteTab {
    override val key: String = FavoriteKeys.PEOPLE
    override val stringId = R.string.people
    override val pagingSource: Flow<PagingData<Media>> =
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { peopleDataBaseRepository.getFavorite() }
        ).flow.map { pagingData ->
            pagingData.map(transform = PeopleEntity::asExternalModel)
        }

    override suspend fun insert(media: Media) {
        peopleDataBaseRepository.insert(media = media)
    }

    override suspend fun delete(media: Media) {
        peopleDataBaseRepository.delete(media = media)
    }
}

class TvTab @Inject constructor(
    private val tvDataBaseRepository: TvDataBaseRepository
) : FavoriteTab {
    override val key: String = FavoriteKeys.TV
    override val stringId = R.string.tv
    override val pagingSource: Flow<PagingData<Media>> =
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { tvDataBaseRepository.getFavorite() }
        ).flow.map { pagingData ->
            pagingData.map(transform = TvEntity::asExternalModel)
        }

    override suspend fun insert(media: Media) {
        tvDataBaseRepository.insert(media = media)
    }

    override suspend fun delete(media: Media) {
        tvDataBaseRepository.delete(media = media)
    }
}