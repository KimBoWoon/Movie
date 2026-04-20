package com.cheeke.surfy.search.navigation

import com.cheeke.surfy.model.SearchType
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchScreen(val query: String = "", val searchType: SearchType = SearchType.MULTI) : Screen

//@Serializable
//data class SearchNavKey(
//    val query: String = "",
//    val searchType: String = "multi"
//) : NavKey
//
//fun EntryProviderScope<NavKey>.searchEntry(
//    goToMovie: (Int) -> Unit,
//    goToTv: (Int) -> Unit,
//    goToPeople: (Int) -> Unit,
//    goToSeries: (Int) -> Unit,
//    onShowSnackbar: suspend (String, String?) -> Boolean
//) {
//    entry<SearchNavKey> { searchNavKey ->
//        Log.d("deeplink query -> ${searchNavKey.query}, deeplink searchType -> ${searchNavKey.searchType}")
//
//        val searchType = when (searchNavKey.searchType) {
//            "multi" -> SearchType.MULTI
//            "movie" -> SearchType.MOVIE
//            "people" -> SearchType.PEOPLE
//            "series" -> SearchType.SERIES
//            "tv" -> SearchType.TV
//            else -> SearchType.MULTI
//        }
//
//        SearchScreen(
//            goToMovie = goToMovie,
//            goToTv = goToTv,
//            goToPeople = goToPeople,
//            goToSeries = goToSeries,
//            onShowSnackbar = onShowSnackbar,
//            viewModel = hiltViewModel<SearchVM, SearchVM.Factory>(
//                key = SearchVM.TAG
//            ) { factory ->
//                factory.create(initialQuery = searchNavKey.query, initialSearchType = searchType)
//            }
//        )
//    }
//}