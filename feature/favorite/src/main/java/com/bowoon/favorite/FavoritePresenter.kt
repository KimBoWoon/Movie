package com.bowoon.favorite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.bowoon.data.repository.DatabaseRepository
import com.bowoon.model.Favorite
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.produceRetainedState
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.launch

class FavoritePresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator,
    @Assisted("goToMovie") private val goToMovie: (Int) -> Unit,
    @Assisted("goToPeople") private val goToPeople: (Int) -> Unit,
    private val databaseRepository: DatabaseRepository
) : Presenter<FavoriteUiState> {
    companion object {
        private const val TAG = "FavoriteVM"
    }

    enum class FavoriteTabs(val label: String) {
        MOVIE("영화"), PEOPLE("인물")
    }

    @Composable
    override fun present(): FavoriteUiState {
        val scope = rememberCoroutineScope()
        val favoriteMovies by produceRetainedState<List<Favorite>>(initialValue = emptyList()) {
            databaseRepository.getMovies().collect { value = it }
        }
        val favoritePeoples by produceRetainedState<List<Favorite>>(initialValue = emptyList()) {
            databaseRepository.getPeople().collect { value = it }
        }

        return FavoriteUiState(
            favoriteMovies = favoriteMovies,
            favoritePeoples = favoritePeoples,
        ) { event ->
            when (event) {
                is FavoriteEvent.GoToMovie -> goToMovie(event.id)
                is FavoriteEvent.GoToPeople -> goToPeople(event.id)
                is FavoriteEvent.DeleteFavoriteMovie -> {
                    scope.launch { databaseRepository.deleteMovie(movie = event.favorite) }
                }
                is FavoriteEvent.DeleteFavoritePeople -> {
                    scope.launch { databaseRepository.deletePeople(people = event.favorite) }
                }
            }
        }
    }

    @CircuitInject(com.bowoon.favorite.navigation.Favorite::class, ActivityRetainedComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            navigator: Navigator,
            @Assisted("goToMovie") goToMovie: ((Int) -> Unit) = {},
            @Assisted("goToPeople") goToPeople: ((Int) -> Unit) = {}
        ): FavoritePresenter
    }
}

data class FavoriteUiState(
    val favoriteMovies: List<Favorite>,
    val favoritePeoples: List<Favorite>,
    val eventSink: (FavoriteEvent) -> Unit
) : CircuitUiState

sealed interface FavoriteEvent : CircuitUiEvent {
    data class GoToMovie(val id: Int) : FavoriteEvent
    data class GoToPeople(val id: Int) : FavoriteEvent
    data class DeleteFavoriteMovie(val favorite: Favorite) : FavoriteEvent
    data class DeleteFavoritePeople(val favorite: Favorite) : FavoriteEvent
}