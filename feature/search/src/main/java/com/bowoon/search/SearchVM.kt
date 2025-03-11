package com.bowoon.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.data.repository.PagingRepository
import com.bowoon.model.Movie
import com.bowoon.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pagingRepository: PagingRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
    }

    var keyword by mutableStateOf("")
        private set

    var searchType by mutableIntStateOf(savedStateHandle.get<Int>("searchType") ?: 0)
    val searchMovieState = MutableStateFlow<PagingData<Movie>>(PagingData.empty())

    fun updateKeyword(keyword: String) {
        this@SearchVM.keyword = keyword
    }

    fun updateSearchType(searchType: SearchType) {
        this@SearchVM.searchType = searchType.ordinal
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            pagingRepository.searchMovies(SearchType.entries[searchType].label, query)
                .cachedIn(viewModelScope)
                .collect {
                    searchMovieState.value = it
                }
        }
    }
}