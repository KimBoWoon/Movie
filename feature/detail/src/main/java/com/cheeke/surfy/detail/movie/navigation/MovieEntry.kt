package com.cheeke.surfy.detail.movie.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.movie.MovieScreen
import com.cheeke.surfy.detail.movie.MovieVM
import com.cheeke.surfy.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class MovieNavKey(
    val id: Int
) : NavKey

fun EntryProviderScope<NavKey>.movieEntry(
    goToBack: () -> Unit,
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<MovieNavKey> { detailRoute ->
        MovieScreen(
            goToBack = goToBack,
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar,
            viewModel = hiltViewModel<MovieVM, MovieVM.Factory>(
                key = detailRoute.id.toString(),
            ) { factory ->
                factory.create(id = detailRoute.id)
            }
        )
    }
}

fun Navigator.navigateToMovie(
    id: Int
) {
    navigate(route = MovieNavKey(id = id))
}