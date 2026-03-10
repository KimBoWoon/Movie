package com.cheeke.surfy.search.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.search.SearchScreen
import com.cheeke.surfy.search.SearchVM
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(
    val query: String = "",
    val searchType: String = "surfy"
) : NavKey

fun EntryProviderScope<NavKey>.searchEntry(
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<SearchNavKey> { searchNavKey ->
        Log.d("deeplink query -> ${searchNavKey.query}, deeplink searchType -> ${searchNavKey.searchType}")

        val searchType = when (searchNavKey.searchType) {
            "surfy" -> SearchType.MOVIE
            "people" -> SearchType.PEOPLE
            "series" -> SearchType.SERIES
            else -> SearchType.MOVIE
        }

        SearchScreen(
            goToMovie = goToMovie,
            goToTv = goToTv,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar,
            viewModel = hiltViewModel<SearchVM, SearchVM.Factory>(
                key = SearchVM.Companion.TAG
            ) { factory ->
                factory.create(initialQuery = searchNavKey.query, initialSearchType = searchType)
            }
        )
    }
}