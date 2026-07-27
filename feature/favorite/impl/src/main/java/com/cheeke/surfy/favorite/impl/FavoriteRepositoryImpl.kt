package com.cheeke.surfy.favorite.impl

import androidx.paging.PagingData
import com.cheeke.surfy.detail.api.movie.MovieRepository
import com.cheeke.surfy.detail.api.people.PeopleRepository
import com.cheeke.surfy.detail.api.tv.TvRepository
import com.cheeke.surfy.favorite.api.FavoriteContentType
import com.cheeke.surfy.model.Media
import com.cheeke.surfy.model.Movie
import com.cheeke.surfy.model.People
import com.cheeke.surfy.model.Tv
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    val key: FavoriteContentType
    val pagingSource: Flow<PagingData<Media>>

    suspend fun insert(media: Media)
    suspend fun delete(media: Media)
}

class FavoriteMovieRepository @Inject constructor(
    private val repository: MovieRepository
) : FavoriteRepository {
    override val key: FavoriteContentType = FavoriteContentType.MOVIE
    override val pagingSource: Flow<PagingData<Media>> = repository.getFavorite() as Flow<PagingData<Media>>

    override suspend fun insert(media: Media) {
        repository.insert(media = media as Movie)
    }

    override suspend fun delete(media: Media) {
        repository.delete(media = media as Movie)
    }
}

class FavoritePeopleRepository @Inject constructor(
    private val repository: PeopleRepository
) : FavoriteRepository {
    override val key: FavoriteContentType = FavoriteContentType.PEOPLE
    override val pagingSource: Flow<PagingData<Media>> = repository.getFavorite() as Flow<PagingData<Media>>

    override suspend fun insert(media: Media) {
        repository.insert(media = media as People)
    }

    override suspend fun delete(media: Media) {
        repository.delete(media = media as People)
    }
}

class FavoriteTvRepository @Inject constructor(
    private val repository: TvRepository
) : FavoriteRepository {
    override val key: FavoriteContentType = FavoriteContentType.TV
    override val pagingSource: Flow<PagingData<Media>> = repository.getFavorite() as Flow<PagingData<Media>>

    override suspend fun insert(media: Media) {
        repository.insert(media = media as Tv)
    }

    override suspend fun delete(media: Media) {
        repository.delete(media = media as Tv)
    }
}