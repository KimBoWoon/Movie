package com.bowoon.detail

import com.bowoon.model.Favorite
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailScreen(val id: Int) : Screen {
    data class State(
        val state: MovieDetailState,
        val eventSink: (Event) -> Unit
    ) : CircuitUiState

    sealed interface Event : CircuitUiEvent {
        data object GoToBack : Event
        data class GoToMovie(val id: Int) : Event
        data class GoToPeople(val id: Int) : Event
        data class AddFavorite(val favorite: Favorite) : Event
        data class RemoveFavorite(val favorite: Favorite) : Event
    }
}

fun Navigator.goToMovie(id: Int) {
    goTo(DetailScreen(id = id))
}