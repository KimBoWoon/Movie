package com.bowoon.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.common.Log
import com.bowoon.model.SearchType
import com.bowoon.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data class SearchNavKey(
    val query: String = "",
    val searchType: String = "movie"
) : NavKey

fun EntryProviderScope<NavKey>.searchEntry(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<SearchNavKey> { searchNavKey ->
        Log.d("deeplink query -> ${searchNavKey.query}, deeplink searchType -> ${searchNavKey.searchType}")

        val searchType = when (searchNavKey.searchType) {
            "movie" -> SearchType.MOVIE
            "people" -> SearchType.PEOPLE
            "series" -> SearchType.SERIES
            else -> SearchType.MOVIE
        }

        SearchScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar,
            query = searchNavKey.query,
            searchType = searchType
        )
    }
}