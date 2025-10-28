package com.bowoon.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.Movie
import com.bowoon.model.People
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteVM @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    companion object {
        private const val TAG = "FavoriteVM"
    }

    val favoriteMovies = databaseRepository.getMovies()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.Lazily
        )
    val favoritePeoples = databaseRepository.getPeople()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.Lazily
        )

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