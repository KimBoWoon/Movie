package com.bowoon.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.bowoon.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchNavKey : NavKey

fun EntryProviderScope<NavKey>.searchEntry(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<SearchNavKey> {
        SearchScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar
        )
    }
}