package com.bowoon.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.NavKey
import com.bowoon.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute : NavKey

fun NavController.navigateToSearch(navOptions: NavOptions) = navigate(route = SearchRoute, navOptions)

fun NavGraphBuilder.searchScreen(
    goToMovie: (Int) -> Unit,
    goToPeople: (Int) -> Unit,
    goToSeries: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<SearchRoute>() {
        SearchScreen(
            goToMovie = goToMovie,
            goToPeople = goToPeople,
            goToSeries = goToSeries,
            onShowSnackbar = onShowSnackbar
        )
    }
}