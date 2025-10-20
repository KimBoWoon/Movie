package com.bowoon.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val pagingRepository: PagingRepository,
    userDataRepository: UserDataRepository,
    movieAppDataRepository: MovieAppDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
        private const val GENRE = "genre"
        private const val SEARCH_TYPE = "searchType"
    }

    private var recommendKeywordJob: Job? = null
    val movieAppData = movieAppDataRepository.movieAppData
    var searchQuery by mutableStateOf(value = "")
        private set
    val selectedGenre = savedStateHandle.getStateFlow<Genre?>(key = GENRE, initialValue = null)
    val searchType = savedStateHandle.getStateFlow<SearchType>(key = SEARCH_TYPE, initialValue = SearchType.MOVIE)
    val searchResult = MutableStateFlow<SearchUiState>(value = SearchUiState.SearchHint)
    val recommendKeywordPaging = MutableStateFlow<RecommendKeywordUiState>(value = RecommendKeywordUiState.Loading)
    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val recommendKeywordFlow = MutableStateFlow<String>("")
    private val userData = userDataRepository.internalData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    init {
        recommendKeywordJob = viewModelScope.launch {
            recommendKeywordPaging.emit(
                value = RecommendKeywordUiState.Success(
                    recommendKeywordFlow.debounce(300L)
                        .flatMapLatest {
                            Pager(
                                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                                initialKey = 1,
                                pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = it) }
                            ).flow.cachedIn(scope = viewModelScope)
                        }
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()

        if (recommendKeywordJob != null) {
            recommendKeywordJob?.cancel()
            recommendKeywordJob = null
        }
    }

    fun updateGenre(genre: Genre?) {
        savedStateHandle[GENRE] = if (genre == selectedGenre.value) null else genre
    }

    fun updateKeyword(keyword: String) {
        searchQuery = keyword
        viewModelScope.launch { recommendKeywordFlow.emit(value = keyword) }
    }

    fun updateSearchType(searchType: SearchType) {
        savedStateHandle[SEARCH_TYPE] = searchType
        viewModelScope.launch {
            searchResult.emit(value = SearchUiState.SearchHint)
        }
    }

    fun searchMovies() {
        viewModelScope.launch {
            searchQuery.trim().takeIf { it.isNotEmpty() }?.let { query ->
                searchResult.emit(
                    value = SearchUiState.Success(
                        pagingData = combine(
                            Pager(
                                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                                initialKey = 1,
                                pagingSourceFactory = {
                                    pagingRepository.getSearchPagingSource(
                                        type = searchType.value,
                                        query = query,
                                        language = "${userData.value?.language}-${userData.value?.region}",
                                        region = userData.value?.region ?: "",
                                        isAdult = userData.value?.isAdult ?: true
                                    )
                                }
                            ).flow.cachedIn(scope = viewModelScope),
                            selectedGenre
                        ) { pagingData, genre ->
                            if (genre != null) {
                                pagingData.filter { genre.id in (it.genres?.map { genre -> genre.id } ?: emptyList()) }
                            } else {
                                pagingData
                            }
                        }
                    )
                )
            } ?: showSnackbar.emit(value = Unit)
        }
    }
}

sealed interface SearchUiState {
    data object SearchHint : SearchUiState
    data class Success(val pagingData: Flow<PagingData<Movie>>) : SearchUiState
    data class Error(val throwable: Throwable) : SearchUiState
}

sealed interface RecommendKeywordUiState {
    data object Loading : RecommendKeywordUiState
    data class Success(val pagingData: Flow<PagingData<SearchKeyword>>) : RecommendKeywordUiState
}