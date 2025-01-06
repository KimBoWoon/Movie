package com.bowoon.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetSearchListUseCase
import com.bowoon.model.MovieDetail
import com.bowoon.model.TMDBSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    private val getSearchListUseCase: GetSearchListUseCase,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
    }

    val searchMovieState = MutableStateFlow<PagingData<TMDBSearchResult>>(PagingData.empty())

    fun searchMovies(query: String) {
        viewModelScope.launch {
            getSearchListUseCase(query)
                .cachedIn(viewModelScope)
                .collect {
                    searchMovieState.value = it
                }
        }
    }

    fun insertMovie(movie: MovieDetail) {
        viewModelScope.launch {
            databaseRepository.insert(movie)
        }
    }

    fun deleteMovie(movie: MovieDetail) {
        viewModelScope.launch {
            databaseRepository.delete(movie)
        }
    }
}