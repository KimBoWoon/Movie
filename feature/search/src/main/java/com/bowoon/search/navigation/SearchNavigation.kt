package com.bowoon.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bowoon.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

fun NavController.navigateToSearch(navOptions: NavOptions) = navigate(route = SearchRoute, navOptions)

fun NavGraphBuilder.searchScreen(
    onMovieClick: (String, String, String) -> Unit
) {
    composable<SearchRoute>() {
        SearchScreen(onMovieClick)
    }
}