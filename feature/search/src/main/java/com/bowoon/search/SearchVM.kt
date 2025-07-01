package com.bowoon.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.bowoon.data.repository.MovieAppDataRepository
import com.bowoon.domain.GetRecommendKeywordUseCase
import com.bowoon.domain.GetSearchUseCase
import com.bowoon.model.Genre
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchKeyword
import com.bowoon.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getSearchUseCase: GetSearchUseCase,
    private val getRecommendKeywordUseCase: GetRecommendKeywordUseCase,
    movieAppDataRepository: MovieAppDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
        private const val GENRE = "genre"
        private const val SEARCH_TYPE = "searchType"
    }

    private var recommendedKeywordJob: Job? = null
    val movieAppData = movieAppDataRepository.movieAppData
    var searchQuery by mutableStateOf(value = "")
        private set
    val selectedGenre = savedStateHandle.getStateFlow<Genre?>(GENRE, null)
    val searchType = savedStateHandle.getStateFlow<SearchType>(SEARCH_TYPE, SearchType.MOVIE)
    val searchResult = MutableStateFlow<SearchUiState>(SearchUiState.SearchHint)
    val recommendedKeywordPaging = MutableStateFlow<PagingData<SearchKeyword>>(PagingData.empty())
    val showSnackbar = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    init {
        recommendedKeywordJob = viewModelScope.launch {
            getRecommendKeywordUseCase()
                .collect {
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

        getSearchUseCase.close(message = "SearchVM is destroy", cause = null)
        getRecommendKeywordUseCase.close(message = "SearchVM is destroy", cause = null)
    }

    fun updateGenre(genre: Genre?) {
        savedStateHandle[GENRE] = if (genre == selectedGenre.value) null else genre
    }

    fun updateKeyword(keyword: String) {
        searchQuery = keyword
        viewModelScope.launch {
            getRecommendKeywordUseCase.setKeyword(keyword)
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
                        getSearchUseCase(
                            searchType = searchType.value,
                            query = query,
                            selectedGenre = selectedGenre
                        )
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