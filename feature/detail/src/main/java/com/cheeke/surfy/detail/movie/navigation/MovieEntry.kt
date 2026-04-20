package com.cheeke.surfy.detail.movie.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieScreen(val id: Int) : Screen

fun Navigator.goToMovie(id: Int) { goTo(screen = MovieScreen(id = id)) }

//@Serializable
//data class MovieNavKey(
//    val id: Int
//) : NavKey
//
//fun EntryProviderScope<NavKey>.movieEntry(
//    goToBack: () -> Unit,
//    goToMovie: (Int) -> Unit,
//    goToPeople: (Int) -> Unit,
//    goToSeries: (Int) -> Unit,
//    onShowSnackbar: suspend (String, String?) -> Boolean
//) {
//    entry<MovieNavKey> { detailRoute ->
//        MovieScreen(
//            goToBack = goToBack,
//            goToMovie = goToMovie,
//            goToPeople = goToPeople,
//            goToSeries = goToSeries,
//            onShowSnackbar = onShowSnackbar,
//            viewModel = hiltViewModel<MovieVM, MovieVM.Factory>(
//                key = detailRoute.id.toString(),
//            ) { factory ->
//                factory.create(id = detailRoute.id)
//            }
//        )
//    }
//}
//
//fun Navigator.navigateToMovie(
//    id: Int
//) {
//    navigate(route = MovieNavKey(id = id))
//}