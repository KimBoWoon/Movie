package com.cheeke.surfy.detail.series.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeriesScreen(val id: Int) : Screen

fun Navigator.goToSeries(id: Int) { goTo(screen = SeriesScreen(id = id)) }

//@Serializable
//data class SeriesNavKey(
//    val id: Int
//) : NavKey
//
//fun EntryProviderScope<NavKey>.seriesEntry(
//    goToBack: () -> Unit,
//    goToMovie: (Int) -> Unit
//) {
//    entry<SeriesNavKey> { seriesRoute ->
//        SeriesScreen(
//            goToBack = goToBack,
//            goToMovie = goToMovie,
//            viewModel = hiltViewModel<SeriesVM, SeriesVM.Factory>(
//                key = seriesRoute.id.toString(),
//            ) { factory ->
//                factory.create(id = seriesRoute.id)
//            }
//        )
//    }
//}
//
//fun Navigator.navigateToSeries(
//    id: Int
//) {
//    navigate(route = SeriesNavKey(id = id))
//}