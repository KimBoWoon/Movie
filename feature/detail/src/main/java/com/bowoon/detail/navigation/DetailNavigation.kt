package com.bowoon.detail.navigation

import androidx.paging.compose.LazyPagingItems
import com.bowoon.model.Favorite
import com.bowoon.model.Movie
import com.bowoon.model.MovieDetail
import com.bowoon.model.MovieSeries
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class Detail(val id: Int) : Screen {
    class DetailState(
        val movieDetail: MovieDetail?,
        val movieSeries: MovieSeries?,
        val similarMovies: LazyPagingItems<Movie>,
        val eventSink: (DetailEvent) -> Unit
    ) : CircuitUiState

    sealed interface DetailEvent : CircuitUiEvent {
        data object GoToBack : DetailEvent
        data class GoToMovie(val id: Int) : DetailEvent
        data class GoToPeople(val id: Int) : DetailEvent
        data class AddFavorite(val favorite: Favorite) : DetailEvent
        data class RemoveFavorite(val favorite: Favorite) : DetailEvent
    }
}

fun Navigator.goToMovie(id: Int) {
    goTo(Detail(id = id))
}