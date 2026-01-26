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
import com.bowoon.data.repository.PagingRepository
import com.bowoon.data.util.DataManager
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.MovieAppData
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = SearchVM.Factory::class)
class SearchVM @AssistedInject constructor(
    @Assisted initialQuery: String,
    @Assisted initialSearchType: SearchType,
    dataManager: DataManager,
    private val savedStateHandle: SavedStateHandle,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    companion object {
        internal const val TAG = "SearchVM"
        private const val GENRE = "genre"
        private const val SEARCH_TYPE = "searchType"
    }

    @AssistedFactory
    interface Factory {
        fun create(
            initialQuery: String,
            initialSearchType: SearchType
        ): SearchVM
    }

    var searchQuery by mutableStateOf(value = initialQuery)
        private set
    val selectedGenre = savedStateHandle.getStateFlow<Genre?>(key = GENRE, initialValue = null)
    val searchType = savedStateHandle.getStateFlow<SearchType>(key = SEARCH_TYPE, initialValue = initialSearchType)
    val searchResult = MutableStateFlow<SearchUiState>(value = SearchUiState.SearchHint)
    var recommendKeywordPaging: Flow<PagingData<SearchKeyword>> = emptyFlow()
    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val recommendKeywordFlow = MutableStateFlow<String>(value = "")
    val movieAppData = dataManager.movieAppData
        .map { it.getMovieAppData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MovieAppData()
        )

    init {
        if (initialQuery.trim().isNotEmpty()) {
            searchMovies()
        }

        viewModelScope.launch {
            recommendKeywordFlow.debounce(timeoutMillis = 300L)
                .collect { query ->
                    recommendKeywordPaging = Pager(
                        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                        initialKey = 1,
                        pagingSourceFactory = { pagingRepository.getRecommendKeywordPagingSource(query = query) }
                    ).flow.cachedIn(scope = viewModelScope)
                }
        }
    }

    fun updateGenre(genre: Genre?) {
        savedStateHandle[GENRE] = if (genre == selectedGenre.value) null else genre
    }

    fun updateQuery(query: String) {
        searchQuery = query
        viewModelScope.launch { recommendKeywordFlow.emit(value = query) }
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
                                        query = query
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