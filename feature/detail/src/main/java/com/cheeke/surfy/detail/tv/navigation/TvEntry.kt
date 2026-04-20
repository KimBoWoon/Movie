package com.cheeke.surfy.detail.tv.navigation

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class TvScreen(val id: Int) : Screen

fun Navigator.goToTv(id: Int) { goTo(screen = TvScreen(id = id)) }

//@Serializable
//data class TvNavKey(
//    val id: Int
//) : NavKey
//
//fun EntryProviderScope<NavKey>.tvEntry(
//    goToBack: () -> Unit,
//    goToTv: (Int) -> Unit,
//    goToPeople: (Int) -> Unit,
//    onShowSnackbar: suspend (String, String?) -> Boolean
//) {
//    entry<TvNavKey> { detailRoute ->
//        TvScreen(
//            goToBack = goToBack,
//            goToTv = goToTv,
//            goToPeople = goToPeople,
//            onShowSnackbar = onShowSnackbar,
//            viewModel = hiltViewModel<TvVM, TvVM.Factory>(
//                key = detailRoute.id.toString(),
//            ) { factory ->
//                factory.create(id = detailRoute.id)
//            }
//        )
//    }
//}
//
//fun Navigator.navigateToTv(
//    id: Int
//) {
//    navigate(route = TvNavKey(id = id))
//}