package com.bowoon.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.domain.GetSearchListUseCase
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSearchListUseCase: GetSearchListUseCase,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "SearchVM"
    }

    @OptIn(SavedStateHandleSaveableApi::class)
    var keyword by savedStateHandle.saveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var searchType = savedStateHandle.get<Int>("searchType") ?: 0
    val searchMovieState = MutableStateFlow<PagingData<Movie>>(PagingData.empty())

    fun update(newKeyword: TextFieldValue) {
        keyword = newKeyword
    }

    fun searchMovies(query: String) {
        viewModelScope.launch {
            getSearchListUseCase(SearchType.entries[searchType].label, query)
                .cachedIn(viewModelScope)
                .collect {
                    searchMovieState.value = it
                }
        }
    }

    fun insertMovie(movie: MovieDetail) {
        viewModelScope.launch {
            databaseRepository.insertMovie(movie)
        }
    }

    fun deleteMovie(movie: MovieDetail) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }
}