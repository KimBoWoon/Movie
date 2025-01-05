package com.bowoon.domain

import androidx.paging.PagingData
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.model.TMDBSearchResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchListUseCase @Inject constructor(
    private val tmdbRepository: TMDBRepository
) {
    suspend operator fun invoke(query: String): Flow<PagingData<TMDBSearchResult>> =
        tmdbRepository.searchMovies(query = query)
}