package com.bowoon.domain

import androidx.paging.PagingData
import com.bowoon.data.repository.TMDBRepository
import com.bowoon.model.SearchItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchListUseCase @Inject constructor(
    private val tmdbRepository: TMDBRepository
) {
    suspend operator fun invoke(type: String, query: String): Flow<PagingData<SearchItem>> =
        tmdbRepository.searchMovies(type = type, query = query)
}