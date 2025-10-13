package com.bowoon.home.navigation

import androidx.annotation.Keep
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bowoon.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
@Keep
data object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavGraphBuilder.homeSection(
    goToMovie: (Int) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable<HomeRoute>() {
        HomeScreen(
            goToMovie = goToMovie,
            onShowSnackbar = onShowSnackbar
        )
    }
}