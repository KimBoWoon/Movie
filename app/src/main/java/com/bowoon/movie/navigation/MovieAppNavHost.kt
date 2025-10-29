package com.bowoon.movie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.bowoon.detail.navigation.detailSection
import com.bowoon.detail.navigation.navigateToDetail
import com.bowoon.favorite.navigation.favoriteScreen
import com.bowoon.home.navigation.HomeRoute
import com.bowoon.home.navigation.homeSection
import com.bowoon.movie.MovieAppState
import com.bowoon.my.navigation.myScreen
import com.bowoon.people.navigation.navigateToPeople
import com.bowoon.people.navigation.peopleScreen
import com.bowoon.search.navigation.searchScreen
import com.bowoon.series.navigation.navigateToSeries
import com.bowoon.series.navigation.seriesScreen

@Composable
fun MovieAppNavHost(
    modifier: Modifier,
    appState: MovieAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        /**
         * main navigation
         */
        homeSection(
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
            onShowSnackbar = onShowSnackbar,
        )
        myScreen()

        /**
         * other screen
         */
        detailSection(
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