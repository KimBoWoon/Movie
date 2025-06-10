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
import androidx.paging.map
import com.bowoon.data.repository.PagingRepository
import com.bowoon.domain.GetMovieAppDataUseCase
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.People
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import com.bowoon.model.Series
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchVM @Inject constructor(
    movieAppDataUseCase: GetMovieAppDataUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
        private const val GENRE = "genre"
        private const val SEARCH_TYPE = "searchType"
    }

    var searchQuery by mutableStateOf("")
        private set
    val selectedGenre = savedStateHandle.getStateFlow<Genre?>(GENRE, null)
    val searchType = savedStateHandle.getStateFlow<SearchType>(SEARCH_TYPE, SearchType.MOVIE)
    var searchResult = MutableStateFlow<SearchUiState>(SearchUiState.SearchHint)
    private val movieAppData = movieAppDataUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    var recommendedKeywordPaging = MutableStateFlow<PagingData<SearchKeyword>>(PagingData.empty())
    private val recommendedKeywordFlow = MutableStateFlow<String>("")
    private var recommendedKeywordJob: Job? = null
    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        @OptIn(FlowPreview::class)
        recommendedKeywordJob = viewModelScope.launch {
            recommendedKeywordFlow
                .debounce(300L)
                .flatMapLatest {
                    Pager(
                        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                        initialKey = 1,
                        pagingSourceFactory = { pagingRepository.getSearchKeyword(query = it) }
                    ).flow.cachedIn(viewModelScope)
                }.collect {
                    recommendedKeywordPaging.emit(it)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()

        if (recommendedKeywordJob != null) {
            recommendedKeywordJob?.cancel()
            recommendedKeywordJob = null
        }
    }

    fun updateGenre(genre: Genre?) {
        savedStateHandle[GENRE] = if (genre == selectedGenre.value) null else genre
    }

    fun updateKeyword(keyword: String) {
        searchQuery = keyword
        viewModelScope.launch {
            recommendedKeywordFlow.emit(keyword)
        }
    }

    fun updateSearchType(searchType: SearchType) {
        savedStateHandle[SEARCH_TYPE] = searchType
        viewModelScope.launch {
            searchResult.emit(SearchUiState.SearchHint)
        }
    }

    fun searchMovies() {
        viewModelScope.launch {
            searchQuery.trim().takeIf { it.isNotEmpty() }?.let { query ->
                searchResult.emit(
                    SearchUiState.Success(
                        combine(
                            Pager(
                                config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                                initialKey = 1,
                                pagingSourceFactory = {
                                    pagingRepository.searchMovieSource(
                                        type = searchType.value,
                                        query = query,
                                        language = "${movieAppData.value?.getLanguage()}-${movieAppData.value?.getRegion()}",
                                        region = movieAppData.value?.getRegion() ?: "",
                                        isAdult = movieAppData.value?.isAdult ?: true
                                    )
                                }
                            ).flow.cachedIn(viewModelScope).map {
                                it.map { searchGroup ->
                                    when (searchGroup) {
                                        is Movie -> searchGroup.copy(imagePath = "${movieAppData.value?.getImageUrl() ?: ""}${searchGroup.imagePath}")
                                        is People -> searchGroup.copy(imagePath = "${movieAppData.value?.getImageUrl() ?: ""}${searchGroup.imagePath}")
                                        is Series -> searchGroup.copy(imagePath = "${movieAppData.value?.getImageUrl() ?: ""}${searchGroup.imagePath}")
                                    }
                                }
                            },
                            selectedGenre
                        ) { pagingData, selectedGenre ->
                            selectedGenre?.let { genre ->
                                pagingData.filter { it is Movie && genre.id in (it.genreIds ?: emptyList()) }
                            } ?: pagingData
                        }
                    )
                )
            } ?: showSnackbar.emit(Unit)
        }
    }
}

sealed interface SearchUiState {
    data object SearchHint : SearchUiState
    data class Success(val pagingData: Flow<PagingData<SearchGroup>>) : SearchUiState
    data class Error(val throwable: Throwable) : SearchUiState
}