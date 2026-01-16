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
    entry<SearchNavKey> {
        Log.d("deeplink query -> ${it.query}, deeplink searchType -> ${it.searchType}")

        val searchType = when (it.searchType) {
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
            query = it.query,
            searchType = searchType
        )
    }
}