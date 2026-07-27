package com.cheeke.surfy.search.impl

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.cheeke.surfy.common.Log
import com.cheeke.surfy.model.SearchType
import com.cheeke.surfy.search.api.SearchNavKey

fun EntryProviderScope<NavKey>.searchEntry(
    goToMovie: (Int) -> Unit,
    goToTv: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<SearchNavKey> { searchNavKey ->
        Log.d("deeplink query -> ${searchNavKey.query}, deeplink searchType -> ${searchNavKey.searchType}")

        SearchScreen(
            goToMovie = goToMovie,
            goToTv = goToTv,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar,
            viewModel = hiltViewModel<SearchVM, SearchVM.Factory>(
                key = SearchVM.TAG
            ) { factory ->
                factory.create(initialQuery = searchNavKey.query, initialSearchType = searchNavKey.searchType)
            }
        )
    }
}

fun NavBackStack<NavKey>.goToSearch(query: String, searchType: SearchType) = add(element = SearchNavKey(query = query, searchType = searchType))