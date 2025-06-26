package com.bowoon.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchType
import com.bowoon.model.Series
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class GetSearchUseCase @Inject constructor(
    private val pagingRepository: PagingRepository,
    userDataRepository: UserDataRepository,
    movieAppDataRepository: MovieAppDataRepository
) {
    private val coroutineScopeExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        close(message = throwable.message ?: "something wrong...", cause = throwable)
    }
    private val backgroundScope = CoroutineScope(Dispatchers.IO + coroutineScopeExceptionHandler)
    private val userData = userDataRepository.internalData.stateIn(
        scope = backgroundScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    val movieAppData = movieAppDataRepository.movieAppData

    operator fun invoke(
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
        ).flow.cachedIn(scope = backgroundScope).map { pagingData ->
            pagingData.map { searchGroup ->
                when (searchGroup) {
                    is Movie -> searchGroup.copy(imagePath = "${movieAppData.value.getImageUrl()}${searchGroup.imagePath}")
                    is People -> searchGroup.copy(imagePath = "${movieAppData.value.getImageUrl()}${searchGroup.imagePath}")
                    is Series -> searchGroup.copy(imagePath = "${movieAppData.value.getImageUrl()}${searchGroup.imagePath}")
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

    private fun close(message: String, cause: Throwable) {
        backgroundScope.cancel(message = message, cause = cause)
    }
}