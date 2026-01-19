package com.bowoon.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.Movie
import com.bowoon.model.People
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FavoriteVM.Factory::class)
class FavoriteVM @AssistedInject constructor(
    @Assisted initialTabIndex: Int,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        internal const val TAG = "FavoriteVM"
    }

    @AssistedFactory
    interface Factory {
        fun create(tab: Int): FavoriteVM
    }

    private val _tabIndex = MutableStateFlow(value = initialTabIndex)
    val tabIndex = _tabIndex.asStateFlow()

    val favoriteMovies = databaseRepository.getMovies()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed()
        )
    val favoritePeoples = databaseRepository.getPeople()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed()
        )

    fun updateTabIndex(index: Int) {
        _tabIndex.value = index
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie = movie)
        }
    }

    fun deletePeople(people: People) {
        viewModelScope.launch {
            databaseRepository.deletePeople(people = people)
        }
    }
}