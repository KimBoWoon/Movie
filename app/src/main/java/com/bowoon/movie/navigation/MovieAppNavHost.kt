package com.bowoon.movie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.bowoon.detail.navigation.detailSection
import com.bowoon.detail.navigation.navigateToDetail
import com.bowoon.favorite.navigation.favoriteScreen
import com.bowoon.home.navigation.HomeBaseRoute
import com.bowoon.home.navigation.homeSection
import com.bowoon.movie.MovieAppState
import com.bowoon.my.navigation.myScreen
import com.bowoon.people.navigation.navigateToPeople
import com.bowoon.people.navigation.peopleScreen
import com.bowoon.search.navigation.searchScreen

@Composable
fun MovieAppNavHost(
    modifier: Modifier,
    appState: MovieAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier
    ) {
        homeSection(
            onMovieClick = navController::navigateToDetail,
        ) {
            detailSection(
                navController = navController,
                onShowSnackbar = onShowSnackbar
            ) {
                peopleScreen(
                    navController = navController,
                    onMovieClick = navController::navigateToDetail,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
        searchScreen(
            onMovieClick = navController::navigateToDetail
        )
        favoriteScreen(
            onMovieClick = navController::navigateToDetail,
            onPeopleClick = navController::navigateToPeople,
            onShowSnackbar = onShowSnackbar
        )
        myScreen()
    }
}