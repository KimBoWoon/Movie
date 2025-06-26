package com.bowoon.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bowoon.data.repository.PagingRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetRecommendKeywordUseCase @Inject constructor(
    private val pagingRepository: PagingRepository
) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        close(message = throwable.message ?: "something wrong...", cause = throwable)
    }
    private val backgroundScope = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler)
    private val recommendedKeywordFlow = MutableStateFlow<String>("")

    @OptIn(FlowPreview::class)
    operator fun invoke() = recommendedKeywordFlow.debounce(300L)
        .flatMapLatest {
            Pager(
                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                initialKey = 1,
                pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = it) }
            ).flow.cachedIn(scope = backgroundScope)
        }

    suspend fun setKeyword(keyword: String) {
        recommendedKeywordFlow.emit(keyword)
    }

    private fun close(message: String, cause: Throwable) {
        backgroundScope.cancel(message = message, cause = cause)
    }
}