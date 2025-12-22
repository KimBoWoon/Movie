package com.bowoon.movie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.bowoon.detail.movie.navigation.detailScreen
import com.bowoon.detail.movie.navigation.navigateToDetail
import com.bowoon.detail.people.navigation.navigateToPeople
import com.bowoon.detail.people.navigation.peopleScreen
import com.bowoon.detail.series.navigation.navigateToSeries
import com.bowoon.detail.series.navigation.seriesScreen
import com.bowoon.favorite.navigation.favoriteScreen
import com.bowoon.home.navigation.HomeRoute
import com.bowoon.home.navigation.homeScreen
import com.bowoon.movie.MovieAppState
import com.bowoon.my.navigation.myScreen
import com.bowoon.search.navigation.searchScreen

@Composable
fun MovieAppNavHost(
    modifier: Modifier,
    appState: MovieAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val navController = appState.navController

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = HomeRoute
    ) {
        /**
         * main navigation
         */
        homeScreen(
            goToMovie = navController::navigateToDetail
        )
        searchScreen(
            goToMovie = navController::navigateToDetail,
            goToPeople = navController::navigateToPeople,
            goToSeries = navController::navigateToSeries,
            onShowSnackbar = onShowSnackbar
        )
        favoriteScreen(
            goToMovie = navController::navigateToDetail,
            goToPeople = navController::navigateToPeople,
            onShowSnackbar = onShowSnackbar
        )
        myScreen()

        /**
         * other screen
         */
        detailScreen(
            goToBack = navController::navigateUp,
            goToMovie = navController::navigateToDetail,
            goToPeople = navController::navigateToPeople,
            onShowSnackbar = onShowSnackbar
        )
        peopleScreen(
            goToBack = navController::navigateUp,
            goToMovie = navController::navigateToDetail,
            onShowSnackbar = onShowSnackbar
        )
        seriesScreen(
            goToBack = navController::navigateUp,
            goToMovie = navController::navigateToDetail
        )
    }
}