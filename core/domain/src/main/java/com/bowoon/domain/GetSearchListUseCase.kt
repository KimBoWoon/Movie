package com.bowoon.domain

import androidx.paging.PagingData
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchListUseCase @Inject constructor(
    private val pagingRepository: PagingRepository
) {
    suspend operator fun invoke(type: String, query: String): Flow<PagingData<Movie>> =
        pagingRepository.searchMovies(type = type, query = query)
}