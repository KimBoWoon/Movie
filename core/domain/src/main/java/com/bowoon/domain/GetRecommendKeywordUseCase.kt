package com.bowoon.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bowoon.data.repository.PagingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetRecommendKeywordUseCase @Inject constructor(
    private val pagingRepository: PagingRepository
) {
    private val recommendedKeywordFlow = MutableStateFlow<String>("")

    @OptIn(FlowPreview::class)
    operator fun invoke(
        scope: CoroutineScope
    ) = recommendedKeywordFlow.debounce(300L)
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = it) }
            ).flow.cachedIn(scope = scope)
        }

    suspend fun setKeyword(keyword: String) {
        recommendedKeywordFlow.emit(keyword)
    }
}