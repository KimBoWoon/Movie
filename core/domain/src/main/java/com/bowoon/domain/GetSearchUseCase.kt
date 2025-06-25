package com.bowoon.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.bowoon.common.di.ApplicationScope
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchType
import com.bowoon.model.Series
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetSearchUseCase @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val pagingRepository: PagingRepository,
    userDataRepository: UserDataRepository,
    movieAppDataRepository: MovieAppDataRepository
) {
    private val userData = userDataRepository.internalData.stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    val movieAppData = movieAppDataRepository.movieAppData

    operator fun invoke(
        scope: CoroutineScope,
        searchType: SearchType,
        query: String,
        selectedGenre: Flow<Genre?>
    ) = combine(
        Pager(
            config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
            initialKey = 1,
            pagingSourceFactory = {
                pagingRepository.getSearchPagingSource(
                    type = searchType,
                    query = query,
                    language = "${userData.value?.language}-${userData.value?.region}",
                    region = userData.value?.region ?: "",
                    isAdult = userData.value?.isAdult ?: true
                )
            }
        ).flow.cachedIn(scope).map { pagingData ->
            pagingData.map { searchGroup ->
                when (searchGroup) {
                    is Movie -> searchGroup.copy(imagePath = "${movieAppData.value.getImageUrl()}${searchGroup.posterPath}")
                    is People -> searchGroup.copy(imagePath = "${movieAppData.value.getImageUrl()}${searchGroup.profilePath}")
                    is Series -> searchGroup.copy(imagePath = "${movieAppData.value.getImageUrl()}${searchGroup.posterPath}")
                }
            }
        },
        selectedGenre
    ) { pagingData, genre ->
        if (genre != null) {
            pagingData.filter { it is Movie && genre.id in (it.genreIds ?: emptyList()) }
        } else {
            pagingData
        }
    }
}