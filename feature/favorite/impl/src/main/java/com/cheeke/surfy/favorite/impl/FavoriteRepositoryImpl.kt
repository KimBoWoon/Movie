package com.cheeke.surfy.favorite.impl

import androidx.annotation.StringRes
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.cheeke.surfy.database.impl.dao.MovieDao
import com.cheeke.surfy.database.impl.dao.PeopleDao
import com.cheeke.surfy.database.impl.dao.TvDao
import com.cheeke.surfy.database.impl.model.MovieEntity
import com.cheeke.surfy.database.impl.model.PeopleEntity
import com.cheeke.surfy.database.impl.model.TvEntity
import com.cheeke.surfy.database.impl.model.asExternalModel
import com.cheeke.surfy.favorite.api.FavoriteContentType
import com.cheeke.surfy.feature.favorite.impl.R
import com.cheeke.surfy.model.Media
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface FavoriteRepository {
    val key: FavoriteContentType
    val pagingSource: Flow<PagingData<Media>>
    val label: Int

    suspend fun insert(media: Media)
    suspend fun delete(media: Media)
}

class FavoriteMovieRepository @Inject constructor(
    private val movieDao: MovieDao
) : FavoriteRepository {
    override val key: FavoriteContentType = FavoriteContentType.MOVIE
    override val pagingSource: Flow<PagingData<Media>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            movieDao.getFavoriteMovie()
        }.flow.map { pagingData -> pagingData.map(transform = MovieEntity::asExternalModel) }
    @StringRes override val label: Int = R.string.movie

    override suspend fun insert(media: Media) {
        movieDao.insertOrIgnoreMovies(
            movie = MovieEntity(
                id = media.id ?: -1,
                posterPath = media.posterPath ?: "",
                title = media.title ?: "",
                releaseDate = media.releaseDate ?: "",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun delete(media: Media) {
        media.id?.let { id -> movieDao.deleteMovie(id = id) }
    }
}

class FavoritePeopleRepository @Inject constructor(
    private val peopleDao: PeopleDao
) : FavoriteRepository {
    override val key: FavoriteContentType = FavoriteContentType.PEOPLE
    override val pagingSource: Flow<PagingData<Media>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            peopleDao.getFavoritePeople()
        }.flow.map { pagingData -> pagingData.map(transform = PeopleEntity::asExternalModel) }
    @StringRes override val label: Int = R.string.people

    override suspend fun insert(media: Media) {
        peopleDao.insertOrIgnorePeoples(
            people = PeopleEntity(
                id = media.id ?: -1,
                profilePath = media.posterPath ?: "",
                name = media.title ?: "",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun delete(media: Media) {
        media.id?.let { id -> peopleDao.deletePeople(id = id) }
    }
}

class FavoriteTvRepository @Inject constructor(
    private val tvDao: TvDao
) : FavoriteRepository {
    override val key: FavoriteContentType = FavoriteContentType.TV
    override val pagingSource: Flow<PagingData<Media>> =
        Pager(config = PagingConfig(pageSize = 20)) {
            tvDao.getFavoriteTv()
        }.flow.map { pagingData -> pagingData.map(transform = TvEntity::asExternalModel) }
    @StringRes override val label: Int = R.string.tv

    override suspend fun insert(media: Media) {
        tvDao.insertOrIgnoreTvs(
            tv = TvEntity(
                id = media.id ?: -1,
                posterPath = media.posterPath ?: "",
                name = media.title ?: "",
                timestamp = System.currentTimeMillis(),
                firstAirDate = media.firstAirDate,
                lastAirDate = media.lastAirDate
            )
        )
    }

    override suspend fun delete(media: Media) {
        media.id?.let { id -> tvDao.deleteTv(id = id) }
    }
}