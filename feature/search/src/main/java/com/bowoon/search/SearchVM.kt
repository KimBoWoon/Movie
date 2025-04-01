package com.bowoon.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.bowoon.model.Movie
import com.bowoon.model.MovieGenre
import com.bowoon.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    val selectedGenre = savedStateHandle.getStateFlow<MovieGenre?>("genre", null)
    var searchQuery by mutableStateOf("")
        private set
    var searchType by mutableIntStateOf(savedStateHandle.get<Int>("searchType") ?: 0)
    var searchResult = MutableStateFlow<SearchState>(SearchState.Loading)

    fun updateGenre(genre: MovieGenre?) {
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
        this@SearchVM.searchType = searchType.ordinal
    }

    fun searchMovies() {
        viewModelScope.launch {
            searchQuery.trim().takeIf { it.isNotEmpty() }?.let { query ->
                val userdata = userDataRepository.internalData.first()

                combine(
                    Pager(
                        config = PagingConfig(pageSize = 1, initialLoadSize = 1, prefetchDistance = 5),
                        initialKey = 1,
                        pagingSourceFactory = {
                            pagingRepository.searchMovieSource(
                                type = SearchType.entries[searchType],
                                query = query,
                                language = "${userdata.language}-${userdata.region}",
                                region = userdata.region,
                                isAdult = userdata.isAdult
                            )
                        }
                    ).flow.cachedIn(viewModelScope),
                    selectedGenre
                ) { pagingData, selectedGenre ->
                    selectedGenre?.let { genre ->
                        pagingData.filter { genre.id in (it.genreIds ?: emptyList()) }
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
    data class Search(val pagingData: Flow<PagingData<Movie>>) : SearchState
    data class Error(val message: String) : SearchState
}