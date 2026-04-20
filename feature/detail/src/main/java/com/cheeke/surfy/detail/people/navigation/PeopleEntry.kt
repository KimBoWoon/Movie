package com.cheeke.surfy.detail.people.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class PeopleScreen(val id: Int) : Screen

fun Navigator.goToPeople(id: Int) { goTo(screen = PeopleScreen(id = id)) }

//@Serializable
//data class PeopleNavKey(
//    val id: Int
//) : NavKey
//
//fun EntryProviderScope<NavKey>.peopleEntry(
//    goToBack: () -> Unit,
//    goToMovie: (Int) -> Unit,
//    goToTv: (Int) -> Unit,
//    onShowSnackbar: suspend (String, String?) -> Boolean
//) {
//    entry<PeopleNavKey> { peopleRoute ->
//        PeopleScreen(
//            goToBack = goToBack,
//            goToMovie = goToMovie,
//            goToTv = goToTv,
//            onShowSnackbar = onShowSnackbar,
//            viewModel = hiltViewModel<PeopleVM, PeopleVM.Factory>(
//                key = peopleRoute.id.toString(),
//            ) { factory ->
//                factory.create(id = peopleRoute.id)
//            }
//        )
//    }
//}
//
//fun Navigator.navigateToPeople(
//    id: Int
//) {
//    navigate(route = PeopleNavKey(id = id))
//}