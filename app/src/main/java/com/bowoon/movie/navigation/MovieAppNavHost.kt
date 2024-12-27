package com.bowoon.movie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.bowoon.detail.navigation.detailScreen
import com.bowoon.detail.navigation.navigateToDetail
import com.bowoon.favorite.navigation.favoriteScreen
import com.bowoon.home.navigation.HomeBaseRoute
import com.bowoon.home.navigation.homeSection
import com.bowoon.movie.MovieAppState
import com.bowoon.my.navigation.myScreen
import com.bowoon.search.navigation.searchScreen

@Composable
fun MovieAppNavHost(
    modifier: Modifier,
    appState: MovieAppState,
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
            detailScreen(navController)
        }
        searchScreen(onMovieClick = navController::navigateToDetail)
        favoriteScreen(onMovieClick = navController::navigateToDetail)
        myScreen()
    }
}