package com.bowoon.domain

import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.data.repository.DetailRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Tv
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTvDetailUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val detailRepository: DetailRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(id: Int): Flow<TvWithFavorite> = combine(
        detailRepository.getTv(id = id),
        databaseRepository.isFavoriteTv(id = id),
        userDataRepository.internalData
    ) { tv, isFavorite, internalData ->
        TvWithFavorite(tv = tv, isFavorite = isFavorite, autoPlayTrailer = internalData.isAutoPlayTrailer)
    }
}

data class TvWithFavorite(
    val tv: Tv,
    val isFavorite: Boolean,
    val autoPlayTrailer: Boolean
)