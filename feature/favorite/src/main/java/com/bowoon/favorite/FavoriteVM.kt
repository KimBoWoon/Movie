package com.bowoon.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.Favorite
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

    enum class FavoriteTabs(val label: String) {
        MOVIE("영화"), PEOPLE("인물")
    }

    val favoriteMovies = databaseRepository.getMovies()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000)
        )
    val favoritePeoples = databaseRepository.getPeople()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5_000)
        )

    fun deleteMovie(movie: Favorite) {
        viewModelScope.launch {
            databaseRepository.deleteMovie(movie)
        }
    }

    fun deletePeople(people: Favorite) {
        viewModelScope.launch {
            databaseRepository.deletePeople(people)
        }
    }
}