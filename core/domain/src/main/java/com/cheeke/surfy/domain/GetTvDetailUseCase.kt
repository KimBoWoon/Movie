package com.cheeke.surfy.domain

import com.cheeke.surfy.data.repository.DatabaseRepository
import com.cheeke.surfy.data.repository.TvDetailRepository
import com.cheeke.surfy.data.repository.UserDataRepository
import com.cheeke.surfy.model.Tv
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTvDetailUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val detailRepository: TvDetailRepository,
    private val userDataRepository: UserDataRepository
) {
    operator fun invoke(id: Int): Flow<TvWithFavorite> = combine(
        detailRepository.getData(id = id),
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