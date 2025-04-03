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
import com.bowoon.data.repository.UserDataRepository
import com.bowoon.model.Genre
import com.bowoon.model.Movie
import com.bowoon.model.SearchGroup
import com.bowoon.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val pagingRepository: PagingRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
    }

    init {
        viewModelScope.launch {
            userDataRepository.internalData.collect {
                language = it.language
                region = it.region
                isAdult = it.isAdult
            }
        }
    }

    private var language: String = ""
    private var region: String = ""
    private var isAdult: Boolean = true
    var searchQuery by mutableStateOf("")
        private set
    val selectedGenre = savedStateHandle.getStateFlow<Genre?>("genre", null)
    val searchType = savedStateHandle.getStateFlow<SearchType>("searchType", SearchType.MOVIE)
    var searchResult = MutableStateFlow<SearchState>(SearchState.Loading)

    fun updateGenre(genre: Genre?) {
        viewModelScope.launch {
            savedStateHandle["genre"] = if (genre == selectedGenre.value) {
                null
            } else {
                genre
            }
        }
    }

    fun updateKeyword(keyword: String) {
        searchQuery = keyword
    }

    fun updateSearchType(searchType: SearchType) {
        savedStateHandle["searchType"] = searchType
    }

    fun searchMovies() {
        viewModelScope.launch {
            searchQuery.trim().takeIf { it.isNotEmpty() }?.let { query ->
                combine(
                    Pager(
                        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                        initialKey = 1,
                        pagingSourceFactory = {
                            pagingRepository.searchMovieSource(
                                type = searchType.value,
                                query = query,
                                language = "$language-$region",
                                region = region,
                                isAdult = isAdult
                            )
                        }
                    ).flow.cachedIn(viewModelScope),
                    selectedGenre
                ) { pagingData, selectedGenre ->
                    selectedGenre?.let { genre ->
                        pagingData.filter { it is Movie && genre.id in (it.genreIds ?: emptyList()) }
                    } ?: pagingData
                }.let {
                    searchResult.emit(SearchState.Search(it))
                }
            } ?: searchResult.emit(SearchState.Error("검색어를 입력하세요."))
        }
    }
}

sealed interface SearchState {
    data object Loading : SearchState
    data class Search(val pagingData: Flow<PagingData<SearchGroup>>) : SearchState
    data class Error(val message: String) : SearchState
}