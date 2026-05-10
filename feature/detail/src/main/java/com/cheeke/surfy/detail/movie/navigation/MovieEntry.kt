package com.cheeke.surfy.detail.movie.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.detail.movie.MovieScreen
import com.cheeke.surfy.detail.movie.MovieVM
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

fun NavBackStack<NavKey>.goToMovie(id: Int) = add(element = MovieNavKey(id = id))